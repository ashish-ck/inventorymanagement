package com.drivojoy.inventory.dto;

import java.util.ArrayList;
import java.util.List;

public class AttributeDTO {

	private String name;
	private String unit;
	private long id;
	private List<String> possibleValues = new ArrayList<String>();
	
	public AttributeDTO(){}
	
	public AttributeDTO(String name, String unit, List<String> possibleValues) {
		super();
		this.name = name;
		this.unit = unit;
		this.possibleValues = possibleValues;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public List<String> getPossibleValues() {
		return possibleValues;
	}
	public void setPossibleValues(List<String> possibleValues) {
		this.possibleValues = possibleValues;
	}
	@Override
	public String toString() {
		return "AttributeDTO [name=" + name + ", unit=" + unit + ", id=" + id + ", possibleValues=" + possibleValues
				+ "]";
	}

	
}
