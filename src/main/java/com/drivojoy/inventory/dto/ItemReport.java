package com.drivojoy.inventory.dto;

public class ItemReport {
	private String itemCode;
	private double quantityOutgoing;
	private double quantityIncoming;
	private double quantityCurrentStockCount;

	public ItemReport(String itemCode, double quantityOutgoing, double quantityIncoming,
			double quantityCurrentStockCount) {
		super();
		this.itemCode = itemCode;
		this.quantityOutgoing = quantityOutgoing;
		this.quantityIncoming = quantityIncoming;
		this.quantityCurrentStockCount = quantityCurrentStockCount;
	}

	public ItemReport() {
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public double getQuantityOutgoing() {
		return quantityOutgoing;
	}

	public void setQuantityOutgoing(double quantityOutgoing) {
		this.quantityOutgoing = quantityOutgoing;
	}

	public double getQuantityIncoming() {
		return quantityIncoming;
	}

	public void setQuantityIncoming(double quantityIncoming) {
		this.quantityIncoming = quantityIncoming;
	}

	public double getQuantityCurrentStockCount() {
		return quantityCurrentStockCount;
	}

	public void setQuantityCurrentStockCount(double quantityCurrentStockCount) {
		this.quantityCurrentStockCount = quantityCurrentStockCount;
	}

	@Override
	public String toString() {
		return "ItemReport [itemCode=" + itemCode + ", quantityOutgoing=" + quantityOutgoing + ", quantityIncoming="
				+ quantityIncoming + ", quantityCurrentStockCount=" + quantityCurrentStockCount + "]";
	}

}
