package com.drivojoy.inventory.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import com.drivojoy.inventory.dto.ItemRequestPair;
import com.drivojoy.inventory.dto.ItemSalesRequest;
import com.drivojoy.inventory.utils.Constants.ItemStatus;
import com.drivojoy.inventory.utils.Constants.KitStatus;
import com.drivojoy.inventory.utils.Constants.SubkitStatus;

/**
 * 
 * @author ashishsingh
 *
 */
@Entity
public class Kit {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	@Column(unique=true, nullable=false)
	private String kitNumber;
	@ElementCollection(fetch=FetchType.EAGER)
	@JoinTable(name="kit_item_details", joinColumns=@JoinColumn(name="kit_id"))
	private Collection<SalesOrderItemLine> items = new ArrayList<>();
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinTable(name="kit_subkits", joinColumns=@JoinColumn(name="kit_id"))
	private Collection<Subkit> subkits = new ArrayList<>();
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModDttm;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDttm;
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;
	private int status;
	private int kitReopenTimes;
	@Version
	private int version;
	@OneToOne
	private Warehouse assignedWarehouse;
	@OneToOne
	private Warehouse requestWarehouse;
	
	transient private Map<String, SalesOrderItemLine> barcodeKitItemMap = new HashMap<>();
	transient private Map<String, SalesOrderItemLine> barcodeSubkitItemMap = new HashMap<>();
	transient private Map<String, List<SalesOrderItemLine>> itemCodeKitItemMap = new HashMap<>();
	transient private Map<String, List<SalesOrderItemLine>> itemCodeSubkitItemMap = new HashMap<>();
	transient private Map<String, Subkit> barcodeSubkitsMap = new HashMap<>();
	
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
	
	public Kit() {}
	
	/**
	 * Entity that represents a Kit for a request.
	 * A representation of Sales Order
	 * @param id Id of the Kit
	 * @param kitNumber Kit number/order number for the sales order
	 * @param items Items requested in the sales order
	 * @param subkits Collection of subkits in kit 
	 * @param lastModDttm Last modified date and time
	 * @param createdDttm Created date and time
	 * @param kitReopenTimes Number of times kit has been re-opened.
	 * @param version Row Version
	 * @param assignedWarehouse Warehouse to which the request is currently assigned
	 * @param status Status of the kit
	 * @param date Date when the kit is to be delivered
	 * @param requestWarehouse Warehouse to which the request is to be delivered
	 */
	public Kit(long id, String kitNumber, Collection<SalesOrderItemLine> items, Collection<Subkit> subkits, Date lastModDttm, Date createdDttm,
			int kitReopenTimes, int version, Warehouse assignedWarehouse, int status, Date date, Warehouse requestWarehouse) {
		super();
		this.id = id;
		this.kitNumber = kitNumber;
		this.items = items;
		this.subkits = subkits;
		this.lastModDttm = lastModDttm;
		this.createdDttm = createdDttm;
		this.kitReopenTimes = kitReopenTimes;
		this.version = version;
		this.assignedWarehouse = assignedWarehouse;
		this.status = status;
		this.date = date;
		this.requestWarehouse = requestWarehouse;
	}

	public void updateKitStatus() {
		Kit kit = this;
		Map<Integer, Integer> map = new HashMap<>();
		for (Subkit subkit : kit.getSubkits()) {
			kit.updateSubkitStatus(subkit);
			Integer status = subkit.getStatus();
			if (map.containsKey(status))
				map.put(status, map.get(status) + 1);
			else
				map.put(status, 1);
		}
		if (map.get(SubkitStatus.DISPUTED.value()) != null) {
			kit.setStatus(KitStatus.DISPUTED.value());
			return;
		}
		if (map.get(SubkitStatus.RETURN_EXPECTED.value()) != null) {
			kit.setStatus(KitStatus.RETURN_EXPECTED.value());
			return;
		}
		if (map.get(SubkitStatus.RETURN_RECEIVED_BY_HUB.value()) != null) {
			kit.setStatus(KitStatus.RETURN_RECEIVED_BY_HUB.value());
			return;
		}
		if (map.get(SubkitStatus.RETURN_DISPATCHED_BY_HUB.value()) != null) {
			kit.setStatus(KitStatus.RETURN_DISPATCHED_BY_HUB.value());
			return;
		}/*
		if (map.get(SubkitStatus.NEW.value()) != null) {
			boolean allSubkitsReturned = true;
			for (Subkit sk : kit.getSubkits()) {
				if (!SubkitStatus.RETURN_RECEIVED_BY_WAREHOUSE.isEquals(sk.getStatus())
						&& !SubkitStatus.CLOSED.isEquals(sk.getStatus()) && !SubkitStatus.NEW.isEquals(sk.getStatus())) {
					allSubkitsReturned = false;
					break;
				}
			}
			if (allSubkitsReturned) {
				for (Subkit sk : kit.getSubkits()) {
					sk.setStatus(SubkitStatus.CLOSED.value());
				}
				kit.setStatus(KitStatus.CLOSED.value());
				return;
			}
			kit.setStatus(KitStatus.RETURN_RECEIVED_BY_WAREHOUSE.value());
			return;
		}*/
		if (map.get(SubkitStatus.NEW.value()) != null && map.get(SubkitStatus.CLOSED.value()) != null) {
			for (Subkit sk : kit.getSubkits()) {
				if(SubkitStatus.NEW.isEquals(sk.getStatus()))
					;//sk.setStatus(SubkitStatus.CLOSED.value());
			}
		}
		if (map.get(SubkitStatus.RETURN_RECEIVED_BY_WAREHOUSE.value()) != null) {
			boolean allSubkitsReturned = true;
			for (Subkit sk : kit.getSubkits()) {
				if (!SubkitStatus.RETURN_RECEIVED_BY_WAREHOUSE.isEquals(sk.getStatus())
						&& !SubkitStatus.CLOSED.isEquals(sk.getStatus())) {
					allSubkitsReturned = false;
					break;
				}
			}
			if (allSubkitsReturned) {
				for (Subkit sk : kit.getSubkits()) {
					sk.setStatus(SubkitStatus.CLOSED.value());
				}
				kit.setStatus(KitStatus.CLOSED.value());
				return;
			}
			kit.setStatus(KitStatus.RETURN_RECEIVED_BY_WAREHOUSE.value());
			return;
		}
		if (map.get(SubkitStatus.ISSUED.value()) != null || map.get(SubkitStatus.RECEIVED.value()) != null
				|| map.get(SubkitStatus.PARTIALLY_RECEIVED.value()) != null
				|| map.get(SubkitStatus.DISPATCHED.value()) != null
				|| map.get(SubkitStatus.PARTIALLY_DISPATCHED.value()) != null
				|| map.get(SubkitStatus.RESERVED.value()) != null
				|| map.get(SubkitStatus.PARTIALLY_RESERVED.value()) != null) {
			kit.setStatus(KitStatus.IN_PROGRESS.value());
			return;
		}
		if (map.get(SubkitStatus.INVOICED.value()) != null && map.get(SubkitStatus.INVOICED.value()) == size(map)) {
			kit.setStatus(KitStatus.INVOICED.value());
			return;
		}
		if (map.get(SubkitStatus.CLOSED.value()) != null && map.get(SubkitStatus.CLOSED.value()) == size(map)) {
			kit.setStatus(KitStatus.CLOSED.value());
			return;
		}
		if (map.get(SubkitStatus.NEW.value()) != null) {
			kit.setStatus(KitStatus.NEW.value());
			return;
		}
		if (map.get(SubkitStatus.CANCELLED.value()) != null && (map.get(SubkitStatus.RETURN_EXPECTED.value()) != null
				|| map.get(SubkitStatus.RETURN_RECEIVED_BY_HUB.value()) != null
				|| map.get(SubkitStatus.RETURN_DISPATCHED_BY_HUB.value()) != null
				|| map.get(SubkitStatus.RETURN_RECEIVED_BY_WAREHOUSE.value()) != null)) {
			kit.setStatus(KitStatus.RETURN_EXPECTED.value());
			return;
		}
		return;
	}
	
	private int size(Map<Integer, Integer> map) {
		int size = 0;
		for (Integer i : map.keySet()) {
			size += map.get(i);
		}
		return size;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getKitNumber() {
		return kitNumber;
	}

	public void setKitNumber(String kitNumber) {
		this.kitNumber = kitNumber;
	}

	public Collection<SalesOrderItemLine> getItems() {
		return items;
	}

	public void setItems(Collection<SalesOrderItemLine> items) {
		this.items = items;
	}
	
	public Collection<Subkit> getSubkits() {
		return subkits;
	}

	public void setSubkits(Collection<Subkit> subkits) {
		this.subkits = subkits;
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

	public Warehouse getAssignedWarehouse() {
		return assignedWarehouse;
	}

	public void setAssignedWarehouse(Warehouse assignedWarehouse) {
		this.assignedWarehouse = assignedWarehouse;
	}

	public int getKitReopenTimes() {
		return kitReopenTimes;
	}

	public void setKitReopenTimes(int kitReopenTimes) {
		this.kitReopenTimes = kitReopenTimes;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public void updateItemDetails(String barcode, int status){
		try {
			SalesOrderItemLine existingEntry = null;
			int i = 0, index = -1;
			for(SalesOrderItemLine item: getItems()){
				if(item.getBarcode() != null){
					if(item.getBarcode().equals(barcode)){
						existingEntry = item;
						index = i;
						break;
					}
				}
				i++;
			}
			if (index != -1) {
				existingEntry.setStatus(status);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void reserveBarcode(String barcode, String itemCode, double wholesalePrice, double unitPrice){
		try {
			SalesOrderItemLine existingEntry = null;
			int i = 0, index = -1;
			for(SalesOrderItemLine item: getItems()){
				if(item.getBarcode() == null){
					if(item.getItemCode().equals(itemCode)){
						existingEntry = item;
						index = i;
						break;
					}
				}
				i++;
			}
			if (index != -1) {
				existingEntry.setBarcode(barcode);
				existingEntry.setStatus(ItemStatus.RESERVED.value());
				existingEntry.setWholesalePrice(wholesalePrice);
				existingEntry.setUnitPrice(unitPrice);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Map<String, Subkit> invoice(List<ItemRequestPair> requests){
		if(requests == null){
			return null;
		}
		Map<String, Subkit> subkitsToSave = new HashMap<>();
		l:for (ItemRequestPair request : requests){
			for (Subkit sk : getSubkits()){
				for (SalesOrderItemLine salesOrderItemLine : sk.getItems()) {
					if (salesOrderItemLine.getBarcode() != null
							&& salesOrderItemLine.getItemCode().equals(request.getItemCode()) && ((salesOrderItemLine.getStatus() != ItemStatus.INVOICED.value())
							&& (salesOrderItemLine.getStatus() >= ItemStatus.RESERVED.value()
									&& salesOrderItemLine.getStatus() <= ItemStatus.RETURN_EXPECTED.value()))){
						updateItemDetails(salesOrderItemLine.getBarcode(), ItemStatus.INVOICED.value());
						sk.updateItemDetails(salesOrderItemLine.getBarcode(), ItemStatus.INVOICED.value());
						subkitsToSave.put(sk.getSubkitNumber(), sk);
						continue l;
					}
				}
			}
		}
		subkitsToSave.putAll(setReturnExpected());
		return subkitsToSave;
	}
	
	public void update(String barcode, int status){
		prepareMap();
		SalesOrderItemLine kitItem = barcodeKitItemMap.get(barcode);
		SalesOrderItemLine subkitItem = barcodeSubkitItemMap.get(barcode);
		if(kitItem != null && subkitItem != null){
			if(kitItem != null && subkitItem != null){
				kitItem.setStatus(status);
				subkitItem.setStatus(status);
				return;
			}
		}
	}
	public void updateScrap(String barcode, int status){
		prepareMap();
		SalesOrderItemLine kitItem = barcodeKitItemMap.get(barcode);
		SalesOrderItemLine subkitItem = barcodeSubkitItemMap.get(barcode);
		if(kitItem != null && subkitItem != null){
			if(kitItem != null && subkitItem != null){
				kitItem.setScrapStatus(status);
				subkitItem.setScrapStatus(status);
				return;
			}
		}
	}
	
	public void update(String barcode, int status, Warehouse location){
		prepareMap();
		SalesOrderItemLine kitItem = barcodeKitItemMap.get(barcode);
		SalesOrderItemLine subkitItem = barcodeSubkitItemMap.get(barcode);
		if(kitItem != null && subkitItem != null){
			if(kitItem != null && subkitItem != null){
				kitItem.setStatus(status);
				kitItem.setCurrentLocation(location);
				subkitItem.setStatus(status);
				subkitItem.setCurrentLocation(location);
				return;
			}
		}
	}
	
	public void update(String barcode, int status, String itemCode, Warehouse location, double unitPrice, double wholesalePrice){
		prepareMap();
		List<SalesOrderItemLine> kitItemList = itemCodeKitItemMap.get(itemCode + location.getCode());
		List<SalesOrderItemLine> subkitItemList = itemCodeSubkitItemMap.get(itemCode + location.getCode());
		if(kitItemList.size() > 0 && subkitItemList.size() > 0){
			SalesOrderItemLine kitItem = kitItemList.remove(0);
			SalesOrderItemLine subkitItem = subkitItemList.remove(0);
			if(kitItemList.size() > 0)
				itemCodeKitItemMap.put(itemCode + location.getCode(), kitItemList);
			else
				itemCodeKitItemMap.remove(itemCode + location.getCode());
			if(subkitItemList.size() > 0)
				itemCodeSubkitItemMap.put(itemCode + location.getCode(), subkitItemList);
			else
				itemCodeSubkitItemMap.remove(itemCode + location.getCode());
			kitItem.setBarcode(barcode);
			kitItem.setStatus(status);
			kitItem.setCurrentLocation(location);
			//kitItem.setScrapStatus(scrapStatus);
			kitItem.setWholesalePrice(wholesalePrice);
			kitItem.setUnitPrice(unitPrice);
			subkitItem.setBarcode(barcode);
			subkitItem.setStatus(status);
			subkitItem.setCurrentLocation(location);
			//subkitItem.setScrapStatus(scrapStatus);
			subkitItem.setWholesalePrice(wholesalePrice);
			subkitItem.setUnitPrice(unitPrice);
			barcodeKitItemMap.put(barcode, kitItem);
			barcodeSubkitItemMap.put(barcode, subkitItem);
		}
	}
	public void update(String barcode, int status, String itemCode, Warehouse location,  
			int scrapStatus, double unitPrice, double wholesalePrice){
		prepareMap();
		SalesOrderItemLine kitItem = barcodeKitItemMap.get(barcode);
		SalesOrderItemLine subkitItem = barcodeSubkitItemMap.get(barcode);
		if(kitItem != null && subkitItem != null){
			if(kitItem != null && subkitItem != null){
				kitItem.setStatus(status);
				kitItem.setCurrentLocation(location);
				kitItem.setScrapStatus(scrapStatus);
				kitItem.setWholesalePrice(wholesalePrice);
				kitItem.setUnitPrice(unitPrice);
				subkitItem.setStatus(status);
				subkitItem.setCurrentLocation(location);
				subkitItem.setScrapStatus(scrapStatus);
				subkitItem.setWholesalePrice(wholesalePrice);
				subkitItem.setUnitPrice(unitPrice);
				return;
			}
		}
		List<SalesOrderItemLine> kitItemList = itemCodeKitItemMap.get(itemCode + location.getCode());
		List<SalesOrderItemLine> subkitItemList = itemCodeSubkitItemMap.get(itemCode + location.getCode());
		if(kitItemList.size() > 0 && subkitItemList.size() > 0){
			kitItem = kitItemList.remove(0);
			subkitItem = subkitItemList.remove(0);
			if(kitItemList.size() > 0)
				itemCodeKitItemMap.put(itemCode + location.getCode(), kitItemList);
			else
				itemCodeKitItemMap.remove(itemCode + location.getCode());
			if(subkitItemList.size() > 0)
				itemCodeSubkitItemMap.put(itemCode + location.getCode(), subkitItemList);
			else
				itemCodeSubkitItemMap.remove(itemCode + location.getCode());
			kitItem.setBarcode(barcode);
			kitItem.setStatus(status);
			kitItem.setCurrentLocation(location);
			kitItem.setScrapStatus(scrapStatus);
			kitItem.setWholesalePrice(wholesalePrice);
			kitItem.setUnitPrice(unitPrice);
			subkitItem.setBarcode(barcode);
			subkitItem.setStatus(status);
			subkitItem.setCurrentLocation(location);
			subkitItem.setScrapStatus(scrapStatus);
			subkitItem.setWholesalePrice(wholesalePrice);
			subkitItem.setUnitPrice(unitPrice);
			barcodeKitItemMap.put(barcode, kitItem);
			barcodeSubkitItemMap.put(barcode, subkitItem);
		}
	}
	
	private void prepareMap(){
		if(barcodeKitItemMap.size() == 0 || itemCodeKitItemMap.size() == 0){
			for (SalesOrderItemLine salesOrderItemLine : getItems()) {
				if(salesOrderItemLine.getBarcode() == null){
					if(itemCodeKitItemMap.containsKey(salesOrderItemLine.getItemCode() + salesOrderItemLine.getCurrentLocation().getCode())){
						List<SalesOrderItemLine> list = itemCodeKitItemMap.get(salesOrderItemLine.getItemCode() + salesOrderItemLine.getCurrentLocation().getCode());
						list.add(salesOrderItemLine);
						itemCodeKitItemMap.put(salesOrderItemLine.getItemCode() + salesOrderItemLine.getCurrentLocation().getCode(), list);
					}else{
						List<SalesOrderItemLine> list = new ArrayList<>();
						list.add(salesOrderItemLine);
						itemCodeKitItemMap.put(salesOrderItemLine.getItemCode() + salesOrderItemLine.getCurrentLocation().getCode(), list);
					}
				}else{
					barcodeKitItemMap.put(salesOrderItemLine.getBarcode(), salesOrderItemLine);
				}
			}
			for(Subkit subkit : getSubkits())
			for (SalesOrderItemLine salesOrderItemLine : subkit.getItems()) {
				if(salesOrderItemLine.getBarcode() == null){
					if(itemCodeSubkitItemMap.containsKey(salesOrderItemLine.getItemCode() + salesOrderItemLine.getCurrentLocation().getCode())){
						List<SalesOrderItemLine> list = itemCodeSubkitItemMap.get(salesOrderItemLine.getItemCode() + salesOrderItemLine.getCurrentLocation().getCode());
						list.add(salesOrderItemLine);
						itemCodeSubkitItemMap.put(salesOrderItemLine.getItemCode() + salesOrderItemLine.getCurrentLocation().getCode(), list);
					}else{
						List<SalesOrderItemLine> list = new ArrayList<>();
						list.add(salesOrderItemLine);
						itemCodeSubkitItemMap.put(salesOrderItemLine.getItemCode() + salesOrderItemLine.getCurrentLocation().getCode(), list);
					}
				}else{
					barcodeSubkitItemMap.put(salesOrderItemLine.getBarcode(), salesOrderItemLine);
					barcodeSubkitsMap.put(salesOrderItemLine.getBarcode(), subkit);
				}
			}
		}
	}
	public void updateSubkitStatus(Subkit subkit) {
		Kit kit = this;
		try {
			Map<Integer, Integer> map = new HashMap<>();
			for (SalesOrderItemLine salesOrderItemLine : subkit.getItems()) {
				Integer status = salesOrderItemLine.getStatus();
				if (!ItemStatus.OUT_OF_STOCK.isEquals(salesOrderItemLine.getStatus()) && !ItemStatus.AVAILABLE.isEquals(salesOrderItemLine.getStatus())) {
					if (map.containsKey(status))
						map.put(status, map.get(status) + 1);
					else
						map.put(status, 1);
				}
			}
			if (map.get(ItemStatus.STOCK_LOSS.value()) != null) {
				subkit.setStatus(SubkitStatus.DISPUTED.value());
				return;
			} else if (map.get(ItemStatus.RETURN_EXPECTED.value()) != null) {
				subkit.setStatus(SubkitStatus.RETURN_EXPECTED.value());
				return;
			} else if (map.get(ItemStatus.RETURN_RECEIVED_BY_HUB.value()) != null) {
				subkit.setStatus(SubkitStatus.RETURN_RECEIVED_BY_HUB.value());
				return;
			} else if (map.get(ItemStatus.RETURN_DISPATCHED_BY_HUB.value()) != null) {
				subkit.setStatus(SubkitStatus.RETURN_DISPATCHED_BY_HUB.value());
				return;
			} else if (map.get(ItemStatus.RETURN_RECEIVED_BY_WAREHOUSE.value()) != null) {
				if(map.get(ItemStatus.RETURN_RECEIVED_BY_WAREHOUSE.value()) == size(map)){
					subkit.setStatus(SubkitStatus.CLOSED.value());
				} else if(map.get(ItemStatus.INVOICED.value()) != null){
					if((map.get(ItemStatus.RETURN_RECEIVED_BY_WAREHOUSE.value()) + map.get(ItemStatus.INVOICED.value())) == size(map)){
						subkit.setStatus(SubkitStatus.CLOSED.value());
					}
				} else
					subkit.setStatus(SubkitStatus.RETURN_RECEIVED_BY_WAREHOUSE.value());
				return;
			} else if ((map.get(ItemStatus.INVOICED.value()) != null
					&& map.get(ItemStatus.INVOICED.value()) == size(map)) || 
					((map.get(ItemStatus.INVOICED.value()) != null && map.get(ItemStatus.RETURN_RECEIVED_BY_WAREHOUSE.value()) != null
							&& (map.get(ItemStatus.INVOICED.value()) + map.get(ItemStatus.RETURN_RECEIVED_BY_WAREHOUSE.value())) == size(map)))) {
				subkit.setStatus(SubkitStatus.CLOSED.value());
				return;
			} else if (map.get(ItemStatus.ISSUED.value()) != null && map.get(ItemStatus.ISSUED.value()) == size(map)) {
				subkit.setStatus(SubkitStatus.ISSUED.value());
				return;
			} else if (map.get(ItemStatus.RECEIVED.value()) != null
					&& map.get(ItemStatus.RECEIVED.value()) == size(map)) {
				subkit.setStatus(SubkitStatus.RECEIVED.value());
				subkit.setLocation(kit.getRequestWarehouse());
				for (SalesOrderItemLine salesOrderItemLine : subkit.getItems()) {
					if(ItemStatus.RECEIVED.isEquals(salesOrderItemLine.getStatus())){
						salesOrderItemLine.setCurrentLocation(kit.getRequestWarehouse());
						for (SalesOrderItemLine item : kit.getItems()) {
							if(item.getBarcode() != null)
								if(item.getBarcode().equals(salesOrderItemLine.getBarcode()))
									item.setCurrentLocation(kit.getRequestWarehouse());
						}
					}
					if(ItemStatus.OUT_OF_STOCK.isEquals(salesOrderItemLine.getStatus()) ||
							ItemStatus.AVAILABLE.isEquals(salesOrderItemLine.getStatus())){
						salesOrderItemLine.setCurrentLocation(kit.getRequestWarehouse());
						for (SalesOrderItemLine item : kit.getItems()) {
							if(ItemStatus.OUT_OF_STOCK.isEquals(item.getStatus()) ||
									ItemStatus.AVAILABLE.isEquals(item.getStatus()) 
									&& item.getCurrentLocation().getId() == kit.getAssignedWarehouse().getId()){
								item.setCurrentLocation(kit.getRequestWarehouse());
							}
						}
					}
				}
				return;
			} else if (map.get(ItemStatus.RECEIVED.value()) != null
					&& map.get(ItemStatus.RECEIVED.value()) != size(map)) {
				subkit.setStatus(SubkitStatus.PARTIALLY_RECEIVED.value());
				subkit.setLocation(kit.getRequestWarehouse());
				for (SalesOrderItemLine salesOrderItemLine : subkit.getItems()) {
					if(ItemStatus.RECEIVED.isEquals(salesOrderItemLine.getStatus())){
						salesOrderItemLine.setCurrentLocation(kit.getRequestWarehouse());
						for (SalesOrderItemLine item : kit.getItems()) {
							if(item.getBarcode() != null)
								if(item.getBarcode().equals(salesOrderItemLine.getBarcode()))
									item.setCurrentLocation(kit.getRequestWarehouse());
						}
					}
					if(ItemStatus.OUT_OF_STOCK.isEquals(salesOrderItemLine.getStatus()) ||
							ItemStatus.AVAILABLE.isEquals(salesOrderItemLine.getStatus())){
						salesOrderItemLine.setCurrentLocation(kit.getRequestWarehouse());
						for (SalesOrderItemLine item : kit.getItems()) {
							if(ItemStatus.OUT_OF_STOCK.isEquals(item.getStatus()) ||
									ItemStatus.AVAILABLE.isEquals(item.getStatus()) 
									&& item.getCurrentLocation().getId() == kit.getAssignedWarehouse().getId()){
								item.setCurrentLocation(kit.getRequestWarehouse());
							}
						}
					}
				}
				return;
			} else if (map.get(ItemStatus.DISPATCHED.value()) != null
					&& map.get(ItemStatus.DISPATCHED.value()) == size(map)) {
				subkit.setStatus(SubkitStatus.DISPATCHED.value());
				return;
			} else if (map.get(ItemStatus.DISPATCHED.value()) != null
					&& map.get(ItemStatus.DISPATCHED.value()) != size(map)) {
				subkit.setStatus(SubkitStatus.PARTIALLY_DISPATCHED.value());
				return;
			} else if (map.get(ItemStatus.RESERVED.value()) != null
					&& map.get(ItemStatus.RESERVED.value()) == size(map)) {
				subkit.setStatus(SubkitStatus.RESERVED.value());
				return;
			} else if (map.get(ItemStatus.RESERVED.value()) != null
					&& map.get(ItemStatus.RESERVED.value()) != size(map)) {
				subkit.setStatus(SubkitStatus.PARTIALLY_RESERVED.value());
				return;
			}
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<ItemRequestPair> newItems(List<ItemSalesRequest> requests) {
		List<ItemRequestPair> newItems = new ArrayList<>();
		Map<String, List<SalesOrderItemLine>> kitMap = new HashMap<>();
		Map<String, List<ItemSalesRequest>> requestMap = new HashMap<>();
		for (SalesOrderItemLine salesOrderItemLine : getItems()) {
			String itemCode = salesOrderItemLine.getItemCode();
			List<SalesOrderItemLine> value = new ArrayList<>();
			if (kitMap.containsKey(itemCode))
				value = kitMap.get(itemCode);
			value.add(salesOrderItemLine);
			kitMap.put(itemCode, value);
		}
		for (ItemSalesRequest request : requests) {
			String itemCode = request.getItemCode();
			List<ItemSalesRequest> value = new ArrayList<>();
			if (requestMap.containsKey(itemCode))
				value = requestMap.get(itemCode);
			value.add(request);
			requestMap.put(itemCode, value);
		}
		Map<String, List<ItemSalesRequest>> uniqueRequestItems = new HashMap<>();
		Map<String, List<SalesOrderItemLine>> uniqueKitItems = new HashMap<>();
		Map<String, String> matchingItems = new HashMap<>();
		for (String itemCode : requestMap.keySet()) {
			if (!kitMap.containsKey(itemCode)) {
				List<ItemSalesRequest> value = requestMap.get(itemCode);
				uniqueRequestItems.put(itemCode, value);
			} else {
				matchingItems.put(itemCode, itemCode);
			}
		}
		for (String itemCode : kitMap.keySet()) {
			if (!requestMap.containsKey(itemCode)) {
				List<SalesOrderItemLine> value = kitMap.get(itemCode);
				uniqueKitItems.put(itemCode, value);
			}
		}
		for (String itemCode : uniqueRequestItems.keySet()) {
			List<ItemSalesRequest> value = uniqueRequestItems.get(itemCode);
			for (ItemSalesRequest request : value) {
				for (int i = 0; i < request.getQuantityRequested(); i++) {
					ItemRequestPair itemRequestPair = new ItemRequestPair();
					itemRequestPair.setItemCode(request.getItemCode());
					newItems.add(itemRequestPair);
				}
			}
		}
		for (String itemCode : matchingItems.keySet()) {
			List<ItemSalesRequest> matchingRequestItems = requestMap.get(itemCode);
			List<SalesOrderItemLine> matchingKitItems = kitMap.get(itemCode);
			List<ItemRequestPair> existingRequestedItems = new ArrayList<>();
			List<SalesOrderItemLine> existingKitItems = new ArrayList<>();
			for (ItemSalesRequest request : matchingRequestItems) {
				// might contain single item in this list
				for (int i = 0; i < request.getQuantityRequested(); i++) {
					ItemRequestPair itemRequestPair = new ItemRequestPair();
					itemRequestPair.setItemCode(request.getItemCode());
					existingRequestedItems.add(itemRequestPair);
				}
				for (SalesOrderItemLine salesOrderItemLine : matchingKitItems) {
					existingKitItems.add(salesOrderItemLine);
				}
			}
			if (existingKitItems.size() == existingRequestedItems.size()) {
				// do nothing
			} else if (existingKitItems.size() > existingRequestedItems.size()) {
				// remove non reserved items from kit and subkit
			} else if (existingKitItems.size() < existingRequestedItems.size()) {
				int size = existingRequestedItems.size() - existingKitItems.size();
				// new items to be added to kit
				for (int i = 0; i < size; i++) {
					ItemRequestPair itemRequestPair = new ItemRequestPair();
					itemRequestPair.setItemCode(itemCode);
					newItems.add(itemRequestPair);
				}
			}
		}
		return newItems;
	}

	public List<ItemRequestPair> existingItems(List<ItemSalesRequest> requests) {
		List<ItemRequestPair> existingItems = new ArrayList<>();
		Map<String, List<SalesOrderItemLine>> kitMap = new HashMap<>();
		Map<String, List<ItemSalesRequest>> requestMap = new HashMap<>();
		for (SalesOrderItemLine salesOrderItemLine : getItems()) {
			String itemCode = salesOrderItemLine.getItemCode();
			List<SalesOrderItemLine> value = new ArrayList<>();
			if (kitMap.containsKey(itemCode))
				value = kitMap.get(itemCode);
			value.add(salesOrderItemLine);
			kitMap.put(itemCode, value);
		}
		for (ItemSalesRequest request : requests) {
			String itemCode = request.getItemCode();
			List<ItemSalesRequest> value = new ArrayList<>();
			if (requestMap.containsKey(itemCode))
				value = requestMap.get(itemCode);
			value.add(request);
			requestMap.put(itemCode, value);
		}
		Map<String, List<ItemSalesRequest>> uniqueRequestItems = new HashMap<>();
		Map<String, List<SalesOrderItemLine>> uniqueKitItems = new HashMap<>();
		Map<String, String> matchingItems = new HashMap<>();
		for (String itemCode : requestMap.keySet()) {
			if (!kitMap.containsKey(itemCode)) {
				List<ItemSalesRequest> value = requestMap.get(itemCode);
				uniqueRequestItems.put(itemCode, value);
			} else {
				matchingItems.put(itemCode, itemCode);
			}
		}
		for (String itemCode : kitMap.keySet()) {
			if (!requestMap.containsKey(itemCode)) {
				List<SalesOrderItemLine> value = kitMap.get(itemCode);
				uniqueKitItems.put(itemCode, value);
			}
		}
		for (String itemCode : matchingItems.keySet()) {
			List<ItemSalesRequest> matchingRequestItems = requestMap.get(itemCode);
			List<SalesOrderItemLine> matchingKitItems = kitMap.get(itemCode);
			List<ItemRequestPair> existingRequestedItems = new ArrayList<>();
			List<SalesOrderItemLine> existingKitItems = new ArrayList<>();
			for (ItemSalesRequest request : matchingRequestItems) {
				// might contain single item in this list
				for (int i = 0; i < request.getQuantityRequested(); i++) {
					ItemRequestPair itemRequestPair = new ItemRequestPair();
					itemRequestPair.setItemCode(request.getItemCode());
					existingRequestedItems.add(itemRequestPair);
				}
				for (SalesOrderItemLine salesOrderItemLine : matchingKitItems) {
					existingKitItems.add(salesOrderItemLine);
				}
			}
			if (existingKitItems.size() == existingRequestedItems.size()) {
			} else if (existingKitItems.size() > existingRequestedItems.size()) {
				// remove non reserved items from kit and subkit
			} else if (existingKitItems.size() < existingRequestedItems.size()) {
				// int size = existingRequestedItems.size() -
				// existingKitItems.size();
				// new items to be added to kit
			}
			for (int i = 0; i < existingKitItems.size(); i++) {
				ItemRequestPair itemRequestPair = new ItemRequestPair();
				itemRequestPair.setItemCode(itemCode);
				existingItems.add(itemRequestPair);
			}
		}
		return existingItems;
	}
	
	public Map<String, Subkit> setReturnExpected(){
		Map<String, Subkit> subkitsToSave = new HashMap<>();
		for (Subkit sk : getSubkits()){
			for (SalesOrderItemLine salesOrderItemLine : sk.getItems()) {
				if (salesOrderItemLine.getBarcode() != null
						&& salesOrderItemLine.getItemCode().equals(salesOrderItemLine.getItemCode()) && ((salesOrderItemLine.getStatus() != ItemStatus.INVOICED.value())
						&& (salesOrderItemLine.getStatus() >= ItemStatus.RESERVED.value()
								&& salesOrderItemLine.getStatus() < ItemStatus.RETURN_EXPECTED.value()))){
					salesOrderItemLine.setStatus(ItemStatus.RETURN_EXPECTED.value());
					updateItemDetails(salesOrderItemLine.getBarcode(), ItemStatus.RETURN_EXPECTED.value());
					subkitsToSave.put(salesOrderItemLine.getBarcode(), sk);
				}
			}
		}
		return subkitsToSave;
	}
	
	public List<ItemRequestPair> getInvoiceableItems(List<ItemSalesRequest> requests) {
		List<ItemRequestPair> existingItems = new ArrayList<>();
		Map<String, List<SalesOrderItemLine>> kitMap = new HashMap<>();
		Map<String, List<ItemSalesRequest>> requestMap = new HashMap<>();
		for (SalesOrderItemLine salesOrderItemLine : getItems()) {
			String itemCode = salesOrderItemLine.getItemCode();
			List<SalesOrderItemLine> value = new ArrayList<>();
			if (kitMap.containsKey(itemCode))
				value = kitMap.get(itemCode);
			value.add(salesOrderItemLine);
			kitMap.put(itemCode, value);
		}
		for (ItemSalesRequest request : requests) {
			String itemCode = request.getItemCode();
			List<ItemSalesRequest> value = new ArrayList<>();
			if (requestMap.containsKey(itemCode))
				value = requestMap.get(itemCode);
			value.add(request);
			requestMap.put(itemCode, value);
		}
		Map<String, List<ItemSalesRequest>> uniqueRequestItems = new HashMap<>();
		Map<String, List<SalesOrderItemLine>> uniqueKitItems = new HashMap<>();
		Map<String, String> matchingItems = new HashMap<>();
		for (String itemCode : requestMap.keySet()) {
			if (!kitMap.containsKey(itemCode)) {
				List<ItemSalesRequest> value = requestMap.get(itemCode);
				uniqueRequestItems.put(itemCode, value);
			} else {
				matchingItems.put(itemCode, itemCode);
			}
		}
		for (String itemCode : kitMap.keySet()) {
			if (!requestMap.containsKey(itemCode)) {
				List<SalesOrderItemLine> value = kitMap.get(itemCode);
				uniqueKitItems.put(itemCode, value);
			}
		}
		for (String itemCode : matchingItems.keySet()) {
			List<ItemSalesRequest> matchingRequestItems = requestMap.get(itemCode);
			List<SalesOrderItemLine> matchingKitItems = kitMap.get(itemCode);
			List<ItemRequestPair> existingRequestedItems = new ArrayList<>();
			List<SalesOrderItemLine> existingKitItems = new ArrayList<>();
			for (ItemSalesRequest request : matchingRequestItems) {
				// might contain single item in this list
				for (int i = 0; i < request.getQuantityRequested(); i++) {
					ItemRequestPair itemRequestPair = new ItemRequestPair();
					itemRequestPair.setItemCode(request.getItemCode());
					existingRequestedItems.add(itemRequestPair);
				}
				for (SalesOrderItemLine salesOrderItemLine : matchingKitItems) {
					existingKitItems.add(salesOrderItemLine);
				}
			}
			if (existingKitItems.size() == existingRequestedItems.size()) {
			} else if (existingKitItems.size() > existingRequestedItems.size()) {
				// remove non reserved items from kit and subkit
			} else if (existingKitItems.size() < existingRequestedItems.size()) {
				// int size = existingRequestedItems.size() -
				// existingKitItems.size();
				// new items to be added to kit
				throw new RuntimeException("Invoice items exceeded limit.");
			}
			for (int i = 0; i < existingRequestedItems.size(); i++) {
				ItemRequestPair itemRequestPair = new ItemRequestPair();
				itemRequestPair.setItemCode(itemCode);
				existingItems.add(itemRequestPair);
			}
		}
		return existingItems;
	}
	
	public void reopenKit(){
		if(KitStatus.CANCELLED.isEquals(status) || KitStatus.CLOSED.isEquals(status)){
			status = KitStatus.REOPENED.value();
			for (Subkit subkit : getSubkits()){
				subkit.setStatus(SubkitStatus.REOPENED.value());
			}
			kitReopenTimes++;
		}
	}
	
	public SalesOrderItemLine getLineItem(String itemCode){
		for(SalesOrderItemLine item : getItems()){
			if(item.getItemCode().equals(itemCode)){
				return item;
			}
		}
		return null;
	}

	public Warehouse getRequestWarehouse() {
		return requestWarehouse;
	}

	public void setRequestWarehouse(Warehouse requestWarehouse) {
		this.requestWarehouse = requestWarehouse;
	}

	@Override
	public String toString() {
		return "Kit [id=" + id + ", kitNumber=" + kitNumber + ", items=" + items + ", subkits=" + subkits
				+ ", lastModDttm=" + lastModDttm + ", createdDttm=" + createdDttm + ", date=" + date + ", status="
				+ status + ", kitReopenTimes=" + kitReopenTimes + ", version=" + version + ", assignedWarehouse="
				+ assignedWarehouse + ", requestWarehouse=" + requestWarehouse + "]";
	}

	public void updateScrapItemDetails(ItemRequestPair request, int status) {
		try {
			SalesOrderItemLine existingEntry = null;
			int i = 0, index = -1;
			for(SalesOrderItemLine item: getItems()){
				if(item.getBarcode() != null){
					if(item.getBarcode().equals(request.getBarcode())){
						existingEntry = item;
						index = i;
						break;
					}
				}
				i++;
			}
			if (index != -1) {
				existingEntry.setSerialNumber(request.getVendorProductId());
				existingEntry.setScrapStatus(status);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Subkit getSubkitOfNewStatus() {
		for (Subkit subkit : getSubkits()) {
			if (SubkitStatus.NEW.isEquals(subkit.getStatus())) {
				return subkit;
			}
		}
		String subkitNumber = getKitNumber() + "_" + (getSubkits().size() + 1);
		Subkit subkit = new Subkit(0, subkitNumber, getKitNumber(), new ArrayList<>(), null, null,
				Calendar.getInstance().getTime(), SubkitStatus.NEW.value(), getAssignedWarehouse());
		getSubkits().add(subkit);
		return subkit;
	}
	
	public Subkit changeSubkitLocation(Subkit subkit) {
		Kit kit = this;
		Map<String, Integer> m = new HashMap<>();
		for (SalesOrderItemLine salesOrderItemLine : subkit.getItems()) {
			salesOrderItemLine.setCurrentLocation(kit.getRequestWarehouse());
			if (m.containsKey(salesOrderItemLine.getItemCode())) {
				m.put(salesOrderItemLine.getItemCode(), m.get(salesOrderItemLine.getItemCode()) + 1);
			} else {
				m.put(salesOrderItemLine.getItemCode(), 1);
			}
		}
		loop: for (String itemCode : m.keySet()) {
			int i = 0;
			for (SalesOrderItemLine salesOrderItemLine : kit.getItems()) {
				if (i < m.get(itemCode)) {
					if (salesOrderItemLine.getItemCode().equals(itemCode) && salesOrderItemLine.getBarcode() == null
							&& salesOrderItemLine.getCurrentLocation().equals(kit.getAssignedWarehouse())) {
						salesOrderItemLine.setCurrentLocation(kit.getRequestWarehouse());
						i++;
					}
				} else {
					continue loop;
				}
			}
		}
		subkit.setLocation(kit.getRequestWarehouse());
		//subkit = subkitRepository.save(subkit);
		//for (SalesOrderItemLine salesOrderItemLine : subkit.getItems()) {
			//updateKitItemsToOutOfStockAndAvailable(salesOrderItemLine.getItemCode(), kit.getRequestWarehouse().getId());
		//}
		//subkit = subkitRepository.save(subkit);
		//kit = kitRepository.save(kit);
		return subkit;
	}
}
