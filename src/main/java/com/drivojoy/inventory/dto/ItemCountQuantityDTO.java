package com.drivojoy.inventory.dto;

public class ItemCountQuantityDTO {
	private String itemCode;
	private String description;
	private Double quantity;
	private Double rate;

	public ItemCountQuantityDTO(String itemCode, String description, Double quantity, Double rate) {
		super();
		this.itemCode = itemCode;
		this.description = description;
		this.quantity = quantity;
		this.rate = rate;
	}

	public ItemCountQuantityDTO() {
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
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

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	@Override
	public String toString() {
		return "ItemCountQuantityDTO [itemCode=" + itemCode + ", description=" + description + ", quantity=" + quantity
				+ ", rate=" + rate + "]";
	}

}
