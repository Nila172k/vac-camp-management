//$Id$
package com.management.camp.vaccination.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import com.management.camp.vaccination.connection.DBConnection;
import com.management.camp.vaccination.dto.Audit;
import com.management.camp.vaccination.utility.Utils;

public class AuditDao {
	
//	private final String INSERT_AUDIT_QUERY = "INSERT INTO audit (user_id, user_type, operation, "
//			+ "operation_desc, status) VALUES (?, ?, ?, ?, ?)";
	
	public void insertAudit(Audit audit, String query) throws ClassNotFoundException, SQLException {
		Connection conn = DBConnection.getConnection();
		PreparedStatement stmt = conn.prepareStatement(query);
		stmt.setLong(1, audit.getUserId() );
		stmt.setString(2, audit.getUserType());
		stmt.setString(3, audit.getOperation());
		stmt.setString(4, audit.getOperationDesc() );
		stmt.setDate(5, new java.sql.Date(System.currentTimeMillis()) );
		stmt.setTime(6, new java.sql.Time(System.currentTimeMillis()) );
		stmt.setString(7, audit.getStatus() );		
		stmt.executeUpdate();
	}

}
