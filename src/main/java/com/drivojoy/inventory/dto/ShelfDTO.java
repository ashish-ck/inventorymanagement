package com.drivojoy.inventory.dto;

import java.util.ArrayList;
import java.util.Collection;

public class ShelfDTO {
	private long id;
	private String name;
	private String description;
	private Collection<RackDTO> racks = new ArrayList<>();

	public ShelfDTO() {
		super();
	}

	public ShelfDTO(long id, String name, String description, Collection<RackDTO> racks) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.racks = racks;
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

	public Collection<RackDTO> getRacks() {
		return racks;
	}

	public void setRacks(Collection<RackDTO> racks) {
		this.racks = racks;
	}

	@Override
	public String toString() {
		return "ShelfDTO [id=" + id + ", name=" + name + ", description=" + description + ", racks=" + racks + "]";
	}
}
