package com.drivojoy.inventory.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

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
public class PurchaseOrder {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	private String orderNo;
	@OneToOne
	private Vendor vendor;
	private String kitNumber;
	@ElementCollection
	@JoinTable(name="order_items", joinColumns=@JoinColumn(name="order_id"))
	private Collection<ItemOrderLine> items = new ArrayList<>();
	@ElementCollection
	@JoinTable(name="order_status_changes", joinColumns=@JoinColumn(name="order_id"))
	private Collection<String> status = new ArrayList<>();
	@Temporal(TemporalType.DATE)
	private Date date;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDttm;
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModDttm;
	@Version
	private int version;
	@ElementCollection
	@JoinTable(name="order_item_details", joinColumns=@JoinColumn(name="order_id"))
	private Collection<ItemDetails> itemDetails = new ArrayList<>();
	@ElementCollection
	@JoinTable(name="order_return_item_details", joinColumns=@JoinColumn(name="order_id"))
	private Collection<ItemDetails> returnItemDetails = new ArrayList<>();
	private double taxes;
	private double total;
	private double amtPaid;
	private double amtBalance;
	@ElementCollection
	@JoinTable(name="order_payment_details", joinColumns=@JoinColumn(name="order_id"))
	private Collection<PaymentDetails> paymentDetails = new ArrayList<>();	
	@ElementCollection
	@JoinTable(name="order_tax_details", joinColumns=@JoinColumn(name="order_id"))
	private Collection<TaxDetails> taxDetails = new ArrayList<>();
	@OneToOne
	private Warehouse shippingWarehouse;
	private String contactPerson;
	private String contactNumber;
	private double subTotal;
	private String invoiceNo;
	@Temporal(TemporalType.DATE)
	private Date invoiceDate;
	private Double vatTax;
	private Boolean isOnTheFly = false;
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
	
	public PurchaseOrder(){}

	/**
	 * Entity for Purchase Order
	 * @param id Entity Id
	 * @param orderNo Purchase Order number
	 * @param vendor Vendor from whom items are being purchased
	 * @param items Items ordered in the order
	 * @param status Status of the order
	 * @param date Date of the order
	 * @param createdDttm Created date and time
	 * @param version Row Version
	 * @param itemDetails Details of the items delivered
	 * @param taxes Order taxes
	 * @param total Order total
	 * @param amtPaid Amount paid
	 * @param amtBalance Amount yet to be paid 
	 * @param paymentDetails Payment details
	 * @param taxDetails Tax details
	 * @param shippingWarehouse Warehouse where items are to be shipped
	 * @param contactPerson Contact person from vendor
	 * @param contactNumber Contact number
	 * @param subTotal Sub-total without taxes
	 * @param invoiceNo Order invoice number
	 * @param invoiceDate Order invoice date
	 * @param lastModDttm Last modified date and time
	 * @param returnItemDetails Returned items details
	 * @param kitNumber Kit number for which the Purchase order is placed
	 * @param vatTax Vat tax
	 * @param isOnTheFly Is on the fly
	 */
	public PurchaseOrder(long id, String orderNo, Vendor vendor, Collection<ItemOrderLine> items,
			Collection<String> status, Date date, Date createdDttm, int version, Collection<ItemDetails> itemDetails,
			double taxes, double total, double amtPaid, double amtBalance, Collection<PaymentDetails> paymentDetails,
			Collection<TaxDetails> taxDetails, Warehouse shippingWarehouse, String contactPerson, 
			String contactNumber, double subTotal, String invoiceNo, Date invoiceDate, 
			Date lastModDttm, Collection<ItemDetails> returnItemDetails, String kitNumber, Double vatTax, Boolean isOnTheFly) {
		super();
		this.id = id;
		this.orderNo = orderNo;
		this.vendor = vendor;
		this.items = items;
		this.status = status;
		this.date = date;
		this.createdDttm = createdDttm;
		this.version = version;
		this.itemDetails = itemDetails;
		this.taxes = taxes;
		this.total = total;
		this.amtPaid = amtPaid;
		this.amtBalance = amtBalance;
		this.paymentDetails = paymentDetails;
		this.taxDetails = taxDetails;
		this.shippingWarehouse = shippingWarehouse;
		this.contactPerson = contactPerson;
		this.contactNumber = contactNumber;
		this.subTotal = subTotal;
		this.invoiceDate = invoiceDate;
		this.invoiceNo = invoiceNo;
		this.lastModDttm = lastModDttm;
		this.returnItemDetails = returnItemDetails;
		this.kitNumber = kitNumber;
		this.vatTax = vatTax;
		this.isOnTheFly = isOnTheFly;
	}

	public Double getVatTax() {
		return vatTax;
	}

	public void setVatTax(Double vatTax) {
		this.vatTax = vatTax;
	}

	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getOrderNo() {
		return orderNo;
	}


	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}


	public Vendor getVendor() {
		return vendor;
	}


	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}


	public Collection<ItemOrderLine> getItems() {
		return items;
	}


	public void setItems(Collection<ItemOrderLine> items) {
		this.items = items;
	}


	public Collection<String> getStatus() {
		return status;
	}


	public void setStatus(Collection<String> status) {
		this.status = status;
	}


	public Date getDate() {
		return date;
	}


	public void setDate(Date date) {
		this.date = date;
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


	public double getTaxes() {
		return taxes;
	}


	public void setTaxes(double taxes) {
		this.taxes = taxes;
	}


	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public double getAmtPaid() {
		return amtPaid;
	}

	public void setAmtPaid(double amtPaid) {
		this.amtPaid = amtPaid;
	}

	public double getAmtBalance() {
		return amtBalance;
	}

	public void setAmtBalance(double amtBalance) {
		this.amtBalance = amtBalance;
	}

	public Collection<PaymentDetails> getPaymentDetails() {
		return paymentDetails;
	}

	public void setPaymentDetails(Collection<PaymentDetails> paymentDetails) {
		this.paymentDetails = paymentDetails;
	}

	public Collection<TaxDetails> getTaxDetails() {
		return taxDetails;
	}

	public void setTaxDetails(Collection<TaxDetails> taxDetails) {
		this.taxDetails = taxDetails;
	}

	public Warehouse getShippingWarehouse() {
		return shippingWarehouse;
	}

	public void setShippingWarehouse(Warehouse shippingWarehouse) {
		this.shippingWarehouse = shippingWarehouse;
	}


	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public double getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(double subTotal) {
		this.subTotal = subTotal;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}


	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}


	public Date getInvoiceDate() {
		return invoiceDate;
	}


	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}


	public Date getLastModDttm() {
		return lastModDttm;
	}


	public void setLastModDttm(Date lastModDttm) {
		this.lastModDttm = lastModDttm;
	}
	
	public Collection<ItemDetails> getReturnItemDetails() {
		return returnItemDetails;
	}

	public void setReturnItemDetails(Collection<ItemDetails> returnItemDetails) {
		this.returnItemDetails = returnItemDetails;
	}

	public String getKitNumber() {
		return kitNumber;
	}

	public void setKitNumber(String kitNumber) {
		this.kitNumber = kitNumber;
	}

	public Boolean getIsOnTheFly() {
		return isOnTheFly;
	}

	public void setIsOnTheFly(Boolean isOnTheFly) {
		this.isOnTheFly = isOnTheFly;
	}

	@Override
	public String toString() {
		return "PurchaseOrder [id=" + id + ", orderNo=" + orderNo + ", vendor=" + vendor + ", kitNumber=" + kitNumber
				+ ", items=" + items + ", status=" + status + ", date=" + date + ", createdDttm=" + createdDttm
				+ ", lastModDttm=" + lastModDttm + ", version=" + version + ", itemDetails=" + itemDetails
				+ ", returnItemDetails=" + returnItemDetails + ", taxes=" + taxes + ", total=" + total + ", amtPaid="
				+ amtPaid + ", amtBalance=" + amtBalance + ", paymentDetails=" + paymentDetails + ", taxDetails="
				+ taxDetails + ", shippingWarehouse=" + shippingWarehouse + ", contactPerson=" + contactPerson
				+ ", contactNumber=" + contactNumber + ", subTotal=" + subTotal + ", invoiceNo=" + invoiceNo
				+ ", invoiceDate=" + invoiceDate + ", tax=" + vatTax + ", isOnTheFly=" + isOnTheFly + "]";
	}


	
}
