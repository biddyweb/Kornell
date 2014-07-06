package kornell.server.repository

import java.math.BigDecimal
import java.util.Date
import scala.collection.JavaConverters._
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import kornell.core.to.CourseVersionTO
import kornell.core.to.report.CertificateInformationTO
import kornell.core.to.RegistrationRequestTO
import kornell.core.entity.CourseClass
import kornell.core.to.RegistrationsTO
import kornell.core.to.EnrollmentRequestsTO
import kornell.core.to.EnrollmentRequestTO
import kornell.core.to.report.CourseClassReportTO
import kornell.core.to.EnrollmentsTO
import kornell.core.to.RoleTO
import kornell.core.to.CourseVersionsTO
import kornell.core.to.CoursesTO
import kornell.core.to.report.EnrollmentsBreakdownTO
import kornell.core.entity.Person
import kornell.core.entity.Course
import kornell.core.entity.CourseVersion
import kornell.core.entity.Enrollment
import kornell.core.to.EnrollmentTO
import kornell.core.to.TOFactory
import kornell.core.to.CourseClassTO
import kornell.core.entity.Role
import kornell.core.entity.Registration
import kornell.core.util.StringUtils
import kornell.server.repository.s3.S3
import kornell.core.to.LibraryFileTO

//TODO: Consider turning to Object
object TOs {
  val tos = AutoBeanFactorySource.create(classOf[TOFactory])

  def newUserInfoTO = tos.newUserInfoTO.as
  def newRegistrationsTO: RegistrationsTO = tos.newRegistrationsTO.as
  def newEnrollmentsTO: EnrollmentsTO = tos.newEnrollmentsTO.as
  def newCoursesTO: CoursesTO = tos.newCoursesTO.as
  def newCourseVersionsTO: CourseVersionsTO = tos.newCourseVersionsTO.as
  def newLibraryFileTO: LibraryFileTO = tos.newLibraryFileTO.as

  def newRegistrationsTO(registrationList: List[Registration]): RegistrationsTO = {
    val registrations = newRegistrationsTO
    registrations.setRegistrations(registrationList asJava)
    registrations
  }

  def newEnrollmentsTO(enrollmentList: List[EnrollmentTO]): EnrollmentsTO = {
    val enrollments = newEnrollmentsTO
    enrollments.setEnrollmentTOs(enrollmentList asJava)
    enrollments
  }

  def newCoursesTO(coursesList: List[Course]): CoursesTO = {
    val courses = newCoursesTO
    courses.setCourses(coursesList asJava)
    courses
  }

  def newCourseVersionsTO(courseVersionsList: List[CourseVersion]): CourseVersionsTO = {
    val courseVersions = newCourseVersionsTO
    courseVersions.setCourseVersions(courseVersionsList asJava)
    courseVersions
  }

  def newCourseClassesTO(l: List[CourseClassTO]) = {
    val to = tos.newCourseClassesTO.as
    to.setCourseClasses(l asJava)
    to
  }

  def newCourseClassTO(course: Course, version: CourseVersion, clazz: CourseClass): CourseClassTO = {
    val classTO = tos.newCourseClassTO.as
    classTO.setCourseVersionTO(newCourseVersionTO(course, version))
    classTO.setCourseClass(clazz)
    classTO
  }

  def newEnrollmentTO(enrollment: Enrollment, person: Person): EnrollmentTO = {
    val enrollmentTO = tos.newEnrollmentTO.as
    enrollmentTO.setEnrollment(enrollment)
    enrollmentTO.setPerson(person)
    enrollmentTO
  }

  def newCourseVersionTO(course: Course, version: CourseVersion): CourseVersionTO = {
    val versionTO = tos.newCourseVersionTO.as
    val s3 = S3(version.getRepositoryUUID)
    versionTO.setDistributionURL(StringUtils.composeURL(s3.baseURL, s3.prefix))
    versionTO.setCourse(course)
    versionTO.setCourseVersion(version)
    versionTO
  }

  def newRegistrationRequestTO: RegistrationRequestTO = tos.newRegistrationRequestTO.as
  def newRegistrationRequestTO(institutionUUID: String, fullName: String, email: String, password: String,cpf:String=null,username:String=null): RegistrationRequestTO = {
    val to = newRegistrationRequestTO
    to.setInstitutionUUID(institutionUUID)
    to.setFullName(fullName)
    to.setEmail(email)
    to.setUsername(username)
    to.setPassword(password)
    to.setCPF(cpf)
    to
  }

  def newEnrollmentRequestTO: EnrollmentRequestTO = tos.newEnrollmentRequestTO.as
  def newEnrollmentRequestTO(institutionUUID: String, courseClassUUID: String, fullName: String, email: String, cpf: String): EnrollmentRequestTO = {
    val to = newEnrollmentRequestTO
    to.setInstitutionUUID(institutionUUID)
    to.setCourseClassUUID(courseClassUUID)
    to.setFullName(fullName)
    to.setEmail(email)
    to.setCPF(cpf)
    to
  }

  def newEnrollmentRequestsTO: EnrollmentRequestsTO = tos.newEnrollmentRequestsTO.as
  def newEnrollmentRequestsTO(enrollmentRequests: java.util.List[EnrollmentRequestTO]): EnrollmentRequestsTO = {
    val to = newEnrollmentRequestsTO
    to.setEnrollmentRequests(enrollmentRequests)
    to
  }

  def newCertificateInformationTO: CertificateInformationTO = new CertificateInformationTO
  def newCertificateInformationTO(personFullName: String, personCPF: String, courseTitle: String, courseClassFinishedDate: Date, assetsURL: String, distributionPrefix: String): CertificateInformationTO = {
    val to = newCertificateInformationTO
    to.setPersonFullName(personFullName)
    to.setPersonCPF(personCPF)
    to.setCourseTitle(courseTitle)
    to.setCourseClassFinishedDate(courseClassFinishedDate)
    to.setAssetsURL(assetsURL)
    to.setDistributionPrefix(distributionPrefix)
    to
  }

  def newCourseClassReportTO: CourseClassReportTO = new CourseClassReportTO
  def newCourseClassReportTO(fullName: String, username: String, state: String, progressState: String, progress: Int): CourseClassReportTO = {
    val to = newCourseClassReportTO
    to.setFullName(fullName)
    to.setUsername(username)
    to.setState(state)
    to.setProgressState(progressState)
    to.setProgress(progress)
    to
  }

  def newEnrollmentsBreakdownTO: EnrollmentsBreakdownTO = new EnrollmentsBreakdownTO
  def newEnrollmentsBreakdownTO(name: String, count: Integer): EnrollmentsBreakdownTO = {
    val to = newEnrollmentsBreakdownTO
    to.setName(name)
    to.setCount(count)
    to
  }

  def newRoleTO(role: Role, person: Person) = {
    val r = tos.newRoleTO.as
    r.setRole(role)
    r.setPerson(person)
    r
  }

  def newRolesTO(roleTOs: List[RoleTO]) = {
    val rs = tos.newRolesTO.as
    rs.setRoleTOs(roleTOs.asJava)
    rs
  }

  def newLibraryFilesTO(libraryFileTOs: List[LibraryFileTO]) = {
    val lf = tos.newLibraryFilesTO.as
    lf.setLibraryFiles(libraryFileTOs.asJava)
    lf
  }
}
