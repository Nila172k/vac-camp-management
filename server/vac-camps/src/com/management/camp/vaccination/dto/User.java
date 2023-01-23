//$Id$
package com.management.camp.vaccination.dto;

import java.sql.Date;

public class User {
	
	private long id;
	private String firsName;
	private String lastName;
	private String gender;
	private Date dob;
	private String phoneNumber;
	private long aadharNumber;
	private String email;
	private String password;
	private Date createdDate;
	private String state;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getFirsName() {
		return firsName;
	}
	public void setFirsName(String firsName) {
		this.firsName = firsName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public long getAadharNumber() {
		return aadharNumber;
	}
	public void setAadharNumber(long aadharNumber) {
		this.aadharNumber = aadharNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	@Override
	public String toString() {
		return "User [id=" + id + ", firsName=" + firsName + ", lastName=" +
				lastName + ", gender=" + gender + ", dob=" + dob + ", phoneNumber=" + 
				phoneNumber + ", aadharNumber=" + aadharNumber + ", email=" + email +
				", password=" + password + ", createdDate=" + createdDate + ", state=" + state + "]";
	}
	
}
	
	