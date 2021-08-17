package com.drivojoy.inventory.dto;

public class ItemCountDTO {
	private WarehouseNetworkDTO warehouse;
	private String itemCode;
	private String description;
	private String vendorAlias;
	private String barcode;
	private int status;
	private double reorderPoint;
	private double unitPrice;
	private double wholesalePrice;
	private ShelfDTO shelf;
	private RackDTO rack;
	private CrateDTO crate;

	public ItemCountDTO() {
	}

	public ItemCountDTO(WarehouseNetworkDTO warehouse, String itemCode, String description, String vendorAlias, String barcode, int status, double reorderPoint,
			double unitPrice, double wholesalePrice, ShelfDTO shelf, RackDTO rack, CrateDTO crate) {
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

	public WarehouseNetworkDTO getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(WarehouseNetworkDTO warehouse) {
		this.warehouse = warehouse;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public CrateDTO getCrate() {
		return crate;
	}

	public void setCrate(CrateDTO crate) {
		this.crate = crate;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public RackDTO getRack() {
		return rack;
	}

	public void setRack(RackDTO rack) {
		this.rack = rack;
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

	public ShelfDTO getShelf() {
		return shelf;
	}

	public void setShelf(ShelfDTO shelf) {
		this.shelf = shelf;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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

	@Override
	public String toString() {
		return "ItemCount [warehouse=" + warehouse + ", itemCode=" + itemCode + ", description=" + description
				+ ", vendorAlias=" + vendorAlias + ", barcode=" + barcode + ", status=" + status + ", reorderPoint="
				+ reorderPoint + ", unitPrice=" + unitPrice + ", wholesalePrice=" + wholesalePrice + ", shelf=" + shelf
				+ ", rack=" + rack + ", crate=" + crate + "]";
	}
}
