package com.drivojoy.inventory.dto;

import java.util.List;

public class SalesReportItem {
	// Total sales, total expenses, blended discount, total stock loss.
	private String kitNumber;
	private String assignedWarehouse;
	private String requestWarehouse;
	private double quantityDelivered;
	private double itemsOnTheFly;
	private double quantityInvoiced;
	private double quantityReturnExpected;
	private double quantityRestocked;
	private double quantityStockLoss;
	private double sales;
	private double expenses;
	private double discount;
	private double stockLoss;
	private double itemsRemoved;
	private String statusText;
	private double amountInvoiced;
	private List<SalesOrderLineItemDTO> items;
	private List<SubkitDTO> subkits;

	public SalesReportItem() {
	}

	public SalesReportItem(String kitNumber, String assignedWarehouse, String requestWarehouse,
			double quantityDelivered, double quantityInvoiced, double quantityOnTheFly, double quantityRestocked,
			double quantityStockLoss, double quantityReturnExpected, double itemsRemoved, String statusText,
			double amountInvoiced, List<SalesOrderLineItemDTO> items, List<SubkitDTO> subkits) {
		super();
		this.kitNumber = kitNumber;
		this.assignedWarehouse = assignedWarehouse;
		this.requestWarehouse = requestWarehouse;
		this.quantityDelivered = quantityDelivered;
		this.itemsOnTheFly = quantityOnTheFly;
		this.quantityInvoiced = quantityInvoiced;
		this.quantityRestocked = quantityRestocked;
		this.quantityStockLoss = quantityStockLoss;
		this.quantityReturnExpected = quantityReturnExpected;
		this.itemsRemoved = itemsRemoved;
		this.statusText = statusText;
		this.amountInvoiced = amountInvoiced;
	}

	public String getKitNumber() {
		return kitNumber;
	}

	public void setKitNumber(String kitNumber) {
		this.kitNumber = kitNumber;
	}

	public String getAssignedWarehouse() {
		return assignedWarehouse;
	}

	public void setAssignedWarehouse(String assignedWarehouse) {
		this.assignedWarehouse = assignedWarehouse;
	}

	public double getQuantityDelivered() {
		return quantityDelivered;
	}

	public void setQuantityDelivered(double quantityDelivered) {
		this.quantityDelivered = quantityDelivered;
	}

	public double getItemsOnTheFly() {
		return itemsOnTheFly;
	}

	public void setItemsOnTheFly(double itemsOnTheFly) {
		this.itemsOnTheFly = itemsOnTheFly;
	}

	public double getQuantityInvoiced() {
		return quantityInvoiced;
	}

	public void setQuantityInvoiced(double quantityInvoiced) {
		this.quantityInvoiced = quantityInvoiced;
	}

	public double getQuantityReturnExpected() {
		return quantityReturnExpected;
	}

	public void setQuantityReturnExpected(double quantityReturnExpected) {
		this.quantityReturnExpected = quantityReturnExpected;
	}

	public double getQuantityRestocked() {
		return quantityRestocked;
	}

	public void setQuantityRestocked(double quantityRestocked) {
		this.quantityRestocked = quantityRestocked;
	}

	public double getItemsRemoved() {
		return itemsRemoved;
	}

	public void setItemsRemoved(double itemsRemoved) {
		this.itemsRemoved = itemsRemoved;
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

	public double getQuantityStockLoss() {
		return quantityStockLoss;
	}

	public void setQuantityStockLoss(double quantityStockLoss) {
		this.quantityStockLoss = quantityStockLoss;
	}

	public List<SalesOrderLineItemDTO> getItems() {
		return items;
	}

	public void setItems(List<SalesOrderLineItemDTO> items) {
		this.items = items;
	}

	public List<SubkitDTO> getSubkits() {
		return subkits;
	}

	public void setSubkits(List<SubkitDTO> subkits) {
		this.subkits = subkits;
	}

	public String getRequestWarehouse() {
		return requestWarehouse;
	}

	public void setRequestWarehouse(String requestWarehouse) {
		this.requestWarehouse = requestWarehouse;
	}

	public double getSales() {
		return sales;
	}

	public void setSales(double sales) {
		this.sales = sales;
	}

	public double getExpenses() {
		return expenses;
	}

	public void setExpenses(double expenses) {
		this.expenses = expenses;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public double getStockLoss() {
		return stockLoss;
	}

	public void setStockLoss(double stockLoss) {
		this.stockLoss = stockLoss;
	}

	@Override
	public String toString() {
		return "SalesReportItem [kitNumber=" + kitNumber + ", assignedWarehouse=" + assignedWarehouse
				+ ", quantityDelivered=" + quantityDelivered + ", itemsOnTheFly=" + itemsOnTheFly
				+ ", quantityInvoiced=" + quantityInvoiced + ", quantityReturnExpected=" + quantityReturnExpected
				+ ", quantityRestocked=" + quantityRestocked + ", quantityStockLoss=" + quantityStockLoss
				+ ", itemsRemoved=" + itemsRemoved + ", statusText=" + statusText + ", amountInvoiced=" + amountInvoiced
				+ "]";
	}
}
