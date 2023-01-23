//$Id$
package com.management.camp.vaccination.dto;

import java.sql.Date;

public class Camp {
	
	private long id;
	private long cityId;
	private String address;
	private long stock;
	private long organizedBy;
	private Date createdDate;
	private String state;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getCityId() {
		return cityId;
	}
	public void setCityId(long cityId) {
		this.cityId = cityId;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public long getStock() {
		return stock;
	}
	public void setStock(long stock) {
		this.stock = stock;
	}
	public long getOrganizedBy() {
		return organizedBy;
	}
	public void setOrganizedBy(long getOrganizedBy) {
		this.organizedBy = getOrganizedBy;
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
		return "Camp [id=" + id + ", cityId=" + cityId + ", address=" + address + ", stock=" + stock + ", organizedBy=" + organizedBy + ", createdDate=" + createdDate + ", state=" + state + "]";
	}
	

}
