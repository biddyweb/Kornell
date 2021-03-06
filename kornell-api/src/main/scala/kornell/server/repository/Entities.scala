package kornell.server.repository

import java.math.BigDecimal
import java.util.Date
import java.util.Map
import java.util.UUID
import scala.collection.JavaConverters.seqAsJavaListConverter
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import kornell.core.entity.Assessment
import kornell.core.entity.ContentSpec
import kornell.core.entity.Course
import kornell.core.entity.CourseClassState
import kornell.core.entity.CourseVersion
import kornell.core.entity.Enrollment
import kornell.core.entity.EnrollmentState
import kornell.core.entity.EntityFactory
import kornell.core.entity.Institution
import kornell.core.entity.Person
import kornell.core.entity.PlatformAdminRole
import kornell.core.entity.Role
import kornell.core.entity.RoleType
import kornell.core.util.TimeUtil
import kornell.server.util.ValueFactory
import kornell.core.entity.RegistrationType
import kornell.core.entity.BillingType
import kornell.core.entity.InstitutionRegistrationPrefix
import kornell.core.entity.InstitutionType

object Entities {
  val factory = AutoBeanFactorySource.create(classOf[EntityFactory])

  def randUUID = UUID.randomUUID.toString

  def newPerson: Person = factory.newPerson.as

  def newPerson(
    uuid: String = null,
    fullName: String = null,
    lastPlaceVisited: String = null,
    email: String = null,
    company: String = null,
    title: String = null,
    sex: String = null,
    birthDate: Date = null,
    confirmation: String = null,
    telephone: String = null,
    country: String = null,
    state: String = null,
    city: String = null,
    addressLine1: String = null,
    addressLine2: String = null,
    postalCode: String = null,
    cpf: String = null,
    institutionUUID: String = null,
    termsAcceptedOn: String = null,
    registrationType: RegistrationType = null,
    institutionRegistrationPrefixUUID: String = null) = {

    val bday = ValueFactory.newDate
    val person = factory.newPerson.as
    person.setUUID(uuid)
    person.setFullName(fullName)
    person.setLastPlaceVisited(lastPlaceVisited)
    person.setEmail(email)
    person.setCompany(company)
    person.setTitle(title)
    person.setSex(sex)
    person.setBirthDate(TimeUtil.fromJUD(bday, birthDate))
    person.setConfirmation(confirmation)
    person.setTelephone(telephone)
    person.setCountry(country)
    person.setState(state)
    person.setCity(city)
    person.setAddressLine1(addressLine1)
    person.setAddressLine2(addressLine2)
    person.setPostalCode(postalCode)
    person.setCPF(cpf)
    person.setInstitutionUUID(institutionUUID)
    person.setTermsAcceptedOn(termsAcceptedOn)
    person.setRegistrationType(registrationType)
    person.setInstitutionRegistrationPrefixUUID(institutionRegistrationPrefixUUID)
    person
  }

  def newPeople(people: List[Person]) = {
    val ps = factory.newPeople.as
    ps.setPeople(people.asJava)
    ps
  }

  def newPrincipal(uuid: String, personUUID: String, username: String) = {
    val principal = factory.newPrincipal.as
    principal.setUUID(uuid)
    principal.setPersonUUID(personUUID)
    principal.setUsername(username)
    principal
  }

  def newCourse(uuid: String = randUUID, code: String = null,
    title: String = null, description: String = null,
    infoJson: String = null,
    institutionUUID: String = null,
    childCourse: Boolean): Course = {
    val c = factory.newCourse.as
    c.setUUID(uuid)
    c.setCode(code)
    c.setDescription(description)
    c.setTitle(title)
    c.setInfoJson(infoJson)
    c.setInstitutionUUID(institutionUUID)
    c.setChildCourse(childCourse)
    c
  }

  implicit def toEnrollments(enrollments: List[Enrollment]) = {
    val es = factory.newEnrollments.as
    es.setEnrollments(enrollments.asJava)
    es
  }

  def newRoles(roles: List[Role]) = {
    val rs = factory.newRoles.as
    rs.setRoles(roles.asJava)
    rs
  }

  def newEnrollment(uuid: String = randUUID, enrolledOn: Date = null,
    courseClassUUID: String, personUUID: String,
    progress: Integer = 0, notes: String = null,
    state: EnrollmentState, lastProgressUpdate: String = null,
    assessment: Assessment = null, lastAssessmentUpdate: String = null,
    assessmentScore: BigDecimal = null, certifiedAt: String = null,
    courseVersionUUID: String = null): Enrollment = {
    val e = factory.enrollment.as
    e.setUUID(uuid)
    e.setEnrolledOn(enrolledOn)
    e.setCourseClassUUID(courseClassUUID)
    e.setPersonUUID(personUUID)
    e.setProgress(progress)
    e.setNotes(notes)
    e.setState(state)
    e.setLastProgressUpdate(lastProgressUpdate)
    e.setAssessment(assessment)
    e.setLastAssessmentUpdate(lastAssessmentUpdate)
    e.setAssessmentScore(assessmentScore)
    e.setCertifiedAt(certifiedAt)
    e.setCourseVersionUUID(courseVersionUUID)
    e
  }

  def newEnrollments(enrollments: List[Enrollment]) = {
    val ps = factory.newEnrollments.as
    ps.setEnrollments(enrollments.asJava)
    ps
  }

  //FTW: Default parameter values
  def newInstitution(uuid: String = randUUID, name: String, fullName: String, terms: String, assetsURL: String, baseURL: String, 
      demandsPersonContactDetails: Boolean, validatePersonContactDetails: Boolean, allowRegistration: Boolean, allowRegistrationByUsername: Boolean, 
      activatedAt: Date, skin: String, billingType: BillingType, institutionType: InstitutionType, dashboardVersionUUID: String) = {
    val i = factory.newInstitution.as
    i.setName(name)
    i.setFullName(fullName)
    i.setUUID(uuid)
    if (terms != null)
      i.setTerms(terms.stripMargin)
    i.setAssetsURL(assetsURL)
    i.setBaseURL(baseURL)
    i.setDemandsPersonContactDetails(demandsPersonContactDetails)
    i.setValidatePersonContactDetails(validatePersonContactDetails)
    i.setAllowRegistration(allowRegistration)
    i.setAllowRegistrationByUsername(allowRegistrationByUsername)
    i.setActivatedAt(activatedAt)
    i.setSkin(skin)
    i.setBillingType(billingType)
    i.setInstitutionType(institutionType)
    i.setDashboardVersionUUID(dashboardVersionUUID)
    i
  }

  lazy val newUserRole = {
    val role = factory.newRole().as
    role.setRoleType(RoleType.user)
    role.setUserRole(factory.newUserRole().as())
    role
  }

  def newPlatformAdminRole(): PlatformAdminRole =
    factory.newPlatformAdminRole().as

  def newRoleAsPlatformAdmin(person_uuid: String): Role = {
    val role = factory.newRole().as
    role.setPersonUUID(person_uuid)
    role.setRoleType(RoleType.platformAdmin)
    role.setPlatformAdminRole(newPlatformAdminRole())
    role
  }

  def newInstitutionAdminRole(person_uuid: String, institutionUUID: String) = {
    val role = factory.newRole().as
    role.setPersonUUID(person_uuid)
    val institutionAdminRole = factory.newInstitutionAdminRole().as
    institutionAdminRole.setInstitutionUUID(institutionUUID)
    role.setRoleType(RoleType.institutionAdmin)
    role.setInstitutionAdminRole(institutionAdminRole)
    role
  }

  def newCourseClassAdminRole(person_uuid: String, courseClassUUID: String) = {
    val role = factory.newRole().as
    role.setPersonUUID(person_uuid)
    val courseClassAdminRole = factory.newCourseClassAdminRole().as
    courseClassAdminRole.setCourseClassUUID(courseClassUUID)
    role.setRoleType(RoleType.courseClassAdmin)
    role.setCourseClassAdminRole(courseClassAdminRole)
    role
  }
  
  def newTutorRole(person_uuid: String, courseClassUUID: String) = {
    val role = factory.newRole().as
    role.setPersonUUID(person_uuid)
    val tutorRole = factory.newTutorRole().as
    tutorRole.setCourseClassUUID(courseClassUUID)
    role.setRoleType(RoleType.tutor)
    role.setTutorRole(tutorRole)
    role
  }
  
  def newObserverRole(person_uuid: String, courseClassUUID: String) = {
    val role = factory.newRole().as
    role.setPersonUUID(person_uuid)
    val observerRole = factory.newObserverRole().as
    observerRole.setCourseClassUUID(courseClassUUID)
    role.setRoleType(RoleType.observer)
    role.setObserverRole(observerRole)
    role
  }

  def newCourseVersion(
    uuid: String = randUUID, name: String = null, 
    courseUUID: String = null, repositoryUUID: String = null, 
    versionCreatedAt: Date = new Date, distributionPrefix: String = null, 
    contentSpec: String = null, disabled: Boolean = false, parentVersionUUID: String = null,
    instanceCount: Integer = 1, label: String = null) = {
    val version = factory.newCourseVersion.as
    version.setUUID(uuid);
    version.setName(name);
    version.setCourseUUID(courseUUID);
    version.setRepositoryUUID(repositoryUUID);
    version.setVersionCreatedAt(versionCreatedAt)
    version.setDistributionPrefix(distributionPrefix)
    version.setDisabled(disabled)
    version.setParentVersionUUID(parentVersionUUID)
    version.setInstanceCount(instanceCount)
    version.setLabel(label)
    Option(contentSpec) foreach { spec =>
      val cSpec = ContentSpec.valueOf(spec)
      version.setContentSpec(cSpec);
    }
    version
  }

  def newCourseClass(uuid: String = null, name: String = null,
    courseVersionUUID: String = null, institutionUUID: String = null,
    requiredScore: BigDecimal = null, publicClass: Boolean = false,
    overrideEnrollments: Boolean = false,
    invisible: Boolean = false, maxEnrollments: Integer = null,
    createdAt: Date = null, createdBy: String = null,
    state: CourseClassState = null,
    registrationType: RegistrationType = null,
    institutionRegistrationPrefixUUID: String = null,
    courseClassChatEnabled: Boolean = false) = {
    val clazz = factory.newCourseClass.as
    clazz.setUUID(uuid)
    clazz.setName(name)
    clazz.setCourseVersionUUID(courseVersionUUID)
    clazz.setInstitutionUUID(institutionUUID)
    clazz.setRequiredScore(requiredScore)
    clazz.setPublicClass(publicClass)
    clazz.setOverrideEnrollments(overrideEnrollments)
    clazz.setInvisible(invisible)
    clazz.setMaxEnrollments(maxEnrollments)
    clazz.setCreatedAt(createdAt)
    clazz.setCreatedBy(createdBy)
    clazz.setState(state)
    clazz.setRegistrationType(registrationType)
    clazz.setInstitutionRegistrationPrefixUUID(institutionRegistrationPrefixUUID)
    clazz.setCourseClassChatEnabled(courseClassChatEnabled)
    clazz
  }

  def newWebRepository(uuid: String, distributionURL: String, prefix: String) = {
    val webRepo = factory.newWebReposiory.as
    webRepo.setUUID(uuid)
    webRepo.setPrefix(prefix)
    webRepo.setDistributionURL(distributionURL)
    webRepo
  }

  def newActomEntries(enrollmentUUID: String, actomKey: String, entriesMap: Map[String, String]) = {
    val entries = factory.newActomEntries.as
    entries.setActomKey(actomKey)
    entries.setEnrollmentUUID(enrollmentUUID)
    entries.setEntries(entriesMap)
    entries
  }

  def newS3ContentRepository(uuid: String = null,
    accessKeyId: String = null,
    secretAccessKey: String = null,
    bucketName: String = null,
    prefix: String = null,
    region: String = null,
    institutionUUID: String = null) = {
    val repo = factory.newS3ContentRepository.as
    repo.setUUID(uuid)
    repo.setAccessKeyId(accessKeyId)
    repo.setBucketName(bucketName)
    repo.setPrefix(prefix)
    repo.setRegion(region)
    repo.setSecretAccessKey(secretAccessKey)
    repo.setInstitutionUUID(institutionUUID)
    repo
  }

  def newChatThread(uuid: String = null, createdAt: Date = null, institutionUUID: String = null, courseClassUUID: String = null, personUUID: String = null, threadType: String = null, active: Boolean = true) = {
    val chatThread = factory.newChatThread.as
    chatThread.setUUID(uuid)
    chatThread.setCreatedAt(createdAt)
    chatThread.setInstitutionUUID(institutionUUID)
    chatThread.setCourseClassUUID(courseClassUUID)
    chatThread.setPersonUUID(personUUID)
    chatThread.setThreadType(threadType)
    chatThread.setActive(active)
    chatThread
  }

  def newChatThreadParticipant(uuid: String = null, chatThreadUUID: String = null, personUUID: String = null, 
      chatThreadName: String = null, lastReadAt: Date = null, active: Boolean = false, lastJoinDate: Date = null) ={
    val chatThreadParticipant = factory.newChatThreadParticipant.as
    chatThreadParticipant.setUUID(uuid)
    chatThreadParticipant.setThreadUUID(chatThreadUUID)
    chatThreadParticipant.setPersonUUID(personUUID)
    chatThreadParticipant.setChatThreadName(chatThreadName)
    chatThreadParticipant.setLastReadAt(lastReadAt)
    chatThreadParticipant.setActive(active)
    chatThreadParticipant.setLastJoinDate(lastJoinDate)
    chatThreadParticipant
  }

  def newInstitutionRegistrationPrefix(uuid: String, name: String, institutionUUID: String = null, showEmailOnProfile: Boolean, showCPFOnProfile: Boolean, showContactInformationOnProfile: Boolean): InstitutionRegistrationPrefix = {
    val institutionRegistrationPrefix = factory.newInstitutionRegistrationPrefix.as
    institutionRegistrationPrefix.setUUID(uuid)
    institutionRegistrationPrefix.setName(name)
    institutionRegistrationPrefix.setInstitutionUUID(institutionUUID)
    institutionRegistrationPrefix.setShowEmailOnProfile(showEmailOnProfile)
    institutionRegistrationPrefix.setShowCPFOnProfile(showCPFOnProfile)
    institutionRegistrationPrefix.setShowContactInformationOnProfile(showContactInformationOnProfile)
    institutionRegistrationPrefix
  }
  
  //def newEnrollmentModule()
}
