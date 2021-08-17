package com.drivojoy.inventory.dto;

import java.util.Date;

public class ItemRequestPair {
	private String warehouse;
	private String itemCode;
	private String barcode;
	private Date receiveDate;
	private Date bestBefore;
	private Date sellingDate;
	private int warrantyInMonths;
	private String vendorProductId;
	private int status = 0;
	private String statusText;
	private String assignedSalesOrder;
	private String purchaseOrder;
	private double unitPrice;
	private double wholesalePrice;

	public ItemRequestPair() {
	}

	public ItemRequestPair(String warehouse, String itemCode, String barcode, Date receiveDate, Date bestBefore,
			Date sellingDate, int warrantyInMonths, String vendorProductId, int status, String statusText,
			String assignedSalesOrder, String purchaseOrder, double unitPrice, double wholesalePrice) {
		super();
		this.warehouse = warehouse;
		this.itemCode = itemCode;
		this.barcode = barcode;
		this.receiveDate = receiveDate;
		this.bestBefore = bestBefore;
		this.sellingDate = sellingDate;
		this.warrantyInMonths = warrantyInMonths;
		this.vendorProductId = vendorProductId;
		this.status = status;
		this.statusText = statusText;
		this.assignedSalesOrder = assignedSalesOrder;
		this.purchaseOrder = purchaseOrder;
		this.unitPrice = unitPrice;
		this.wholesalePrice = wholesalePrice;
	}

	public String getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public Date getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}

	public Date getBestBefore() {
		return bestBefore;
	}

	public void setBestBefore(Date bestBefore) {
		this.bestBefore = bestBefore;
	}

	public Date getSellingDate() {
		return sellingDate;
	}

	public void setSellingDate(Date sellingDate) {
		this.sellingDate = sellingDate;
	}

	public int getWarrantyInMonths() {
		return warrantyInMonths;
	}

	public void setWarrantyInMonths(int warrantyInMonths) {
		this.warrantyInMonths = warrantyInMonths;
	}

	public String getVendorProductId() {
		return vendorProductId;
	}

	public void setVendorProductId(String vendorProductId) {
		this.vendorProductId = vendorProductId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStatusText() {
		return statusText;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public String getAssignedSalesOrder() {
		return assignedSalesOrder;
	}

	public void setAssignedSalesOrder(String assignedSalesOrder) {
		this.assignedSalesOrder = assignedSalesOrder;
	}

	public String getPurchaseOrder() {
		return purchaseOrder;
	}

	public void setPurchaseOrder(String purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public double getWholesalePrice() {
		return wholesalePrice;
	}

	public void setWholesalePrice(double wholesalePrice) {
		this.wholesalePrice = wholesalePrice;
	}

	@Override
	public String toString() {
		return "ItemRequestPair [warehouse=" + warehouse + ", itemCode=" + itemCode + ", barcode=" + barcode
				+ ", receiveDate=" + receiveDate + ", bestBefore=" + bestBefore + ", sellingDate=" + sellingDate
				+ ", warrantyInMonths=" + warrantyInMonths + ", vendorProductId=" + vendorProductId + ", status="
				+ status + ", statusText=" + statusText + ", assignedSalesOrder=" + assignedSalesOrder
				+ ", purchaseOrder=" + purchaseOrder + "]";
	}
}
