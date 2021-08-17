package com.drivojoy.inventory.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.drivojoy.inventory.models.Tag;
import com.drivojoy.inventory.models.Tax;

public class ItemDTO {

	private long id;
	/* BASIC DETAILS */
	private String code;
	private String description;
	private String unitOfMeasurement;
	private ItemCategoryDTO category;
	
	private Collection<Tag> tags = new ArrayList<>();
	private boolean active;
	private String vendorAlias;
	private Collection<Tax> taxes = new ArrayList<>();
	
	/* ITEM ATTRIBUTES */
	private Collection<ItemAttributeDTO> itemAttributes = new ArrayList<>();

	/* INVENTORY DETAILS */
	private String barcode;
	private double warningPoint;
	private double reorderPoint;
	private double reorderQuantity;
	private double availableQuantity;
	private Collection<ItemCountDTO> itemCount = new ArrayList<>();
	private double normalPrice;
	private double wholesalePrice;
	private boolean isSerialized;
	private boolean isTaxable;
	private Collection<ItemStorageLocationDTO> itemStorageLocations = new ArrayList<>();
	
	/* INTERNAL DETAILS */
	private Date createdDttm;
	private Collection<ItemRequestPair> itemDetails = new ArrayList<>();
	
	private int version;
	
	public ItemDTO(){}

	public ItemDTO(long id, String code, String description, String unitOfMeasurement, ItemCategoryDTO category,
			Collection<Tag> tags, boolean active, String vendorAlias, Collection<ItemAttributeDTO> itemAttributes,
			String barcode, double warningPoint, double reorderPoint, double reorderQuantity, double availableQuantity,
			Collection<ItemCountDTO> itemCount, double normalPrice, double wholesalePrice, Date createdDttm,
			Collection<ItemRequestPair> itemDetails, boolean isSerialized, Collection<Tax> taxes, boolean isTaxable,
			Collection<ItemStorageLocationDTO> itemStorageLocations) {
		super();
		this.id = id;
		this.code = code;
		this.description = description;
		this.unitOfMeasurement = unitOfMeasurement;
		this.category = category;
		this.tags = tags;
		this.active = active;
		this.vendorAlias = vendorAlias;
		this.itemAttributes = itemAttributes;
		this.barcode = barcode;
		this.warningPoint = warningPoint;
		this.reorderPoint = reorderPoint;
		this.reorderQuantity = reorderQuantity;
		this.availableQuantity = availableQuantity;
		this.itemCount = itemCount;
		this.normalPrice = normalPrice;
		this.wholesalePrice = wholesalePrice;
		this.createdDttm = createdDttm;
		this.itemDetails = itemDetails;
		this.isSerialized = isSerialized;
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

	public double getAvailableQuantity() {
		return availableQuantity;
	}

	public void setAvailableQuantity(double availableQuantity) {
		this.availableQuantity = availableQuantity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUnitOfMeasurement() {
		return unitOfMeasurement;
	}

	public void setUnitOfMeasurement(String unitOfMeasurement) {
		this.unitOfMeasurement = unitOfMeasurement;
	}

	public ItemCategoryDTO getCategory() {
		return category;
	}

	public void setCategory(ItemCategoryDTO category) {
		this.category = category;
	}

	public Collection<Tag> getTags() {
		return tags;
	}

	public void setTags(Collection<Tag> tags) {
		this.tags = tags;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getVendorAlias() {
		return vendorAlias;
	}

	public void setVendorAlias(String vendorAlias) {
		this.vendorAlias = vendorAlias;
	}

	public Collection<ItemAttributeDTO> getItemAttributes() {
		return itemAttributes;
	}

	public void setItemAttributes(Collection<ItemAttributeDTO> itemAttributes) {
		this.itemAttributes = itemAttributes;
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

	public Collection<ItemCountDTO> getItemCount() {
		return itemCount;
	}

	public void setItemCount(Collection<ItemCountDTO> itemCount) {
		this.itemCount = itemCount;
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

	public Date getCreatedDttm() {
		return createdDttm;
	}

	public void setCreatedDttm(Date createdDttm) {
		this.createdDttm = createdDttm;
	}

	public Collection<ItemRequestPair> getItemDetails() {
		return itemDetails;
	}

	public void setItemDetails(Collection<ItemRequestPair> itemDetails) {
		this.itemDetails = itemDetails;
	}
	
	public boolean isSerialized() {
		return isSerialized;
	}

	public void setSerialized(boolean isSerialized) {
		this.isSerialized = isSerialized;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Collection<Tax> getTaxes() {
		return taxes;
	}

	public void setTaxes(Collection<Tax> taxes) {
		this.taxes = taxes;
	}

	public boolean isTaxable() {
		return isTaxable;
	}

	public void setTaxable(boolean isTaxable) {
		this.isTaxable = isTaxable;
	}
	
	public Collection<ItemStorageLocationDTO> getItemStorageLocations() {
		return itemStorageLocations;
	}

	public void setItemStorageLocations(Collection<ItemStorageLocationDTO> itemStorageLocations) {
		this.itemStorageLocations = itemStorageLocations;
	}

	@Override
	public String toString() {
		return "ItemDTO [id=" + id + ", code=" + code + ", description=" + description + ", unitOfMeasurement="
				+ unitOfMeasurement + ", category=" + category + ", tags=" + tags + ", active=" + active
				+ ", vendorAlias=" + vendorAlias + ", taxes=" + taxes + ", itemAttributes=" + itemAttributes
				+ ", barcode=" + barcode + ", warningPoint=" + warningPoint + ", reorderPoint=" + reorderPoint
				+ ", reorderQuantity=" + reorderQuantity + ", availableQuantity=" + availableQuantity + ", itemCount=" + itemCount + ", normalPrice=" + normalPrice
				+ ", wholesalePrice=" + wholesalePrice + ", isSerialized=" + isSerialized + ", isTaxable=" + isTaxable
				+ ", itemStorageLocations=" + itemStorageLocations + ", createdDttm=" + createdDttm + ", itemDetails="
				+ itemDetails + ", version=" + version + "]";
	}

}
