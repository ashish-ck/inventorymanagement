package com.drivojoy.inventory.dto;

import java.util.Collection;

public class WarehouseDTO {

	private long id;
	private String name;

	private String addressLine1;
	private String addressLine2;
	private String city;
	private String location;
	private String state;
	private String country;
	private String pincode;
	private String code;
	private int version;
	private String motherWarehouseCode;
	private boolean isMotherWarehouse;
	private Collection<ShelfDTO> shelves;

	public WarehouseDTO() {
		super();
	}

	public WarehouseDTO(long id, String name, String addressLine1, String addressLine2, String city, String location,
			String state, String country, String pincode, String code, int version, String motherWarehouse,
			boolean isMotherWarehouse, Collection<ShelfDTO> shelves) {
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
		this.code = code;
		this.version = version;
		this.motherWarehouseCode = motherWarehouse;
		this.isMotherWarehouse = isMotherWarehouse;
		this.shelves = shelves;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getMotherWarehouseCode() {
		return motherWarehouseCode;
	}

	public void setMotherWarehouseCode(String motherWarehouse) {
		this.motherWarehouseCode = motherWarehouse;
	}

	public boolean isMotherWarehouse() {
		return isMotherWarehouse;
	}

	public void setMotherWarehouse(boolean isMotherWarehouse) {
		this.isMotherWarehouse = isMotherWarehouse;
	}

	public Collection<ShelfDTO> getShelves() {
		return shelves;
	}

	public void setShelves(Collection<ShelfDTO> shelves) {
		this.shelves = shelves;
	}

	@Override
	public String toString() {
		return "WarehouseDTO [id=" + id + ", name=" + name + ", addressLine1=" + addressLine1 + ", addressLine2="
				+ addressLine2 + ", city=" + city + ", location=" + location + ", state=" + state + ", country="
				+ country + ", pincode=" + pincode + ", code=" + code + ", version=" + version + ", motherWarehouse="
				+ motherWarehouseCode + ", isMotherWarehouse=" + isMotherWarehouse + "]";
	};

}
