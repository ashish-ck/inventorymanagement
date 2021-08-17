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

/**
 * @author Akshay
 *
 */
@Entity
public class Rack {
	/**
	 * 
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	/**
	 * 
	 */
	private String name;
	/**
	 * 
	 */
	private String description;
	/**
	 * 
	 */
	@ElementCollection
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "rack_crates", joinColumns = @JoinColumn(name = "rack_id"))
	private Collection<Crate> crates = new ArrayList<>();

	/**
	 * 
	 */
	public Rack() {
		super();
	}

	/**
	 * @param id Id
	 * @param name Name
	 * @param description Description
	 * @param crates Collection of crates
	 */
	public Rack(long id, String name, String description, Collection<Crate> crates) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.crates = crates;
	}

	/**
	 * @return Id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id Id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name Name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description Description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return List of crates
	 */
	public Collection<Crate> getCrates() {
		return crates;
	}

	/**
	 * @param crates Collection of crate
	 */
	public void setCrates(Collection<Crate> crates) {
		this.crates = crates;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Rack [id=" + id + ", name=" + name + ", description=" + description + ", crates=" + crates + "]";
	}
}
