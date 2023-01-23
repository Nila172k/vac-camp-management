//$Id$
package com.management.camp.vaccination.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.management.camp.vaccination.dao.CampDao;
import com.management.camp.vaccination.dao.CityDao;
import com.management.camp.vaccination.dto.Camp;
import com.management.camp.vaccination.dto.City;
import com.management.camp.vaccination.query.DynamicQuery;

public class UtilService {
	
	private DynamicQuery dynamicQuery = new DynamicQuery();
	private CityDao cityDao = new CityDao();
	private CampDao campDao = new CampDao();
	
	
	public JSONArray getCampsByCity(Properties properties) throws Exception {
		JSONObject rootObject = new JSONObject();
		String campTable = properties.getProperty("com.camp.table.name");
		String cityTable = properties.getProperty("com.city.table.name");
		String campSelectQuery = dynamicQuery.getSelectQuery(campTable, null) ;
		String citySelectQuery = dynamicQuery.getSelectQuery(cityTable, null) ;
		JSONArray rootArray = new JSONArray();
		Map<Long, City> cities = cityDao.getCitiesCache(citySelectQuery);
		Map<Long, Camp> camps = campDao.getCampsCache(campSelectQuery);
		Iterator<Map.Entry<Long, City>> citiesItr =  cities.entrySet().iterator();
		while(citiesItr.hasNext()) {
			Map.Entry<Long, City> cityEntry = citiesItr.next();
			JSONObject jObject = new JSONObject();
			jObject.put("id", cityEntry.getKey());
			jObject.put("name", cityEntry.getValue().getCityName());
			Iterator<Map.Entry<Long, Camp>> campsItr = camps.entrySet().iterator();
			JSONArray campsArray = new JSONArray();
			while(campsItr.hasNext()) {
				Map.Entry<Long, Camp> campEntry = campsItr.next();
				if(campEntry.getValue().getCityId() == cityEntry.getKey()  && campEntry.getValue().getState().equals("active")) {
					JSONObject campJsonObject = new JSONObject();
					campJsonObject.put("id", campEntry.getKey());
					campJsonObject.put("cityId", campEntry.getValue().getCityId());
					campJsonObject.put("address", campEntry.getValue().getAddress());
					campJsonObject.put("organizedBy", campEntry.getValue().getOrganizedBy());
					campJsonObject.put("stock", campEntry.getValue().getStock());
					campsArray.add(campJsonObject);
				}
				
			}
			jObject.put("camps", campsArray.toJSONString());
			rootArray.add(jObject);
		}
		return rootArray;
		
	}
	
//	public JSONArray getCities() throws Exception {
//		JSONArray jAray = new JSONArray();
//		Map<Long, City> cities = cityDao.getCitiesCache();
//		Iterator<Map.Entry<Long, City>> citiesItr =  cities.entrySet().iterator();
//		while(citiesItr.hasNext()) {
//			Map.Entry<Long, City> cityEntry = citiesItr.next();
//			JSONObject jObject = new JSONObject();
//			jObject.put("id", cityEntry.getValue().getCityId());
//			jObject.put("name", cityEntry.getValue().getCityName());
//			jObject.put("createdDate", String.valueOf(cityEntry.getValue().getCreatedDate()));
//			jAray.add(jObject);
//			//return jAray;
//		}
//		return jAray;	
//	}
	
	public JSONArray getCampDetailsByCity(Long cityId, Properties properties) throws Exception {
		
		JSONArray jArray = new JSONArray();
		String campTable = properties.getProperty("com.camp.table.name");
		String campSelectQuery = dynamicQuery.getSelectQuery(campTable, null);
		Map<Long, Camp> camps = campDao.getCampsCache(campSelectQuery);
		Iterator<Map.Entry<Long, Camp>> campsItr = camps.entrySet().iterator();
		while(campsItr.hasNext()) {
			Map.Entry<Long, Camp> campEntry = campsItr.next();
			if(campEntry.getValue().getCityId() == cityId  && campEntry.getValue().getState().equals("active")) {
				JSONObject jObject = new JSONObject();
				jObject.put("id", campEntry.getKey());
				jObject.put("cityId", campEntry.getValue().getCityId());
				jObject.put("address", campEntry.getValue().getAddress());
				jObject.put("stock", campEntry.getValue().getStock());
				jArray.add(jObject);
			}
			
		}
		return jArray;
		
	}

}
