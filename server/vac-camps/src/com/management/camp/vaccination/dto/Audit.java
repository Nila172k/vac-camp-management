//$Id$
package com.management.camp.vaccination.dto;

import java.sql.Date;
import java.sql.Time;

public class Audit {
	private int id;
	private long userId;
	private String userType;
	private String operation;
	private String operationDesc;
	private Date createdDate;
	private Time createdTime;
	private String status;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getOperationDesc() {
		return operationDesc;
	}
	public void setOpeartionDesc(String operationDesc) {
		this.operationDesc = operationDesc;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Time getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Time createdTime) {
		this.createdTime = createdTime;
	}
	public String getStatus() {
		return status;
	}
	public void setState(String status) {
		this.status = status;
	}

}
