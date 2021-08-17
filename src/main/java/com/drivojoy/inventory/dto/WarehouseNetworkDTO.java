package com.drivojoy.inventory.dto;

public class WarehouseNetworkDTO {
	private long id;
	private String name;
	private String code;
	private boolean isParentWarehouse;

	public WarehouseNetworkDTO() {
	}

	public WarehouseNetworkDTO(long id, String name, String code, boolean isParentWarehouse) {
		super();
		this.id = id;
		this.name = name;
		this.code = code;
		this.isParentWarehouse = isParentWarehouse;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean isParentWarehouse() {
		return isParentWarehouse;
	}

	public void setParentWarehouse(boolean isParentWarehouse) {
		this.isParentWarehouse = isParentWarehouse;
	}

	@Override
	public String toString() {
		return "WarehouseNetworkDTO [id=" + id + ", name=" + name + ", code=" + code + ", isParentWarehouse="
				+ isParentWarehouse + "]";
	}
}
