//$Id$
package com.management.camp.vaccination.controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.management.camp.vaccination.dto.Registration;
import com.management.camp.vaccination.dto.User;
import com.management.camp.vaccination.service.AdminService;
import com.management.camp.vaccination.service.UserService;
import com.management.camp.vaccination.service.UtilService;
import com.management.camp.vaccination.utility.JsonUtility;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class Controller extends HttpServlet {
	private Properties properties = new Properties();
	private String filePath = "/Users/vennila-16250/apache/apache-tomcat-10.0.27/webapps/vac-camps/resources/config.properties";
	private UserService userService = new UserService();
	private AdminService adminService = new AdminService();
	private UtilService utilService = new UtilService();
	private Logger logger = Logger.getLogger(Controller.class.getName());

	public Controller() {	
	}

	/**
	 * Handles HTTP GET requests
	 * 
	 * @param HttpServletRequest  - Http Servlet Request
	 * @param HttpServletResponse - Http Servlet Response
	 * @throws IOException, ServletException
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		Font blueH1Font = FontFactory.getFont(FontFactory.HELVETICA, 15, Font.BOLD, new CMYKColor(255, 0, 0, 0));
		Font blueH2Font = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL | Font.UNDERLINE, new CMYKColor(255, 0, 0, 0));
		JSONObject jObject = new JSONObject();
		response.setContentType("application/json");
		String[] uri = request.getPathInfo().split("/");
		FileInputStream fileInputStream = new FileInputStream(filePath);
	    properties.load(fileInputStream);
		if(uri.length == 2 && uri[1].equals("cities")) {
			try {
				JSONArray responseAray = utilService.getCampsByCity(properties);
				JSONObject cityObj = new JSONObject();
				cityObj.put("cities", responseAray.toJSONString());
				response.getWriter().print( cityObj.toString());	
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if(uri.length == 4 &&  uri[1].equals("cities") && uri[3].equals("camps")) {
			try {
				long cityId = Long.parseLong(uri[2]);
				JSONArray jArray = utilService.getCampDetailsByCity(cityId, properties);
				JSONObject campObj = new JSONObject();
				campObj.put("camps", jArray.toJSONString());
				response.getWriter().write(campObj.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if(uri.length == 4 && uri[1].equals("organizations") && uri[3].contains("camps")) {
			try {
				String camps = adminService.fetchCampsByOrgId(uri[2], properties);
				response.getWriter().write(camps);
			} catch(Exception e) {
				logger.severe("Get Camps request failed. " + e.getMessage());
				response.setStatus(500);
				response.getWriter().write(JsonUtility.createFailureJsonObject(e.getMessage()).toJSONString());
				e.printStackTrace();
			}
			
		} else if(uri.length == 4 && uri[1].equals("organizations") && uri[3].equals("summaries")) {
			try {
				String data = adminService.getVaccinationSummary(jObject, uri[2], properties);
				//JSONObject outputResponse =  JsonUtility.createSuccessJsonObject("Account Created Successfully").put("data", data);
				response.getWriter().write(data);	
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} else if( uri.length == 4 && uri[1].equals("users") && uri[3].equals("certificates")) {
			try {
				response.setContentType("appliocation/.pdf");		
				Document document = new Document();
				PdfWriter pdfWriter = PdfWriter.getInstance(document, response.getOutputStream());
				document.open();
				userService.getCertificate(uri[2], document, properties);
				document.close();
				pdfWriter.close();
				return;
				
			} catch(Exception e) {
				response.setContentType("application/json");
				logger.severe("Download E-certificate failed. " + e.getMessage());
				response.setStatus(500);
				response.getOutputStream().print(JsonUtility.createFailureJsonObject(e.getMessage()).toJSONString());
				//response.getWriter().write(JsonUtility.createFailureJsonObject(e.getMessage()).toJSONString());
				e.printStackTrace();
			}
			
		} else {
			System.out.println("URL Not Found");
		}
		
	}

	/**
	 * Handles the Post Requests
	 * 
	 * @param HttpServletRequest  - HTTP Servlet Request
	 * @param HttpServletResponse - HTTP Servlet Response
 	 * @throws IOException,
	 *             ServletException
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject jObject = new JSONObject();
		ServletInputStream requestInput  = request.getInputStream();
		String body = new BufferedReader( new InputStreamReader(requestInput)).lines().collect(Collectors.joining("\n"));
		String[] uri = request.getPathInfo().split("/");
		FileInputStream fileInputStream = new FileInputStream(filePath);
	    properties.load(fileInputStream);

		
		try {
			jObject = JsonUtility.getJsonObject(body);// Converts the HttpReq into JSON Object
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		if (uri.length==2 && uri[1].equals("users")) {//New user registration
			try {
				userService.createUser(jObject, properties);
				response.getWriter().write(JsonUtility.createSuccessJsonObject("Account Created Successfully").toJSONString());

			} catch (Exception e) {
				logger.severe("New User Registration Failed. " + e.getMessage());
				response.setStatus(500);
				response.getWriter().write(JsonUtility.createFailureJsonObject(e.getMessage()).toJSONString());
				e.printStackTrace();
			}

		} else if (uri.length==3 && uri[1].equals("users") && uri[2].equals("login")) {//Existing user login validation
			try {
				JSONObject responseData = userService.validateUser(jObject, properties);
				JSONObject responseJObject = JsonUtility.createSuccessJsonObject("Logged In Successfully");
				responseJObject.put("data", responseData);
				response.getWriter().write(responseJObject.toJSONString());
			} catch (Exception e) {
				logger.warning("Login failed." + e.getMessage());
				response.setStatus(500);
				response.getWriter().write(JsonUtility.createFailureJsonObject(e.getMessage()).toJSONString());
				e.printStackTrace();
			}

		} else if(uri.length == 4 && uri[1].equals("users") && uri[3].equals("vaccines")) {
			try {
				userService.registerVaccinaionSlot(jObject, uri[2], properties);
				JSONObject responseJObject = JsonUtility.createSuccessJsonObject("Registration done successfully");
				response.getWriter().write(responseJObject.toJSONString());
			} catch (Exception e) {
				logger.severe("Slot booking for vaccination failed. " + e.getMessage());
				response.setStatus(500);
				response.getWriter().write(JsonUtility.createFailureJsonObject(e.getMessage()).toJSONString());
				e.printStackTrace();
			}
		} else if (uri.length==3 && uri[1].equals("organizations") && uri[2].equals("login")) {//Existing user login validation
			try {
				JSONObject responseData = adminService.validateOrganization(jObject, properties);
				JSONObject responseJObject = JsonUtility.createSuccessJsonObject("Logged In Successfully");
				responseJObject.put("data", responseData);
				response.getWriter().write(responseJObject.toJSONString());
			} catch (Exception e) {
				logger.warning("Login failed." + e.getMessage());
				response.setStatus(500);
				response.getWriter().write(JsonUtility.createFailureJsonObject(e.getMessage()).toJSONString());
				e.printStackTrace();
			}

		} else if(uri.length == 4 && uri[1].equals("organizations") && uri[3].equals("camps") ) {//Camping site registration
			try {
				adminService.createCampingSite(jObject, uri[2], properties);
				response.getWriter().write(JsonUtility.createSuccessJsonObject("Camping Site creted Successfully").toJSONString());
			} catch(Exception e) {
				logger.severe("Camping site registration failed. " + e.getMessage() + ". Data :" +  jObject.toJSONString());
				response.setStatus(500);
				response.getWriter().write(JsonUtility.createFailureJsonObject(e.getMessage()).toJSONString());
				e.printStackTrace();
				
			}
		} else if(request.getPathInfo().equals("/organizations")) {
			try {
				adminService.createOrganization(jObject, 0, properties);
				response.getWriter().write(JsonUtility.createSuccessJsonObject("Organization Created Successfully").toJSONString());
			} catch(Exception e) {
				logger.severe("Organization registration failed. " + e.getMessage() + ". Data " + JSONObject.toJSONString(jObject) );
				response.setStatus(500);
				response.getWriter().write(JsonUtility.createFailureJsonObject(e.getMessage()).toJSONString());
				e.printStackTrace();
			}
		}  else {
			response.setStatus(404);
			response.getWriter().append("Page Not Found");
		}

	}
	
	/**
	 * Handles Put Request
	 * 
	 * @param HttpServletRequest  - HTTP Request
	 * @param HttpServletResponse - HTTP Response 
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		JSONObject jObject = new JSONObject();
		FileInputStream fileInputStream = new FileInputStream(filePath);
	    properties.load(fileInputStream);
		ServletInputStream requestInput  = request.getInputStream();
		String body = new BufferedReader( new InputStreamReader(requestInput)).lines().collect(Collectors.joining("\n"));
		response.setContentType("application/json");
		String[] uri = request.getPathInfo().split("/");
		
		try {
			jObject = JsonUtility.getJsonObject(body);// Converts the HttpReq body into JSON Object
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		if(uri.length == 5 && uri[1].equals("organizations") && uri[3].equals("camps")) {//Updates vaccine stocks in a city
			try {
				adminService.updateStocksInCity(jObject, uri[2], uri[4], properties);
				response.getWriter().write(JsonUtility.createSuccessJsonObject("Stock updated successfully").toJSONString());
				
			} catch(Exception e) {
				logger.severe(" Stock update failed. " + e.getMessage());
				response.setStatus(500);
				response.getWriter().write(JsonUtility.createFailureJsonObject(e.getMessage()).toJSONString());
				e.printStackTrace();
			}
			
		}
		
		
	}

}
