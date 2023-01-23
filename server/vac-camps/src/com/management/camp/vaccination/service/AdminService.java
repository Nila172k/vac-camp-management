//$Id$
package com.management.camp.vaccination.service;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.management.camp.vaccination.dao.AdminDao;
import com.management.camp.vaccination.dao.AuditDao;
import com.management.camp.vaccination.dao.CampDao;
import com.management.camp.vaccination.dao.CityDao;
import com.management.camp.vaccination.dao.OrganizationDao;
import com.management.camp.vaccination.dao.RegistrationDao;
import com.management.camp.vaccination.dao.UserDao;
import com.management.camp.vaccination.dto.Admin;
import com.management.camp.vaccination.dto.Audit;
import com.management.camp.vaccination.dto.Camp;
import com.management.camp.vaccination.dto.City;
import com.management.camp.vaccination.dto.Organization;
import com.management.camp.vaccination.dto.Registration;
import com.management.camp.vaccination.dto.Summary;
import com.management.camp.vaccination.dto.User;
import com.management.camp.vaccination.exception.DataNotFoundException;
import com.management.camp.vaccination.exception.DuplicateDataFoundException;
import com.management.camp.vaccination.exception.InadequateDataException;
import com.management.camp.vaccination.exception.InvalidDataException;
import com.management.camp.vaccination.query.DynamicQuery;
import com.management.camp.vaccination.utility.JsonUtility;
import com.management.camp.vaccination.utility.Utils;

public class AdminService {
	
	private DynamicQuery dynamicQuery = new DynamicQuery();
	private CampDao campDao = new CampDao(); 
	private CityDao cityDao = new CityDao();
	private AdminDao adminDao = new  AdminDao();
	private UserDao userDao = new UserDao();
	private RegistrationDao registrationdao = new RegistrationDao();
	private OrganizationDao organizationDao = new OrganizationDao();		
	private AuditDao auditDao = new AuditDao();
	private Logger logger = Logger.getLogger(AdminService.class.getName());
	
	/**
	 * Validates the Admin credentials
	 * @param jObject
	 * @throws Exception
	 */
	public JSONObject validateOrganization(JSONObject jObject, Properties properties) throws Exception {
		logger.info("New request for org login " + jObject);
		if (jObject.get("userName") == null || jObject.get("password") == null)
			throw new InadequateDataException("UserName/Password can not be empty.");
		String userName = (String) jObject.get("userName");
		String password = (String) jObject.get("password");
		//password = Utils.encryptPassword(password);
		String orgTable = properties.getProperty("com.organization.table.name");
		String selectQuery = dynamicQuery.getSelectQuery(orgTable, null);
		
		Organization organization = organizationDao.getOrganizationByUserName(userName, selectQuery);
		
		if(organization==null) {
			throw new DataNotFoundException("Invalid userName");
		}
		if(!organization.getPasword().equals(password)) {
			throw new DataNotFoundException("Invalid password");
		}
		JSONObject responseDataObject = new JSONObject();
		responseDataObject.put("orgId", organization.getId());
		logger.info("Org Logged In Successfully" + userName);
		return responseDataObject;
	}
	
	/**
	 * Creates a camping sites for a city
	 * @throws InadequateDataException 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws DuplicateDataFoundException 
	 */
	public void createCampingSite(JSONObject jObject, String orgId, Properties properties) throws Exception {
		Camp newCamp = JsonUtility.getCampObjectFromJson(jObject);
		long userId = Long.parseLong(orgId);
		newCamp.setOrganizedBy(userId);
		String campTableName = properties.getProperty("com.camp.table.name");
		String campSelectQuery = dynamicQuery.getSelectQuery(campTableName, null);
		String auditTable = properties.getProperty("com.audit.table.name");
		String auditSelectQuery = dynamicQuery.getSelectQuery(auditTable, null);
		Map<Long, Camp> camps = campDao.getCampsCache(campSelectQuery);
		
		Audit audit = new Audit();
		audit.setUserId(userId);
		audit.setOperation("Create camping site");
		audit.setUserType("Admin");
		audit.setState("Failed");
		logger.info("New request for camping site registration. Data " + newCamp.toString() );
		
		if( newCamp.getCityId() == 0|| newCamp.getAddress() == null || newCamp.getStock() ==0 ) {
			auditDao.insertAudit(audit, auditSelectQuery);
			throw new InadequateDataException("CityId/Address/Stock can not be empty");
		}
		
		boolean isPassed = checkUniquenessOfCamp(camps, newCamp);
		
		if(!isPassed) {
			auditDao.insertAudit(audit, auditSelectQuery);
			throw new DuplicateDataFoundException("Organization can register only one camp under a city");
		}		
		
		String campFieldsAsString = properties.getProperty("com.camp.table.fields.name");
		String campInsertQuery = dynamicQuery.getInsertQuery(campTableName, campFieldsAsString);
		int result = campDao.createCamp(newCamp, campInsertQuery);		
		if(result > 0) {
			audit.setState("Success");
			auditDao.insertAudit(audit, auditSelectQuery);
			logger.info("New Camping Site Created Successfully. Data " + newCamp.toString() );
			campDao.updateCampCacheByAddingNewCamp(newCamp, campSelectQuery);
		}	
	}
	
	/**
	 * Checks the uniqueness of the new camp
	 * @param existingCamps
	 * @param newCamp
	 */
	public boolean checkUniquenessOfCamp(Map<Long, Camp> existingCamps, Camp newCamp ) {
		Iterator<Map.Entry<Long, Camp>> itr =  existingCamps.entrySet().iterator();
		while(itr.hasNext()) {
			Map.Entry<Long, Camp>  campEntry = itr.next();
			if(campEntry.getValue().getOrganizedBy() == newCamp.getOrganizedBy() && 
					campEntry.getValue().getCityId() == newCamp.getCityId() ) 
				return false;
				
		}
		
		return true;
		
	}
	
	/**
	 * Updates the stock in a city 
	 * @param jObject
	 * @throws InadequateDataException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws InvalidDataException
	 */
	public void updateStocksInCity(JSONObject jObject, String organization, String camp, Properties properties) throws Exception {
		logger.info("New request for updating the stock in camping site. " + jObject );
		
		if(jObject.get("stock") == null )
			throw new InadequateDataException("Stock can not be empty");
		long orgId = Long.parseLong(organization);
		long campId =  Long.parseLong(camp);
		long newStockQuantity = Long.parseLong((String)jObject.get("stock"));
		
		Audit audit = new Audit();
		audit.setUserId(orgId);
		audit.setOperation("Update stock");	
		audit.setUserType("Admin");
		audit.setState("Failed");
		
		String campTable = properties.getProperty("com.camp.table.name");
		String campSelectQuery = dynamicQuery.getSelectQuery(campTable, null) ;
		String auditTable = properties.getProperty("com.audit.table.name");
		String auditSelectQuery = dynamicQuery.getSelectQuery(auditTable, null);
		boolean isEligible = checkTheEligibilityOfOrg(orgId, campId, campSelectQuery);
		if(!isEligible) {
			auditDao.insertAudit(audit, auditSelectQuery);
			throw new InvalidDataException("Not able to update stock. Organization does not have access the camp ");
		}
		
		String campUpdateFilter = "SET stock = stock+? WHERE id = ? and state = 'active' and organized_by = ? ";
		String campUpdateQuery  = dynamicQuery.getUpdateQuery(campTable, campUpdateFilter) ; 
		int result = campDao.updateStockInCity(newStockQuantity, campId, orgId, campSelectQuery, campUpdateQuery);
		if(result>0) {
			logger.info("Updated the stock quantity. CampId : " + campId);
			audit.setOpeartionDesc("Updated the stock in " + campId);
			audit.setState("success");
			auditDao.insertAudit(audit, auditSelectQuery);
		}
		
		
	}
	
	/**
	 * helps to validate the org access to the corresponding city
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public boolean checkTheEligibilityOfOrg(Long orgId, long campId, String selectCampQuery) throws Exception {
		Map<Long, Camp> camps = campDao.getCampsCache(selectCampQuery);
		Camp camp = camps.get(campId);
		if(camp != null && camp.getOrganizedBy()==orgId) 
			return true;
			
		return false;
	}
	
	/**
	 * Helps to get the vaccination summary
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public String getVaccinationSummary(JSONObject jObject, String org, Properties properties) throws Exception {
		Long orgId = Long.parseLong((String) org);
		logger.info("New request for getting vacciantion summary. orgId : " + orgId );
		String registrationTable = properties.getProperty("com.registration.table.name");
		String campTable = properties.getProperty("com.camp.table.name");
		String userTable = properties.getProperty("com.user.table.name");
		String cityTable = properties.getProperty("com.city.table.name");
		String regSelectQuery = dynamicQuery.getSelectQuery(registrationTable, null);
		String campSelectQuery = dynamicQuery.getSelectQuery(campTable, null);
		String userSelectQuery = dynamicQuery.getSelectQuery(userTable, null);
		String citySelectQuery = dynamicQuery.getSelectQuery(cityTable, null);
		Map<Long, City> cities = cityDao.getCitiesCache(citySelectQuery); 	
		List<Registration>  registrations = registrationdao.getRegistrationCache(regSelectQuery);
		Map<Long, Camp> camps = campDao.getCampsCache(campSelectQuery);
		Map<Long, User> users = userDao.getUsersCache(userSelectQuery); 
		JSONArray jArray = new JSONArray();
		JSONObject responseObject = new JSONObject();
		long totalPartiallyVaccinated= 0;
		long totalFullyVaccinated = 0;
		long totalActiveCamps = 0;
		long totalVaccinatedMen = 0;
		long totalVaccinatedWomen = 0;
		long totalVaccinatedOthers = 0;
		Iterator<Map.Entry<Long, Camp>> campsItr = camps.entrySet().iterator();
		while( campsItr.hasNext() ) { 
			JSONObject summaryJObject = new JSONObject();
			Map.Entry<Long, Camp> campEntry = campsItr.next();
			if(campEntry.getValue().getOrganizedBy() ==orgId ) {
				totalActiveCamps += 1; 
				Long campId = campEntry.getKey();
				summaryJObject.put("CampId", campId);
				Long cityId = campEntry.getValue().getCityId();
				City city = cities.get(cityId);
				summaryJObject.put("City", city.getCityName());
				long partivallyVaccinated = 0;
				long fullyVaccinated = 0;
				long partiallyVaccinatedMen = 0;
				long partiallyVaccinatedWomen = 0;
				long partiallyVaccinatedOthers = 0;
				long fullyVaccinatedMen = 0;
				long fullyVaccinatedWomen = 0;
				long fullyVaccinatedOthers = 0;
				for(Registration registration : registrations) {
					if(registration.getChoosenCampId() ==  campId && registration.getStatus().equals("Processed")) {
						User user = users.get(registration.getUserId() );
						if(registration.getDosageCount() == 1) {
							partivallyVaccinated += 1;
							if(user != null) {
								if(user.getGender().equals("Male")) {
									partiallyVaccinatedMen += 1; 
								} else if(user.getGender().equals("Female")) {
									partiallyVaccinatedWomen += 1;
								} else if(user.getGender().equals("Others")) {
									partiallyVaccinatedOthers += 1;
								}
							}
								
						}		
						else if(registration.getDosageCount() == 2) {
							fullyVaccinated += 1;
							if(user != null) {
								if(user.getGender().equals("Male")) {
									fullyVaccinatedMen += 1; 
								} else if(user.getGender().equals("Female")) {
									fullyVaccinatedWomen += 1;
								} else if(user.getGender().equals("Others")) {
									fullyVaccinatedOthers += 1;
								}
							}
						}
							
					}
				}
				
				partivallyVaccinated = Math.abs(fullyVaccinated-partivallyVaccinated) ;
				totalVaccinatedMen += Math.abs(fullyVaccinatedMen - partiallyVaccinatedMen);
				totalVaccinatedWomen += Math.abs(fullyVaccinatedWomen - partiallyVaccinatedWomen);
				totalVaccinatedOthers += Math.abs(fullyVaccinatedOthers - partiallyVaccinatedOthers);
				totalPartiallyVaccinated +=  partivallyVaccinated;
				totalFullyVaccinated += fullyVaccinated;
				summaryJObject.put("partiallyVaccinated", partivallyVaccinated);
				summaryJObject.put("fullyVaccinated", fullyVaccinated);
				jArray.add(summaryJObject);
			}
		}
		
		//Map<Long, Map<Integer, Summary>> summaryAtCampLevel = new HashMap<Long, Map<Integer, Summary>>();
//		while(campsItr.hasNext()) {
//			Map<Integer, Summary> summaryAtCampAndDosageLevel = new HashMap<Integer, Summary>();
//			Map.Entry<Long, Camp> campEnty = campsItr.next();
//			if(campEnty.getValue().getOrganizedBy() == orgId) {
//				for(Registration registration : registrations) {
//					if(registration.getChoosenCampId() == campEnty.getKey() ) {
//						Summary summary = summaryAtCampAndDosageLevel.get(registration.getDosageCount()); 
//						if(summary != null) {
//							summary.setVaccinatedCount(summary.getVaccinatedCount()+1);
//							summary.setCampId(campEnty.getKey());
//							summary.setCityId(campEnty.getValue().getCityId() );
//							summary.setCityName(cities.get(campEnty.getValue().getCityId()).getCityName());
//							summaryAtCampAndDosageLevel.put(registration.getDosageCount(), summary);
//							summaryAtCampLevel.put(campEnty.getKey(), summaryAtCampAndDosageLevel);
//							
//						}
//						else {
//							Summary tempSummary = new Summary();
//							tempSummary.setVaccinatedCount(1);
//							tempSummary.setCampId(campEnty.getKey());
//							tempSummary.setCityId(campEnty.getValue().getCityId() );
//							tempSummary.setCityName(cities.get(campEnty.getValue().getCityId()).getCityName());
//							summaryAtCampAndDosageLevel.put(registration.getDosageCount(), tempSummary);
//							summaryAtCampLevel.put(campEnty.getKey(), summaryAtCampAndDosageLevel);
//						}
//						
//					}
//				}
//			}
//		}
		
		//JSONObject newJObject = new JSONObject(summaryAtCampLevel);
//		String summaryString = getJsonObjectFromSummary(summaryAtCampLevel);
//		return summaryString;
		responseObject.put("totalFullyVaccinated", totalFullyVaccinated);
		responseObject.put("totalPartiallyVaccinated", totalPartiallyVaccinated);
		responseObject.put("totalVacciantedMen", totalVaccinatedMen);
		responseObject.put("totalVacciantedWomen", totalVaccinatedWomen);
		responseObject.put("totalVacciantedOthers", totalVaccinatedOthers);
		responseObject.put("totalActiveCamps", totalActiveCamps);	
		responseObject.put("summary", jArray);
		return responseObject.toJSONString();
			
	}
	
	public void sortJsonArray(String jsonStr) {
	
		
		
	}
	
	/**
	 * Returns the JSON Object from the Summary Map
	 * @param summary
	 */
	public String getJsonObjectFromSummary(Map<Long, Map<Integer, Summary>> summary) {
		Iterator<Map.Entry<Long, Map<Integer, Summary>>> summaryItr = summary.entrySet().iterator();
		JSONObject root = new JSONObject();
		while(summaryItr.hasNext()) {
			JSONArray jArray = new JSONArray();
			JSONObject parentObject = new JSONObject();
			Map.Entry< Long, Map<Integer, Summary>>  summaryItrEntry = summaryItr.next();
			Iterator<Map.Entry<Integer, Summary>> summaryAtDosageLevelItr = summaryItrEntry.getValue().entrySet().iterator();
			while(summaryAtDosageLevelItr.hasNext()) {
				Map.Entry<Integer, Summary> summaryAtDosageLevelEntry = summaryAtDosageLevelItr.next();
								JSONObject jObject = new JSONObject();
				JSONObject rootParentOnject = new JSONObject();
				jObject.put("campId", summaryAtDosageLevelEntry.getValue().getCampId());
				jObject.put("VaccinatedPeopleCount", summaryAtDosageLevelEntry.getValue().getVaccinatedCount());
				jObject.put("cityId", summaryAtDosageLevelEntry.getValue().getCityId());
				jObject.put("cityName", summaryAtDosageLevelEntry.getValue().getCityName());
				parentObject.put( summaryAtDosageLevelEntry.getKey() , jObject);
				
				//rootParentOnject.put(summaryItrEntry.getKey(), jArray);
			}
			jArray.add(parentObject);
			root.put(summaryItrEntry.getKey(), jArray);
			
		}
		return root.toJSONString();
		
		
	}
	
	/**
	 * 
	 * @param org
	 * @return
	 * @throws Exception
	 */
	public String fetchCampsByOrgId(String org, Properties properties) throws Exception {
		long orgId = Long.parseLong(org);
		logger.info("New Request for get camp. OrgId " + orgId );
		String cityTable = properties.getProperty("com.city.table.name");
		String citySelectQuery = dynamicQuery.getSelectQuery(cityTable, null);
		List<Camp> camps = campDao.getCampByOrgId(orgId, citySelectQuery);
		Map<Long, City> cities = cityDao.getCitiesCache(citySelectQuery);
		JSONObject rootJObject = new JSONObject();
		JSONArray jArray = new JSONArray();
		for(Camp camp: camps) {
			JSONObject jObject = new JSONObject();
			jObject.put("campId", camp.getId());
			jObject.put("orgId", camp.getOrganizedBy());
			jObject.put("address", camp.getAddress());
			City city = cities.get(camp.getCityId());
			jObject.put("City", city.getCityName());
			jObject.put("Stock", camp.getStock());
			jObject.put("createdOn", camp.getCreatedDate().toString());
			jArray.add(jObject);
		}
		rootJObject.put("camps", jArray);
		return rootJObject.toJSONString();
		
	}
	
	/**
	 * Creates an organisation
	 * 
	 * @param jObject - JSON Object
	 */
	public void createOrganization(JSONObject jObject,long userId, Properties properties) throws Exception{
		Organization org = JsonUtility.getOrganizationObjectFromJson(jObject);
		//Admin admin = JsonUtility.getAdminObjectFromJson(jObject);
		logger.info("New Request for organization registration. Data : " + org.toString());
		Audit audit = new Audit();
		audit.setUserId(userId);
		audit.setUserType("Super Admin");
		audit.setOperation("Create Organization");
		audit.setState("Failed");
		
		String orgTable = properties.getProperty("com.organization.table.name");
		String orgSelectQuery = dynamicQuery.getSelectQuery(orgTable, null);
		String auditTable = properties.getProperty("com.audit.table.name");
		String auditSelectQuery = dynamicQuery.getSelectQuery(auditTable, null);
		validateOrganizationObject(org, audit, auditSelectQuery, orgSelectQuery);
		//validateAdminObject(admin, audit);
		
		String orgFieldsAsString = properties.getProperty("com.organization.table.fields.name");
		String orgInsertQuery = dynamicQuery.getInsertQuery(orgTable, orgFieldsAsString);
		int result = organizationDao.insertOrganization(org,orgInsertQuery);
		if(result > 0 ) {
			audit.setState("Success");
			auditDao.insertAudit(audit, auditSelectQuery);
			organizationDao.updateOrganizationCache(org, orgSelectQuery);
			logger.info("Created new organization. OrganizationId : " +  org.getId());
			//admin.setOrgId(org.getId());
			//createAdmin(admin, audit);
		}
		
		
	}
	
	public boolean validateOrganizationObject(Organization organization, Audit audit, String auditSelectQuery, String orgSelectQuery) throws Exception{
		boolean isPassed = true;
		if(organization.getName() == null || organization.getName().isEmpty() || 
				organization.getUserName() == null || organization.getUserName().isEmpty() ||
				organization.getPasword() == null || organization.getPasword().isEmpty()) {
			isPassed = false;
			auditDao.insertAudit(audit,auditSelectQuery);
			throw new InadequateDataException("OrganizationName/loginCredentials can not be empty");
		}
		
		Map<Integer, Organization> organizationCache = organizationDao.getOrganizations(orgSelectQuery);
		isPassed = checkUniquenessOfOrgName(organizationCache, organization.getName());
		if(!isPassed) {
			auditDao.insertAudit(audit,auditSelectQuery);
			throw new DuplicateDataFoundException("Organization name should be unique");
		}
		
		isPassed = checkUniquenessOfOrgUserName(organizationCache, organization.getUserName());
		if(!isPassed) {
			auditDao.insertAudit(audit, auditSelectQuery);
			throw new DuplicateDataFoundException("org user name should be unique");
		}
			
		return isPassed;
		
	}
	
//	public boolean validateAdminObject( Admin admin, Audit audit) throws InadequateDataException, ClassNotFoundException, SQLException, DuplicateDataFoundException {
//		boolean isPassed = true;
//		audit.setOperation("Create Admin");
//		if(admin.getUserName() == null || admin.getPassword() == null ) {
//			isPassed = false;
//			auditDao.insertAudit(audit);
//			throw new InadequateDataException("UserName/Password/OrganizationId can not be empty");
//		}
//		
//		Map<String, Admin> admins = adminDao.getAdminsCache();
//		isPassed = checkUniquenessOfOrgId(admins, admin.getId());
//		
//		if(!isPassed) {
//			auditDao.insertAudit(audit);
//			throw new DuplicateDataFoundException("Only one admin can register under an organization");
//		}
//		
//		isPassed = checkUniquenessOfAdminUserName(admins, admin.getUserName());
//		if(!isPassed) {
//			auditDao.insertAudit(audit);
//			throw new DuplicateDataFoundException("Admin name should be unique");
//			
//		}
//		
//		return isPassed;
//		
//	}
//	
	

//	/**
//	 * Checks the uniqueness of AdminUserName
//	 * @param admins        - All admins
//	 * @param adminUserName - New admin user name 
//	 * @return
//	 */
//	public boolean checkUniquenessOfAdminUserName(Map<String, Admin> admins, String adminUserName) {
//		Iterator<Map.Entry<String, Admin>> itr =  admins.entrySet().iterator();
//		while(itr.hasNext()) {
//			Map.Entry<String, Admin> adminEntry = itr.next();
//			if(adminEntry.getValue().getUserName().equalsIgnoreCase(adminUserName))
//				return false;
//		}
//		
//		return true;
//		
//	}
	
	/**
	 * Checks the uniqueness of orgName
	 * @param admins
	 * @param orgId
	 * @return
	 */
	public boolean checkUniquenessOfOrgName(Map<Integer, Organization> organization, String newOrgName) {
		
		Iterator<Map.Entry<Integer, Organization>> itr =  organization.entrySet().iterator();
		while(itr.hasNext()) {
			Map.Entry<Integer, Organization> org = itr.next();
			if(org.getValue().getName().equalsIgnoreCase(newOrgName))
				return false;
		}
		
		return true;
		
	}
	
	/**
	 * Checks the uniqueness of orgUserName
	 * @param admins
	 * @param orgId
	 * @return
	 */
	public boolean checkUniquenessOfOrgUserName(Map<Integer, Organization> organization, String newOrgUserNameName) {
		
		Iterator<Map.Entry<Integer, Organization>> itr =  organization.entrySet().iterator();
		while(itr.hasNext()) {
			Map.Entry<Integer, Organization> org = itr.next();
			if(org.getValue().getUserName().equalsIgnoreCase(newOrgUserNameName))
				return false;
		}
		
		return true;
		
	}
	
	
	
	/**
	 * Creates New Admin
	 * 
	 * @param jObject
	 * @throws InadequateDataException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws DuplicateDataFoundException 
	 * @throws NoSuchAlgorithmException 
	 */
//	public void createAdmin(Admin admin, Audit audit) throws InadequateDataException, ClassNotFoundException, SQLException, DuplicateDataFoundException, NoSuchAlgorithmException {
//		logger.info("New Request for Admin Registration. Data : " + admin.toString() );
//		audit.setOperation("Create Admin");
//		audit.setState("Failed");
//	
//		admin.setPassword( Utils.encryptPassword(admin.getPassword()));
//		
//		int result = adminDao.insertAdmin(admin);
//		if(result > 0) {
//			audit.setState("Success");
//			auditDao.insertAudit(audit);
//			adminDao.updateAdminCache(admin);
//			logger.info("New Admin Created successfully. AdminId : " + admin.getId());
//		}
//		
//	}
	
	/**
	 * Checks the uniqueness of orgId
	 * @param admins
	 * @param orgId
	 * @return
	 */
	public  boolean checkUniquenessOfOrgId(Map<String, Admin> admins, int orgId) {
		Iterator<Map.Entry<String, Admin>> itr = admins.entrySet().iterator();
		while(itr.hasNext()) {
			Map.Entry<String, Admin> admin =  itr.next();
			if( admin.getValue().getOrgId() == orgId) {
				return false;
			}	
			
		}
		
		return true;
	}
 	

}
