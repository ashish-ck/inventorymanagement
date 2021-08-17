package com.drivojoy.inventory.models;

import javax.persistence.Embeddable;
import javax.persistence.OneToOne;

@Embeddable
public class SalesOrderItemLine {
	private String itemCode;
	private String description;
	private String vendorAlias;
	@OneToOne
	private Warehouse currentLocation;
	private String kitNumber;
	private String subkitNumber;
	private String orderNo;
	private String barcode;
	private String serialNumber;
	private int status;
	private int scrapStatus;
	private Double unitPrice;
	private Double wholesalePrice;
	transient private String suggestion;

	public SalesOrderItemLine() {
	}

	public SalesOrderItemLine(String itemCode, String description, String vendorAlias, Warehouse currentLocation,
			String kitNumber, String subkitNumber, String orderNo, String barcode, String serialNumber, int status,
			int scrapStatus, Double unitPrice, Double wholesalePrice) {
		super();
		this.itemCode = itemCode;
		this.description = description;
		this.vendorAlias = vendorAlias;
		this.currentLocation = currentLocation;
		this.kitNumber = kitNumber;
		this.subkitNumber = subkitNumber;
		this.orderNo = orderNo;
		this.barcode = barcode;
		this.serialNumber = serialNumber;
		this.status = status;
		this.scrapStatus = scrapStatus;
		this.unitPrice = unitPrice;
		this.wholesalePrice = wholesalePrice;
	}

	public String getKitNumber() {
		return kitNumber;
	}

	public void setKitNumber(String kitNumber) {
		this.kitNumber = kitNumber;
	}

	public String getSubkitNumber() {
		return subkitNumber;
	}

	public void setSubkitNumber(String subkitNumber) {
		this.subkitNumber = subkitNumber;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public Warehouse getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(Warehouse currentLocation) {
		this.currentLocation = currentLocation;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Double getWholesalePrice() {
		return wholesalePrice;
	}

	public void setWholesalePrice(Double wholesalePrice) {
		this.wholesalePrice = wholesalePrice;
	}

	public String getVendorAlias() {
		return vendorAlias;
	}

	public void setVendorAlias(String vendorAlias) {
		this.vendorAlias = vendorAlias;
	}

	public int getScrapStatus() {
		return scrapStatus;
	}

	public void setScrapStatus(int scrapStatus) {
		this.scrapStatus = scrapStatus;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void update(String barcode, String itemCode, Warehouse location, int status) {
		setStatus(status);
		setBarcode(barcode);
		if (location != null)
			setCurrentLocation(location);
	}

	public String getSuggestion() {
		return suggestion;
	}

	@Override
	public String toString() {
		return "SalesOrderItemLine [itemCode=" + itemCode + ", description=" + description + ", vendorAlias="
				+ vendorAlias + ", currentLocation=" + currentLocation + ", barcode=" + barcode + ", serialNumber="
				+ serialNumber + ", status=" + status + ", scrapStatus=" + scrapStatus + ", unitPrice=" + unitPrice
				+ ", wholesalePrice=" + wholesalePrice + "]";
	}

	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}
}
