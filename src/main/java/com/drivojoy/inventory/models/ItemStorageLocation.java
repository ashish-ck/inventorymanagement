package com.drivojoy.inventory.models;

import javax.persistence.Embeddable;
import javax.persistence.OneToOne;

/**
 * 
 * @author ashishsingh
 *
 */
@Embeddable
public class ItemStorageLocation {

	@OneToOne
	private Warehouse warehouse;
	@OneToOne
	private Shelf shelf;
	@OneToOne
	private Rack rack;
	@OneToOne
	private Crate crate;

	public ItemStorageLocation() {
	}

	public ItemStorageLocation(Warehouse warehouse, Shelf shelf, Rack rack, Crate crate) {
		super();
		this.warehouse = warehouse;
		this.rack = rack;
		this.crate = crate;
		this.shelf = shelf;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
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

	public Shelf getShelf() {
		return shelf;
	}

	public void setShelf(Shelf shelf) {
		this.shelf = shelf;
	}

	@Override
	public String toString() {
		return "ItemStorageLocation [warehouse=" + warehouse + ", rack=" + rack + ", crate=" + crate + ", shelf="
				+ shelf + "]";
	}
}
