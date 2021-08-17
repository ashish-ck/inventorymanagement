package com.drivojoy.inventory.models;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

@Entity
public class Tax {


	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	@Column(unique=true)
	private String code;
	@Column(nullable=false, unique=false)
	private String series;
	private String description;
	private double rate;
	private boolean isCompounded;
	@OneToOne
	private Tax parentTax;
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
		version ++;
	}

	public Tax(){}
	
	/**
	 * 
	 * @param id Tax Id
	 * @param code Tax code
	 * @param description Tax description
	 * @param rate Tax rate
	 * @param isCompounded Is the tax compounded
	 * @param parentTax Parent Tax
	 * @param entryDttm Entry date and time
	 * @param version Row version
	 * @param lastModDttm Last modified date and time
	 */
	public Tax(long id, String code, String description, double rate, boolean isCompounded, Tax parentTax,
			Date entryDttm, int version, Date lastModDttm) {
		super();
		this.id = id;
		this.code = code;
		this.description = description;
		this.rate = rate;
		this.isCompounded = isCompounded;
		this.parentTax = parentTax;
		this.entryDttm = entryDttm;
		this.version = version;
		this.lastModDttm = lastModDttm;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public boolean isCompounded() {
		return isCompounded;
	}

	public void setCompounded(boolean isCompounded) {
		this.isCompounded = isCompounded;
	}

	public Tax getParentTax() {
		return parentTax;
	}

	public void setParentTax(Tax parentTax) {
		this.parentTax = parentTax;
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

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	@Override
	public String toString() {
		return "Tax [id=" + id + ", code=" + code + ", series=" + series + ", description=" + description + ", rate="
				+ rate + ", isCompounded=" + isCompounded + ", parentTax=" + parentTax + ", entryDttm=" + entryDttm
				+ ", version=" + version + ", lastModDttm=" + lastModDttm + "]";
	}
	
}
