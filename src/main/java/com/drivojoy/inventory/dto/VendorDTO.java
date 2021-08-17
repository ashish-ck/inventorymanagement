package com.drivojoy.inventory.dto;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.ManyToMany;

public class VendorDTO {

	private long id;
	private String name;

	private String addressLine1;
	private String addressLine2;
	private String city;
	private String location;
	private String state;
	private String country;
	private String pincode;
	private int version;
	private String contactNo;
	private String contactPerson;
	@ManyToMany
	private Collection<ItemDTO> itemsSold = new ArrayList<ItemDTO>();
	private Boolean isOrganised;

	public VendorDTO() {

	}

	public VendorDTO(long id, String name, String addressLine1, String addressLine2, String city, String location,
			String state, String country, String pincode, String contactNo, String contactPerson,
			Collection<ItemDTO> itemsSold, int version, Boolean isOrganised) {
		super();
		this.id = id;
		this.name = name;
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.city = city;
		this.location = location;
		this.state = state;
		this.country = country;
		this.pincode = pincode;
		this.contactNo = contactNo;
		this.contactPerson = contactPerson;
		this.itemsSold = itemsSold;
		this.version = version;
		this.isOrganised = isOrganised;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public Collection<ItemDTO> getItemsSold() {
		return itemsSold;
	}

	public void setItemsSold(Collection<ItemDTO> itemsSold) {
		this.itemsSold = itemsSold;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Boolean getIsOrganised() {
		return isOrganised;
	}

	public void setIsOrganised(Boolean isOrganised) {
		this.isOrganised = isOrganised;
	}

	@Override
	public String toString() {
		return "VendorDTO [id=" + id + ", name=" + name + ", addressLine1=" + addressLine1 + ", addressLine2="
				+ addressLine2 + ", city=" + city + ", location=" + location + ", state=" + state + ", country="
				+ country + ", pincode=" + pincode + ", contactNo=" + contactNo + ", contactPerson=" + contactPerson
				+ ", itemsSold=" + itemsSold + ", isOrganised=" + isOrganised + "]";
	}

}
