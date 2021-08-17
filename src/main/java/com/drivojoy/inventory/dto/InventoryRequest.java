package com.drivojoy.inventory.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InventoryRequest {

	private Date date;
	private String voucherRef; 
	private List<ItemRequestPair> listOfItems = new ArrayList<ItemRequestPair>();
	private String workshopRef;
	private int status;
	private int version;
	
	public InventoryRequest() {
	}

	public InventoryRequest(Date date, String voucherRef, List<ItemRequestPair> listOfItems, String workshopRef, int status, int version) {
		super();
		this.date = date;
		this.voucherRef = voucherRef;
		this.listOfItems = listOfItems;
		this.workshopRef = workshopRef;
		this.status = status;
		this.version = version;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getVoucherRef() {
		return voucherRef;
	}

	public void setVoucherRef(String voucherRef) {
		this.voucherRef = voucherRef;
	}

	public List<ItemRequestPair> getListOfItems() {
		return listOfItems;
	}

	public void setListOfItems(List<ItemRequestPair> listOfItems) {
		this.listOfItems = listOfItems;
	}

	
	public String getWorkshopRef() {
		return workshopRef;
	}

	public void setWorkshopRef(String workshopRef) {
		this.workshopRef = workshopRef;
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

	@Override
	public String toString() {
		return "InventoryRequest [date=" + date + ", voucherRef=" + voucherRef + ", listOfItems=" + listOfItems
				+ ", workshopRef=" + workshopRef + ", status=" + status + ", version=" + version + "]";
	}
	
	
}
