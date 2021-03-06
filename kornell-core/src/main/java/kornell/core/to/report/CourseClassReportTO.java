package kornell.core.to.report;

import java.math.BigDecimal;

public class CourseClassReportTO {
	private String fullName;
	private String username;
	private String email;
	private String state;
	private String progressState;
	private Integer progress;
	private BigDecimal assessmentScore;
	private String certifiedAt;
	private String enrolledAt;
	private String courseName;
	private String courseVersionName;
	private String courseClassName;
	private EnrollmentsBreakdownTO enrollmentsBreakdownTO;

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getProgressState() {
		return progressState;
	}

	public void setProgressState(String progressState) {
		this.progressState = progressState;
	}

	public Integer getProgress() {
		return progress;
	}

	public void setProgress(Integer progress) {
		this.progress = progress;
	}

	public EnrollmentsBreakdownTO getEnrollmentsBreakdownTO() {
		return enrollmentsBreakdownTO;
	}

	public void setEnrollmentsBreakdownTO(
			EnrollmentsBreakdownTO enrollmentsBreakdownTO) {
		this.enrollmentsBreakdownTO = enrollmentsBreakdownTO;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public BigDecimal getAssessmentScore() {
		return assessmentScore;
	}

	public void setAssessmentScore(BigDecimal assessmentScore) {
		this.assessmentScore = assessmentScore;
	}

	public String getCertifiedAt() {
		return certifiedAt;
	}

	public void setCertifiedAt(String certifiedAt) {
		this.certifiedAt = certifiedAt;
	}

	public String getEnrolledAt() {
		return enrolledAt;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCourseVersionName() {
		return courseVersionName;
	}

	public void setCourseVersionName(String courseVersionName) {
		this.courseVersionName = courseVersionName;
	}

	public String getCourseClassName() {
		return courseClassName;
	}

	public void setCourseClassName(String courseClassName) {
		this.courseClassName = courseClassName;
	}

	public void setEnrolledAt(String enrolledAt) {
		this.enrolledAt = enrolledAt;
	}

}
