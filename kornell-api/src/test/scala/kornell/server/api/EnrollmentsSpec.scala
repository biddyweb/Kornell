package kornell.server.api

import java.util.ArrayList
import scala.collection.JavaConverters.asScalaBufferConverter
import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.junit.JUnitRunner
import org.scalatest.junit.JUnitRunner
import kornell.core.entity.EnrollmentState
import kornell.core.entity.RoleType
import kornell.core.to.EnrollmentRequestTO
import kornell.server.jdbc.SQL._
import kornell.server.jdbc.repository.EnrollmentsRepo
import kornell.server.jdbc.repository.InstitutionsRepo
import kornell.server.jdbc.repository.PeopleRepo
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.repository.Entities
import kornell.server.repository.TOs
import kornell.server.repository.TOs
import kornell.server.test.UnitSpec
import kornell.core.entity.Enrollment
import kornell.core.entity.CourseClass
import kornell.server.jdbc.repository.CourseClassesRepo


@RunWith(classOf[JUnitRunner])
class EnrollmentsSpec extends UnitSpec with BeforeAndAfter{
  val userResource = new UserResource
  val courseClassesResource = new CourseClassesResource
  val enrollmentsResource = new EnrollmentsResource
  val institution = InstitutionsRepo.create(Entities.newInstitution(randUUID, randStr, randStr, randStr, randURL, randURL, false))
	val course = Entities.newCourse(randUUID, randStr, randStr, randStr, randStr)
	val courseVersion = Entities.newCourseVersion(randUUID, randStr, course.getUUID, randStr, null, randStr)
	val className = randStr
	val courseClass = Entities.newCourseClass(randUUID, className, courseVersion.getUUID, institution.getUUID, new java.math.BigDecimal(60), true, false, 1000)
	val courseClass2 = Entities.newCourseClass(randUUID, randStr, courseVersion.getUUID, institution.getUUID, new java.math.BigDecimal(60), true, false, 1000)
	val courseClass3 = Entities.newCourseClass(randUUID, randStr, courseVersion.getUUID, institution.getUUID, new java.math.BigDecimal(60), true, false, 1000)
	val fullName = randName
  val email = randEmail
  val cpf = randStr
  val platformAdminCPF = randStr
  val institutionAdminCPF = randStr
  val notAnAdminCPF = randStr
  var mockHttpServletResponse = new MockHttpServletResponse(0, "")
  
  
  val platformAdmin = {
    val platformAdmin = PeopleRepo.createPersonCPF(platformAdminCPF, randName)
    PersonRepo(platformAdmin.getUUID).setPassword(platformAdmin.getCPF, platformAdmin.getCPF).registerOn(institution.getUUID)
    sql"""
    	insert into Role (uuid, username, role, institution_uuid, course_class_uuid)
    	values (${randUUID}, ${platformAdmin.getCPF}, 
    	${RoleType.platformAdmin.toString}, 
    	${null}, 
    	${null} )
	    """.executeUpdate
	  platformAdmin
  }
  
  val platformAdminSecurityContext = new MockSecurityContext(platformAdmin.getCPF)
  
  val institutionAdmin = {
    val institutionAdmin = PeopleRepo.createPersonCPF(institutionAdminCPF, randName)
    PersonRepo(institutionAdmin.getUUID).setPassword(institutionAdmin.getCPF, institutionAdmin.getCPF).registerOn(institution.getUUID)
    
    sql"""
    	insert into Role (uuid, username, role, institution_uuid, course_class_uuid)
    	values (${randUUID}, ${institutionAdmin.getCPF}, 
    	${RoleType.institutionAdmin.toString}, 
    	${institution.getUUID}, 
    	${null} )
	    """.executeUpdate
	  institutionAdmin
  }
  
  val institutionAdminSecurityContext = new MockSecurityContext(institutionAdmin.getCPF)
  
  val notAnAdmin = {
    val notAnAdmin = PeopleRepo.createPersonCPF(notAnAdminCPF, randName)
    PersonRepo(notAnAdmin.getUUID).setPassword(notAnAdmin.getCPF, notAnAdmin.getCPF).registerOn(institution.getUUID)
	  notAnAdmin
  }
  
  val enrollment = EnrollmentsRepo.create(Entities.newEnrollment(randUUID, null, courseClass.getUUID, notAnAdmin.getUUID, null, "", EnrollmentState.requested))
  val enrollmentResource = new EnrollmentResource(enrollment.getUUID)
  
  val notAnAdminSecurityContext = new MockSecurityContext(notAnAdmin.getCPF)
   
  
  
  before {
    mockHttpServletResponse.sendError(0, "")
  }

  after {
  }
  
  "The platformAdmin" should "be able to create a class" in {
    val x = System.getProperty("TEST_MODE")
    val y = System.getProperty("TEST_MODEx")
    val courseClassNew = courseClassesResource.create(platformAdminSecurityContext, mockHttpServletResponse, courseClass).asInstanceOf[CourseClass]
    assert(CourseClassesRepo.byInstitution(institution.getUUID).length == 1 && mockHttpServletResponse.getStatus == 0 && courseClassNew != null && courseClassNew.getCourseVersionUUID == courseVersion.getUUID)
  } 
  
  "The platformAdmin" should "not be able to create a class with the same uuid" in {
    courseClass.setName(randStr)
    courseClassesResource.create(platformAdminSecurityContext, mockHttpServletResponse, courseClass)
    assert(CourseClassesRepo.byInstitution(institution.getUUID).length == 1 && mockHttpServletResponse.getStatus != 0)
  }
  
  "The platformAdmin" should "not be able to create a class with the same name" in {
    courseClass.setUUID(randUUID)
    courseClass.setName(className)
    courseClassesResource.create(platformAdminSecurityContext, mockHttpServletResponse, courseClass)
    assert(CourseClassesRepo.byInstitution(institution.getUUID).length == 1 && mockHttpServletResponse.getStatus != 0)
  }
  
  "The institutionAdmin" should "be able to create a class" in {
    val courseClass2New = courseClassesResource.create(institutionAdminSecurityContext, mockHttpServletResponse, courseClass2).asInstanceOf[CourseClass]
    assert(CourseClassesRepo.byInstitution(institution.getUUID).length == 2 && mockHttpServletResponse.getStatus == 0 && courseClass2New != null && courseClass2New.getCourseVersionUUID == courseVersion.getUUID)
  }
  
  "A user that's not a platform or institutionAdmin" should "not be able to create a class" in {
    courseClassesResource.create(notAnAdminSecurityContext, mockHttpServletResponse, courseClass3)
    assert(CourseClassesRepo.byInstitution(institution.getUUID).length == 2 && mockHttpServletResponse.getStatus != 0)
  }
  
  "The platformAdmin" should "be able to register and enroll one participant with the email" in {
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])    
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, fullName, email, null))
    enrollmentsResource.putEnrollments(platformAdminSecurityContext, mockHttpServletResponse, enrollmentRequestsTO)
    
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollments.size == enrollmentRequestsTO.getEnrollmentRequests.size && { 
      enrollmentsCreated.getEnrollments.asScala exists(e => e.getPerson.getFullName.equals(fullName))
    })
  }  
  
  "The platformAdmin" should "be able to register and enroll one participant with the cpf" in {
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])    
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, fullName, null, cpf))
    enrollmentsResource.putEnrollments(platformAdminSecurityContext, mockHttpServletResponse, enrollmentRequestsTO)
    
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollments.size == 2)
  }
  
  "The platformAdmin" should "not be able to register participants with duplicate emails or cpfs" in {
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])    
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, fullName, email, null))
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, fullName, null, cpf))
    enrollmentsResource.putEnrollments(platformAdminSecurityContext, mockHttpServletResponse, enrollmentRequestsTO)
    
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollments.size == 2)
  }
  
  "The institutionAdmin" should "be able to register and enroll one participant with the email" in {
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])    
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, "institutionAdmin"+fullName, "institutionAdmin"+email, null))
    val x = institutionAdminSecurityContext.getUserPrincipal().getName()
    enrollmentsResource.putEnrollments(institutionAdminSecurityContext, mockHttpServletResponse, enrollmentRequestsTO)
    
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollments.size == 3 && { 
      enrollmentsCreated.getEnrollments.asScala exists(e => e.getPerson.getFullName.equals("institutionAdmin"+fullName))
    })
  }  
  
  "A user that's not a platform or institutionAdmin" should "not be able to register and enroll one participant" in {
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])    
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, "notAnAdmin"+fullName, "notAnAdmin"+email, null))
    enrollmentsResource.putEnrollments(notAnAdminSecurityContext, mockHttpServletResponse, enrollmentRequestsTO)
    
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollments.size == 3 && !{ 
      enrollmentsCreated.getEnrollments.asScala exists(e => e.getPerson.getFullName.equals("notAnAdmin"+fullName))
    	} && mockHttpServletResponse.getStatus != 0)
  }  
  
  "A user" should "be able to request enrollment to a class" in {
    val enrollment = Entities.newEnrollment(randUUID, null, courseClass.getUUID, notAnAdmin.getUUID, null, null, EnrollmentState.requested)
    enrollmentsResource.create(notAnAdminSecurityContext, enrollment)
    
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollments.size == 4 && { 
      enrollmentsCreated.getEnrollments.asScala exists(e => e.getPerson.getFullName.equals(notAnAdmin.getFullName))
    	})
  }  
  
  "The platformAdmin" should "be able to update an enrollment" in {
    val x = "NEW NOTES"
    enrollment.setNotes(x)
    val newEnrollment = enrollmentResource.update(platformAdminSecurityContext, mockHttpServletResponse, enrollment).asInstanceOf[Enrollment]
    assert(x.equals(newEnrollment.getNotes))
  }
  
  "The institutionAdmin" should "be able to update an enrollment" in {
    val x = "NEW NOTES2"
    enrollment.setNotes(x)
    val newEnrollment = enrollmentResource.update(institutionAdminSecurityContext, mockHttpServletResponse, enrollment).asInstanceOf[Enrollment]
    assert(x.equals(newEnrollment.getNotes))
  }
  
  "A user" should "be able to update his own enrollment" in {
    val x = "NEW NOTES3"
    enrollment.setNotes(x)
    val newEnrollment = enrollmentResource.update(notAnAdminSecurityContext, mockHttpServletResponse, enrollment).asInstanceOf[Enrollment]
    assert(x.equals(newEnrollment.getNotes))
  }
  
  "A user" should "not be able to update an enrollment that doesn't belong to him" in {
    val enrollment = EnrollmentsRepo.create(Entities.newEnrollment(randUUID, null, courseClass.getUUID, institutionAdmin.getUUID, null, "", EnrollmentState.requested))
    val x = "NEW NOTES4"
    enrollment.setNotes(x)
    enrollmentResource.update(notAnAdminSecurityContext, mockHttpServletResponse, enrollment)
    assert(mockHttpServletResponse.getStatus != 0)
  } 
  
}