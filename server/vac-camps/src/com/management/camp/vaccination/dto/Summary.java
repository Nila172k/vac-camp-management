//$Id$
package com.management.camp.vaccination.dto;

public class Summary {
	private long campId;
	private long vaccinatedCount;
	private long cityId;
	private String cityName;
	
	public long getCampId() {
		return campId;
	}
	public void setCampId(long campId) {
		this.campId = campId;
	}
	public long getVaccinatedCount() {
		return vaccinatedCount;
	}
	public void setVaccinatedCount(long vaccinatedCount) {
		this.vaccinatedCount = vaccinatedCount;
	}
	public long getCityId() {
		return cityId;
	}
	public void setCityId(long cityId) {
		this.cityId = cityId;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	
	@Override
	public String toString() {
		return "Summary [campId=" + campId + ", vaccinatedCount=" + vaccinatedCount + ", cityId=" + cityId + ", cityName=" + cityName + "]";
	}

}
