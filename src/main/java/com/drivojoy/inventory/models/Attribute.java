package com.drivojoy.inventory.models;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;

/**
 * Item Attribute Master Entity
 * @author ashishsingh
 *
 */
@Entity
public class Attribute {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	@Column(unique=true)
	private String name;
	@OneToOne
	private UoM unit;
	@ElementCollection
	@JoinTable(name="attribute_possible_values", joinColumns=@JoinColumn(name="attribute_id"))
	private Collection<String> possibleValues = new ArrayList<String>();
	
	public Attribute(){ }

	/**
	 * 
	 * @param name Name of attribute
	 * @param unit Unit of measurement
	 * @param possibleValues All possible values an attribute can have
	 */
	public Attribute(String name, UoM unit, Collection<String> possibleValues) {
		super();
		this.name = name;
		this.unit = unit;
		this.possibleValues = possibleValues;
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

	public UoM getUnit() {
		return unit;
	}

	public void setUnit(UoM unit) {
		this.unit = unit;
	}

	public Collection<String> getPossibleValues() {
		return possibleValues;
	}

	public void setPossibleValues(Collection<String> possibleValues) {
		this.possibleValues = possibleValues;
	}

	@Override
	public String toString() {
		return "Attribute [id=" + id + ", name=" + name + ", unit=" + unit + ", possibleValues=" + possibleValues + "]";
	}

}
