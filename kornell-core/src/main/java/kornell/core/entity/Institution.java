package kornell.core.entity;

import java.util.Date;


public interface Institution extends Named {
	public static String TYPE = EntityFactory.PREFIX + "institution+json";

	String getFullName();
	void setFullName(String fullName);

	String getTerms();
	void setTerms(String terms);
	
	String getAssetsURL();
	void setAssetsURL(String assetsURL);
	
	String getBaseURL();
	void setBaseURL(String baseURL);
	
	boolean isDemandsPersonContactDetails();
	void setDemandsPersonContactDetails(boolean demandsPersonContactDetails);
	
	boolean isValidatePersonContactDetails();
	void setValidatePersonContactDetails(boolean validatePersonContactDetails);

	boolean isAllowRegistration();
	void setAllowRegistration(boolean allowRegistration);

	boolean isAllowRegistrationByUsername();
	void setAllowRegistrationByUsername(boolean allowRegistrationByUsername);
	
	Date getActivatedAt();
	void setActivatedAt(Date activatedAt);

	String getSkin();
	void setSkin(String skin);
	
	BillingType getBillingType();
	void setBillingType(BillingType billingType);
	
	InstitutionType getInstitutionType();
	void setInstitutionType(InstitutionType institutionType);
	
	String getDashboardVersionUUID();
	void setDashboardVersionUUID(String dashboardVersionUUID);
}
