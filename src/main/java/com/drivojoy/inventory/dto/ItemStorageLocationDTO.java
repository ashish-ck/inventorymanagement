package com.drivojoy.inventory.dto;

public class ItemStorageLocationDTO {

	private String warehouse;
	private ShelfDTO shelf;
	private RackDTO rack;
	private CrateDTO crate;

	public ItemStorageLocationDTO() {
	}

	public ItemStorageLocationDTO(String warehouse, ShelfDTO shelf, RackDTO rack, CrateDTO crate) {
		super();
		this.warehouse = warehouse;
		this.rack = rack;
		this.shelf = shelf;
		this.crate = crate;
	}

	public String getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}

	public RackDTO getRack() {
		return rack;
	}

	public void setRack(RackDTO rack) {
		this.rack = rack;
	}

	public ShelfDTO getShelf() {
		return shelf;
	}

	public void setShelf(ShelfDTO shelf) {
		this.shelf = shelf;
	}

	public CrateDTO getCrate() {
		return crate;
	}

	public void setCrate(CrateDTO crate) {
		this.crate = crate;
	}

	@Override
	public String toString() {
		return "ItemStorageLocationDTO [warehouse=" + warehouse + ", rack=" + rack + ", shelf=" + shelf + ", crate="
				+ crate + "]";
	}

}
