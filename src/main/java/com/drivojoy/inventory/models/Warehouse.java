package com.drivojoy.inventory.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Warehouse {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@Column(unique = true, nullable = false)
	private String name;

	/*
	 * This field is supposed to have same values as the workshop code in the
	 * dashboard
	 */

	@Column(unique = true, nullable = false)
	private String code;
	@Embedded
	private Address address;
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModDttm;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDttm;
	private int version;
	@OneToOne
	private Warehouse motherWarehouse;
	@ElementCollection
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "warehouse_shelves", joinColumns = @JoinColumn(name = "warehouse_id"))
	private Collection<Shelf> shelves = new ArrayList<>();
	private boolean isParentWarehouse;

	@PrePersist
	protected void onCreate() {
		createdDttm = Calendar.getInstance().getTime();
		lastModDttm = Calendar.getInstance().getTime();
		version = 1;
	}

	@PreUpdate
	protected void onUpdate() {
		lastModDttm = Calendar.getInstance().getTime();
		version++;
	}

	public Warehouse() {
	};

	public Warehouse(String name, Address address, Collection<ItemCount> items, int version, String code,
			Warehouse warehouse, boolean isParentWarehouse, Collection<Shelf> shelves) {
		super();
		this.name = name;
		this.address = address;
		this.version = version;
		this.code = code;
		this.motherWarehouse = warehouse;
		this.isParentWarehouse = isParentWarehouse;
		this.shelves = shelves;
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

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "Warehouse [id=" + id + ", name=" + name + ", code=" + code + ", address=" + address + ", lastModDttm="
				+ lastModDttm + ", createdDttm=" + createdDttm + ", version=" + version + ", motherWarehouse="
				+ motherWarehouse + ", isParentWarehouse=" + isParentWarehouse + "]";
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getLastModDttm() {
		return lastModDttm;
	}

	public void setLastModDttm(Date lastModDttm) {
		this.lastModDttm = lastModDttm;
	}

	public Date getCreatedDttm() {
		return createdDttm;
	}

	public void setCreatedDttm(Date createdDttm) {
		this.createdDttm = createdDttm;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Warehouse getMotherWarehouse() {
		return motherWarehouse;
	}

	public void setMotherWarehouse(Warehouse motherWarehouse) {
		this.motherWarehouse = motherWarehouse;
	}

	public boolean isParentWarehouse() {
		return isParentWarehouse;
	}

	public void setParentWarehouse(boolean isParentWarehouse) {
		this.isParentWarehouse = isParentWarehouse;
	}

	public Collection<Shelf> getShelves() {
		return shelves;
	}

	public void setShelves(Collection<Shelf> shelves) {
		this.shelves = shelves;
	}

	@Override
	public boolean equals(Object obj) {
		Warehouse warehouse = (Warehouse) obj;
		return this.name.equalsIgnoreCase(warehouse.getName());
	}
}
