//$Id$
package com.management.camp.vaccination.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


import com.management.camp.vaccination.connection.DBConnection;
import com.management.camp.vaccination.dto.Registration;
import com.management.camp.vaccination.exception.SlotsNotAvailableException;

public class RegistrationDao {
	
	private static List<Registration> registrationsCache = new ArrayList<Registration>();
	
	public int bookSlotForVaccination(Registration registration, String query) throws ClassNotFoundException, SQLException, SlotsNotAvailableException {
		Connection conn = DBConnection.getConnection();
		PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		stmt.setLong(1, registration.getUserId() );
		stmt.setLong(2, registration.getChoosenCampId());
		stmt.setDate(3, registration.getDateOfVaccination());
		stmt.setInt(4, registration.getChoosenSlotId());
		stmt.setInt(5, registration.getDosageCount());
		stmt.setString(6, registration.getStatus());
		
		int result = stmt.executeUpdate();
		ResultSet rs = stmt.getGeneratedKeys();
		rs.next();
		registration.setId(rs.getLong(1));	
		return result;
		
	}
	
	public List<Registration> getRegistrationCache(String query) throws ClassNotFoundException, SQLException {
		
		if(registrationsCache == null || registrationsCache.isEmpty()) {
			Connection conn = DBConnection.getConnection();
			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				updateRegistrationCacheFromResultSet(rs);
				
			}
		}
		
		return registrationsCache;
		
	}
	
	public void updateRegistrationCacheFromResultSet(ResultSet rs) throws SQLException {
		Registration registration = new Registration();
		registration.setId(rs.getLong("id"));
		registration.setUserId(rs.getLong("user_id"));
		registration.setChoosenCampId(rs.getLong("chosen_camp_id"));
		registration.setChoosenSlotId(rs.getInt("chosen_slot_id"));
		registration.setDateOfVaccination(rs.getDate("date_of_vaccination"));
		registration.setDosageCount(rs.getInt("dosage_count"));
		registration.setStatus(rs.getString("status"));
		
		registrationsCache.add(registration);
			
	}
	
	public void updateRegistrationCache(Registration registration, String selectQuery) throws ClassNotFoundException, SQLException {
		getRegistrationCache(selectQuery);
		registrationsCache.add(registration);
		
	}
	
	
	
	

}
