package com.drivojoy.inventory.dto;

import java.util.ArrayList;
import java.util.Collection;

public class ItemAttributeDTO {

	private long id;
	private AttributeDTO attribute;
	private Collection<String> value = new ArrayList<>();
	private int version;
	
	public ItemAttributeDTO() {}

	public ItemAttributeDTO(long id, AttributeDTO attribute, Collection<String> value, int version) {
		super();
		this.id = id;
		this.attribute = attribute;
		this.value = value;
		this.version = version;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public AttributeDTO getAttribute() {
		return attribute;
	}

	public void setAttribute(AttributeDTO attribute) {
		this.attribute = attribute;
	}

	public Collection<String> getValue() {
		return value;
	}

	public void setValue(Collection<String> value) {
		this.value = value;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "ItemAttributeDTO [id=" + id + ", attribute=" + attribute + ", value=" + value + "]";
	}

}
