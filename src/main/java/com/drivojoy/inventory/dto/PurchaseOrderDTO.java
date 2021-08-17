package com.drivojoy.inventory.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.drivojoy.inventory.models.PaymentDetails;
import com.drivojoy.inventory.models.TaxDetails;

public class PurchaseOrderDTO {

	private long id;
	private String orderNo;
	private VendorDTO vendor;
	private Collection<ItemOrderLineDTO> items = new ArrayList<>();
	private Collection<String> status = new ArrayList<>();
	@DateTimeFormat(iso = ISO.DATE)
	private Date date;
	private Collection<ItemRequestPair> itemDetails = new ArrayList<>();
	private Collection<ItemRequestPair> returnItemDetails = new ArrayList<>();
	private double taxes;
	private double total;
	private double amtPaid;
	private double amtBalance;
	private String kitNumber;
	private Collection<PaymentDetails> paymentDetails = new ArrayList<>();	
	private Collection<TaxDetails> taxDetails = new ArrayList<>();
	private WarehouseNetworkDTO shippingWarehouse;
	private String contactPerson;
	private String contactNumber;
	private double subTotal;
	private String invoiceNo;
	@DateTimeFormat(iso = ISO.DATE)
	private Date invoiceDate;
	@DateTimeFormat(iso = ISO.DATE)
	private Date shippingDate;
	private int version;
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date createdDttm;
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date lastModDttm;
	private Double vatTax;
	private Boolean isOnTheFly = false;
	
	public PurchaseOrderDTO(){}
	
	public PurchaseOrderDTO(long id, String orderNo, VendorDTO vendor, Collection<ItemOrderLineDTO> items,
			Collection<String> status, Date date, Collection<ItemRequestPair> itemDetails, double taxes, double total, double amtPaid, double amtBalance, Collection<PaymentDetails> paymentDetails,
			Collection<TaxDetails> taxDetails, WarehouseNetworkDTO shippingWarehouse, String contactPerson, 
			String contactNumber, double subTotal, String invoiceNo, Date invoiceDate, int version, 
			Date createdDttm, Date lastModDttm, Collection<ItemRequestPair> returnItemDetails, String kitNumber, Double vatTax, Boolean isOnTheFly) {
		super();
		this.id = id;
		this.orderNo = orderNo;
		this.vendor = vendor;
		this.items = items;
		this.status = status;
		this.date = date;
		this.itemDetails = itemDetails;
		this.taxes = taxes;
		this.total = total;
		this.amtPaid = amtPaid;
		this.amtBalance = amtBalance;
		this.paymentDetails = paymentDetails;
		this.taxDetails = taxDetails;
		this.shippingWarehouse = shippingWarehouse;
		this.contactNumber = contactNumber;
		this.contactPerson = contactPerson;
		this.subTotal = subTotal;
		this.invoiceNo = invoiceNo;
		this.invoiceDate = invoiceDate;
		this.version = version;
		this.createdDttm = createdDttm;
		this.lastModDttm = lastModDttm;
		this.returnItemDetails = returnItemDetails;
		this.kitNumber = kitNumber;
		this.vatTax = vatTax;
		this.isOnTheFly = isOnTheFly;
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

	public VendorDTO getVendor() {
		return vendor;
	}

	public void setVendor(VendorDTO vendor) {
		this.vendor = vendor;
	}

	public Collection<ItemOrderLineDTO> getItems() {
		return items;
	}

	public void setItems(Collection<ItemOrderLineDTO> items) {
		this.items = items;
	}

	public Collection<String> getStatus() {
		return status;
	}

	public void setStatus(Collection<String> status) {
		if(status != null)
			this.status = status;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Collection<ItemRequestPair> getItemDetails() {
		return itemDetails;
	}

	public void setItemDetails(Collection<ItemRequestPair> itemDetails) {
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

	public WarehouseNetworkDTO getShippingWarehouse() {
		return shippingWarehouse;
	}

	public void setShippingWarehouse(WarehouseNetworkDTO shippingWarehouse) {
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

	public Date getShippingDate() {
		return shippingDate;
	}

	public void setShippingDate(Date shippingDate) {
		this.shippingDate = shippingDate;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Date getCreatedDttm() {
		return createdDttm;
	}

	public void setCreatedDttm(Date createdDttm) {
		this.createdDttm = createdDttm;
	}

	public Date getLastModDttm() {
		return lastModDttm;
	}

	public void setLastModDttm(Date lastModDttm) {
		this.lastModDttm = lastModDttm;
	}

	public Collection<ItemRequestPair> getReturnItemDetails() {
		return returnItemDetails;
	}

	public void setReturnItemDetails(Collection<ItemRequestPair> returnItemDetails) {
		this.returnItemDetails = returnItemDetails;
	}

	public String getKitNumber() {
		return kitNumber;
	}

	public void setKitNumber(String kitNumber) {
		this.kitNumber = kitNumber;
	}

	public Double getVatTax() {
		return vatTax;
	}

	public void setVatTax(Double vatTax) {
		this.vatTax = vatTax;
	}

	public Boolean getIsOnTheFly() {
		return isOnTheFly;
	}

	public void setIsOnTheFly(Boolean isOnTheFly) {
		this.isOnTheFly = isOnTheFly;
	}

	@Override
	public String toString() {
		return "PurchaseOrderDTO [id=" + id + ", orderNo=" + orderNo + ", vendor=" + vendor + ", items=" + items
				+ ", status=" + status + ", date=" + date + ", itemDetails=" + itemDetails + ", returnItemDetails="
				+ returnItemDetails + ", taxes=" + taxes + ", total=" + total + ", amtPaid=" + amtPaid + ", amtBalance="
				+ amtBalance + ", kitNumber=" + kitNumber + ", paymentDetails=" + paymentDetails + ", taxDetails="
				+ taxDetails + ", shippingWarehouse=" + shippingWarehouse + ", contactPerson=" + contactPerson
				+ ", contactNumber=" + contactNumber + ", subTotal=" + subTotal + ", invoiceNo=" + invoiceNo
				+ ", invoiceDate=" + invoiceDate + ", shippingDate=" + shippingDate + ", version=" + version
				+ ", createdDttm=" + createdDttm + ", lastModDttm=" + lastModDttm + ", vatTax=" + vatTax + ", isOnTheFly=" + isOnTheFly + "]";
	}

}
