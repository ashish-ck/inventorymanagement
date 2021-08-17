package com.drivojoy.inventory.dto;

import java.util.List;

public class PurchaseOrderReportItem {
	private String orderNo;
	private String kitNumber;
	private String shippingWarehouse;
	private String vendor;
	private List<ItemRequestPair> items;
	private double totalWholesalePrice;
	private double totalUnitPrice;
	private double quantityDelivered;
	private double vatTax;
	private String statusText;
	private double amountInvoiced;

	public PurchaseOrderReportItem() {
	}

	public PurchaseOrderReportItem(String orderNo, String kitNumber, String shippingWarehouse, String vendor,
			List<ItemRequestPair> items, double totalWholesalePrice, double totalUnitPrice, double quantityDelivered,
			double vatTax, String statusText, double amountInvoiced) {
		super();
		this.orderNo = orderNo;
		this.kitNumber = kitNumber;
		this.shippingWarehouse = shippingWarehouse;
		this.vendor = vendor;
		this.items = items;
		this.totalWholesalePrice = totalWholesalePrice;
		this.totalUnitPrice = totalUnitPrice;
		this.quantityDelivered = quantityDelivered;
		this.vatTax = vatTax;
		this.statusText = statusText;
		this.amountInvoiced = amountInvoiced;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getKitNumber() {
		return kitNumber;
	}

	public void setKitNumber(String kitNumber) {
		this.kitNumber = kitNumber;
	}

	public String getShippingWarehouse() {
		return shippingWarehouse;
	}

	public void setShippingWarehouse(String shippingWarehouse) {
		this.shippingWarehouse = shippingWarehouse;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public List<ItemRequestPair> getItems() {
		return items;
	}

	public void setItems(List<ItemRequestPair> items) {
		this.items = items;
	}

	public double getTotalWholesalePrice() {
		return totalWholesalePrice;
	}

	public void setTotalWholesalePrice(double totalWholesalePrice) {
		this.totalWholesalePrice = totalWholesalePrice;
	}

	public double getTotalUnitPrice() {
		return totalUnitPrice;
	}

	public void setTotalUnitPrice(double totalUnitPrice) {
		this.totalUnitPrice = totalUnitPrice;
	}

	public double getQuantityDelivered() {
		return quantityDelivered;
	}

	public void setQuantityDelivered(double quantityDelivered) {
		this.quantityDelivered = quantityDelivered;
	}

	public double getVatTax() {
		return vatTax;
	}

	public void setVatTax(double vatTax) {
		this.vatTax = vatTax;
	}

	public String getStatusText() {
		return statusText;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public double getAmountInvoiced() {
		return amountInvoiced;
	}

	public void setAmountInvoiced(double amountInvoiced) {
		this.amountInvoiced = amountInvoiced;
	}

	@Override
	public String toString() {
		return "PurchaseOrderReportItem [orderNo=" + orderNo + ", kitNumber=" + kitNumber + ", shippingWarehouse="
				+ shippingWarehouse + ", vendor=" + vendor + ", items=" + items + ", totalWholesalePrice="
				+ totalWholesalePrice + ", totalUnitPrice=" + totalUnitPrice + ", quantityDelivered="
				+ quantityDelivered + ", vatTax=" + vatTax + ", statusText=" + statusText + ", amountInvoiced="
				+ amountInvoiced + "]";
	}

}
