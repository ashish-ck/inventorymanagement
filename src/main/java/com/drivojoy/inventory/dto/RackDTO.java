package com.drivojoy.inventory.dto;

import java.util.ArrayList;
import java.util.Collection;

public class RackDTO {
	private long id;
	private String name;
	private String description;
	private Collection<CrateDTO> crates = new ArrayList<>();

	public RackDTO() {
		super();
	}

	public RackDTO(long id, String name, String description, Collection<CrateDTO> crates) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.crates = crates;
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

	public Collection<CrateDTO> getCrates() {
		return crates;
	}

	public void setCrates(Collection<CrateDTO> crates) {
		this.crates = crates;
	}

	@Override
	public String toString() {
		return "Rack [id=" + id + ", name=" + name + ", description=" + description + ", crates=" + crates + "]";
	}
}
