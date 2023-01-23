//$Id$
package com.management.camp.vaccination.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Writer;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.omg.CORBA.Request;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.log.SysoCounter;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.management.camp.vaccination.dao.AuditDao;
import com.management.camp.vaccination.dao.CampDao;
import com.management.camp.vaccination.dao.RegistrationDao;
import com.management.camp.vaccination.dao.UserDao;
import com.management.camp.vaccination.dto.Audit;
import com.management.camp.vaccination.dto.Camp;
import com.management.camp.vaccination.dto.Registration;
import com.management.camp.vaccination.dto.User;
import com.management.camp.vaccination.exception.DataNotFoundException;
import com.management.camp.vaccination.exception.DuplicateDataFoundException;
import com.management.camp.vaccination.exception.InadequateDataException;
import com.management.camp.vaccination.exception.RegistrationCanNotBeDoneException;
import com.management.camp.vaccination.exception.SlotsNotAvailableException;
import com.management.camp.vaccination.query.DynamicQuery;
import com.management.camp.vaccination.utility.JsonUtility;
import com.management.camp.vaccination.utility.LoggerFormatter;
import com.management.camp.vaccination.utility.Utils;

public class UserService extends LoggerFormatter{
	
	Properties properties = new Properties();
	private DynamicQuery dynamiQuery = new DynamicQuery();
	private UserDao userDao = new UserDao();
	private CampDao campDao = new CampDao();
	private RegistrationDao registrationDao = new RegistrationDao();
	private AuditDao auditDao = new AuditDao();
	private Logger logger = Logger.getLogger(UserService.class.getName());
	
	

	/**
	 * Handles new user request
	 * 
	 * @param jObject
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws NoSuchAlgorithmException
	 * @throws InadequateDataException
	 * @throws DuplicateDataFoundException
	 */
	public void createUser(JSONObject jObject, Properties properties) throws Exception {
		User user = JsonUtility.getUserObjectFromJson(jObject);//Gets the User Object from JSON Object 
		logger.info("New user registration request. Data "  + user.toString() );
		
		if (user.getPassword() == null || user.getPassword().isEmpty() || 
				user.getFirsName() == null || user.getFirsName().isEmpty() || 
				user.getEmail() == null || user.getEmail().isEmpty() || 
				user.getAadharNumber() == 0 ) {
			throw new InadequateDataException("FirstName/Email/AadharNumber/Password can not be empty");
		}
			
		// Encrypts the User Password
		user.setPassword(Utils.encryptPassword(user.getPassword()));
		String userTable = properties.getProperty("com.user.table.name");
		String query = dynamiQuery.getSelectQuery(userTable, null);
		boolean isAvailable = userDao.findDuplicateUser(user, query);
		
		if(isAvailable) 
			throw new DuplicateDataFoundException("Duplicate email/aadhar number found");
		
		String userFieldsAsString = properties.getProperty("com.user.table.fields.name");
		String insertQuery = dynamiQuery.getInsertQuery(userTable, userFieldsAsString);
		int result = userDao.insertUser(user, insertQuery);
		
		if (result > 0) {
			String auditTable = properties.getProperty("com.audit.table.name");
			String auditFieldsAsString = properties.getProperty("com.audit.table.fields.name");
			String auditSelectQuery = dynamiQuery.getInsertQuery(auditTable, auditFieldsAsString);
			Audit audit = new Audit();
			audit.setUserId(user.getId() );
			audit.setUserType("Public");
			audit.setOperation("New User");
			audit.setOpeartionDesc("New User Account created");
			audit.setState("Success");
			auditDao.insertAudit(audit,auditSelectQuery);
			userDao.updateAllUsersCache(user, query);	
			logger.info("New user registration completed successfully. Data : " + user.toString() );
		}

	}

	/**
	 * Validates the user login
	 * 
	 * @param object
	 * @throws InadequateDataException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws NoSuchAlgorithmException
	 * @throws DataNotFoundException
	 */
	public JSONObject validateUser(JSONObject jObject, Properties properties) throws Exception {
		if (jObject.get("userName") == null || jObject.get("password") == null)
			throw new InadequateDataException("UserName/Password can not be empty.");

		String userName = (String) jObject.get("userName");
		String password = (String) jObject.get("password");
		password = Utils.encryptPassword(password);
		String userTable = properties.getProperty("com.user.table.name");
		String userSelectQuery = dynamiQuery.getSelectQuery(userTable, null);
		String registrationTable = properties.getProperty("com.registration.table.name");
		String registrationSelectQuery =dynamiQuery.getSelectQuery(registrationTable, null);
		User user = userDao.getUserByEmail(userName, userSelectQuery); 
		if (user==null) {
			throw new DataNotFoundException("Invalid userName");
		} 
		if(!user.getPassword().equals(password)) {
			throw new DataNotFoundException("Invalid Password");
		}		
		List<Registration> registrations = registrationDao.getRegistrationCache(registrationSelectQuery);
		int registionCount = 0;
		for(Registration registration : registrations) {
			if(registration.getUserId() == user.getId()) {
				registionCount++;
			} 
				
		}
		
		JSONObject responseDataObject = new JSONObject();
		responseDataObject.put("userId", user.getId());
		responseDataObject.put("registrationCount", registionCount);
		
		logger.info("User Logged In Successfully" + userName);
		return responseDataObject;

	}

	/**
	 * Registers slot for vaccination
	 * 
	 * @throws SlotsNotAvailableException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws RegistrationCanNotBeDoneException
	 * @throws ParseException
	 */
	public void registerVaccinaionSlot(JSONObject jObject, String userId, Properties properties) throws Exception {
		Long parsedUserId = Long.parseLong(userId);
		Registration registration = JsonUtility.getRegistrationObjectFromJson(jObject);
		String userTable = properties.getProperty("com.user.table.name");
		String userSelectquery = dynamiQuery.getSelectQuery(userTable, null);
		String registrationTable = properties.getProperty("com.registration.table.name");
		String registrationSelectQuery = dynamiQuery.getSelectQuery(registrationTable, null) ;
		String campTable = properties.getProperty("com.camp.table.name");
		String campSelectQuery = dynamiQuery.getSelectQuery(campTable, null);
		String auditTable = properties.getProperty("com.audit.table.name");
		String auditFieldsAsString = properties.getProperty("com.audit.table.fields.name");
		System.out.println(auditFieldsAsString);
		String auditInsertQuery = dynamiQuery.getInsertQuery(auditTable, auditFieldsAsString);
		logger.info("New camping site registration request. Data : " + registration.toString() );
		registration.setUserId(parsedUserId);
		
		Audit audit = new Audit();
		audit.setUserId(parsedUserId);
		audit.setUserType("Public");
		audit.setOperation("Slot registration for vaccinaation");
		
		
		User user = userDao.getUserById((Long.parseLong(userId)), userSelectquery );
		if(user==null)
			throw new DataNotFoundException("Invalid User Id");
		
		boolean isELigible = checkTheUserEligibilityForVaccination(registration.getUserId(), registration.getDosageCount(),
				registration.getDateOfVaccination(),registrationSelectQuery);
		
		if(isELigible) {
			boolean isAvailable = checkAvailabilityOfStock(registration.getChoosenCampId(), campSelectQuery);
			if(!isAvailable) {
				audit.setState("Failed");
				auditDao.insertAudit(audit, auditInsertQuery);
				logger.warning("Vaccine stock not available. Data " + registration.toString() );
				throw new RegistrationCanNotBeDoneException("Vaccine stock not available");
			} 
			
			isAvailable = checkAvailabilityOfSlots(registration, registrationSelectQuery);
			if(!isAvailable) {
				audit.setState("Failed");
				auditDao.insertAudit(audit, auditInsertQuery);
				logger.warning("Slots not available. Data" +  registration.toString() );
				throw new SlotsNotAvailableException("Slots not available.");
			}
			
			String regFieldsAsString = properties.getProperty("com.registration.table.fields.name");
			String registrationInsertQuery = dynamiQuery.getInsertQuery(registrationTable, regFieldsAsString);
			int result = registrationDao.bookSlotForVaccination(registration, registrationInsertQuery);	
			
			if (result > 0) {
				logger.info("Slot booking for vaccination confirmed. Data : " + registration.toString() );
				audit.setState("Success");
				auditDao.insertAudit(audit, auditInsertQuery);
				String updateFilter = "SET stock = stock+? WHERE id = ? and state = 'active' ";
				String updatequery = dynamiQuery.getUpdateQuery( campTable, updateFilter);
				campDao.reduceStockwhileSlotRegistration(-1, registration.getChoosenCampId(), campSelectQuery, updatequery);
				registrationDao.updateRegistrationCache(registration, campSelectQuery);
			} 
			
		}

	}

	/**
	 * Checks the eligibility of the user for the Vaccination. There should be 45 days gap between first and second dosage.
	 * 
	 * @param userName                  - User Name
	 * @param dosageCount               - Dosage Count
	 * @param choosenDateOfVaccination  - Date of Vaccination
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws RegistrationCanNotBeDoneException
	 */
	public boolean checkTheUserEligibilityForVaccination(Long userId, int dosageCount, 
			Date choosenDateOfVaccination, String selectQuery) throws Exception {
		boolean isELigible = true;
		List<Registration> registrations = registrationDao.getRegistrationCache(selectQuery);
		//Validates for the first dosage Users
		if (dosageCount == 1) {
			for (Registration registration : registrations) {
				if (registration.getUserId().equals(userId)) {
					isELigible = false;
					if(registration.getStatus().equals("Inprogress")) 
						throw new RegistrationCanNotBeDoneException("You already registered for 1st dosage");
					else if(registration.getStatus().equals("Processed"))
						throw new RegistrationCanNotBeDoneException("Seems you already taken first dosage");
				}
			}
		} else if (dosageCount == 2) { //Validates for the second dosage users
			boolean isUserFound = false;
			for (Registration registration : registrations) {
				if(registration.getUserId().equals(userId)) {
					isUserFound = true;
					if(registration.getDosageCount() == 1) {
						isELigible = false;
						if(registration.getStatus().equals("Inprogress")) {
							throw new RegistrationCanNotBeDoneException("Your 1st vaccination is inprogress.");
						} else if(registration.getStatus().equals("Processed")) {
							long timeDifference = TimeUnit.MILLISECONDS.toDays(choosenDateOfVaccination.getTime() - 
									registration.getDateOfVaccination().getTime());
							if(timeDifference < 45) {
								throw new RegistrationCanNotBeDoneException("You are allowed to " + 
										"take 2nd dosage only after 45 Days of first vaccination.");
								
							}
						}
					} else if(registration.getDosageCount() == 2) {
						isELigible = false;
						if(registration.getStatus().equals("Inprogress"))
							throw new RegistrationCanNotBeDoneException("You already registered for 2nd dosage");
						else if(registration.getStatus().equals("Processed"))
							throw new RegistrationCanNotBeDoneException("You are fully Vaccinated");
						
					}
				}

			}
			if(!isUserFound) {
				isELigible =  false;
				throw new RegistrationCanNotBeDoneException("Seems you haven't taken first dosage");
				
			}

		} else {
			isELigible =  false;
			throw new RegistrationCanNotBeDoneException("Invalid Dosage Count");
		}
		
		return isELigible;

	}
	
	/**
	 * Check the availability of slots
	 * @param SlotRegistration
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public boolean checkAvailabilityOfSlots(Registration newRegistration, String selectQuery) throws Exception {
		List<Registration> registrationsCache= registrationDao.getRegistrationCache(selectQuery);
		int totalRegistration = 0 ;
		for(Registration registration: registrationsCache) {
			if(registration.getDateOfVaccination().equals(newRegistration.getDateOfVaccination()) &&
					registration.getChoosenSlotId() == (newRegistration.getChoosenSlotId())) {
				totalRegistration +=1;
				if(totalRegistration >=  10 )
					return false;
			}
		}
		if(totalRegistration < 10)
			return true;

		return false;
	}
	
	/**
	 * Checks the availability of stocks in a city
	 * 
	 * @param campId - Camp Id
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws RegistrationCanNotBeDoneException 
	 */
	public boolean checkAvailabilityOfStock( long campId, String selectQuery) throws Exception {
		boolean isAvailable  = false;
		Map<Long, Camp> campsCache = campDao.getCampsCache(selectQuery);
		Camp camp = campsCache.get(campId);
		if(camp == null) {
			throw new RegistrationCanNotBeDoneException("Invalid Camp Id");
		}
			
		if(camp.getStock() > 0  && camp.getState().equals("active")) 
			isAvailable = true;

		return isAvailable;
	}
	
	
	public Registration getRegistrationByDosage(Long userId,int dosageCount, String regSelectQuery) throws Exception {
		//Long userId = Long.parseLong(user);
		List<Registration>  registrations = registrationDao.getRegistrationCache(regSelectQuery);
		for(Registration registration : registrations) {
			if(registration.getUserId().equals(userId) && registration.getDosageCount() == dosageCount
					&& registration.getStatus().equals("Processed"))
				return registration;
		}
		return null;
		
	}
	
	
	public void getCertificate(String usrId, Document document, Properties properties)  throws Exception{
		Long userId = Long.parseLong(usrId);
		String userTable = properties.getProperty("com.user.table.name");
		String userSelectQuery = dynamiQuery.getSelectQuery(userTable, null);
		String regTable = properties.getProperty("com.registration.table.name");
		String regSelectQuery = dynamiQuery.getSelectQuery(regTable, null);
		Font blueH1Font = FontFactory.getFont(FontFactory.HELVETICA, 15, Font.BOLD, new CMYKColor(255, 0, 0, 0));
		Font blueH2Font = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL | Font.UNDERLINE, new CMYKColor(255, 0, 0, 0));

		
		User user = userDao.getUserById(userId, userSelectQuery);
		Registration registrationOne = getRegistrationByDosage(userId, 1, regSelectQuery);
		if(registrationOne == null) {
			throw new Exception("Vaccination have not taken yet");
		}
			
		Image image = Image.getInstance("/Users/vennila-16250/apache/apache-tomcat-10.0.27/webapps/vac-camps/health-ministry-of-india-logo.png");
		image.scaleAbsolute(120, 120);
		image.setAlignment(Element.ALIGN_CENTER);
		document.add(image);
			
		Paragraph h1 = new Paragraph("Certificate for Covid-19 Vaccination", blueH1Font);
		h1.setAlignment(Element.ALIGN_CENTER);
		document.add(h1);
			
		Paragraph h2 = new Paragraph("Beneficiary Details", blueH2Font);
		document.add(h2);
			
		Paragraph p = new Paragraph(" ");
		document.add(p);
			
		PdfPTable table = new PdfPTable(2);
		PdfPCell cell1 = new PdfPCell( new Paragraph("Name"));
		PdfPCell cell2 = new PdfPCell(new Paragraph(user.getFirsName() + " " + user.getLastName()));
		cell1.setBorder(0) ;
		cell2.setBorder(0);
		//cell2.setUserBorderPadding(true);
		table.addCell(cell1);
		table.addCell(cell2);
			
		PdfPCell cell3  = new PdfPCell(new Paragraph("Aadhar Number"));
		PdfPCell cell4 = new PdfPCell(new Paragraph( ""+ user.getAadharNumber()));
		cell3.setBorder(0);
		cell4.setBorder(0);
		table.addCell(cell3);
		table.addCell(cell4);
			
		document.add(table);
		document.add(p);
			
		Paragraph h3 = new Paragraph("Vaccination Details", blueH2Font);
		document.add(h3);
			
		Registration registrationTwo = getRegistrationByDosage(userId, 2, regSelectQuery);
		if(registrationTwo == null) {
			PdfPTable table1 =  new PdfPTable(2);
					
			PdfPCell cell5 = new PdfPCell(new Paragraph("Status"));
			PdfPCell cell6 = new PdfPCell(new Paragraph("Vaccinated"));
			cell5.setBorder(0);
			cell6.setBorder(0);
			table1.addCell(cell5);
			table1.addCell(cell6);
					
			PdfPCell cell7 = new PdfPCell(new Paragraph("Dosages"));
			PdfPCell cell8 = new PdfPCell(new Paragraph(""+registrationOne.getDosageCount() ) );
			cell7.setBorder(0);
			cell8.setBorder(0);
			table1.addCell(cell7);
			table1.addCell(cell8);
					
			PdfPCell cell9 = new PdfPCell(new Paragraph("Vaccinated Date"));
			PdfPCell cell10 = new PdfPCell(new Paragraph (registrationOne.getDateOfVaccination().toString() ));
			cell9.setBorder(0);
			cell10.setBorder(0);
			table1.addCell(cell9);
			table1.addCell(cell10);
			document.add(p);
			document.add(table1);
			} else {
				PdfPTable table1 =  new PdfPTable(3);
					
				PdfPCell cell5 = new PdfPCell(new Paragraph("Status"));
				PdfPCell cell6 = new PdfPCell(new Paragraph("Fully vaccinated"));
				PdfPCell cell20 = new PdfPCell(p);
				cell5.setBorder(0);
				cell6.setBorder(0);
				cell20.setBorder(0);
				table1.addCell(cell5);
				table1.addCell(cell6);
				table1.addCell(cell20);
					
				PdfPCell cell7 = new PdfPCell(new Paragraph("Dosages"));
				PdfPCell cell8 = new PdfPCell(new Paragraph("" +registrationOne.getDosageCount() ));
				PdfPCell cell9 = new PdfPCell(new Paragraph(""+registrationTwo.getDosageCount() ));
				cell7.setBorder(0);
				cell8.setBorder(0);
				cell9.setBorder(0);
				table1.addCell(cell7);
				table1.addCell(cell8);
				table1.addCell(cell9);
					
				PdfPCell cell10 = new PdfPCell(new Paragraph("Vaccinated Date"));
				PdfPCell cell11 = new PdfPCell(new Paragraph (registrationOne.getDateOfVaccination().toString() ));
				PdfPCell cell12 = new PdfPCell(new Paragraph (registrationTwo.getDateOfVaccination().toString() ));
				cell10.setBorder(0);
				cell11.setBorder(0);
				cell12.setBorder(0);
				table1.addCell(cell10);
				table1.addCell(cell11);
				table1.addCell(cell12);
				document.add(p);
				document.add(table1);
					
			}		
		
	}

}
