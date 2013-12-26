package kornell.server.repository.s3

import kornell.server.repository.jdbc.SQLInterpolation._
import java.sql.ResultSet
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.s3.model.ObjectListing
import com.amazonaws.services.s3.model.S3ObjectSummary
import java.io.ByteArrayInputStream
import com.amazonaws.services.s3.model.PutObjectRequest
import scala.io.Source
import kornell.core.util.StringUtils._
import kornell.server.repository.jdbc.CourseClasses

class S3(regionName: String,
  val accessKey: String,
  val secretKey: String,
  val bucket: String,
  val prefix: String,
  distributionURL: String) {

  //TODO: An actom is an undivisible unit of learning content. Pls write that on the wiki, bur for now it is just a key...
  type Actom = String

  val region = Region.getRegion(Regions.fromName(regionName))
  val creds = new BasicAWSCredentials(accessKey, secretKey)
  val s3 = new AmazonS3Client(creds)
  lazy val client = s3

  s3.setRegion(region)

  lazy val first: ObjectListing = s3.listObjects(bucket, prefix)
  def next(prev: ObjectListing): ObjectListing = s3.listNextBatchOfObjects(prev)

  def isActom(key: String) = key.endsWith("html")

  lazy val listings: Stream[ObjectListing] = first #::
    listings.map(next)
    .takeWhile(!_.getObjectSummaries.isEmpty)

  lazy val keys = listings
    .flatten(_.getObjectSummaries.asScala)
    .map(_.getKey)

  lazy val actoms: Stream[Actom] = keys.filter(isActom)

  def put(key: String, value: String) =
    s3.putObject(bucket, prefix + "/" + key, new ByteArrayInputStream(value.getBytes()), null)

  def getObject(key: String) =
    s3.getObject(bucket, prefix + "/" + key)

  def source(key: String) =
    Source.fromURL(url(key), "utf-8")

  def exists(key: String) = ??? //TODO: HTTP HEAD

  def url(key: String) = composeURL(baseURL, prefix, key)

  //TODO: Resolve base url from region
  lazy val baseURL =
    if (distributionURL != null)
      distributionURL
    else s"http://${bucket}.s3-sa-east-1.amazonaws.com/"

}

object S3 {
  implicit def toS3(rs: ResultSet) = new S3(
    rs.getString("region"),
    rs.getString("accessKeyId"), rs.getString("secretAccessKey"),
    rs.getString("bucketName"), rs.getString("prefix"),
    rs.getString("distributionURL"))

  def apply(repository_uuid: String) =
    sql"""
    	select region,accessKeyId,secretAccessKey,bucketName,prefix,distributionURL
    	from S3ContentRepository
    	where uuid=$repository_uuid
    """.first[S3].getOrElse({ throw new IllegalArgumentException(s"Could not find repository [$repository_uuid]") })

  def main(args: Array[String]) {

    /*S3("F7A4A77F-D519-4348-8F62-EDB0C2C48395")
      .actoms
      .foreach { println(_) }*/

    val classRepo = CourseClasses("B6A60AB5-3889-47B4-93DA-60E515309DAF")
    val versionRepo = classRepo.version
    val version = versionRepo.get
    val repositoryUUID = version.getRepositoryUUID();
    val s3 = S3(repositoryUUID)
    val structureSrc = s3.source("structure.knl")
    val structureText = structureSrc.mkString("")
    val baseURL = s3.baseURL
    printf(baseURL)
    //val visited = classRepo.actomsVisitedBy(person) 
    //val contents = ContentsParser.parse(baseURL, s3.prefix, structureText, visited)
    /*
    val s3 = new S3("sa-east-1","AKIAJZREK4G3OPFKQQTA","vFn6ZEE9PjKxDJ0Sqe2fit5wqL8AeFExVHELUtJ2","aws-examples","ten-thousand")
    s3.actoms.foreach {println(_)} 
    */

  }
}