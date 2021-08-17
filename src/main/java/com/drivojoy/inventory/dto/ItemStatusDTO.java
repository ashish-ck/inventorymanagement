package com.drivojoy.inventory.dto;

public class ItemStatusDTO {
	private int status;
	private String text;

	public ItemStatusDTO() {
	}

	public ItemStatusDTO(int status, String text) {
		super();
		this.status = status;
		this.text = text;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "ItemStatusDTO [status=" + status + ", text=" + text + "]";
	}
}
