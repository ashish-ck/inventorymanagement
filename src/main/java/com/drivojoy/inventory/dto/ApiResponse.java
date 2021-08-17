package com.drivojoy.inventory.dto;

public class ApiResponse<T> {

	public boolean valid;
	public T payload;
	public String message;
	
	public ApiResponse(){}
	
	public ApiResponse(boolean valid, T payload, String message) {
		super();
		this.valid = valid;
		this.payload = payload;
		this.message = message;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public T getPayload() {
		return payload;
	}

	public void setPayload(T payload) {
		this.payload = payload;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ReponseEntity [valid=" + valid + ", payload=" + payload + ", message=" + message + "]";
	}
	
}
