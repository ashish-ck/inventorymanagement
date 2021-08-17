package com.drivojoy.inventory.dto;

public class ItemNetworkDTO {

	private long id;
	private String code;
	private String description;
	private String vendorAlias;

	public ItemNetworkDTO() {
	}

	public ItemNetworkDTO(long id, String code, String description, String vendorAlias) {
		super();
		this.id = id;
		this.code = code;
		this.description = description;
		this.vendorAlias = vendorAlias;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVendorAlias() {
		return vendorAlias;
	}

	public void setVendorAlias(String vendorAlias) {
		this.vendorAlias = vendorAlias;
	}

	@Override
	public String toString() {
		return "ItemNetworkDTO [id = " + id + ", code=" + code + ", description=" + description + ", vendorAlias="
				+ vendorAlias + "]";
	}
}
