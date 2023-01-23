//$Id$
package com.management.camp.vaccination.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.management.camp.vaccination.connection.DBConnection;
import com.management.camp.vaccination.dto.Admin;

public class AdminDao {
	
	private final String SELECT_ADMIN_QUERY = "SELECT * FROM admins";
	private final String INSERT_ADMIN_QUERY = "INSERT INTO admins (user_name, hashed_password, org_id, created_date, state)"
			+ "VALUES (?, ?, ?, ?, ?)";
	
	Map<String, Admin> adminsCache = new HashMap<String, Admin>();
	
	
	public int insertAdmin(Admin admin) throws ClassNotFoundException, SQLException {
		Connection conn = DBConnection.getConnection();
		PreparedStatement stmt = conn.prepareStatement(INSERT_ADMIN_QUERY, Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, admin.getUserName());
		stmt.setString(2, admin.getPassword());
		stmt.setInt(3, admin.getOrgId());
		stmt.setDate(4, admin.getCreatedDate());
		stmt.setString(5, admin.getState());
		
		int result = stmt.executeUpdate();
		ResultSet rs  = stmt.getGeneratedKeys();
		rs.next();
		admin.setId(rs.getInt(1));
		return result;
	}
	
	/**
	 * Fetches all the admin records from the table
	 * @return 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public Map<String, Admin> getAdminsCache() throws ClassNotFoundException, SQLException {
		if(adminsCache == null || adminsCache.isEmpty()) {
			Connection conn = DBConnection.getConnection();
			PreparedStatement stmt = conn.prepareStatement(SELECT_ADMIN_QUERY);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {	
				Admin admin = getAdminFromResultSet(rs);
				adminsCache.put(admin.getUserName(), admin);
			}
			
		}
		
		return adminsCache;
		
	}
	
	/**
	 * Creates&Returns the Admin Object from ResultSet
	 * @param rs
	 * @throws SQLException 
	 */
	public Admin getAdminFromResultSet(ResultSet rs) throws SQLException {
		Admin admin = new Admin();
		admin.setId(rs.getInt("id"));
		admin.setUserName(rs.getString("user_name")); 
		admin.setOrgId(rs.getInt("org_id"));
		admin.setCreatedDate(rs.getDate("created_date"));
		admin.setPassword(rs.getString("hashed_password"));
		admin.setState(rs.getString("state"));
		return admin;
	}
	
	public Map<String, Admin> updateAdminCache(Admin admin) throws ClassNotFoundException, SQLException {
		getAdminsCache();
		adminsCache.put(admin.getUserName(), admin);
		return adminsCache;
		
	} 
	
	/**
	 * Gets the Admin by userName
	 * @param userName
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Admin getAdminByUserName(String userName ) throws ClassNotFoundException, SQLException {
		getAdminsCache();
		Admin admin = adminsCache.get(userName);
		if(admin!=null)
			return admin;
		return null;
	}

}
