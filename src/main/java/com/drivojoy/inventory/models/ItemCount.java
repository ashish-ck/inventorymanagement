package com.drivojoy.inventory.models;

import javax.persistence.Embeddable;
import javax.persistence.OneToOne;

@Embeddable
public class ItemCount {
	@OneToOne
	private Warehouse warehouse;
	private String itemCode;
	private String description;
	private String vendorAlias;
	private String barcode;
	private int status;
	private double reorderPoint;
	private double unitPrice;
	private double wholesalePrice;
	@OneToOne
	private Shelf shelf;
	@OneToOne
	private Rack rack;
	@OneToOne
	private Crate crate;

	public ItemCount() {
	}

	public ItemCount(Warehouse warehouse, String itemCode, String description, String vendorAlias, String barcode,
			int status, double reorderPoint, double unitPrice, double wholesalePrice, Shelf shelf, Rack rack,
			Crate crate) {
		super();
		this.warehouse = warehouse;
		this.itemCode = itemCode;
		this.description = description;
		this.vendorAlias = vendorAlias;
		this.barcode = barcode;
		this.status = status;
		this.reorderPoint = reorderPoint;
		this.unitPrice = unitPrice;
		this.wholesalePrice = wholesalePrice;
		this.shelf = shelf;
		this.rack = rack;
		this.crate = crate;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVendorAlias() {
		return vendorAlias;
	}

	public void setVendorAlias(String vendorAlias) {
		this.vendorAlias = vendorAlias;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public double getReorderPoint() {
		return reorderPoint;
	}

	public void setReorderPoint(double reorderPoint) {
		this.reorderPoint = reorderPoint;
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

	public Shelf getShelf() {
		return shelf;
	}

	public void setShelf(Shelf shelf) {
		this.shelf = shelf;
	}

	public Rack getRack() {
		return rack;
	}

	public void setRack(Rack rack) {
		this.rack = rack;
	}

	public Crate getCrate() {
		return crate;
	}

	public void setCrate(Crate crate) {
		this.crate = crate;
	}

	@Override
	public String toString() {
		return "ItemCount [warehouse=" + warehouse + ", itemCode=" + itemCode + ", description=" + description
				+ ", vendorAlias=" + vendorAlias + ", barcode=" + barcode + ", status=" + status + ", reorderPoint="
				+ reorderPoint + ", unitPrice=" + unitPrice + ", wholesalePrice=" + wholesalePrice + ", shelf=" + shelf
				+ ", rack=" + rack + ", crate=" + crate + "]";
	}
}
