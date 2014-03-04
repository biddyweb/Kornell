package kornell.server.api

import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.GET
import java.util.Properties
import kornell.server.jdbc.SQL._

@Path("")
class RootResource {
  @Produces(Array("text/plain"))
  @GET
  def get = {
    val version = new Properties();
    //TODO: Say how long ago too
    val properties = Option(getClass().getClassLoader().getResourceAsStream("version.properties"))
    if (properties.isDefined)
      version.load(properties.get);
    val two = try
      sql"select 'rootResource'".map { rs => rs.getInt(1) }.head
    catch { case e: Exception => e.getMessage }

    s"""|Welcome to Kornell API\n
	  |
	  |Build number ${version.getProperty("build.number", "#development_build")}
	  """.stripMargin
  }
}