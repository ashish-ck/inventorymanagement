package com.drivojoy.inventory.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

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

/**
 * Attribute defined for an item
 * @author ashishsingh
 *
 */
@Entity
public class ItemAttribute {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	@OneToOne
	private Attribute attribute;
	@ElementCollection(fetch=FetchType.EAGER)
	@JoinTable(name="item_attribute_values", joinColumns=@JoinColumn(name="item_attribute_id"))
	private Collection<String> value = new ArrayList<>();
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModDttm;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDttm;
	@Version
	private int version;
	
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
	
	public ItemAttribute(){}
	
	/**
	 * 
	 * @param id Entity Id
	 * @param attribute Attribute definition from the attribute master entity
	 * @param value Possible values of attribute for this item
	 * @param version Row version
	 */
	public ItemAttribute(long id, Attribute attribute, Collection<String> value, int version) {
		super();
		this.id = id;
		this.attribute = attribute;
		this.value = value;
		this.version = version;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public Collection<String> getValue() {
		return value;
	}

	public void setValue(Collection<String> value) {
		this.value = value;
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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "ItemAttribute [id=" + id + ", attribute=" + attribute + ", value=" + value + "]";
	}

	
}
