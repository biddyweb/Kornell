package kornell.server.jdbc.repository

import kornell.server.jdbc.SQL._
import java.sql.ResultSet
import kornell.core.util.UUID
import kornell.server.repository.TOs
import kornell.core.to.InstitutionHostNamesTO
import scala.collection.JavaConverters._

class InstitutionHostNameRepo(institutionUUID: String) {

  def get = {
    TOs.newInstitutionHostNamesTO(
        sql"""
        | select hostName from InstitutionHostName 
        | where institutionUUID = ${institutionUUID}"""
    .map[String])
  }
  
  def updateHostnames(hostnames: InstitutionHostNamesTO) = {
    removeHostnames(institutionUUID)
    hostnames.getInstitutionHostNames.asScala.foreach(hostname => addHostname(hostname))
    hostnames
  }
  
  def removeHostnames(institutionUUID: String) = {
    sql"""delete from InstitutionHostName where institutionUUID = ${institutionUUID}""".executeUpdate
    this
  }
  
  def addHostname(hostName: String) = {
    sql"""insert into InstitutionHostName (uuid, hostName, institutionUUID) values
    (${UUID.random},
    ${hostName},
    ${institutionUUID})""".executeUpdate
  }
}

object InstitutionHostNameRepo {
  def apply(institutionUUID: String) = new InstitutionHostNameRepo(institutionUUID)
}