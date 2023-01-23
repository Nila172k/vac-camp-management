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
import com.management.camp.vaccination.dto.Organization;

public class OrganizationDao {
	
//	private final String INSERT_ORGANIZATION_QUERY = "INSERT INTO organizations(name, user_name, "
//			+ "hased_password, created_date, state) VALUES (?, ?, ?, ?, ?)";
	private static Map<Integer, Organization> organizationsCache = new HashMap<Integer, Organization>();
	
	/**
	 * Inserts a record in table
	 * @param organization - Organization Object 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public int insertOrganization(Organization organization, String query) throws ClassNotFoundException, SQLException {
		Connection conn = DBConnection.getConnection();
		PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, organization.getName());
		stmt.setString(2, organization.getUserName());
		stmt.setString(3, organization.getPasword());
		stmt.setDate(4, organization.getCreatedDate());
		stmt.setString(5, organization.getStatus());
		int result = stmt.executeUpdate();
		ResultSet rs = stmt.getGeneratedKeys();
		rs.next();
		organization.setId(rs.getInt(1));
		return result;
	}
	
	/**
	 * Fetches the organizations details from table and stores it in cahce
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Map<Integer, Organization> getOrganizations(String query) throws ClassNotFoundException, SQLException {
		if(organizationsCache == null || organizationsCache.isEmpty()) {
			Connection conn = DBConnection.getConnection();
			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				Organization organization = getOrganizationObjectFromResultSet(rs);
				organizationsCache.put(organization.getId(), organization);
			}
			
		}
		return organizationsCache;
		
	}
	
	/**
	 * Returns the organization object from Result set
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public Organization getOrganizationObjectFromResultSet(ResultSet rs) throws SQLException {
		Organization organization = new Organization();
		organization.setId(rs.getInt("id"));
		organization.setName(rs.getString("name"));
		organization.setUserName(rs.getString("user_name"));
		organization.setPasword(rs.getString("hashed_password"));
		organization.setCreatedDate(rs.getDate("created_date"));
		organization.setStatus(rs.getString("state"));
		return organization;
	}
	
	/**
	 * Updates the organoizationsCache with new organization
	 * @param organization
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Map<Integer, Organization> updateOrganizationCache(Organization organization, String query) throws ClassNotFoundException, SQLException {
		getOrganizations(query);
		organizationsCache.put(organization.getId(), organization);
		return organizationsCache;
		
	}
	
	public Organization getOrganizationByUserName(String userName, String selectQuery) throws ClassNotFoundException, SQLException {
		getOrganizations(selectQuery);
		Iterator<Map.Entry<Integer, Organization>> organizations = organizationsCache.entrySet().iterator();
		while(organizations.hasNext()) {
			Map.Entry<Integer, Organization> organizationItr = organizations.next();
			if(organizationItr.getValue().getUserName().equals(userName))
				return organizationItr.getValue();
		}
		return null;
		
	}
}
