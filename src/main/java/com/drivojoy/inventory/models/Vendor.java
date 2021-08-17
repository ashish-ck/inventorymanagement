package com.drivojoy.inventory.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

@Entity
public class Vendor {

	@Id
	@GeneratedValue
	private long id;
	@Column(unique=true, nullable=false)
	private String name;
	private Address outletAddress;
	private String contactNo;
	private String contactPerson;
	@ManyToMany(cascade=CascadeType.DETACH, fetch=FetchType.LAZY)
	private Collection<Item> itemsSold = new ArrayList<Item>();
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModDttm;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDttm;
	@Version
	private int version;
	private Boolean isOrganised;
	
	@PrePersist
	protected void onCreate() {
		createdDttm = Calendar.getInstance().getTime();
		lastModDttm = Calendar.getInstance().getTime();
		version = 1;
	}

	@PreUpdate
	protected void onUpdate() {
		lastModDttm = Calendar.getInstance().getTime();
		version ++;
	}
	public Vendor (){}
	
	/**
	 * 
	 * @param id Vendor Id
	 * @param name Vendor name
	 * @param outletAddress Vendor address
	 * @param contactNo Vendor contact number
	 * @param contactPerson Vendor contact person
	 * @param itemsSold Items sold by the vendor
	 * @param version Row version
	 * @param isOrganised Is vendor organised
	 */
	public Vendor(long id, String name, Address outletAddress, String contactNo, String contactPerson,
			Collection<Item> itemsSold, int version, Boolean isOrganised) {
		super();
		this.id = id;
		this.name = name;
		this.outletAddress = outletAddress;
		this.contactNo = contactNo;
		this.contactPerson = contactPerson;
		this.itemsSold = itemsSold;
		this.version = version;
		this.isOrganised = isOrganised;
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

	public Address getOutletAddress() {
		return outletAddress;
	}

	public void setOutletAddress(Address outletAddress) {
		this.outletAddress = outletAddress;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public Collection<Item> getItemsSold() {
		return itemsSold;
	}

	public void setItemsSold(Collection<Item> itemsSold) {
		this.itemsSold = itemsSold;
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

	public Boolean getIsOrganised() {
		return isOrganised;
	}

	public void setIsOrganised(Boolean isOrganised) {
		this.isOrganised = isOrganised;
	}

	@Override
	public String toString() {
		return "Vendor [id=" + id + ", name=" + name + ", outletAddress=" + outletAddress + ", contactNo=" + contactNo
				+ ", contactPerson=" + contactPerson + ", itemsSold=" + itemsSold + ", isOrganised= " + isOrganised + "]";
	}
	
}
