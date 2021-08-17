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

import com.drivojoy.inventory.dto.ItemRequestPair;
import com.drivojoy.inventory.utils.Constants.SubkitStatus;

@Entity
public class Subkit {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@Column(unique = true, nullable = false)
	private String subkitNumber;
	private String kitNumber;
	@ElementCollection(fetch = FetchType.EAGER)
	@JoinTable(name = "subkit_item_details", joinColumns = @JoinColumn(name = "subkit_id"))
	private Collection<SalesOrderItemLine> items = new ArrayList<>();
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModDttm;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDttm;
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;
	@Version
	int version;
	private int status;
	@OneToOne
	private Warehouse location;

	public Subkit() {
		this.status = SubkitStatus.NEW.value();
	}

	@PrePersist
	protected void onCreate() {
		createdDttm = Calendar.getInstance().getTime();
		lastModDttm = Calendar.getInstance().getTime();
		version = 1;
	}

	@PreUpdate
	protected void onUpdate() {
		lastModDttm = Calendar.getInstance().getTime();
		++version;
	}

	public Subkit(long id, String subkitNumber, String kitNumber, Collection<SalesOrderItemLine> items,
			Date lastModDttm, Date createdDttm, Date date, int status, Warehouse location) {
		super();
		this.id = id;
		this.subkitNumber = subkitNumber;
		this.kitNumber = kitNumber;
		this.items = items;
		this.lastModDttm = lastModDttm;
		this.createdDttm = createdDttm;
		this.date = date;
		this.status = status;
		this.location = location;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSubkitNumber() {
		return subkitNumber;
	}

	public void setSubkitNumber(String subkitNumber) {
		this.subkitNumber = subkitNumber;
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getKitNumber() {
		return kitNumber;
	}

	public void setKitNumber(String kitNumber) {
		this.kitNumber = kitNumber;
	}

	public Warehouse getLocation() {
		return location;
	}

	public void setLocation(Warehouse location) {
		this.location = location;
	}
	
	public void updateItemDetails(String barcode, int status){
		try {
			SalesOrderItemLine existingEntry = null;
			int i = 0, index = -1;
			for(SalesOrderItemLine item: getItems()){
				if(item.getBarcode() != null){
					if(item.getBarcode().equals(barcode)){
						existingEntry = item;
						index = i;
						break;
					}
				}
				i++;
			}
			if (index != -1) {
				existingEntry.setStatus(status);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateScrapItemDetails(ItemRequestPair request, int status) {
		try {
			SalesOrderItemLine existingEntry = null;
			int i = 0, index = -1;
			for (SalesOrderItemLine item : getItems()) {
				if (item.getBarcode() != null) {
					if (item.getBarcode().equals(request.getBarcode())) {
						existingEntry = item;
						index = i;
						break;
					}
				}
				i++;
			}
			if (index != -1) {
				existingEntry.setSerialNumber(request.getVendorProductId());
				existingEntry.setScrapStatus(status);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "Subkit [id=" + id + ", subkitNumber=" + subkitNumber + ", kitNumber=" + kitNumber + ", items=" + items
				+ ", lastModDttm=" + lastModDttm + ", createdDttm=" + createdDttm + ", date=" + date + ", status="
				+ status + ", location=" + location + "]";
	}
}
