//$Id$
package com.management.camp.vaccination.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.management.camp.vaccination.connection.DBConnection;
import com.management.camp.vaccination.dto.Camp;

public class CampDao {

//	private final String INSERT_CAMP_QUERY = "INSERT INTO camps (city_id, address, stock, organized_by, created_date, state ) VALUES  (?, ?, ?, ?, ?, ?)";
//	private final String UPDATE_CAMP_STOCK_QUERY = "UPDATE camps SET stock = stock+? WHERE id = ? and state = 'active' ";
	private static Map<Long, Camp> campsCache = new HashMap<Long, Camp>();
	
	/**
	 * Creates a camping site for vaccination
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public int createCamp( Camp newCamp, String query) throws ClassNotFoundException, SQLException {
		Connection conn = DBConnection.getConnection();
		//Creates a camping site
		PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		stmt.setLong(1, newCamp.getOrganizedBy() );
		stmt.setLong(2, newCamp.getCityId());
		stmt.setString(3, newCamp.getAddress());
		stmt.setLong(4, newCamp.getStock());
		stmt.setDate(5, newCamp.getCreatedDate());
		stmt.setString(6, newCamp.getState());
		
		int result = stmt.executeUpdate();
		ResultSet rs = stmt.getGeneratedKeys();
		rs.next();
		newCamp.setId(rs.getLong(1));
		//newCamp.setState("active");
		return result;
		
	}
	
//	/**
//	 * Marks the camp as Inactive in campCache
//	 * @param cityId
//	 * @throws ClassNotFoundException
//	 * @throws SQLException
//	 */
//	public void updateCampAsInactive(long cityId) throws ClassNotFoundException, SQLException {
//		Map<Long, Camp> camps = getCampsCache();
//		Iterator<Map.Entry<Long, Camp>> itr = camps.entrySet().iterator(); 
//		while(itr.hasNext()) {
//			Map.Entry<Long, Camp> camp = itr.next();
//			if(camp.getValue().getCityId() == cityId && 
//					camp.getValue().getState().equals("Active")) {
//				camp.getValue().setState("Inactive");
//				break;
//			}
//		}
//		
//		
//	}
	
	/**
	 * Updates the newly created camp in campCache
	 * 
	 * @param camp
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void updateCampCacheByAddingNewCamp(Camp camp, String selectQuery) throws ClassNotFoundException, SQLException {
		getCampsCache(selectQuery);
		campsCache.put(camp.getId(), camp);
	}
	
	/**
	 * Reduce the stock by 1 in a camp
	 * 
	 * @param count     - Count to be reduced from the stock
	 * @param campId    - Camp Id
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public int reduceStockwhileSlotRegistration(long count, long campId, String selectQuery, String updateQuery) throws ClassNotFoundException, SQLException {
		getCampsCache(selectQuery);
		Connection conn = DBConnection.getConnection();
		PreparedStatement stmt = conn.prepareStatement(updateQuery);
		stmt.setLong(1, count);
		stmt.setLong(2, campId);
		int result = stmt.executeUpdate();
		Camp camp = campsCache.get(campId);
		camp.setStock( camp.getStock() + count );
		campsCache.put(campId, camp);
		return result;	
	}
	

	/**
	 * Updates the stock in a camp
	 * 
	 * @param count     - Count to be updated in a stock
	 * @param campId    - Camp Id
	 * @param adminId   - Admin Id
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public int updateStockInCity(long count, long campId, long orgId, String selectQuery, String updateQuery) throws ClassNotFoundException, SQLException {
		getCampsCache(selectQuery);
		Camp camp = campsCache.get(campId);
		if(camp.getOrganizedBy() == orgId) {
			Connection conn = DBConnection.getConnection();
			String tempUpdateQuery = updateQuery  + " and organized_by = ?";
			PreparedStatement stmt = conn.prepareStatement(tempUpdateQuery);
			stmt.setLong(1, count);
			stmt.setLong(2, campId);
			stmt.setLong(3, orgId);
			int result = stmt.executeUpdate();
			camp.setStock( camp.getStock() + count );
			campsCache.put(campId, camp);
			return result;
			
		}
		
		return 0;
		
	}
	

	/**
	 * Gets all camps from the table
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Map<Long, Camp> getCampsCache(String query) throws ClassNotFoundException, SQLException {
		if(campsCache == null || campsCache.isEmpty()) {
			Connection conn = DBConnection.getConnection();
			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs =  stmt.executeQuery();
			while(rs.next()) {
				Camp camp = getCampFromResultSet(rs);
				campsCache.put(camp.getId(), camp);
				
			}
			
		}
		return campsCache;
		
	}
	
	/**
	 * Gets Camp Object from the result set
	 * 
	 * @param rs   - Result Set
	 * @return
	 * @throws SQLException
	 */
	public Camp getCampFromResultSet(ResultSet rs) throws SQLException {
		Camp camp = new Camp();
		camp.setId(rs.getLong("id"));
		camp.setOrganizedBy(rs.getLong("organized_by"));
		camp.setCityId(rs.getLong("city_id"));
		camp.setAddress(rs.getString("address"));
		camp.setStock(rs.getLong("stock"));
		camp.setCreatedDate(rs.getDate("created_date"));
		camp.setState(rs.getString("state"));
		return camp;
	}
	
//	/**
//	 * Fetches the camping site by city Id
//	 * 
//	 * @param cityId - City Id
//	 * @return
//	 * @throws SQLException 
//	 * @throws ClassNotFoundException 
//	 */
//	public Camp getCampByCityId(long cityId) throws ClassNotFoundException, SQLException {
//		getCampsCache();
//		Iterator<Map.Entry<Long, Camp>> itr = campsCache.entrySet().iterator();
//		while(itr.hasNext()) {
//			Map.Entry<Long, Camp> camp = itr.next();
//			if(camp.getValue().getCityId() == cityId) 
//				return camp.getValue();
//		}
//		
//		return null;
//		
//	}
//	
	/**
	 * Fetches the camping site by orgId
	 * 
	 * @param orgId - Organization Id
	 * @return
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public List<Camp> getCampByOrgId(long cityId, String query) throws ClassNotFoundException, SQLException {
		List<Camp> campsByOrg = new ArrayList<Camp>();
		getCampsCache(query);
		Iterator<Map.Entry<Long, Camp>> itr = campsCache.entrySet().iterator();
		while(itr.hasNext()) {
			Map.Entry<Long, Camp> camp = itr.next();
			if(camp.getValue().getOrganizedBy() == cityId) {
				campsByOrg.add(camp.getValue());
			}	
		}
		
		return campsByOrg;
		
	}
	
	
	

}
