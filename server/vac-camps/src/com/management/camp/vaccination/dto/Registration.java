//$Id$
package com.management.camp.vaccination.dto;

import java.sql.Date;

public class Registration {
	private long id;
	private long userId;
	private Date dateOfVaccination;
	private long choosenCampId ;
	private int dosageCount;
	private int choosenSlotId;
	private String status;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Date getDateOfVaccination() {
		return dateOfVaccination;
	}
	public void setDateOfVaccination(Date dateOfVaccination) {
		this.dateOfVaccination = dateOfVaccination;
	}
	public int getChoosenSlotId() {
		return choosenSlotId;
	}
	public void setChoosenSlotId(int choosenSlotId) {
		this.choosenSlotId = choosenSlotId;
	}
	public int getDosageCount() {
		return dosageCount;
	}
	public void setDosageCount(int dosageCount) {
		this.dosageCount = dosageCount;
	}
	public long getChoosenCampId() {
		return choosenCampId;
	}
	public void setChoosenCampId(long choosenCampId) {
		this.choosenCampId = choosenCampId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return "Registration [id=" + id + ", userId=" + userId + ", dateOfVaccination=" + dateOfVaccination + ", choosenCampId=" + choosenCampId + ", dosageCount=" + dosageCount + ", choosenSlotId=" + choosenSlotId + ", status=" + status + "]";
	}
	
}
