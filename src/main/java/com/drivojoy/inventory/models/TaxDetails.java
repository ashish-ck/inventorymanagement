package com.drivojoy.inventory.models;

import javax.persistence.Embeddable;

@Embeddable
public class TaxDetails {

	private String tax;
	private double rate;
	private double taxAmount;
	
	public TaxDetails(){}

	/**
	 * Details of tax paid for an order
	 * @param tax Tax code
	 * @param rate Rate of the tax
	 * @param taxAmount Tax amount
	 */
	public TaxDetails(String tax, double rate, double taxAmount) {
		super();
		this.tax = tax;
		this.rate = rate;
		this.taxAmount = taxAmount;
	}

	public String getTax() {
		return tax;
	}

	public void setTax(String tax) {
		this.tax = tax;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public double getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(double taxAmount) {
		this.taxAmount = taxAmount;
	}

	@Override
	public String toString() {
		return "TaxDetails [tax=" + tax + ", rate=" + rate + ", taxAmount=" + taxAmount + "]";
	}
	
	
	
}
