package com.drivojoy.inventory.models;

import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Embeddable
public class PaymentDetails {

	private String paymentMode;
	@Temporal(TemporalType.DATE)
	private Date paymentDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date entryDate;
	private double amountPaid;
	private String transactionId;
	
	public PaymentDetails(){}

	/**
	 * Entity to record payment details for an order
	 * @param paymentMode Payment Mode
	 * @param paymentDate Payment Date
	 * @param entryDate Entry Date
	 * @param amountPaid Amount Paid
	 * @param transactionId Transaction Id
	 */
	public PaymentDetails(String paymentMode, Date paymentDate, Date entryDate, double amountPaid,
			String transactionId) {
		super();
		this.paymentMode = paymentMode;
		this.paymentDate = paymentDate;
		this.entryDate = entryDate;
		this.amountPaid = amountPaid;
		this.transactionId = transactionId;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	public double getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(double amountPaid) {
		this.amountPaid = amountPaid;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	@Override
	public String toString() {
		return "PaymentDetails [paymentMode=" + paymentMode + ", paymentDate=" + paymentDate + ", entryDate="
				+ entryDate + ", amountPaid=" + amountPaid + ", transactionId=" + transactionId + "]";
	}

	
	
	
}
