package com.drivojoy.inventory.dto;

public class VendorShortDTO {
	private long id;
	private String name;

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

	public VendorShortDTO(long id, String name) {
		this.id = id;
		this.name = name;
	}

	public VendorShortDTO() {
	}

	@Override
	public String toString() {
		return "VendorShortDTO [id=" + id + ", name=" + name + "]";
	}

}