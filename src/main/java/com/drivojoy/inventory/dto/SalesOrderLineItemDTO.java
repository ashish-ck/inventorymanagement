package com.drivojoy.inventory.dto;

public class SalesOrderLineItemDTO {
	private ItemNetworkDTO item;
	private WarehouseNetworkDTO currentLocation;
	private String barcode;
	private String serialNumber;
	private int status;
	private int scrapStatus;
	private Double unitPrice;
	private Double wholesalePrice;
	private String kitNumber;
	private String subkitNumber;
	private String orderNo;
	private String suggestion;

	public SalesOrderLineItemDTO() {
	}

	public SalesOrderLineItemDTO(ItemNetworkDTO item, WarehouseNetworkDTO currentLocation, String barcode,
			String serialNumber, int status, int scrapStatus, Double unitPrice, Double wholesalePrice, String kitNumber,
			String subkitNumber, String orderNo) {
		super();
		this.item = item;
		this.currentLocation = currentLocation;
		this.barcode = barcode;
		this.serialNumber = serialNumber;
		this.status = status;
		this.scrapStatus = scrapStatus;
		this.unitPrice = unitPrice;
		this.wholesalePrice = wholesalePrice;
		this.kitNumber = kitNumber;
		this.subkitNumber = subkitNumber;
		this.orderNo = orderNo;
	}

	public int getScrapStatus() {
		return scrapStatus;
	}

	public void setScrapStatus(int scrapStatus) {
		this.scrapStatus = scrapStatus;
	}

	public WarehouseNetworkDTO getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(WarehouseNetworkDTO currentLocation) {
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

	public ItemNetworkDTO getItem() {
		return item;
	}

	public void setItem(ItemNetworkDTO item) {
		this.item = item;
	}

	public String getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
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

	@Override
	public String toString() {
		return "SalesOrderLineItemDTO [item=" + item + ", description=" + ", currentLocation=" + currentLocation
				+ ", barcode=" + barcode + ", serialNumber=" + serialNumber + ", status=" + status + ", scrapStatus="
				+ scrapStatus + ", unitPrice=" + unitPrice + ", wholesalePrice=" + wholesalePrice + "]";
	}
}
