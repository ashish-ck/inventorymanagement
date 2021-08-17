package com.drivojoy.inventory.dto;

import java.util.Date;

import com.drivojoy.inventory.models.datatables.DTParameters;

public class RequestParamsDTO {
	private DTParameters params;
	private Date fromDate;
	private Date toDate;
	private Long requestWarehouseId;
	private Long assignedWarehouseId;
	private Integer status;

	public RequestParamsDTO(DTParameters params, Date fromDate, Date toDate, Long requestWarehouseId,
			Long assignedWarehouseId, Integer status) {
		this.params = params;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.requestWarehouseId = requestWarehouseId;
		this.assignedWarehouseId = assignedWarehouseId;
		this.status = status;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public DTParameters getParams() {
		return params;
	}

	public void setParams(DTParameters params) {
		this.params = params;
	}

	public RequestParamsDTO() {
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Long getRequestWarehouseId() {
		return requestWarehouseId;
	}

	public void setRequestWarehouseId(Long requestWarehouseId) {
		this.requestWarehouseId = requestWarehouseId;
	}

	public Long getAssignedWarehouseId() {
		return assignedWarehouseId;
	}

	public void setAssignedWarehouseId(Long assignedWarehouseId) {
		this.assignedWarehouseId = assignedWarehouseId;
	}

}
