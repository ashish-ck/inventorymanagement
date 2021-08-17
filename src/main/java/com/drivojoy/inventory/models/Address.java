package com.drivojoy.inventory.models;

import javax.persistence.Embeddable;

/**
 * Address object that can be embedded in other entities 
 * @author ashishsingh
 *
 */
@Embeddable
public class Address {

	private String addressLine1;
	private String addressLine2;
	private String city;
	private String location;
	private String state;
	private String country;
	private String pincode;
	
	public Address(){};
	
	/**
	 * 
	 * @param addressLine1 Address line 1
	 * @param addressLine2 Address line 2
	 * @param city City
	 * @param location Location 
	 * @param state State
	 * @param country Country
	 * @param pincode Pincode
	 */
	public Address(String addressLine1, String addressLine2, String city, String location, String state, String country,
			String pincode) {
		super();
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.city = city;
		this.location = location;
		this.state = state;
		this.country = country;
		this.pincode = pincode;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	@Override
	public String toString() {
		return "Address [addressLine1=" + addressLine1 + ", addressLine2=" + addressLine2 + ", city=" + city
				+ ", location=" + location + ", state=" + state + ", country=" + country + ", pincode=" + pincode + "]";
	}
	
	
}
