//$Id$
package com.management.camp.vaccination.dto;

import java.sql.Date;

public class Organization {
	
	private int id;
	private String name;
	private String userName;
	private String password;
	private Date createdDate;
	private String status;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPasword() {
		return password;
	}
	public void setPasword(String password) {
		this.password = password;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return "Organization [id=" + id + ", name=" + name + ", "
				+ "userName=" + userName + ", password=" + password + ", "
						+ "createdDate=" + createdDate + ", status=" + status + "]";
	}

}
