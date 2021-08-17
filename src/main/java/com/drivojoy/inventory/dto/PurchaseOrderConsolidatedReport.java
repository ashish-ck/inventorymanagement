package com.drivojoy.inventory.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class PurchaseOrderConsolidatedReport {
	private Date fromDate;
	private Date toDate;
	private Collection<PurchaseOrderReportItem> orders = new ArrayList<>();
	private double totalQuantityDelivered = 0.0;
	private double totalWholesalePrice = 0.0;
	private double totalUnitPrice = 0.0;
	private double vatTax = 0.0;

	@Override
	public String toString() {
		return "PurchaseOrderConsolidatedReport [fromDate=" + fromDate + ", toDate=" + toDate + ", orders=" + orders
				+ ", totalQuantityDelivered=" + totalQuantityDelivered + ", totalWholesalePrice=" + totalWholesalePrice
				+ ", totalUnitPrice=" + totalUnitPrice + ", vatTax=" + vatTax + "]";
	}

	public PurchaseOrderConsolidatedReport(Date fromDate, Date toDate, Collection<PurchaseOrderReportItem> orders,
			double totalQuantityDelivered, double totalWholesalePrice, double totalUnitPrice, double vatTax) {
		super();
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.orders = orders;
		this.totalQuantityDelivered = totalQuantityDelivered;
		this.totalWholesalePrice = totalWholesalePrice;
		this.totalUnitPrice = totalUnitPrice;
		this.vatTax = vatTax;
	}

	public PurchaseOrderConsolidatedReport() {
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Collection<PurchaseOrderReportItem> getOrders() {
		return orders;
	}

	public void setOrders(Collection<PurchaseOrderReportItem> orders) {
		this.orders = orders;
	}

	public double getTotalQuantityDelivered() {
		return totalQuantityDelivered;
	}

	public void setTotalQuantityDelivered(double totalQuantityDelivered) {
		this.totalQuantityDelivered = totalQuantityDelivered;
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

	public double getVatTax() {
		return vatTax;
	}

	public void setVatTax(double vatTax) {
		this.vatTax = vatTax;
	}

}
