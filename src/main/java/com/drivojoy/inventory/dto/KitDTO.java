package com.drivojoy.inventory.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class KitDTO {

	private long id;
	private String kitNumber;
	private Collection<SalesOrderLineItemDTO> items = new ArrayList<>();
	private Collection<SubkitDTO> subkits = new ArrayList<>();
	private Date lastModDttm;
	private Date createdDttm;
	private Date date;
	private int status;
	private int kitReopenTimes;
	private int version;
	private WarehouseDTO assignedWarehouse;
	private WarehouseDTO requestWarehouse;
	
	public KitDTO() {}
	
	public KitDTO(long id, String kitNumber, Collection<SalesOrderLineItemDTO> items, Collection<SubkitDTO> subkits, Date lastModDttm,
			Date createdDttm, Date date, int status, int kitReopenTimes, int version, WarehouseDTO assignedWarehouse,
			WarehouseDTO requestWarehouse) {
		super();
		this.id = id;
		this.kitNumber = kitNumber;
		this.items = items;
		this.subkits = subkits;
		this.lastModDttm = lastModDttm;
		this.createdDttm = createdDttm;
		this.date = date;
		this.status = status;
		this.kitReopenTimes = kitReopenTimes;
		this.version = version;
		this.assignedWarehouse = assignedWarehouse;
		this.requestWarehouse = requestWarehouse;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getKitNumber() {
		return kitNumber;
	}

	public void setKitNumber(String kitNumber) {
		this.kitNumber = kitNumber;
	}

	public Collection<SalesOrderLineItemDTO> getItems() {
		return items;
	}

	public void setItems(Collection<SalesOrderLineItemDTO> items) {
		this.items = items;
	}

	public Collection<SubkitDTO> getSubkits() {
		return subkits;
	}

	public void setSubkits(Collection<SubkitDTO> subkits) {
		this.subkits = subkits;
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public WarehouseDTO getAssignedWarehouse() {
		return assignedWarehouse;
	}

	public void setAssignedWarehouse(WarehouseDTO assignedWarehouse) {
		this.assignedWarehouse = assignedWarehouse;
	}

	public WarehouseDTO getRequestWarehouse() {
		return requestWarehouse;
	}

	public void setRequestWarehouse(WarehouseDTO requestWarehouse) {
		this.requestWarehouse = requestWarehouse;
	}

	public int getKitReopenTimes() {
		return kitReopenTimes;
	}

	public void setKitReopenTimes(int kitReopenTimes) {
		this.kitReopenTimes = kitReopenTimes;
	}

	@Override
	public String toString() {
		return "KitDTO [id=" + id + ", kitNumber=" + kitNumber + ", items=" + items + ", subkits=" + subkits
				+ ", lastModDttm=" + lastModDttm + ", createdDttm=" + createdDttm + ", date=" + date + ", status="
				+ status + ", kitReopenTimes=" + kitReopenTimes + ", version=" + version + ", assignedWarehouse="
				+ assignedWarehouse + ", requestWarehouse=" + requestWarehouse + "]";
	}
}
