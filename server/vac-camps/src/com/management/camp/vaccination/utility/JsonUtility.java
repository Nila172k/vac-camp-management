//$Id$
package com.management.camp.vaccination.utility;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.SimpleFormatter;

import javax.print.attribute.standard.JobName;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.management.camp.vaccination.dto.Admin;
import com.management.camp.vaccination.dto.Camp;
import com.management.camp.vaccination.dto.Organization;
import com.management.camp.vaccination.dto.Registration;
import com.management.camp.vaccination.dto.User;

public class JsonUtility {
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public static JSONObject getJsonObject(String jsonString) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(jsonString);
		return json;
	}
	
	public static JSONObject createGenerateJsonObject( Map<String, String> jsonKeyValue) {
		JSONObject jObject = new JSONObject();
		Iterator<Map.Entry<String, String>> itr = jsonKeyValue.entrySet().iterator();
		while(itr.hasNext()) {
			Map.Entry<String, String> keyValuePair  = itr.next();
			jObject.put(keyValuePair.getKey(), keyValuePair.getValue());
		}
		
		return jObject;
		
	}
	
	public static JSONObject createSuccessJsonObject(String message) {
		JSONObject jObject = new JSONObject();
		jObject.put("status", "Success");
		jObject.put("message", message);
		return jObject;	
	}
	
	public static JSONObject createFailureJsonObject(String message) {
		JSONObject jObject = new JSONObject();
		jObject.put("status", "Failure");
		jObject.put("message", message);
		return jObject;
		
	}
	
	/**
	 * Return the User Object from the JSON Object
	 * @param jObject - JSON Object
	 * @return
	 */
	public static User getUserObjectFromJson(JSONObject jObject) {
		User user = new User();
		if(jObject.get("firstName")!= null)
			user.setFirsName((String) jObject.get("firstName"));
		if(jObject.get("lastName") != null)
			user.setLastName((String) jObject.get("lastName"));
		if(jObject.get("gender") != null)
			user.setGender( (String) jObject.get("gender"));
		if(jObject.get("dob") != null)
			user.setDob( Date.valueOf( (String) jObject.get("dob")) );
		if(jObject.get("phoneNumber") !=  null)
			user.setPhoneNumber((String) jObject.get("phoneNumber"));
		if(jObject.get("aadharNumber") != null)
			user.setAadharNumber( Long.parseLong((String) jObject.get("aadharNumber")));
		if(jObject.get("email") != null)
			user.setEmail((String) jObject.get("email"));
		if(jObject.get("password") != null)
			user.setPassword((String) jObject.get("password")) ;
		user.setCreatedDate(new Date(System.currentTimeMillis() ));
		user.setState("active");
		return user;
		
	}
	
	public static Registration getRegistrationObjectFromJson(JSONObject jObject) {
		Registration registration = new Registration();
		if(jObject.get("chosenCampId") != null)
			registration.setChoosenCampId(Long.valueOf( (String) jObject.get("chosenCampId")));
		if(jObject.get("chosenSlotId") != null)
			registration.setChoosenSlotId(Integer.parseInt( (String) jObject.get("chosenSlotId") ));
		if(jObject.get("dateOfVaccination") != null)
			registration.setDateOfVaccination(Date.valueOf( (String) jObject.get("dateOfVaccination")));	
		if(jObject.get("dosageCount") != null)
			registration.setDosageCount( Integer.parseInt(((String) jObject.get("dosageCount"))) );
		registration.setStatus("Inprogress");
		return registration;
	}
	
	public static Camp getCampObjectFromJson(JSONObject jObject) {

		Camp camp = new Camp();
		if(jObject.get("address") != null)
			camp.setAddress( (String) jObject.get("address"));
		if(jObject.get("stock") != null)	
			camp.setStock( Long.parseLong((String) jObject.get("stock")) ); 
		if(jObject.get("cityId") != null)
			camp.setCityId( Long.parseLong((String) jObject.get("cityId")) );
		camp.setCreatedDate(new Date(System.currentTimeMillis()));	
		camp.setState("active");
		return camp;
		
	}
	
	public static Organization getOrganizationObjectFromJson(JSONObject jObject) {
		Organization organization = new Organization();
		if(jObject.get("orgName") != null)
			organization.setName( (String) jObject.get("orgName"));
		if(jObject.get("userName") != null)
			organization.setUserName( (String) jObject.get("userName"));
		if(jObject.get("password") != null)
			organization.setPasword( (String) jObject.get("password"));
		organization.setCreatedDate(new Date(System.currentTimeMillis()));
		organization.setStatus("active");
		return organization;
	}
	
	public static Admin getAdminObjectFromJson(JSONObject jObject) {
		Admin admin = new Admin();
		if(jObject.get("userName") != null)
			admin.setUserName( (String)  jObject.get("userName"));
		if(jObject.get("password") != null)
			admin.setPassword( (String) jObject.get("password"));
		admin.setCreatedDate( new Date(System.currentTimeMillis()) );
		admin.setState("active");
		return admin;
	}

}
