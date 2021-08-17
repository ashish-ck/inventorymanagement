package com.drivojoy.inventory.dto;

import java.util.ArrayList;
import java.util.List;

public class ItemReconcileReport {
	private List<ItemReport> items;
	private double totalQuantityOutgoing;
	private double totalQuantityIncoming;
	private double totalQuantityCurrentStockCount;

	public List<ItemReport> getItems() {
		return items;
	}

	public void setItems(List<ItemReport> items) {
		this.items = items;
	}

	public double getTotalQuantityOutgoing() {
		return totalQuantityOutgoing;
	}

	public void setTotalQuantityOutgoing(double totalQuantityOutgoing) {
		this.totalQuantityOutgoing = totalQuantityOutgoing;
	}

	public double getTotalQuantityIncoming() {
		return totalQuantityIncoming;
	}

	public void setTotalQuantityIncoming(double totalQuantityIncoming) {
		this.totalQuantityIncoming = totalQuantityIncoming;
	}

	public double getTotalQuantityCurrentStockCount() {
		return totalQuantityCurrentStockCount;
	}

	public void setTotalQuantityCurrentStockCount(double totalQuantityCurrentStockCount) {
		this.totalQuantityCurrentStockCount = totalQuantityCurrentStockCount;
	}

	public ItemReconcileReport() {
		items = new ArrayList<>();
		this.totalQuantityOutgoing = 0.0;
		this.totalQuantityIncoming = 0.0;
		this.totalQuantityCurrentStockCount = 0.0;
	}

	public ItemReconcileReport(List<ItemReport> items, double totalQuantityOutgoing, double totalQuantityIncoming,
			double totalQuantityCurrentStockCount) {
		super();
		this.items = items;
		this.totalQuantityOutgoing = totalQuantityOutgoing;
		this.totalQuantityIncoming = totalQuantityIncoming;
		this.totalQuantityCurrentStockCount = totalQuantityCurrentStockCount;
	}

	@Override
	public String toString() {
		return "ItemReconcileReport [items=" + items + ", totalQuantityOutgoing=" + totalQuantityOutgoing
				+ ", totalQuantityIncoming=" + totalQuantityIncoming + ", totalQuantityCurrentStockCount="
				+ totalQuantityCurrentStockCount + "]";
	}
}
