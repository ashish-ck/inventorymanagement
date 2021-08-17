package com.drivojoy.inventory.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
public class StockAdjustment {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	@Column(unique=true, nullable=false)
	private String orderNumber;
	@Temporal(TemporalType.DATE)
	private Date orderDate;
	private int status;
	@ElementCollection(fetch=FetchType.EAGER)
	@JoinTable(name="adjustment_item_details", joinColumns=@JoinColumn(name="order_id"))
	private Collection<SalesOrderItemLine> items = new ArrayList<>();
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModDttm;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDttm;
	@Version
	private int version;
	@OneToOne
	private Warehouse warehouse;
	
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
	
	public StockAdjustment(){}
	
	/**
	 * Stock adjustment order
	 * @param id Order Id
	 * @param orderNumber Order number
	 * @param orderDate Order Date
	 * @param status Status of adjustment order
	 * @param items Items adjusted in the order
	 * @param lastModDttm Last modified date and time
	 * @param createdDttm Created date and time
	 * @param version Row Version
	 * @param warehouse Warehouse for which adjustment is made
	 */
	public StockAdjustment(long id, String orderNumber, Date orderDate, int status,
			Collection<SalesOrderItemLine> items, Date lastModDttm, Date createdDttm, int version,
			Warehouse warehouse) {
		super();
		this.id = id;
		this.orderNumber = orderNumber;
		this.orderDate = orderDate;
		this.status = status;
		this.items = items;
		this.lastModDttm = lastModDttm;
		this.createdDttm = createdDttm;
		this.version = version;
		this.warehouse = warehouse;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
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

	public Collection<SalesOrderItemLine> getItems() {
		return items;
	}

	public void setItems(Collection<SalesOrderItemLine> items) {
		this.items = items;
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

	@Override
	public String toString() {
		return "StockAdjustment [id=" + id + ", orderNumber=" + orderNumber + ", orderDate=" + orderDate + ", status="
				+ status + ", items=" + items + ", lastModDttm=" + lastModDttm + ", createdDttm=" + createdDttm
				+ ", version=" + version + "]";
	}
}
