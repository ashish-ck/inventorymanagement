package com.drivojoy.inventory.dto;

import java.util.Date;

public class CountByDate {

	private double qtyDelivered;
	private double qtyReturnExpected;
	private double qtyRestocked;
	private double qtyOnTheFly;
	private Date date;
	
	public CountByDate(){}
	
	public CountByDate(double qtyDelivered, double qtyReturnExpected, double qtyRestocked, double qtyOnTheFly,
			Date date) {
		super();
		this.qtyDelivered = qtyDelivered;
		this.qtyReturnExpected = qtyReturnExpected;
		this.qtyRestocked = qtyRestocked;
		this.qtyOnTheFly = qtyOnTheFly;
		this.date = date;
	}

	public double getQtyDelivered() {
		return qtyDelivered;
	}
	public void setQtyDelivered(double qtyDelivered) {
		this.qtyDelivered = qtyDelivered;
	}
	public double getQtyReturnExpected() {
		return qtyReturnExpected;
	}
	public void setQtyReturnExpected(double qtyReturnExpected) {
		this.qtyReturnExpected = qtyReturnExpected;
	}
	public double getQtyRestocked() {
		return qtyRestocked;
	}
	public void setQtyRestocked(double qtyRestocked) {
		this.qtyRestocked = qtyRestocked;
	}
	public double getQtyOnTheFly() {
		return qtyOnTheFly;
	}
	public void setQtyOnTheFly(double qtyOnTheFly) {
		this.qtyOnTheFly = qtyOnTheFly;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "CountByDate [qtyDelivered=" + qtyDelivered + ", qtyReturnExpected=" + qtyReturnExpected
				+ ", qtyRestocked=" + qtyRestocked + ", qtyOnTheFly=" + qtyOnTheFly + ", date=" + date + "]";
	}
	
	
	
}
