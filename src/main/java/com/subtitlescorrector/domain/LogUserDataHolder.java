package com.subtitlescorrector.domain;

import java.io.Serializable;
import java.util.Objects;

import org.slf4j.MDC;

public class LogUserDataHolder implements Serializable{
	
	private static final long serialVersionUID = -3749858856634387612L;
	
	private String ip;
	private String city;
	private String isp;
	private String country;
	private String countryIso;
	private String postalCode;
	private String subdivisionIso;
	private String timeZone;
	private float cityGeonameId;
	private float countryGeonameId;
	private float subdivisionGeonameId;
	private float ispId;
	private float latitude;
	private float longitude;
	private String fingerprint;
	private String session;
	private float fraud;
	private boolean tor;

    public void putAllIntoMDC() {
        MDC.put("ip", ip);
        MDC.put("city", city);
        MDC.put("isp", isp);
        MDC.put("country", country);
        MDC.put("countryIso", countryIso);
        MDC.put("postalCode", postalCode);
        MDC.put("subdivisionIso", subdivisionIso);
        MDC.put("timeZone", timeZone);
        MDC.put("cityGeonameId", String.valueOf(cityGeonameId));
        MDC.put("countryGeonameId", String.valueOf(countryGeonameId));
        MDC.put("subdivisionGeonameId", String.valueOf(subdivisionGeonameId));
        MDC.put("ispId", String.valueOf(ispId));
        MDC.put("latitude", String.valueOf(latitude));
        MDC.put("longitude", String.valueOf(longitude));
        MDC.put("fingerprint", fingerprint);
        MDC.put("session", session);
        MDC.put("fraud", String.valueOf(fraud));
        MDC.put("tor", String.valueOf(tor));
    }
    
    public void clearFromMDC() {
        MDC.remove("ip");
        MDC.remove("city");
        MDC.remove("isp");
        MDC.remove("country");
        MDC.remove("countryIso");
        MDC.remove("postalCode");
        MDC.remove("subdivisionIso");
        MDC.remove("timeZone");
        MDC.remove("cityGeonameId");
        MDC.remove("countryGeonameId");
        MDC.remove("subdivisionGeonameId");
        MDC.remove("ispId");
        MDC.remove("latitude");
        MDC.remove("longitude");
        MDC.remove("fingerprint");
        MDC.remove("session");
        MDC.remove("fraud");
        MDC.remove("tor");
    }
	
	public String getIp() {
		return ip;
	}

	public String getCity() {
		return city;
	}

	public String getIsp() {
		return isp;
	}

	public String getCountry() {
		return country;
	}

	public String getCountryIso() {
		return countryIso;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public String getSubdivisionIso() {
		return subdivisionIso;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public float getCityGeonameId() {
		return cityGeonameId;
	}

	public float getCountryGeonameId() {
		return countryGeonameId;
	}

	public float getSubdivisionGeonameId() {
		return subdivisionGeonameId;
	}

	public float getIspId() {
		return ispId;
	}

	public float getLatitude() {
		return latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public String getFingerprint() {
		return fingerprint;
	}

	public String getSession() {
		return session;
	}

	public float getFraud() {
		return fraud;
	}

	public boolean getTor() {
		return tor;
	}

	// Setter Methods

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setIsp(String isp) {
		this.isp = isp;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setCountryIso(String countryIso) {
		this.countryIso = countryIso;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public void setSubdivisionIso(String subdivisionIso) {
		this.subdivisionIso = subdivisionIso;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public void setCityGeonameId(float cityGeonameId) {
		this.cityGeonameId = cityGeonameId;
	}

	public void setCountryGeonameId(float countryGeonameId) {
		this.countryGeonameId = countryGeonameId;
	}

	public void setSubdivisionGeonameId(float subdivisionGeonameId) {
		this.subdivisionGeonameId = subdivisionGeonameId;
	}

	public void setIspId(float ispId) {
		this.ispId = ispId;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public void setFraud(float fraud) {
		this.fraud = fraud;
	}

	public void setTor(boolean tor) {
		this.tor = tor;
	}

	@Override
	public int hashCode() {
		return Objects.hash(city, cityGeonameId, country, countryGeonameId, countryIso, fingerprint, fraud, ip, isp,
				ispId, latitude, longitude, postalCode, session, subdivisionGeonameId, subdivisionIso, timeZone, tor);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogUserDataHolder other = (LogUserDataHolder) obj;
		return Objects.equals(city, other.city)
				&& Float.floatToIntBits(cityGeonameId) == Float.floatToIntBits(other.cityGeonameId)
				&& Objects.equals(country, other.country)
				&& Float.floatToIntBits(countryGeonameId) == Float.floatToIntBits(other.countryGeonameId)
				&& Objects.equals(countryIso, other.countryIso) && Objects.equals(fingerprint, other.fingerprint)
				&& Float.floatToIntBits(fraud) == Float.floatToIntBits(other.fraud) && Objects.equals(ip, other.ip)
				&& Objects.equals(isp, other.isp) && Float.floatToIntBits(ispId) == Float.floatToIntBits(other.ispId)
				&& Float.floatToIntBits(latitude) == Float.floatToIntBits(other.latitude)
				&& Float.floatToIntBits(longitude) == Float.floatToIntBits(other.longitude)
				&& Objects.equals(postalCode, other.postalCode) && Objects.equals(session, other.session)
				&& Float.floatToIntBits(subdivisionGeonameId) == Float.floatToIntBits(other.subdivisionGeonameId)
				&& Objects.equals(subdivisionIso, other.subdivisionIso) && Objects.equals(timeZone, other.timeZone)
				&& tor == other.tor;
	}

	@Override
	public String toString() {
		return "LogUserDataHolder [ip=" + ip + ", city=" + city + ", isp=" + isp + ", country=" + country
				+ ", countryIso=" + countryIso + ", postalCode=" + postalCode + ", subdivisionIso=" + subdivisionIso
				+ ", timeZone=" + timeZone + ", cityGeonameId=" + cityGeonameId + ", countryGeonameId="
				+ countryGeonameId + ", subdivisionGeonameId=" + subdivisionGeonameId + ", ispId=" + ispId
				+ ", latitude=" + latitude + ", longitude=" + longitude + ", fingerprint=" + fingerprint + ", session="
				+ session + ", fraud=" + fraud + ", tor=" + tor + "]";
	}
	
	

}
