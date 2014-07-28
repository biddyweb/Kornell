package kornell.gui.client;

import com.google.gwt.i18n.client.Constants;

public interface KornellConstants extends Constants {
	
	@DefaultStringValue("skins/first/icons/")
	String imagesPath();
	
	@DefaultStringValue("Loading...")
	String loading();
	
	/**
	 * 
	 * GenericWelcomeView
	 * 
	 */
	@DefaultStringValue("All courses")
	String allCourses();

	@DefaultStringValue("Courses finished:")
	String coursesFinished();

	@DefaultStringValue("Finished")
	String finished();

	@DefaultStringValue("Courses to acquire:")
	String coursesToAcquire();

	@DefaultStringValue("To acquire")
	String toAcquire();

	@DefaultStringValue("Courses to start:")
	String coursesToStart();

	@DefaultStringValue("To start")
	String toStart();

	@DefaultStringValue("Courses in progress:")
	String coursesInProgress();

	@DefaultStringValue("In progress")
	String inProgress();



	/**
	 * 
	 * GenericMenuLeftItemView
	 * 
	 */
	@DefaultStringValue(":")
	String colon();

	@DefaultStringValue("Network Activities")
	String networkActivities();

	@DefaultStringValue("Messages")
	String messages();

	@DefaultStringValue("Forums")
	String forums();

	@DefaultStringValue("Complete")
	String complete();

	@DefaultStringValue("Courses")
	String courses();

	@DefaultStringValue("Notifications")
	String notifications();

	@DefaultStringValue("My Participation")
	String myParticipation();

	@DefaultStringValue("Profile")
	String profile();



	/**
	 * 
	 * GenericCourseSummaryView
	 * 
	 */
	@DefaultStringValue("Course finished.")
	String courseFinished();
	@DefaultStringValue("Certificate")
	String certificate();



	/**
	 * 
	 * GenericCourseDetailsView
	 * 
	 */
	@DefaultStringValue("Course details: ")
	String detailsHeader();
	
	@DefaultStringValue("Class: ")
	String detailsSubHeader();
	
	@DefaultStringValue("About the course")
	String btnAbout();
	
	@DefaultStringValue("Topics")
	String btnTopics();
	
	@DefaultStringValue("Certification")
	String btnCertification();
	
	@DefaultStringValue("Library")
	String btnLibrary();
	
	@DefaultStringValue("General view")
	String btnAboutInfo();
	
	@DefaultStringValue("Main topics covered on this course")
	String btnTopicsInfo();
	
	@DefaultStringValue("Evaluations and tests")
	String btnCertificationInfo();
	
	@DefaultStringValue("Close Details")
	String closeDetails();

	@DefaultStringValue("Topic")
	String topic();

	
	/**
	 * 
	 * GenericBarView
	 */
	
	@DefaultStringValue("course")
	String course();
	
	@DefaultStringValue("details")
	String details();
	
	@DefaultStringValue("library")
	String library();
	
	@DefaultStringValue("forum")
	String forum();
	
	@DefaultStringValue("chat")
	String chat();
	
	@DefaultStringValue("specialists")
	String specialists();
	
	@DefaultStringValue("notes")
	String notes();
	
	@DefaultStringValue("back")
	String back();
	
	@DefaultStringValue("next")
	String next();
	
	@DefaultStringValue("previous")
	String previous();
	
	@DefaultStringValue("institution")
	String institution();
	
	@DefaultStringValue("classes")
	String classes();

	

	/**
	 * 
	 * Util
	 */
	@DefaultStringValue("January")
	String january();
	@DefaultStringValue("February")
	String february();
	@DefaultStringValue("March")
	String march();
	@DefaultStringValue("April")
	String april();
	@DefaultStringValue("May")
	String may();
	@DefaultStringValue("June")
	String june();
	@DefaultStringValue("July")
	String july();
	@DefaultStringValue("August")
	String august();
	@DefaultStringValue("September")
	String september();
	@DefaultStringValue("October")
	String october();
	@DefaultStringValue("November")
	String november();
	@DefaultStringValue("December")
	String december();
	


	

	/**
	 * 
	 * EnrollmentState
	 */
	@DefaultStringValue("notEnrolled")
	String notEnrolled();
	@DefaultStringValue("enrolled")
	String enrolled();
	@DefaultStringValue("requested")
	String requested();
	@DefaultStringValue("denied")
	String denied();
	@DefaultStringValue("cancelled")
	String cancelled();
}