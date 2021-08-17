package com.drivojoy.inventory.models;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 * Entity that records opening and closing balances of a item in a particular
 * warehouse
 * 
 * @author ashishsingh
 *
 */
@Entity
@Table
public class InventoryBalances {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private long id;
	@Temporal(TemporalType.DATE)
	private Date date;
	@OneToOne
	private Item item;
	@OneToOne
	private Warehouse warehouse;
	private int openingStatus;
	private String openingStatusText;
	private int closingStatus;
	private String closingStatusText;
	@Temporal(TemporalType.TIMESTAMP)
	private Date entryDttm;
	@Version
	private int version;
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModDttm;

	@PrePersist
	protected void onCreate() {
		entryDttm = Calendar.getInstance().getTime();
		lastModDttm = Calendar.getInstance().getTime();
		version = 1;
	}

	@PreUpdate
	protected void onUpdate() {
		lastModDttm = Calendar.getInstance().getTime();
		version++;
	}

	public InventoryBalances() {
	}

	public InventoryBalances(long id, Date date, Item item, int openingStatus, String openingStatusText,
			int closingStatus, String closingStatusText, Date entryDttm, int version, Date lastModDttm,
			Warehouse warehouse) {
		super();
		this.id = id;
		this.date = date;
		this.item = item;
		this.openingStatus = openingStatus;
		this.openingStatusText = openingStatusText;
		this.closingStatus = closingStatus;
		this.closingStatusText = closingStatusText;
		this.entryDttm = entryDttm;
		this.version = version;
		this.lastModDttm = lastModDttm;
		this.warehouse = warehouse;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public int getOpeningBalance() {
		return openingStatus;
	}

	public void setOpeningBalance(int openingStatus) {
		this.openingStatus = openingStatus;
	}

	public int getClosingBalance() {
		return closingStatus;
	}

	public void setClosingBalance(int closingStatus) {
		this.closingStatus = closingStatus;
	}

	public Date getEntryDttm() {
		return entryDttm;
	}

	public void setEntryDttm(Date entryDttm) {
		this.entryDttm = entryDttm;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Date getLastModDttm() {
		return lastModDttm;
	}

	public void setLastModDttm(Date lastModDttm) {
		this.lastModDttm = lastModDttm;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public int getOpeningStatus() {
		return openingStatus;
	}

	public void setOpeningStatus(int openingStatus) {
		this.openingStatus = openingStatus;
	}

	public String getOpeningStatusText() {
		return openingStatusText;
	}

	public void setOpeningStatusText(String openingStatusText) {
		this.openingStatusText = openingStatusText;
	}

	public int getClosingStatus() {
		return closingStatus;
	}

	public void setClosingStatus(int closingStatus) {
		this.closingStatus = closingStatus;
	}

	public String getClosingStatusText() {
		return closingStatusText;
	}

	public void setClosingStatusText(String closingStatusText) {
		this.closingStatusText = closingStatusText;
	}

	@Override
	public String toString() {
		return "InventoryBalances [id=" + id + ", date=" + date + ", item=" + item + ", warehouse=" + warehouse
				+ ", openingStatus=" + openingStatus + ", openingStatusText=" + openingStatusText + ", closingStatus="
				+ closingStatus + ", closingStatusText=" + closingStatusText + ", entryDttm=" + entryDttm + ", version="
				+ version + ", lastModDttm=" + lastModDttm + "]";
	}
}
