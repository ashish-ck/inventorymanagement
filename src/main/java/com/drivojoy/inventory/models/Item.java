package com.drivojoy.inventory.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 * Inventory Item
 * @author ashishsingh
 *
 */
@Entity
public class Item {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	@Column(unique=true, nullable=false)
	private String code;
	private String vendorAlias;
	private String description;
	@OneToOne
	private UoM unitOfMeasurement;
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinTable(name="item_attributes", joinColumns=@JoinColumn(name="item_id"))
	private Collection<ItemAttribute> attributes = new ArrayList<ItemAttribute>();
	@OneToOne
	private ItemCategory category;
	@Column(unique=true, nullable=true)
	private String barcode;
	private double warningPoint = 0;
	private double reorderPoint = 0;
	private double reorderQuantity = 0;
	private double normalPrice = 0;
	private double wholesalePrice = 0;
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModDttm;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDttm;
	private boolean isActive = true;
	@ElementCollection(fetch=FetchType.EAGER)
	@JoinTable(name="item_count", joinColumns=@JoinColumn(name="item_id"))
	private Collection<ItemCount> itemCount = new ArrayList<>();
	@ElementCollection(fetch=FetchType.LAZY)
	@JoinTable(name="item_details", joinColumns=@JoinColumn(name="item_id"))
	private Collection<ItemDetails> itemDetails = new ArrayList<>();
	@ManyToMany(fetch=FetchType.EAGER)
	private Collection<Tag> tags = new ArrayList<>();
	private boolean isSerialized;
	private boolean isTaxable;
	@ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinTable(name="item_taxes", joinColumns=@JoinColumn(name="item_id"))
	private Collection<Tax> taxes = new ArrayList<Tax>();
	@ElementCollection(fetch=FetchType.LAZY)
	@JoinTable(name="item_storage_locations", joinColumns=@JoinColumn(name="item_id"))
	private Collection<ItemStorageLocation> itemStorageLocations = new ArrayList<>();
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
	
	public Item(){}

	/**
	 * 
	 * @param id Id of the item
	 * @param code Item code
	 * @param vendorAlias Name of item given by vendor
	 * @param description Description of the item
	 * @param unitOfMeasurement Unit of measurement for item
	 * @param attributes Attributes for an item
	 * @param category Category of the item
	 * @param barcode Item barcode
	 * @param warningPoint Warning point of item
	 * @param reorderPoint Reorder point of item
	 * @param reorderQuantity Reorder quantity for item
	 * @param normalPrice Normal Price
	 * @param wholesalePrice Wholesale Price
	 * @param lastModDttm Last modified date and time
	 * @param createdDttm Created date and time
	 * @param isActive Is Item Active
	 * @param itemCount Item inventory count details
	 * @param itemDetails Item inventory details
	 * @param tags Item tags
	 * @param isSerialized Is Item serialized
	 * @param version Item row version number
	 * @param taxes Item taxes
	 * @param isTaxable Is Item Taxable
	 * @param itemStorageLocations Item storage information details
	 */
	public Item(long id, String code, String vendorAlias, String description, UoM unitOfMeasurement,
			Collection<ItemAttribute> attributes, ItemCategory category, String barcode, double warningPoint,
			double reorderPoint, double reorderQuantity, double normalPrice, double wholesalePrice,
			Date lastModDttm, Date createdDttm, boolean isActive, Collection<ItemCount> itemCount
			,Collection<ItemDetails> itemDetails, Collection<Tag> tags, boolean isSerialized, int version
			,Collection<Tax> taxes, boolean isTaxable, Collection<ItemStorageLocation> itemStorageLocations) {
		super();
		this.id = id;
		this.code = code;
		this.vendorAlias = vendorAlias;
		this.description = description;
		this.unitOfMeasurement = unitOfMeasurement;
		this.attributes = attributes;
		this.category = category;
		this.barcode = barcode;
		this.warningPoint = warningPoint;
		this.reorderPoint = reorderPoint;
		this.reorderQuantity = reorderQuantity;
		this.normalPrice = normalPrice;
		this.wholesalePrice = wholesalePrice;
		this.lastModDttm = lastModDttm;
		this.createdDttm = createdDttm;
		this.isActive = isActive;
		this.itemCount = itemCount;
		this.itemDetails = itemDetails;
		this.tags = tags;
		this.isSerialized = isSerialized;
		this.version = version;
		this.isTaxable = isTaxable;
		this.taxes = taxes;
		this.itemStorageLocations = itemStorageLocations;
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

	public String getVendorAlias() {
		return vendorAlias;
	}

	public void setVendorAlias(String vendorAlias) {
		this.vendorAlias = vendorAlias;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public UoM getUnitOfMeasurement() {
		return unitOfMeasurement;
	}

	public void setUnitOfMeasurement(UoM unitOfMeasurement) {
		this.unitOfMeasurement = unitOfMeasurement;
	}

	public Collection<ItemAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Collection<ItemAttribute> attributes) {
		this.attributes = attributes;
	}

	public ItemCategory getCategory() {
		return category;
	}

	public void setCategory(ItemCategory category) {
		this.category = category;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public double getWarningPoint() {
		return warningPoint;
	}

	public void setWarningPoint(double warningPoint) {
		this.warningPoint = warningPoint;
	}

	public double getReorderPoint() {
		return reorderPoint;
	}

	public void setReorderPoint(double reorderPoint) {
		this.reorderPoint = reorderPoint;
	}

	public double getReorderQuantity() {
		return reorderQuantity;
	}

	public void setReorderQuantity(double reorderQuantity) {
		this.reorderQuantity = reorderQuantity;
	}

	public double getNormalPrice() {
		return normalPrice;
	}

	public void setNormalPrice(double normalPrice) {
		this.normalPrice = normalPrice;
	}

	public double getWholesalePrice() {
		return wholesalePrice;
	}

	public void setWholesalePrice(double wholesalePrice) {
		this.wholesalePrice = wholesalePrice;
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

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public Collection<ItemCount> getItemCount() {
		return itemCount;
	}

	public void setItemCount(Collection<ItemCount> itemCount) {
		this.itemCount = itemCount;
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
	public Collection<Tag> getTags() {
		return tags;
	}

	public void setTags(Collection<Tag> tags) {
		this.tags = tags;
	}

	public boolean isSerialized() {
		return isSerialized;
	}

	public void setSerialized(boolean isSerialized) {
		this.isSerialized = isSerialized;
	}

	public boolean isTaxable() {
		return isTaxable;
	}

	public void setTaxable(boolean isTaxable) {
		this.isTaxable = isTaxable;
	}

	public Collection<Tax> getTaxes() {
		return taxes;
	}

	public void setTaxes(Collection<Tax> taxes) {
		this.taxes = taxes;
	}

	public Collection<ItemStorageLocation> getItemStorageLocations() {
		return itemStorageLocations;
	}

	public void setItemStorageLocations(Collection<ItemStorageLocation> itemStorageLocations) {
		this.itemStorageLocations = itemStorageLocations;
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", code=" + code + ", vendorAlias=" + vendorAlias + ", description=" + description
				+ ", unitOfMeasurement=" + unitOfMeasurement + ", attributes=" + attributes + ", category=" + category
				+ ", barcode=" + barcode + ", warningPoint=" + warningPoint + ", reorderPoint=" + reorderPoint
				+ ", reorderQuantity=" + reorderQuantity + ", normalPrice=" + normalPrice + ", wholesalePrice="
				+ wholesalePrice + ", lastModDttm=" + lastModDttm + ", createdDttm=" + createdDttm + ", isActive="
				+ isActive + ", itemCount=" + itemCount + ", itemDetails=" + itemDetails + ", tags=" + tags
				+ ", isSerialized=" + isSerialized + ", isTaxable=" + isTaxable + ", taxes=" + taxes
				+ ", itemStorageLocations=" + itemStorageLocations + ", version=" + version + "]";
	}

	@Override
	public boolean equals(Object obj) {
		return this.code.equals(obj);
	};
	
}
