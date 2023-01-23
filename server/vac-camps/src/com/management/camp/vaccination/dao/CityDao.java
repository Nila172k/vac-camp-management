//$Id$
package com.management.camp.vaccination.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.management.camp.vaccination.connection.DBConnection;
import com.management.camp.vaccination.dto.City;

public class CityDao {
	//private final String SELECT_CITIES_QUERY = "SELECT * FROM cities";
	private static Map<Long, City> citiesCache = new HashMap<Long, City>();
	
	/**
	 * Fetches the cities from the table
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Map<Long, City>  getCitiesCache(String query) throws ClassNotFoundException, SQLException {
		if(citiesCache == null || citiesCache.isEmpty()) {
			Connection conn = DBConnection.getConnection();
			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				City city = new City();
				city.setCityId(rs.getLong("id"));
				city.setCityName(rs.getString("name"));
				city.setState(rs.getString("state"));
				city.setCreatedDate(rs.getDate("created_date"));
				citiesCache.put(city.getCityId(), city);
			}
		}
		
		return citiesCache;
	}

}
