package com.drivojoy.inventory.dto;

import java.util.Date;

public class OutOfStockKitItemsDTO {

	private String itemCode;
	private String itemDescription;
	private String warehouse;
	private Date date;
	private String voucherRef;

	public OutOfStockKitItemsDTO() {
	}

	public OutOfStockKitItemsDTO(String itemCode, String itemDescription, String warehouse, Date date,
			String voucherRef) {
		super();
		this.itemCode = itemCode;
		this.itemDescription = itemDescription;
		this.warehouse = warehouse;
		this.date = date;
		this.voucherRef = voucherRef;
	}

	public String getVoucherRef() {
		return voucherRef;
	}

	public void setVoucherRef(String voucherRef) {
		this.voucherRef = voucherRef;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public String getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "OutOfStockKitItemsDTO [itemCode=" + itemCode + ", itemDescription=" + itemDescription + ", warehouse="
				+ warehouse + ", date=" + date + ", voucherRef=" + voucherRef + "]";
	}

}
