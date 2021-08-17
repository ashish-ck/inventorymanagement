package com.drivojoy.inventory.models;

import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.OneToOne;

@Embeddable
public class ItemDetails {
	private String vendorProductId;
	private Date receiveDate;
	private Date bestBefore;
	private Date sellingDate;
	private int warrantyInMonths;
	private String barcode;
	private double qtyPerUnit;
	@OneToOne
	private Warehouse warehouse;
	private String itemCode;
	private int status;
	private String assignedSalesOrder;
	private String purchaseOrder;
	private double unitPrice;
	private double wholesalePrice;

	public ItemDetails() {
	}

	public ItemDetails(String vendorProductId, Date receiveDate, Date bestBefore, Date sellingDate,
			int warrantyInMonths, String barcode, double qtyPerUnit, Warehouse warehouse, String itemCode,
			int status, String assignedSalesOrder, String purchaseOrder, double unitPrice, double wholesalePrice) {
		super();
		this.vendorProductId = vendorProductId;
		this.receiveDate = receiveDate;
		this.bestBefore = bestBefore;
		this.sellingDate = sellingDate;
		this.warrantyInMonths = warrantyInMonths;
		this.barcode = barcode;
		this.qtyPerUnit = qtyPerUnit;
		this.warehouse = warehouse;
		this.itemCode = itemCode;
		this.status = status;
		this.assignedSalesOrder = assignedSalesOrder;
		this.purchaseOrder = purchaseOrder;
		this.unitPrice = unitPrice;
		this.wholesalePrice = wholesalePrice;
	}

	public String getVendorProductId() {
		return vendorProductId;
	}

	public void setVendorProductId(String vendorProductId) {
		this.vendorProductId = vendorProductId;
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

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public double getQtyPerUnit() {
		return qtyPerUnit;
	}

	public void setQtyPerUnit(double qtyPerUnit) {
		this.qtyPerUnit = qtyPerUnit;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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


}
