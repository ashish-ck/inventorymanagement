package com.drivojoy.inventory.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.drivojoy.inventory.dto.ItemRequestPair;
import com.drivojoy.inventory.helpers.UserAuthentication;

/**
 * Inventory transaction ledger
 * 
 * @author ashishsingh
 *
 */
@Entity
public class InventoryTransaction {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;
	@OneToOne
	private Warehouse warehouse;
	@ElementCollection
	@JoinTable(name="inv_txn_item_code_barcodes", joinColumns=@JoinColumn(name="inventory_transaction_id"))
	private List<String> item = new ArrayList<>();
	private int status;
	private String statusText;
	private String voucherReference;
	private String transactionType;
	@Temporal(TemporalType.DATE)
	private Date transactionDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date entryDttm;
	@Version
	private int version;
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModDttm;
	private String email;

	@PrePersist
	protected void onCreate() {
		entryDttm = Calendar.getInstance().getTime();
		lastModDttm = Calendar.getInstance().getTime();
		version = 1;
		UserAuthentication auth = (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();
		if (!(((Authentication) auth) instanceof AnonymousAuthenticationToken)) {
			email = auth.getEmail();
		}
	}

	@PreUpdate
	protected void onUpdate() {
		lastModDttm = Calendar.getInstance().getTime();
		version++;
		UserAuthentication auth = (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();
		if (!(((Authentication) auth) instanceof AnonymousAuthenticationToken)) {
			email = auth.getEmail();
		}
	}

	public InventoryTransaction() {
	}

	public InventoryTransaction(Long id, Warehouse warehouse, List<ItemRequestPair> request, int status,
			String statusText, double quantityBefore, double quantityAfter, double quantity, String voucherReference,
			String transactionType, Date transactionDate, Date entryDttm, int version, Date lastModDttm, String email) {
		super();
		this.id = id;
		this.warehouse = warehouse;
		this.item = new ArrayList<>();
		if(request != null)
			for (ItemRequestPair pair : request)
				this.item.add(pair.getItemCode() + ", " + pair.getBarcode());
		this.status = status;
		this.statusText = statusText;
		this.voucherReference = voucherReference;
		this.transactionType = transactionType;
		this.transactionDate = transactionDate;
		this.entryDttm = entryDttm;
		this.version = version;
		this.lastModDttm = lastModDttm;
		this.email = email;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public List<String> getItem() {
		return item;
	}

	public void setItem(List<String> item) {
		this.item = item;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStatusText() {
		return statusText;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public String getVoucherReference() {
		return voucherReference;
	}

	public void setVoucherReference(String voucherReference) {
		this.voucherReference = voucherReference;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
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

	public void setVersion(int verion) {
		this.version = verion;
	}

	public Date getLastModDttm() {
		return lastModDttm;
	}

	public void setLastModDttm(Date lastModDttm) {
		this.lastModDttm = lastModDttm;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "InventoryTransaction [id=" + id + ", warehouse=" + warehouse + ", item=" + item
				+ ", status=" + status + ", statusText=" + statusText + ", voucherReference=" + voucherReference
				+ ", transactionType=" + transactionType + ", transactionDate=" + transactionDate + ", entryDttm="
				+ entryDttm + ", version=" + version + ", lastModDttm=" + lastModDttm + ", email=" + email + "]";
	}
}
