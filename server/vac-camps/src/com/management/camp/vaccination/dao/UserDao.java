//$Id$
package com.management.camp.vaccination.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.management.camp.vaccination.connection.DBConnection;
import com.management.camp.vaccination.dto.User;
import com.management.camp.vaccination.exception.DuplicateDataFoundException;

public class UserDao {

	private static Map<Long, User> usersCache = new HashMap<Long, User>();
	
	/**
	 * Inserts the User Object in table
	 * 
	 * @param user
	 *            - User Object
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws DuplicateDataFoundException 
	 */
	public int insertUser(User user, String insertQuery) throws SQLException, ClassNotFoundException, DuplicateDataFoundException {
		Connection conn = DBConnection.getConnection();
		PreparedStatement stmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, user.getFirsName());
		stmt.setString(2, user.getLastName());
		stmt.setString(3, user.getGender());
		stmt.setDate(4, user.getDob());
		stmt.setString(5, user.getPhoneNumber());
		stmt.setLong(6, user.getAadharNumber());
		stmt.setString(7, user.getEmail());
		stmt.setString(8, user.getPassword());
		stmt.setDate(9, user.getCreatedDate());
		stmt.setString(10, user.getState());
		
		int result = stmt.executeUpdate();
		ResultSet rs = stmt.getGeneratedKeys();
		rs.next();
		user.setId(rs.getLong(1));		
		return result;

	}
	/**
	 * Return the user by email id
	 * @param userName - User name
	 * @param password - Password
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public User getUserByEmail(String userName, String query) throws ClassNotFoundException, SQLException {
		getUsersCache(query);
		Iterator<Map.Entry<Long, User>> userItr = usersCache.entrySet().iterator();
		while(userItr.hasNext()) {
			Map.Entry<Long, User> userEntry = userItr.next();
			if(userEntry.getValue().getEmail().equals(userName))
				return userEntry.getValue();
		}
		
		return null;
		
	}
	/**
	 * Return the user by id
	 * @param userName - User name
	 * @param password - Password
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public User getUserById(Long userId, String query) throws Exception {
		//String userName = (String) jObject.get("userName");
		//String userName = "k@gmail.com";
		Map<Long, User> users = getUsersCache(query);
		User user = users.get(userId);
		if(user!= null)
			return user;
		
		return null;
	}

	/**
	 * Fetches all the Users from the table and stores the result set in map
	 * 
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public Map<Long, User> getUsersCache(String query) throws SQLException, ClassNotFoundException {
		if (usersCache == null || usersCache.isEmpty()) {
			Connection conn = DBConnection.getConnection();
			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				updateUsersCacheFromResultSet(rs);
			}

		}

		return usersCache;

	}

	/**
	 * Adds newly created user to the internal cache
	 * 
	 * @param user - New User Object
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public void updateAllUsersCache(User user, String query) throws ClassNotFoundException, SQLException {
		getUsersCache(query);
		usersCache.put(user.getId() , user);

	}
	
	public void updateUsersCacheFromResultSet(ResultSet rs) throws SQLException {
		User user = new User();
		user.setId(rs.getLong("id"));
		user.setFirsName(rs.getString("first_name"));
		user.setLastName(rs.getString("last_name"));
		user.setGender(rs.getString("gender"));
		user.setDob(rs.getDate("dob"));
		user.setPhoneNumber(rs.getString("phone_number"));
		user.setAadharNumber(rs.getLong("aadhar_number"));
		user.setEmail(rs.getString("email"));
		user.setPassword(rs.getString("hashed_password"));
		user.setCreatedDate(rs.getDate("created_date"));
		user.setState(rs.getString("state"));
		usersCache.put(user.getId(), user);
		
	}
	
	/**
	 * Identifies whether any user is present in the table with same email/aadhar
	 * @param user
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public boolean findDuplicateUser(User user, String query) throws SQLException, ClassNotFoundException {	
		getUsersCache(query);
		Iterator<Map.Entry<Long, User>>itr = usersCache.entrySet().iterator();
		boolean isPresent = false;
		while(itr.hasNext()) {
			Map.Entry<Long, User> entry = itr.next();
			if(entry.getValue().getEmail().equals(user.getEmail()) || entry.getValue().getAadharNumber() == user.getAadharNumber()) {
				isPresent = true;
			}
				
		}
		
		return isPresent;
		
		
	}
	

}
