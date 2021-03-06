package kornell.server.jdbc.repository
import kornell.server.jdbc.SQL._
import kornell.core.entity.Person
import java.sql.ResultSet
import kornell.server.repository.Entities
import kornell.server.repository.Entities._
import java.util.Date
import kornell.core.entity.RoleCategory
import scala.collection.JavaConverters._
import kornell.core.entity.RoleType
import java.text.SimpleDateFormat
import java.text.DateFormat
import kornell.core.util.TimeUtil
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import java.util.concurrent.TimeUnit
import kornell.server.jdbc.PreparedStmt
import kornell.core.util.StringUtils._
import kornell.core.error.exception.EntityConflictException

class PersonRepo(val uuid: String) {

  def setPassword(institutionUUID: String, username: String, password: String): PersonRepo = {
    AuthRepo().setPlainPassword(institutionUUID, uuid, username, password)
    PersonRepo.this
  }
  
  def updatePassword(personUUID: String, plainPassword: String): PersonRepo = {
    AuthRepo().updatePassword(personUUID, plainPassword)
    PersonRepo.this
  }

  lazy val finder = sql" SELECT * FROM Person e WHERE uuid = ${uuid}"

  def get: Person = finder.get[Person]

  def first: Option[Person] =
    finder.first[Person]

  def update(person: Person) = {    
    sql"""
	    	update Person set fullName = ${person.getFullName},
	    	email = ${person.getEmail}, company = ${person.getCompany}, title = ${person.getTitle},
	    	sex = ${person.getSex}, birthDate = ${person.getBirthDate}, confirmation = ${person.getConfirmation},
	    	telephone = ${person.getTelephone}, country = ${person.getCountry}, state = ${person.getState}, city = ${person.getCity}, 
	    	addressLine1 = ${person.getAddressLine1}, addressLine2 = ${person.getAddressLine2}, postalCode = ${person.getPostalCode},
	    	cpf = ${person.getCPF}, registrationType = ${person.getRegistrationType.toString}, institutionRegistrationPrefixUUID = ${person.getInstitutionRegistrationPrefixUUID}
	    	where uuid = $uuid
	    """.executeUpdate
	  PeopleRepo.updateCaches(person)
    PersonRepo.this
  }

  def hasPassword(institutionUUID: String): Boolean = {
    var sql = s"select count(*) from Person p join Password pw on pw.person_uuid = p.uuid where p.uuid = '${uuid}' and p.institutionUUID = '${institutionUUID}'"
    val pstmt = new PreparedStmt(sql,List())    
    val result = pstmt.get[Boolean]
    result
  }

  //TODO: Better security against SQLInjection?
  //TODO: Better dynamic queries
  //TODO: Teste BOTH args case!!
  def isRegistered(institutionUUID: String, cpf: String,email:String): Boolean = {
    var sql = s"select count(*) from Person p left join Password pw on pw.person_uuid = p.uuid where p.uuid != '${uuid}' and p.institutionUUID = '${institutionUUID}' "
    if (isSome(cpf)) {
      sql = sql + s"and (p.cpf = '${digitsOf(cpf)}' or pw.username = '${digitsOf(cpf)}')";
    }
    if (isSome(email)) {
    	sql = sql + s"and (p.email = '${email}' or pw.username = '${email}')";
    }
    if (sql.contains("--")) throw new EntityConflictException("invalidValue")
    val pstmt = new PreparedStmt(sql,List())    
    val result = pstmt.get[Boolean]
    result
  }

  def hasPowerOver(targetPersonUUID: String) = {
    val actorRoles = AuthRepo().rolesOf(uuid)
    val actorRolesSet = (Set.empty ++ actorRoles).asJava
    val targetPerson = PersonRepo(targetPersonUUID).get

    val targetUsername = AuthRepo().getUsernameByPersonUUID(targetPersonUUID)

    //if there's no username yet, any admin can have power
    (!targetUsername.isDefined) ||
      {
        val targetRoles = AuthRepo().rolesOf(targetPersonUUID)
        val targetRolesSet = (Set.empty ++ targetRoles).asJava

        //people have power over themselves
        (uuid == targetPersonUUID) ||
          {
            //platformAdmin has power over everyone, except other platformAdmins
            !RoleCategory.isPlatformAdmin(targetRolesSet) &&
              RoleCategory.isPlatformAdmin(actorRolesSet)
          } || {
            //institutionAdmin doesn't have power over platformAdmins, other institutionAdmins or people from other institutions 
            !RoleCategory.isPlatformAdmin(targetRolesSet) &&
              !RoleCategory.hasRole(targetRolesSet, RoleType.institutionAdmin) && 
              RoleCategory.isInstitutionAdmin(actorRolesSet, targetPerson.getInstitutionUUID)
          } || {
            //courseClassAdmin doesn't have power over platformAdmins, institutionAdmins, other courseClassAdmins or non enrolled users
            val enrollmentTOs = EnrollmentsRepo.byPerson(targetPersonUUID)
            !RoleCategory.isPlatformAdmin(targetRolesSet) &&
              !RoleCategory.hasRole(targetRolesSet, RoleType.institutionAdmin) &&
              !RoleCategory.hasRole(targetRolesSet, RoleType.courseClassAdmin) && {
                enrollmentTOs exists {
                  to => RoleCategory.isCourseClassAdmin(actorRolesSet, to.getCourseClassUUID)
                }
              }
          }
      }
  }
  
  def getUsername = sql"""select username from Password where person_uuid=${uuid}""".first[String].getOrElse(null)
  
  def acceptTerms() =
    sql"""update Person
      	 set termsAcceptedOn = now()
      	 where uuid=${uuid}
      	   """.executeUpdate
  
  def actomsVisitedBy(enrollmentUUID: String): List[String] = sql"""
  	select actomKey from ActomEntered ae
  	join Enrollment e on ae.enrollmentUUID=e.uuid
  	where e.uuid = ${enrollmentUUID}
  	and e.person_uuid = ${uuid}
  	order by eventFiredAt
  	""".map[String]({ rs => rs.getString("actomKey") })

}

object PersonRepo {
    
  val uuidLoader = new CacheLoader[String, Option[Person]]() {
    override def load(uuid: String): Option[Person] = PersonRepo(uuid).first
  } 

  val personCache = CacheBuilder
    .newBuilder()
    .expireAfterAccess(5, TimeUnit.MINUTES)
    .maximumSize(1000)
    .build(uuidLoader)
    
  
    
  def apply(uuid: String) = new PersonRepo(uuid)
}
