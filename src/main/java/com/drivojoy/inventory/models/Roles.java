package com.drivojoy.inventory.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 * 
 * @author ashishsingh
 *
 */
@Entity
public class Roles {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	@Temporal(TemporalType.TIMESTAMP)
	private Date entryDttm;
	@Version
	private int version;
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModDttm;
	@Column(unique=true, nullable=false)
	private String role;
	@ElementCollection
	@JoinTable(name="role_permissions", joinColumns=@JoinColumn(name="role_id"))
	private Collection<String> permissions = new ArrayList<String>();
	
	public Roles(){};
	
	/**
	 * Roles for SERM
	 * @param id Role Id
	 * @param role Name of role
	 * @param permissions Permissions of a particular role
	 */
	public Roles(long id, String role, Collection<String> permissions) {
		super();
		this.id = id;
		this.role = role;
		this.permissions = permissions;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Collection<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(Collection<String> permissions) {
		this.permissions = permissions;
	}

	@Override
	public String toString() {
		return "Roles [id=" + id + ", entryDttm=" + entryDttm + ", version=" + version + ", lastModDttm=" + lastModDttm
				+ ", role=" + role + ", permissions=" + permissions + "]";
	}
}
