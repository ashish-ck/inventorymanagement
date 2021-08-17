package com.drivojoy.inventory.models;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

@Entity
public class Shelf {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String name;
	private String description;
	@ElementCollection
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinTable(name="shelf_racks", joinColumns=@JoinColumn(name="shelf_id"))
	private Collection<Rack> racks = new ArrayList<>();

	public Shelf() {
		super();
	}

	public Shelf(long id, String name, String description, Collection<Rack> racks) {
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

	public Collection<Rack> getRacks() {
		return racks;
	}

	public void setRacks(Collection<Rack> racks) {
		this.racks = racks;
	}

	@Override
	public String toString() {
		return "Shelf [id=" + id + ", name=" + name + ", description=" + description + ", racks=" + racks + "]";
	}
}
