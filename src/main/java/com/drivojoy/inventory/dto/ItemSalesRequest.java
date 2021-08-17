package com.drivojoy.inventory.dto;

import java.util.ArrayList;
import java.util.List;

public class ItemSalesRequest {
	private String itemCode = null;
	private double quantityRequested = 0.0;
	private double quantityReturnExpected = 0.0;
	private boolean removed = false;
	private List<String> quantityRequestedBarcodes = new ArrayList<>();
	private List<String> quantityReturnExpectedBarcodes = new ArrayList<>();

	public ItemSalesRequest() {
	}

	public ItemSalesRequest(String itemCode, double quantityRequested, double quantityReturnExpected, boolean removed,
			List<String> quantityRequestedBarcodes, List<String> quantityReturnExpectedBarcodes) {
		this.itemCode = itemCode;
		this.quantityRequested = quantityRequested;
		this.quantityReturnExpected = quantityReturnExpected;
		this.removed = removed;
		this.quantityRequestedBarcodes = quantityRequestedBarcodes;
		this.quantityReturnExpectedBarcodes = quantityReturnExpectedBarcodes;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public double getQuantityRequested() {
		return quantityRequested;
	}

	public void setQuantityRequested(double quantityRequested) {
		this.quantityRequested = quantityRequested;
	}

	public double getQuantityReturnExpected() {
		return quantityReturnExpected;
	}

	public void setQuantityReturnExpected(double quantityReturnExpected) {
		this.quantityReturnExpected = quantityReturnExpected;
	}

	public boolean isRemoved() {
		return removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	public List<String> getQuantityRequestedBarcodes() {
		return quantityRequestedBarcodes;
	}

	public void setQuantityRequestedBarcodes(List<String> quantityRequestedBarcodes) {
		this.quantityRequestedBarcodes = quantityRequestedBarcodes;
	}

	public List<String> getQuantityReturnExpectedBarcodes() {
		return quantityReturnExpectedBarcodes;
	}

	public void setQuantityReturnExpectedBarcodes(List<String> quantityReturnExpectedBarcodes) {
		this.quantityReturnExpectedBarcodes = quantityReturnExpectedBarcodes;
	}

	@Override
	public String toString() {
		return "ItemSalesRequest [itemCode=" + itemCode + ", quantityRequested=" + quantityRequested
				+ ", quantityReturnExpected=" + quantityReturnExpected + ", removed=" + removed
				+ ", quantityRequestedBarcodes=" + quantityRequestedBarcodes + ", quantityReturnExpectedBarcodes="
				+ quantityReturnExpectedBarcodes + "]";
	}
}
