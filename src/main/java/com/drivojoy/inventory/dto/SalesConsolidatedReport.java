package com.drivojoy.inventory.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class SalesConsolidatedReport {
	private Date fromDate;
	private Date toDate;
	private Collection<SalesReportItem> kitItems = new ArrayList<>();
	private Collection<CountByDate> countByDate = new ArrayList<>();
	private double totalQuantityDelivered = 0.0;
	private double totalQuantityInvoiced = 0.0;
	private double totalQuantityReturnExpected = 0.0;
	private double totalQuantityRestocked = 0.0;
	private double totalQuantityOnTheFly = 0.0;
	private double totalSales = 0.0;
	private double totalExpenses = 0.0;
	private double blendedDiscount = 0.0;
	private double totalStockLoss = 0.0;
	private double amountInvoiced = 0.0;

	public SalesConsolidatedReport() {
	}

	public SalesConsolidatedReport(Date fromDate, Date toDate, Collection<SalesReportItem> kitItems,
			double totalQuantityDelivered, double totalQuantityRestocked, double totalQuantityOnTheFly,
			double totalQuantityInvoiced, double totalQuantityReturnExpected, Collection<CountByDate> countByDate,
			double amountInvoiced) {
		super();
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.kitItems = kitItems;
		this.totalQuantityDelivered = totalQuantityDelivered;
		this.totalQuantityRestocked = totalQuantityRestocked;
		this.totalQuantityOnTheFly = totalQuantityOnTheFly;
		this.totalQuantityInvoiced = totalQuantityInvoiced;
		this.totalQuantityReturnExpected = totalQuantityReturnExpected;
		this.countByDate = countByDate;
		this.amountInvoiced = amountInvoiced;
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

	public Collection<SalesReportItem> getKitItems() {
		return kitItems;
	}

	public void setKitItems(Collection<SalesReportItem> kitItems) {
		this.kitItems = kitItems;
	}

	public double getTotalQuantityDelivered() {
		return totalQuantityDelivered;
	}

	public void setTotalQuantityDelivered(double totalQuantityDelivered) {
		this.totalQuantityDelivered = totalQuantityDelivered;
	}

	public double getTotalQuantityRestocked() {
		return totalQuantityRestocked;
	}

	public void setTotalQuantityRestocked(double totalQuantityRestocked) {
		this.totalQuantityRestocked = totalQuantityRestocked;
	}

	public double getTotalQuantityOnTheFly() {
		return totalQuantityOnTheFly;
	}

	public void setTotalQuantityOnTheFly(double totalQuantityOnTheFly) {
		this.totalQuantityOnTheFly = totalQuantityOnTheFly;
	}

	public double getTotalQuantityReturnExpected() {
		return totalQuantityReturnExpected;
	}

	public void setTotalQuantityReturnExpected(double totalQuantityReturnExpected) {
		this.totalQuantityReturnExpected = totalQuantityReturnExpected;
	}

	public Collection<CountByDate> getCountByDate() {
		return countByDate;
	}

	public void setCountByDate(Collection<CountByDate> countByDate) {
		this.countByDate = countByDate;
	}

	public double getAmountInvoiced() {
		return amountInvoiced;
	}

	public void setAmountInvoiced(double amountInvoiced) {
		this.amountInvoiced = amountInvoiced;
	}

	public double getTotalQuantityInvoiced() {
		return totalQuantityInvoiced;
	}

	public void setTotalQuantityInvoiced(double totalQuantityInvoiced) {
		this.totalQuantityInvoiced = totalQuantityInvoiced;
	}

	public double getTotalSales() {
		return totalSales;
	}

	public void setTotalSales(double totalSales) {
		this.totalSales = totalSales;
	}

	public double getTotalExpenses() {
		return totalExpenses;
	}

	public void setTotalExpenses(double totalExpenses) {
		this.totalExpenses = totalExpenses;
	}

	public double getBlendedDiscount() {
		return blendedDiscount;
	}

	public void setBlendedDiscount(double blendedDiscount) {
		this.blendedDiscount = blendedDiscount;
	}

	public double getTotalStockLoss() {
		return totalStockLoss;
	}

	public void setTotalStockLoss(double totalStockLoss) {
		this.totalStockLoss = totalStockLoss;
	}

	@Override
	public String toString() {
		return "SalesConsolidatedReport [fromDate=" + fromDate + ", toDate=" + toDate + ", kitItems=" + kitItems
				+ ", countByDate=" + countByDate + ", totalQuantityDelivered=" + totalQuantityDelivered
				+ ", totalQuantityRestocked=" + totalQuantityRestocked + ", totalQuantityOnTheFly="
				+ totalQuantityOnTheFly + ", totalQuantityInvoiced=" + totalQuantityInvoiced
				+ ", totalQuantityReturnExpected=" + totalQuantityReturnExpected + ", amountInvoiced=" + amountInvoiced
				+ "]";
	}
}
