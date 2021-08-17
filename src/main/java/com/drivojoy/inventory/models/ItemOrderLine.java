package com.drivojoy.inventory.models;

import javax.persistence.Embeddable;
import javax.persistence.OneToOne;

@Embeddable
public class ItemOrderLine {
	private String itemCode;
	private String vendorAlias;
	@OneToOne
	private UoM unit;
	private double unitPrice;
	private double wholesalePrice;
	private String description;
	private boolean isItemSerialized;
	private String barcode;

	public ItemOrderLine() {
	}

	public ItemOrderLine(String itemCode, String vendorAlias, UoM unit, double unitPrice, double wholesalePrice,
			String description, boolean isItemSerialized, String barcode) {
		super();
		this.itemCode = itemCode;
		this.vendorAlias = vendorAlias;
		this.unit = unit;
		this.unitPrice = unitPrice;
		this.wholesalePrice = wholesalePrice;
		this.description = description;
		this.isItemSerialized = isItemSerialized;
		this.barcode = barcode;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getVendorAlias() {
		return vendorAlias;
	}

	public void setVendorAlias(String vendorAlias) {
		this.vendorAlias = vendorAlias;
	}

	public UoM getUnit() {
		return unit;
	}

	public void setUnit(UoM unit) {
		this.unit = unit;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public double getTotalPrice() {
		return wholesalePrice;
	}

	public void setWholesalePrice(double wholesalePrice) {
		this.wholesalePrice = wholesalePrice;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isItemSerialized() {
		return isItemSerialized;
	}

	public void setItemSerialized(boolean isItemSerialized) {
		this.isItemSerialized = isItemSerialized;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public double getWholesalePrice() {
		return wholesalePrice;
	}

	@Override
	public String toString() {
		return "ItemOrderLine [itemCode=" + itemCode + ", vendorAlias=" + vendorAlias + ", unit=" + unit
				+ ", unitPrice=" + unitPrice + ", wholesalePrice=" + wholesalePrice + ", description=" + description
				+ ", isItemSerialized=" + isItemSerialized + ", barcode=" + barcode + "]";
	}
}
