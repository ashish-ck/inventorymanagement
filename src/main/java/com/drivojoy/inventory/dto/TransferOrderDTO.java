package com.drivojoy.inventory.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.drivojoy.inventory.utils.Constants;

public class TransferOrderDTO {
	
	private long id;
	private String orderNumber;
	@DateTimeFormat(iso = ISO.DATE)
	private Date orderDate;
	private int status;
	private Collection<ItemOrderLineDTO> items = new ArrayList<>();
	private WarehouseDTO fromWarehouse;
	private WarehouseDTO toWarehouse;
	private int version;
	private Collection<ItemRequestPair> itemDetails = new ArrayList<>();
	private Collection<ItemRequestPair> recvdItemDetails = new ArrayList<>();
	private Date sendDate;
	private Date receiveDate;
	private String kitNumber;
	private String statusText;
	
	
	
	public TransferOrderDTO() {
		super();
		// TODO Auto-generated constructor stub
	}



	public TransferOrderDTO(long id, String orderNumber, Date orderDate, int status,
			Collection<ItemRequestPair> itemDetails, WarehouseDTO fromWarehouse, WarehouseDTO toWarehouse,
			int version, Collection<ItemOrderLineDTO> items, Date sendDate, Date receiveDate, String kitNumber, Collection<ItemRequestPair> recvdItems) {
		super();
		this.id = id;
		this.orderNumber = orderNumber;
		this.orderDate = orderDate;
		this.status = status;
		this.itemDetails = itemDetails;
		this.fromWarehouse = fromWarehouse;
		this.toWarehouse = toWarehouse;
		this.version = version;
		this.items = items;
		this.sendDate = sendDate;
		this.receiveDate = receiveDate;
		this.kitNumber = kitNumber;
		this.recvdItemDetails = recvdItems;
	}

	public long getId() {
		return id;
	}



	public void setId(long id) {
		this.id = id;
	}



	public String getOrderNumber() {
		return orderNumber;
	}



	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}



	public Date getOrderDate() {
		return orderDate;
	}



	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}



	public int getStatus() {
		return status;
	}



	public void setStatus(int status) {
		this.status = status;
	}



	public Collection<ItemRequestPair> getItemDetails() {
		return itemDetails;
	}



	public void setItemDetails(Collection<ItemRequestPair> itemDetails) {
		this.itemDetails = itemDetails;
	}



	public WarehouseDTO getFromWarehouse() {
		return fromWarehouse;
	}



	public void setFromWarehouse(WarehouseDTO fromWarehouse) {
		this.fromWarehouse = fromWarehouse;
	}



	public WarehouseDTO getToWarehouse() {
		return toWarehouse;
	}



	public void setToWarehouse(WarehouseDTO toWarehouse) {
		this.toWarehouse = toWarehouse;
	}



	public int getVersion() {
		return version;
	}



	public void setVersion(int version) {
		this.version = version;
	}



	public Collection<ItemOrderLineDTO> getItems() {
		return items;
	}



	public void setItems(Collection<ItemOrderLineDTO> items) {
		this.items = items;
	}



	public Date getSendDate() {
		return sendDate;
	}



	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}



	public Date getReceiveDate() {
		return receiveDate;
	}



	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}



	public String getKitNumber() {
		return kitNumber;
	}



	public void setKitNumber(String kitNumber) {
		this.kitNumber = kitNumber;
	}


	public String getStatusText() {
		switch(this.status){
			case Constants._STATUS_OPEN: this.statusText = Constants._STATUS_OPEN_TEXT; break;
			case Constants._STATUS_DISPATCHED: this.statusText = Constants._STATUS_DISPATCHED_TEXT; break;
			case Constants._STATUS_DELIVERED: this.statusText = Constants._STATUS_DELIVERED_TEXT; break;
			case Constants._STATUS_FULFILLED: this.statusText = Constants._STATUS_FULFILLED_TEXT; break;
		}
		return this.statusText;
	}



	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}



	public Collection<ItemRequestPair> getRecvdItemDetails() {
		return recvdItemDetails;
	}



	public void setRecvdItemDetails(Collection<ItemRequestPair> recvdItemDetails) {
		this.recvdItemDetails = recvdItemDetails;
	}



	@Override
	public String toString() {
		return "TransferOrderDTO [id=" + id + ", orderNumber=" + orderNumber + ", orderDate=" + orderDate + ", status="
				+ status + ", items=" + items + ", fromWarehouse=" + fromWarehouse + ", toWarehouse=" + toWarehouse
				+ ", version=" + version + ", itemDetails=" + itemDetails + ", recvdItemDetails=" + recvdItemDetails
				+ ", sendDate=" + sendDate + ", receiveDate=" + receiveDate + ", kitNumber=" + kitNumber
				+ ", statusText=" + statusText + "]";
	}
	
	

}
