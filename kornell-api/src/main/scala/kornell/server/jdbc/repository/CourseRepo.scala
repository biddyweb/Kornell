package kornell.server.jdbc.repository

import java.sql.ResultSet
import kornell.core.entity.Course
import kornell.core.entity.Person
import kornell.server.repository.Entities.newCourse
import kornell.server.jdbc.SQL._ 


class CourseRepo(uuid: String) {

  def get = sql"""select * from Course where uuid=$uuid""".get[Course]
  
  def update(course: Course): Course = {    
    sql"""
    | update Course c
    | set c.code = ${course.getCode},
    | c.title = ${course.getTitle}, 
    | c.description = ${course.getDescription},
    | c.infoJson = ${course.getInfoJson},
    | c.institutionUUID = ${course.getInstitutionUUID},
    | c.childCourse = ${course.isChildCourse}
    | where c.uuid = ${course.getUUID}""".executeUpdate
    course
  }

}

object CourseRepo {
  def apply(uuid: String) = new CourseRepo(uuid)
}