package kornell.server.api

import scala.collection.JavaConverters._
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.core.entity.Assessment
import kornell.core.entity.Enrollment
import kornell.core.lom.Contents
import kornell.server.jdbc.SQL._
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.jdbc.repository.CourseClassRepo
import kornell.server.jdbc.repository.EnrollmentRepo
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.util.Conditional.toConditional
import kornell.server.util.Err
import kornell.server.util.AccessDeniedErr
import kornell.server.repository.ContentRepository

@Produces(Array(Enrollment.TYPE))
class EnrollmentResource(uuid: String) {
  lazy val enrollment = get
  lazy val enrollmentRepo = EnrollmentRepo(uuid)

  def get = enrollmentRepo.get

  @GET
  def first = enrollmentRepo.first

  @PUT
  @Produces(Array("text/plain"))
  @Consumes(Array(Enrollment.TYPE))
  def update(enrollment: Enrollment) = {
    EnrollmentRepo(enrollment.getUUID).update(enrollment)
  }
  .requiring(PersonRepo(getAuthenticatedPersonUUID).hasPowerOver(enrollment.getPersonUUID),  AccessDeniedErr() )
  .get
  
  
  @Path("actoms/{actomKey}")
  def actom(@PathParam("actomKey") actomKey: String) = ActomResource(uuid, actomKey)

  @GET
  @Path("contents")
  @Produces(Array(Contents.TYPE))
  def contents(implicit @Context sc: SecurityContext): Option[Contents] = AuthRepo().withPerson { person =>  
    first map { e =>
    	ContentRepository.findKNLVisitedContent(e)
    }
   }


  @GET
  @Path("approved")
  @Produces(Array("application/octet-stream"))
  def approved =  {
    val e = first.get
    if(Assessment.PASSED == e.getAssessment){
      e.getAssessmentScore.toString
    } else {
      ""
    }
  }
  
  
  @DELETE
  @Produces(Array(Enrollment.TYPE))
  def delete(implicit @Context sc: SecurityContext) = {
    val enrollmentRepo = EnrollmentRepo(uuid)
    val enrollment = enrollmentRepo.get   
    enrollmentRepo.delete(uuid)
    enrollment
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin(CourseClassRepo(EnrollmentRepo(uuid).get.getCourseClassUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isCourseClassAdmin(EnrollmentRepo(uuid).get.getCourseClassUUID), AccessDeniedErr())
    
  
}