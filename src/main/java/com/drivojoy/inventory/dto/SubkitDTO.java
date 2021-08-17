package com.drivojoy.inventory.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class SubkitDTO {
	private long id;
	private String subkitNumber;
	private String kitNumber;
	private Collection<SalesOrderLineItemDTO> items = new ArrayList<>();
	private Date lastModDttm;
	private Date createdDttm;
	private Date date;
	private int status;
	private WarehouseDTO location;

	public SubkitDTO() {
	}

	public SubkitDTO(long id, String subkitNumber, String kitNumber, Collection<SalesOrderLineItemDTO> items,
			Date lastModDttm, Date createdDttm, Date date, int status, WarehouseDTO location) {
		super();
		this.id = id;
		this.subkitNumber = subkitNumber;
		this.kitNumber = kitNumber;
		this.items = items;
		this.lastModDttm = lastModDttm;
		this.createdDttm = createdDttm;
		this.date = date;
		this.status = status;
		this.location = location;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSubkitNumber() {
		return subkitNumber;
	}

	public void setSubkitNumber(String subkitNumber) {
		this.subkitNumber = subkitNumber;
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

	public WarehouseDTO getLocation() {
		return location;
	}

	public void setLocation(WarehouseDTO location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return "SubkitDTO [id=" + id + ", subkitNumber=" + subkitNumber + ", kitNumber=" + kitNumber + ", items="
				+ items + ", lastModDttm=" + lastModDttm + ", createdDttm=" + createdDttm + ", date=" + date
				+ ", status=" + status + ", location=" + location + "]";
	}
}
