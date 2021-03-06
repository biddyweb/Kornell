package kornell.server.repository.s3

import java.io.ByteArrayInputStream
import java.sql.ResultSet
import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.immutable.Stream.consWrapper
import scala.io.Source
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectListing
import kornell.server.jdbc.SQL.SQLHelper
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider
import kornell.server.util.Settings
import com.amazonaws.services.s3.model.ObjectMetadata
import scala.collection.JavaConverters._
import java.io.InputStream
import kornell.core.util.StringUtils._
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.auth.AWSCredentials
import java.io.BufferedInputStream
import com.google.gwt.http.client.URL
import java.net.URL
import kornell.core.error.exception.EntityNotFoundException
import java.util.logging.Logger
import scala.util.Try

class S3(regionName: String,
  val accessKey: String,
  val secretKey: String,
  val bucket: String,
  val prefix: String) {
  
  val region = Region.getRegion(Regions.fromName(regionName))

  val logger = Logger.getLogger(classOf[S3].getName)
  
  //TODO: Option instead of null checks
  val creds: AWSCredentials = if (accessKey != null)
    new BasicAWSCredentials(accessKey, secretKey)
  else if (getClass().getResourceAsStream("/AwsCredentials.properties") != null)
    new ClasspathPropertiesFileCredentialsProvider().getCredentials()
  else null

  val s3 = if (creds != null)
    new AmazonS3Client(creds)
  else new AmazonS3Client()

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

  def put(key: String, value: String) =
    s3.putObject(bucket, composeURL(prefix, key), new ByteArrayInputStream(value.getBytes()), null)

  def put(key: String, value: InputStream, contentType: String, contentDisposition: String, metadataMap: Map[String, String]) = {
    val metadata = new ObjectMetadata()
    metadata.setUserMetadata(metadataMap asJava)
    Option(contentType).foreach { metadata.setContentType(_) }
    Option(contentDisposition).foreach { metadata.setContentDisposition(_) }
    s3.putObject(bucket, composeURL(prefix, key), value, metadata)
  }

  def delete(key: String) = {
    s3.deleteObject(bucket, composeURL(prefix, key))
  }

  def getObject(key: String) =
    s3.getObject(bucket, prefix + "/" + key)

  def source( infix: String, key: String) =
    inputStream(infix,key).map{Source.fromInputStream(_,"UTF-8")}
  

  def inputStream(infix: String, key: String) = Try {
    logger.info("*************")
    logger.info(s"inputStream(${infix}, ${key})")    
    val fqkn = url(infix, key)

    try	{ 
      s3.getObject(bucket, fqkn).getObjectContent
    }catch {
      case e:Throwable => {
        val cmd = s"aws s3api get-object --bucket ${bucket} --key ${fqkn} --region ${region} file.out"
        logger.warning("Could not load object. Try ["+cmd+"]")
        throw e
      }
    }
  }

  def url(infix: String, key: String) = composeURL(prefix, infix, key)

  //TODO: https is giving security warnings on chrome
  def url(key: String) = s3.getResourceUrl(bucket,composeURL(prefix, key)).replace("https://", "http://")
  


}

object S3 {
  val DEFAULT_USER_CONTENT_BUCKET = "usercontent.test.eduvem.com";
  val USER_CONTENT_BUCKET = Settings.get("USER_CONTENT_BUCKET").getOrElse(DEFAULT_USER_CONTENT_BUCKET)

  val certificates = new S3("sa-east-1",
    null, null,
    USER_CONTENT_BUCKET,
    "usercontent/certificates")

  implicit def toS3(rs: ResultSet) = new S3(
    rs.getString("region"),
    rs.getString("accessKeyId"), rs.getString("secretAccessKey"),
    rs.getString("bucketName"), rs.getString("prefix"))

  def apply(repository_uuid: String) =
    sql"""
    	select region,accessKeyId,secretAccessKey,bucketName,prefix 
    	from S3ContentRepository
    	where uuid=$repository_uuid
    """.first[S3].getOrElse({ throw new EntityNotFoundException("repositoryNotFound") })

}