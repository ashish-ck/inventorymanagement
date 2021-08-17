package com.drivojoy.inventory.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

@Entity
public class TransferOrder {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	@Column(unique=true, nullable=false)
	private String orderNumber;
	@Temporal(TemporalType.DATE)
	private Date orderDate;
	@Temporal(TemporalType.DATE)
	private Date sendDate;
	@Temporal(TemporalType.DATE)
	private Date receiveDate;
	private String kitNumber;
	private int status;
	@ElementCollection
	@JoinTable(name="transfer_order_items", joinColumns=@JoinColumn(name="order_id"))
	private Collection<ItemOrderLine> items = new ArrayList<>();
	@ElementCollection
	@JoinTable(name="transfer_order_item_details", joinColumns=@JoinColumn(name="order_id"))
	private Collection<ItemDetails> itemDetails = new ArrayList<>();
	@ElementCollection
	@JoinTable(name="transfer_order_recvd_item_details", joinColumns=@JoinColumn(name="order_id"))
	private Collection<ItemDetails> recvdItemDetails = new ArrayList<>();
	@OneToOne
	private Warehouse fromWarehouse;
	@OneToOne
	private Warehouse toWarehouse;
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModDttm;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDttm;
	@Version
	private int version;
	
	@PrePersist
	protected void onCreate() {
		createdDttm = Calendar.getInstance().getTime();
		lastModDttm = Calendar.getInstance().getTime();
		version = 1;
	}

	@PreUpdate
	protected void onUpdate() {
		lastModDttm = Calendar.getInstance().getTime();
		version ++;
	}

	public TransferOrder() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * @param id Order Id
	 * @param orderNumber Order number
	 * @param orderDate Order date
	 * @param status Status of the order
	 * @param items Items transfered in the order
	 * @param fromWarehouse Source warehouse
	 * @param toWarehouse Destination warehouse
	 * @param version Row version
	 * @param itemDetails Sent item details
	 * @param sendDate Items send date
	 * @param receiveDate Items receive date
	 * @param kitNumber Sales order number for which the transfer is made
	 * @param recvdItemDetails Received item details
	 */
	public TransferOrder(long id, String orderNumber, Date orderDate, int status, Collection<ItemOrderLine> items,
			Warehouse fromWarehouse, Warehouse toWarehouse, int version, Collection<ItemDetails> itemDetails, Date sendDate, Date receiveDate, String kitNumber, Collection<ItemDetails> recvdItemDetails) {
		super();
		this.id = id;
		this.orderNumber = orderNumber;
		this.orderDate = orderDate;
		this.status = status;
		this.items = items;
		this.fromWarehouse = fromWarehouse;
		this.toWarehouse = toWarehouse;
		this.version = version;
		this.itemDetails = itemDetails;
		this.sendDate = sendDate;
		this.receiveDate = receiveDate;
		this.kitNumber = kitNumber;
		this.recvdItemDetails = recvdItemDetails;
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

	public Collection<ItemOrderLine> getItems() {
		return items;
	}

	public void setItems(Collection<ItemOrderLine> items) {
		this.items = items;
	}

	public Warehouse getFromWarehouse() {
		return fromWarehouse;
	}

	public void setFromWarehouse(Warehouse fromWarehouse) {
		this.fromWarehouse = fromWarehouse;
	}

	public Warehouse getToWarehouse() {
		return toWarehouse;
	}

	public void setToWarehouse(Warehouse toWarehouse) {
		this.toWarehouse = toWarehouse;
	}

	public Date getLastModDttm() {
		return lastModDttm;
	}

	public void setLastModDttm(Date lastModDttm) {
		this.lastModDttm = lastModDttm;
	}

	public Date getCreatedDttm() {
		return createdDttm;
	}

	public void setCreatedDttm(Date createdDttm) {
		this.createdDttm = createdDttm;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Collection<ItemDetails> getItemDetails() {
		return itemDetails;
	}

	public void setItemDetails(Collection<ItemDetails> itemDetails) {
		this.itemDetails = itemDetails;
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

	public Collection<ItemDetails> getRecvdItemDetails() {
		return recvdItemDetails;
	}

	public void setRecvdItemDetails(Collection<ItemDetails> recvdItemDetails) {
		this.recvdItemDetails = recvdItemDetails;
	}

	@Override
	public String toString() {
		return "TransferOrder [id=" + id + ", orderNumber=" + orderNumber + ", orderDate=" + orderDate + ", sendDate="
				+ sendDate + ", receiveDate=" + receiveDate + ", kitNumber=" + kitNumber + ", status=" + status
				+ ", items=" + items + ", itemDetails=" + itemDetails + ", recvdItemDetails=" + recvdItemDetails
				+ ", fromWarehouse=" + fromWarehouse + ", toWarehouse=" + toWarehouse + ", lastModDttm=" + lastModDttm
				+ ", createdDttm=" + createdDttm + ", version=" + version + "]";
	}
	
	
	
}
