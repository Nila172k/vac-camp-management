//$Id$
package com.management.camp.vaccination.utility;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.management.camp.vaccination.dto.User;

public class Utils {
	
	public static String encryptPassword(String password) throws NoSuchAlgorithmException {
		MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
		byte[] hashedPassword = mDigest.digest(password.getBytes(StandardCharsets.UTF_8));
		BigInteger number = new BigInteger(1, hashedPassword);
		StringBuilder hexString = new StringBuilder(number.toString(16));  
				
		while (hexString.length() < 32)  
        {  
            hexString.insert(0, '0');  
        }  
  
        return hexString.toString();
	}
	
	public static JSONObject getJsonObject(String jsonString) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(jsonString);
		return json;
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

	
}
