package com.drivojoy.inventory.dto;

public class ItemOrderLineDTO {
	private String itemCode;
	private String vendorAlias;
	private String unit;
	private double unitPrice;
	private double wholesalePrice;
	private String description;
	private boolean isItemSerialized;
	private String barcode;

	public ItemOrderLineDTO(){}
	
	public ItemOrderLineDTO(String itemCode, String vendorAlias, String unit, double unitPrice,
			double wholesalePrice, String description, boolean isItemSerialized, String barcode) {
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
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
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

	@Override
	public String toString() {
		return "ItemOrderLineDTO [itemCode=" + itemCode + ", vendorAlias=" + vendorAlias
				+ ", unit=" + unit + ", unitPrice=" + unitPrice + ", wholesalePrice=" + wholesalePrice + ", description="
				+ description + ", isItemSerialized=" + isItemSerialized + ", barcode=" + barcode + "]";
	}
	
	
}
