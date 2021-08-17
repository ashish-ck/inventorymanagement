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

/**
 * Item category entity
 * @author ashishsingh
 *
 */
@Entity
public class ItemCategory {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	@Column(unique=true, nullable=false)
	private String name;
	private String description;
	@OneToOne
	private ItemCategory parentCategory;
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
	
	public ItemCategory(){}

	/**
	 * 
	 * @param id Entity Id
	 * @param name Category name
	 * @param description Category Description
	 * @param parentCategory Parent Category
	 * @param version Row version
	 */
	public ItemCategory(long id, String name, String description, ItemCategory parentCategory, int version) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.parentCategory = parentCategory;
		this.version = version;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ItemCategory getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(ItemCategory parentCategory) {
		this.parentCategory = parentCategory;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "ItemCategory [id=" + id + ", name=" + name + ", description=" + description + ", parentCategory="
				+ parentCategory + ", lastModDttm=" + lastModDttm + ", createdDttm=" + createdDttm + ", version="
				+ version + "]";
	};

}
