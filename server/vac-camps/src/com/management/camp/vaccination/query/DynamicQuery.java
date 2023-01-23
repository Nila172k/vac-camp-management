//$Id$
package com.management.camp.vaccination.query;

public class DynamicQuery {
	
	public final String SELECT_QUERY = "SELECT * FROM ";
	public final String INSERT_QUERY = "INSERT INTO" ;
	public final String UPDATE_QUERY = "UPDATE ";
	
	public String getSelectQuery(String tableName, String filter) {
		
		String requiredSelectQuery = SELECT_QUERY + tableName ;
		if(filter != null) 
			requiredSelectQuery = requiredSelectQuery +" "+ filter;
		
		return requiredSelectQuery;
	}
	
	public String getInsertQuery(String tableName, String fieldsString) {
		String[] fields = fieldsString.split(",");
		String requiredInsertQuery = INSERT_QUERY + " " + tableName ;
		String tempOne = null;
		String tempTwo = null;
		tempOne = "( ";
		tempTwo = "(";
		for(String field :  fields) {
			if(!field.equals("id") ) {
				tempOne = tempOne + field + ",";
				tempTwo = tempTwo + "?,";
			}
			
		}
		tempOne = tempOne.substring(0, tempOne.length()-1) + ")";
		tempTwo = tempTwo.substring(0, tempTwo.length()-1) + ")";
		
		requiredInsertQuery = requiredInsertQuery + " " + tempOne + " VALUES " + tempTwo;
		return requiredInsertQuery;
		
	}
	
	public String getUpdateQuery(String tabelName, String updateFilter ) {
		
		String requiredUpdateQuery = UPDATE_QUERY + " " + tabelName + " " + updateFilter;
		return requiredUpdateQuery;
		
	}

}
