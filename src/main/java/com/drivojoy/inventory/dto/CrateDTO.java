package com.drivojoy.inventory.dto;

public class CrateDTO {
	private long id;
	private String name;
	private String description;

	public CrateDTO() {
		super();
	}

	public CrateDTO(long id, String name, String description) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "CrateDTO [id=" + id + ", name=" + name + ", description=" + description + "]";
	}
}
