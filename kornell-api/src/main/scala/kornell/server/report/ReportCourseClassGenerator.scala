package kornell.server.report

import java.io.InputStream
import java.sql.ResultSet
import java.util.Date
import java.util.HashMap

import scala.collection.JavaConverters.seqAsJavaListConverter
import scala.collection.mutable.ListBuffer

import kornell.core.to.report.CourseClassReportTO
import kornell.core.to.report.EnrollmentsBreakdownTO
import kornell.server.jdbc.SQL.SQLHelper
import kornell.server.repository.TOs

object ReportCourseClassGenerator {

  implicit def toCourseClassReportTO(rs: ResultSet): CourseClassReportTO =
    TOs.newCourseClassReportTO(
      rs.getString("fullName"),
      rs.getString("username"),
      rs.getString("state"),
      rs.getString("progressState"),
      rs.getInt("progress"))
      
  type BreakdownData = Tuple2[String,Integer] 
  implicit def breakdownConvertion(rs:ResultSet): BreakdownData = (rs.getString(1), rs.getInt(2))
  
  def generateCourseClassReport(courseClassUUID: String, fileType: String): Array[Byte] = {
    val courseClassReportTO = sql"""
				select 
					p.fullName,
					case    
						when p.cpf is null then p.email  
						else p.cpf   
					end as username,
					e.state,
					case    
						when progress is null OR progress = 0 then 'notStarted'  
						when progress > 0 and progress < 100 then 'inProgress'  
						when progress = 100 and certifiedAt is null then 'waitingEvaluation'  
						else 'completed'   
					end as progressState,
    			e.progress
				from 
					Enrollment e 
					join Person p on p.uuid = e.person_uuid
				where
					e.state = 'enrolled' and
    		  e.class_uuid = ${courseClassUUID}
				order by 
					progressState,
    			progress,
    			p.fullName
	    """.map[CourseClassReportTO](toCourseClassReportTO)
	    
	    val parameters = getTotalsAsParameters(courseClassUUID)
	    addInfoParameters(courseClassUUID, parameters)
	
	    val enrollmentBreakdowns: ListBuffer[EnrollmentsBreakdownTO] = ListBuffer()
	    enrollmentBreakdowns += TOs.newEnrollmentsBreakdownTO("aa", new Integer(1))
	    enrollmentBreakdowns.toList
		  
	    val cl = Thread.currentThread.getContextClassLoader
	    val jasperStream = cl.getResourceAsStream("reports/courseClassInfo.jasper")
	    val bytes = ReportGenerator.getReportBytesFromStream(courseClassReportTO, parameters, jasperStream)
	    
	    bytes
  }
      
  type ReportHeaderData = Tuple7[String,String, String, Date, String, String, String]
  implicit def headerDataConvertion(rs:ResultSet): ReportHeaderData = (rs.getString(1), rs.getString(2), rs.getString(3), rs.getDate(4), rs.getString(5), rs.getString(6), rs.getString(7))
  
  private def addInfoParameters(courseClassUUID: String, parameters: HashMap[String, Object]) = {
    val headerInfo = sql"""
					select 
						i.fullName as 'institutionName',
						c.title as 'courseTitle',
						cc.name as 'courseClassName',
						cc.createdAt,
    				cc.maxEnrollments,
						i.assetsURL,
						(select eventFiredAt from CourseClassStateChanged 
							where toState = 'inactive' and courseClassUUID = cc.uuid
							order by eventFiredAt desc) as disabledAt
					from
						CourseClass cc
						join CourseVersion cv on cc.courseVersion_uuid = cv.uuid
						join Course c on cv.course_uuid = c.uuid
						join Institution i on i.uuid = cc.institution_uuid
					where cc.uuid = ${courseClassUUID}
		    """.first[ReportHeaderData](headerDataConvertion)
    parameters.put("institutionName", headerInfo.get._1)
    parameters.put("courseTitle", headerInfo.get._2)
    parameters.put("courseClassName", headerInfo.get._3)
    parameters.put("createdAt", headerInfo.get._4)
    parameters.put("maxEnrollments", headerInfo.get._5)
    parameters.put("assetsURL", headerInfo.get._6)
    parameters.put("disabledAt", headerInfo.get._7)
    
    
    parameters
  }

  private def getTotalsAsParameters(courseClassUUID: String): HashMap[String,Object] = {
    val enrollmentStateBreakdown = sql"""
    		select 
					case    
						when progress is null OR progress = 0 then 'notStarted'  
						when progress > 0 and progress < 100 then 'inProgress'  
						when progress = 100 and certifiedAt is null then 'waitingEvaluation'  
						else 'completed'   
					end as progressState,
					count(*) as total
				from 
					Enrollment e 
				where
					e.state = 'enrolled' and
    		  e.class_uuid = ${courseClassUUID}
				group by 
					case    
						when progress is null OR progress = 0 then 'notStarted'  
						when progress > 0 and progress < 100 then 'inProgress' 
						when progress = 100 and certifiedAt is null then 'waitingEvaluation'  
						else 'completed'   
					end
		    """.map[BreakdownData](breakdownConvertion)
		    
    val parameters: HashMap[String, Object] = new HashMap()
    enrollmentStateBreakdown.foreach(rd => parameters.put(rd._1, rd._2)) 
    parameters
  }

 
}