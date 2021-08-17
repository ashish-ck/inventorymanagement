package com.drivojoy.inventory.models;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

@Entity
public class UserReports {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	@Temporal(TemporalType.TIMESTAMP)
	private Date entryDttm;
	@Version
	private int version;
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModDttm;
	@Column(nullable=false)
	private String userReference;
	@Column(nullable=false)
	private String reportUrl;
	
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
	
	public UserReports(){}

	/**
	 * 
	 * @param id Report Id
	 * @param userReference User reference for the report
	 * @param reportUrl Url of the report on the server
	 */
	public UserReports(long id, String userReference, String reportUrl) {
		super();
		this.id = id;
		this.userReference = userReference;
		this.reportUrl = reportUrl;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUserReference() {
		return userReference;
	}

	public void setUserReference(String userReference) {
		this.userReference = userReference;
	}

	public String getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
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

	@Override
	public String toString() {
		return "UserReports [id=" + id + ", entryDttm=" + entryDttm + ", version=" + version + ", lastModDttm="
				+ lastModDttm + ", userReference=" + userReference + ", reportUrl=" + reportUrl + "]";
	}
	
}
