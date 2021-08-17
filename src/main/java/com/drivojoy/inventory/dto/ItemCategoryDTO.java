package com.drivojoy.inventory.dto;

public class ItemCategoryDTO {

	private long id;
	private String name;
	private String description;
	private long parentCategory;
	private int version;
	
	ItemCategoryDTO(){}

	public ItemCategoryDTO(long id, String name, String description, long parentCategory, int version) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.parentCategory = parentCategory;
		this.version = version;
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

	public long getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(long parentCategory) {
		this.parentCategory = parentCategory;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "ItemCategoryDTO [id=" + id + ", name=" + name + ", description=" + description + ", parentCategory="
				+ parentCategory + ", version=" + version + "]";
	}
}
