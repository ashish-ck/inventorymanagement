package com.drivojoy.inventory.servicesImpl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.drivojoy.inventory.dto.CountByDate;
import com.drivojoy.inventory.dto.InventoryRequest;
import com.drivojoy.inventory.dto.ItemNetworkDTO;
import com.drivojoy.inventory.dto.ItemRequestPair;
import com.drivojoy.inventory.dto.ItemSalesRequest;
import com.drivojoy.inventory.dto.KitConsolidatedReport;
import com.drivojoy.inventory.dto.KitDTO;
import com.drivojoy.inventory.dto.KitReportItem;
import com.drivojoy.inventory.dto.OutOfStockKitItemsDTO;
import com.drivojoy.inventory.dto.SalesConsolidatedReport;
import com.drivojoy.inventory.dto.SalesReportItem;
import com.drivojoy.inventory.dto.SubkitDTO;
import com.drivojoy.inventory.models.Item;
import com.drivojoy.inventory.models.ItemCount;
import com.drivojoy.inventory.models.ItemOrderLine;
import com.drivojoy.inventory.models.Kit;
import com.drivojoy.inventory.models.SalesOrderItemLine;
import com.drivojoy.inventory.models.Subkit;
import com.drivojoy.inventory.models.Warehouse;
import com.drivojoy.inventory.models.datatables.DTParameters;
import com.drivojoy.inventory.models.datatables.DTResult;
import com.drivojoy.inventory.repositories.KitRepository;
import com.drivojoy.inventory.repositories.SubkitRepository;
import com.drivojoy.inventory.services.IInventoryTransactionService;
import com.drivojoy.inventory.services.IItemService;
import com.drivojoy.inventory.services.IKitService;
import com.drivojoy.inventory.services.IPurchaseOrderService;
import com.drivojoy.inventory.services.IWarehouseService;
import com.drivojoy.inventory.utils.Constants;
import com.drivojoy.inventory.utils.Constants.ItemStatus;
import com.drivojoy.inventory.utils.Constants.KitStatus;
import com.drivojoy.inventory.utils.Constants.SubkitStatus;
import com.drivojoy.inventory.utils.IDTOWrapper;

@Component
public class KitServiceImpl implements IKitService {
	@Autowired
	private KitRepository kitRepository;
	@Autowired
	private SubkitRepository subkitRepository;
	@Autowired
	private IWarehouseService warehouseService;
	@Autowired
	private IItemService itemService;
	@Autowired
	private IPurchaseOrderService purchaseOrderService;
	@Autowired
	private IInventoryTransactionService inventoryTransactionService;
	@Autowired
	private IDTOWrapper dtoWrapper;
	@PersistenceContext
	private EntityManager em;
	private String _GET_ALL_QUERY_WITH_SEARCH = "SELECT * FROM kit WHERE kit_number LIKE \'%%%s%%\' AND (status %s OR status = 1100) %s ORDER BY %s %s LIMIT %d OFFSET %d";
	private String _GET_ALL_QUERY = "SELECT * FROM kit WHERE (status %s OR status = 1100) %s ORDER BY %s %s LIMIT %d OFFSET %d";
	private String _GET_ALL_ADMIN_QUERY = "SELECT * FROM kit WHERE (status %s OR status = 1100) ORDER BY %s %s LIMIT %d OFFSET %d";
	private String _GET_ALL_ADMIN_SEARCH_QUERY = "SELECT * FROM kit WHERE kit_number LIKE \'%%%s%%\' AND (status %s OR status = 1100) ORDER BY %s %s LIMIT %d OFFSET %d";
	private String _COUNT_ALL_QUERY_WITH_SEARCH = "SELECT COUNT(*) FROM kit WHERE kit_number LIKE \'%%%s%%\' AND (status %s OR status = 1100) %s";
	private String _COUNT_ALL_QUERY = "SELECT COUNT(*) FROM kit WHERE (status %s OR status = 1100) %s";
	private String _COUNT_ALL_ADMIN_QUERY = "SELECT COUNT(*) FROM kit WHERE (status %s OR status = 1100)";
	private String _COUNT_ALL_ADMIN_SEARCH_QUERY = "SELECT COUNT(*) FROM kit WHERE (kit_number LIKE \'%%%s%%\') AND (status %s OR status = 1100)";
	private final int _ORDER_NUMBER_COL = 0;
	private final int _ORDER_DATE_COL = 1;
	private final int _ORDER_WAREHOUSE_COL = 2;
	private final int _ORDER_STATUS_COL = 3;
	private final Logger logger = LoggerFactory.getLogger(KitServiceImpl.class);

	@Override
	public Kit saveKit(Kit kit) {
		try {
			return kitRepository.save(kit);
		} /*
			 * catch (OptimisticLockException ex) {
			 * logger.error("concurrent access to this kit is detected " +
			 * ex.getMessage()); throw new ConcurrencyFailureException(
			 * "This is not the latest copy of the kit you are trying to edit, please refresh to get latest copy!"
			 * ); }
			 */ catch (Exception ex) {
			logger.error("Exception thwon while updating kit :" + ex.getMessage());
			throw new RuntimeException(Constants._GENERIC_ERROR_MESSAGE);
		}
	}

	private int getNewStatus(int status) {
		switch (status) {
		case 1:
			status = KitStatus.NEW.value();
			break;
		case 3:
			status = KitStatus.IN_PROGRESS.value();
			break;
		case 4:
			status = KitStatus.IN_PROGRESS.value();
			break;
		case 5:
			status = KitStatus.IN_PROGRESS.value();
			break;
		case 6:
			status = KitStatus.IN_PROGRESS.value();
			break;
		case 7:
			status = KitStatus.INVOICED.value();
			break;
		case 10:
			status = KitStatus.CLOSED.value();
			break;
		case 11:
			status = KitStatus.CANCELLED.value();
			break;
		case 12:
			status = KitStatus.REOPENED.value();
			break;
		}
		return status;
	}

	@Override
	public Kit reopenKit(String kitNumber) {
		Kit kit = kitRepository.findByKitNumber(kitNumber);
		if (kit == null) {
			return null;
		}
		kit.reopenKit();
		for (Subkit subkit : kit.getSubkits()) {
			subkitRepository.save(subkit);
		}
		kit = kitRepository.save(kit);
		return kit;
	}

	@Override
	public Kit validateKitRequest(Date date, String voucherRef, List<ItemSalesRequest> newRequests, String workshopRef,
			int status) {
		try {
			status = getNewStatus(status);
			Kit kit = kitRepository.findByKitNumber(voucherRef);
			List<ItemRequestPair> requests = dtoWrapper.convertToItemRequestPairList(newRequests);
			if (kit == null) {
				if (KitStatus.CANCELLED.isEquals(status)) {
					return null;
				}
				Warehouse assignedWarehouse = warehouseService.getMotherWarehouse(workshopRef);
				Warehouse requestWarehouse = warehouseService.getWarehouseByCode(workshopRef);
				kit = new Kit(0, voucherRef, new ArrayList<>(), new ArrayList<>(), null, null, 0, 0, assignedWarehouse,
						status, date, requestWarehouse);
				Subkit subkit = kit.getSubkitOfNewStatus();
				subkit = addToSubkitWithRequest(kit, subkit, requests, status, assignedWarehouse);
				kit.updateKitStatus();
				subkit = subkitRepository.saveAndFlush(subkit);
				kit = kitRepository.saveAndFlush(kit);
				return kit;
			}
			if (KitStatus.CLOSED.isEquals(kit.getStatus()) || KitStatus.CANCELLED.isEquals(kit.getStatus())) {
				return kit;
			}
			List<ItemRequestPair> newItems = kit.newItems(newRequests);
			List<ItemRequestPair> existingItems = kit.existingItems(newRequests);
			Map<String, Integer> itemCounts = new HashMap<>();
			Map<String, SalesOrderItemLine> kitItemsMap = new HashMap<>();
			Map<String, Subkit> barcodeSubkitsMap = new HashMap<>();
			Map<String, Subkit> subkitsToSave = new HashMap<>();
			if (newItems.size() > 0) {
				Subkit subkit = kit.getSubkitOfNewStatus();
				subkitsToSave.put(subkit.getSubkitNumber(), subkit);
				subkit = addToSubkitWithRequest(kit, subkit, newItems, status, kit.getAssignedWarehouse());
			}
			if (KitStatus.CANCELLED.isEquals(status)) {
				for (String barcode : kitItemsMap.keySet()) {
					if (kitItemsMap.get(barcode).getStatus() >= ItemStatus.RESERVED.value()
							&& kitItemsMap.get(barcode).getStatus() <= ItemStatus.INVOICED.value() && kitItemsMap.get(barcode).getBarcode() != null) {
						kit.update(barcode, ItemStatus.RETURN_EXPECTED.value());
						itemCounts.put(barcode, ItemStatus.RETURN_EXPECTED.value());
						subkitsToSave.put(barcodeSubkitsMap.get(barcode).getSubkitNumber(),
								barcodeSubkitsMap.get(barcode));
					}
				}
				for (Subkit sk : kit.getSubkits()) {
					if (SubkitStatus.NEW.isEquals(sk.getStatus())) {
						sk.setStatus(SubkitStatus.CLOSED.value());
						subkitsToSave.put(sk.getSubkitNumber(), sk);
					}
				}
				for (String subkitNumber : subkitsToSave.keySet()) {
					subkitRepository.save(subkitsToSave.get(subkitNumber));
				}
			} else if (KitStatus.INVOICED.isEquals(status)) {
				existingItems = kit.getInvoiceableItems(newRequests);
				l: for (ItemRequestPair request : existingItems) {
					for (Subkit sk : kit.getSubkits()) {
						for (SalesOrderItemLine salesOrderItemLine : sk.getItems()) {
							if (salesOrderItemLine.getBarcode() != null
									&& salesOrderItemLine.getItemCode().equals(request.getItemCode())
									&& ((salesOrderItemLine.getStatus() != ItemStatus.INVOICED.value())
											&& (salesOrderItemLine.getStatus() >= ItemStatus.RESERVED.value()
													&& salesOrderItemLine.getStatus() <= ItemStatus.RETURN_EXPECTED
															.value()))) {
								kit.update(salesOrderItemLine.getBarcode(), ItemStatus.INVOICED.value());
								itemCounts.put(salesOrderItemLine.getBarcode(), ItemStatus.INVOICED.value());
								subkitsToSave.put(sk.getSubkitNumber(), sk);
								continue l;
							}
						}
					}
				}
				for (Subkit sk : kit.getSubkits()) {
					for (SalesOrderItemLine salesOrderItemLine : sk.getItems()) {
						if (salesOrderItemLine.getBarcode() != null
								&& salesOrderItemLine.getItemCode().equals(salesOrderItemLine.getItemCode())
								&& ((salesOrderItemLine.getStatus() != ItemStatus.INVOICED.value())
										&& (salesOrderItemLine.getStatus() >= ItemStatus.RESERVED.value()
												&& salesOrderItemLine.getStatus() < ItemStatus.RETURN_EXPECTED
														.value()))) {
							kit.update(salesOrderItemLine.getBarcode(), ItemStatus.RETURN_EXPECTED.value());
							itemCounts.put(salesOrderItemLine.getBarcode(), ItemStatus.RETURN_EXPECTED.value());
							subkitsToSave.put(sk.getSubkitNumber(), sk);
						}
					}
				}
				for (String subkitNumber : subkitsToSave.keySet()) {
					subkitRepository.save(subkitsToSave.get(subkitNumber));
				}
				for (Subkit subkit : kit.getSubkits()){
					if(SubkitStatus.NEW.isEquals(subkit.getStatus())){
						subkit.setStatus(SubkitStatus.CLOSED.value());
					}
				}
			}
			kit.updateKitStatus();
			kit = kitRepository.saveAndFlush(kit);
			for (String barcode : itemCounts.keySet()) {
				itemService.setItemCountStatusByBarcode(itemCounts.get(barcode), barcode);
			}
			Warehouse location = null;
			if (kit.getAssignedWarehouse().getCode() == workshopRef) {
				location = kit.getAssignedWarehouse();
			} else {
				location = kit.getRequestWarehouse();
			}
			inventoryTransactionService.recordTransaction(date, requests, location, status,
					KitStatus.getStatus(status).text(), Constants._SUBKIT_UPDATE, workshopRef);
			return kit;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	@Override
	public void updateKitItemsToOutOfStockAndAvailable(String itemCode, long warehouseId) {
		Integer itemCounts = itemService.getAvailableCountByCodeAndWarehouseId(itemCode, warehouseId);
		if (itemCounts == null || itemCounts == 0) {
			kitRepository.updateKitItemsToOutOfStock(itemCode, warehouseId);
			kitRepository.updateSubkitItemsToOutOfStock(itemCode, warehouseId);
		} else {
			kitRepository.updateKitItemStatusToAvailable(itemCode, warehouseId);
			kitRepository.updateSubkitItemStatusToAvailable(itemCode, warehouseId);
		}
	}

	@Override
	public Subkit addToSubkitWithRequest(Kit kit, Subkit subkit, List<ItemRequestPair> requests, int status,
			Warehouse warehouse) {
		Collection<SalesOrderItemLine> items = new ArrayList<>();
		Map<String, ItemNetworkDTO> itemMap = new HashMap<>();
		Map<String, Integer> itemCountMap = new HashMap<>();
		for (ItemRequestPair request : requests) {
			int count;
			if(itemCountMap.containsKey(request.getItemCode())){
				count = itemCountMap.get(request.getItemCode());
			}else{
				Query nativeCountQuery = em.createNativeQuery("SELECT COUNT(item.code) FROM item WHERE item.code = \'" + request.getItemCode() + "\'");
				count = ((BigInteger) nativeCountQuery.getSingleResult()).intValue();
				itemCountMap.put(request.getItemCode(), count);
			}
			if (count == 0) {
				continue;
			}
			int scrapStatus = ItemStatus.SCRAP_NOT_AVAILABLE.value();
			SalesOrderItemLine salesOrderItemLine = null;
			Integer itemCounts = itemService.getAvailableCountByCodeAndWarehouseId(request.getItemCode(), warehouse.getId());
			ItemNetworkDTO item = null;
			if (itemMap.containsKey(request.getItemCode())) {
				item = itemMap.get(request.getItemCode());
			} else {
				item = itemService.getItemNetworkByCode(request.getItemCode());
				itemMap.put(request.getItemCode(), item);
			}
			if (itemCounts > 0) {
				salesOrderItemLine = new SalesOrderItemLine(request.getItemCode(), item.getDescription(),
						item.getVendorAlias(), subkit.getLocation(), kit.getKitNumber(), subkit.getSubkitNumber(), null, null, null, ItemStatus.AVAILABLE.value(),
						scrapStatus, 0.0, 0.0);
			} else {
				salesOrderItemLine = new SalesOrderItemLine(request.getItemCode(), item.getDescription(),
						item.getVendorAlias(), subkit.getLocation(), kit.getKitNumber(), subkit.getSubkitNumber(), null, null, null, ItemStatus.OUT_OF_STOCK.value(),
						scrapStatus, 0.0, 0.0);
			}
			items.add(salesOrderItemLine);
		}
		kit.getItems().addAll(items);
		subkit.getItems().addAll(items);
		return subkit;
	}

	@Override
	public List<Kit> getAllKits() {
		return kitRepository.findAll();
	}

	@Override
	public Kit updateKitItemDetails(InventoryRequest inventoryRequest) {
		Map<String, Integer> itemCounts = new HashMap<>();
		Map<String, Long> updateItemCountLocations = new HashMap<>();
		try {
			String kitNumber = inventoryRequest.getVoucherRef().substring(0,
					inventoryRequest.getVoucherRef().indexOf('_'));
			Kit kit = getByKitNumber(kitNumber);
			Subkit subkit = null;
			if (kit != null)
				for (Subkit sk : kit.getSubkits()) {
					if (sk.getSubkitNumber().equals(inventoryRequest.getVoucherRef())) {
						subkit = sk;
						break;
					}
				}
			String errorListOfItemCodes = "";
			Collection<ItemRequestPair> errorItems = new ArrayList<>();
			Collection<ItemStatus> preConditions = ItemStatus.getStatus(inventoryRequest.getStatus()).preConditions();
			boolean satisfied = false;
			boolean allPreConditionsSatisfied = true;
			for (ItemRequestPair request : inventoryRequest.getListOfItems()) {
				satisfied = false;
				for (ItemStatus status : preConditions) {
					if (status.isEquals(request.getStatus())) {
						satisfied = true;
					}
				}
				if (!satisfied || preConditions.size() == 0) {
					errorItems.add(request);
					errorListOfItemCodes = errorListOfItemCodes + request.getBarcode() + ", ";
					allPreConditionsSatisfied = false;
				}
			}
			if (allPreConditionsSatisfied) {
				for (ItemRequestPair request : inventoryRequest.getListOfItems()) {
					if (ItemStatus.RESERVED.isEquals(inventoryRequest.getStatus()) && request.getBarcode() != null) {
						ItemOrderLine itemOrderLine = purchaseOrderService.getOrderItemByBarcode(request.getBarcode());
						double wholesalePrice = 0.0, unitPrice = 0.0;
						if (itemOrderLine != null) {
							wholesalePrice = itemOrderLine.getWholesalePrice();
							unitPrice = itemOrderLine.getUnitPrice();
						} else {
							ItemCount itemCount = itemService.getItemCountByBarcode(request.getBarcode());
							wholesalePrice = itemCount.getWholesalePrice();
							unitPrice = itemCount.getUnitPrice();
						}
						if (subkit.getLocation().getId() == kit.getRequestWarehouse().getId()) {
							kit.update(request.getBarcode(), ItemStatus.RECEIVED.value(), request.getItemCode(),
									subkit.getLocation(), unitPrice, wholesalePrice);
						} else
							kit.update(request.getBarcode(), inventoryRequest.getStatus(), request.getItemCode(),
								subkit.getLocation(), unitPrice, wholesalePrice);
						itemCounts.put(request.getBarcode(), inventoryRequest.getStatus());
					} else if (ItemStatus.RECEIVED.isEquals(inventoryRequest.getStatus())) {
						kit.update(request.getBarcode(), inventoryRequest.getStatus(), kit.getRequestWarehouse());
						itemCounts.put(request.getBarcode(), inventoryRequest.getStatus());
						updateItemCountLocations.put(request.getBarcode(), kit.getRequestWarehouse().getId());
					}
					if (ItemStatus.RETURN_RECEIVED_BY_WAREHOUSE.isEquals(inventoryRequest.getStatus())) {
						kit.update(request.getBarcode(), inventoryRequest.getStatus(), kit.getAssignedWarehouse());
						itemCounts.put(request.getBarcode(), ItemStatus.AVAILABLE.value());
						updateItemCountLocations.put(request.getBarcode(), kit.getAssignedWarehouse().getId());
					} else {
						kit.update(request.getBarcode(), inventoryRequest.getStatus());
					}
				}
				kit.updateKitStatus();
				subkit = subkitRepository.saveAndFlush(subkit);
				kit = kitRepository.saveAndFlush(kit);
				for (String barcode : itemCounts.keySet()) {
					itemService.setItemCountStatusByBarcode(itemCounts.get(barcode), barcode);
				}
				inventoryTransactionService.recordTransaction(inventoryRequest.getDate(),
						inventoryRequest.getListOfItems(), subkit.getLocation(), inventoryRequest.getStatus(),
						ItemStatus.getStatus(inventoryRequest.getStatus()).text(), Constants._SUBKIT_UPDATE,
						inventoryRequest.getVoucherRef());
				for (String barcode : updateItemCountLocations.keySet()) {
					itemService.updateItemCountWarehouseLocation(barcode, updateItemCountLocations.get(barcode));
				}
				Map<String, String> countMap = new HashMap<>();
				if(ItemStatus.RECEIVED.isEquals(inventoryRequest.getStatus())
						|| ItemStatus.RETURN_RECEIVED_BY_WAREHOUSE.isEquals(inventoryRequest.getStatus())
						|| ItemStatus.RESERVED.isEquals(inventoryRequest.getStatus()))
				for (SalesOrderItemLine item : subkit.getItems()) {
					if(ItemStatus.AVAILABLE.isEquals(item.getStatus()) ||
							ItemStatus.OUT_OF_STOCK.isEquals(item.getStatus())){
						if(!countMap.containsKey(item.getItemCode() + kit.getAssignedWarehouse().getId())){
							updateKitItemsToOutOfStockAndAvailable(item.getItemCode(), kit.getAssignedWarehouse().getId());
							countMap.put(item.getItemCode()+ kit.getAssignedWarehouse().getId(), "");
						}
						if(!countMap.containsKey(item.getItemCode() + kit.getRequestWarehouse().getId())){
							updateKitItemsToOutOfStockAndAvailable(item.getItemCode(), kit.getRequestWarehouse().getId());
							countMap.put(item.getItemCode()+ kit.getRequestWarehouse().getId(), "");
						}
					}
				}
				return kit;
			} else if (preConditions.size() == 0 && inventoryRequest.getStatus() >= ItemStatus.SCRAP_RECEIVED.value()
					&& inventoryRequest.getStatus() <= ItemStatus.SCRAP_NOT_EXPECTED.value()) {
				for (ItemRequestPair request : inventoryRequest.getListOfItems()) {
					kit.updateScrap(request.getBarcode(), inventoryRequest.getStatus());
				}
				kit.updateKitStatus();
				subkit = subkitRepository.saveAndFlush(subkit);
				kit = kitRepository.saveAndFlush(kit);
				for (String barcode : itemCounts.keySet()) {
					itemService.setItemCountStatusByBarcode(itemCounts.get(barcode), barcode);
				}
				inventoryTransactionService.recordTransaction(inventoryRequest.getDate(),
						inventoryRequest.getListOfItems(), subkit.getLocation(), inventoryRequest.getStatus(),
						ItemStatus.getStatus(inventoryRequest.getStatus()).text(), Constants._SUBKIT_UPDATE,
						inventoryRequest.getVoucherRef());
				for (String barcode : updateItemCountLocations.keySet()) {
					itemService.updateItemCountWarehouseLocation(barcode, updateItemCountLocations.get(barcode));
				}
				Map<String, String> countMap = new HashMap<>();
				if(ItemStatus.RECEIVED.isEquals(inventoryRequest.getStatus())
						|| ItemStatus.RETURN_RECEIVED_BY_WAREHOUSE.isEquals(inventoryRequest.getStatus())
						|| ItemStatus.RESERVED.isEquals(inventoryRequest.getStatus()))
				for (SalesOrderItemLine item : subkit.getItems()) {
					if(ItemStatus.AVAILABLE.isEquals(item.getStatus()) ||
							ItemStatus.OUT_OF_STOCK.isEquals(item.getStatus())){
						if(!countMap.containsKey(item.getItemCode() + kit.getAssignedWarehouse().getId())){
							updateKitItemsToOutOfStockAndAvailable(item.getItemCode(), kit.getAssignedWarehouse().getId());
							countMap.put(item.getItemCode()+ kit.getAssignedWarehouse().getId(), "");
						}
						if(!countMap.containsKey(item.getItemCode() + kit.getRequestWarehouse().getId())){
							updateKitItemsToOutOfStockAndAvailable(item.getItemCode(), kit.getRequestWarehouse().getId());
							countMap.put(item.getItemCode()+ kit.getRequestWarehouse().getId(), "");
						}
					}
				}
				return kit;
			} else {
				errorListOfItemCodes = errorListOfItemCodes.substring(0, errorListOfItemCodes.length() - 2);
				throw new RuntimeException(errorListOfItemCodes);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	@Override
	public Kit updateKitItemLocation(String kitNumber, String warehouse, int status,
			List<ItemRequestPair> listOfItems) {
		Kit existingKit = kitRepository.findByKitNumber(kitNumber);
		Warehouse newWarehouse = warehouseService.getWarehouseByName(warehouse);
		HashMap<String, ItemRequestPair> items = new HashMap<>();
		for (ItemRequestPair request : listOfItems) {
			items.put(request.getBarcode(), request);
		}
		if (existingKit == null) {
			throw new RuntimeException("Kit does not exist");
		}
		for (SalesOrderItemLine item : existingKit.getItems()) {
			if (items.containsKey(item.getBarcode())) {
				item.setCurrentLocation(newWarehouse);
				item.setStatus(status);
			}
		}
		existingKit.setAssignedWarehouse(newWarehouse);
		existingKit = saveKit(existingKit);
		return existingKit;
	}

	@Override
	public Kit getByKitNumber(String kitNumber) {
		return kitRepository.findByKitNumber(kitNumber);
	}
	
	@Override
	public Kit getByKitNumberWithSuggestion(String kitNumber) {
		Kit kit = kitRepository.findByKitNumber(kitNumber);
		List<String> list1 = new ArrayList<>();
		List<String> list2 = new ArrayList<>();
		for (SalesOrderItemLine item : kit.getItems()){
			if(ItemStatus.AVAILABLE.isEquals(item.getStatus()) ||
					ItemStatus.OUT_OF_STOCK.isEquals(item.getStatus())){
				if(kit.getAssignedWarehouse().getId() == item.getCurrentLocation().getId()){
					list1.add(item.getItemCode());
				}else
					list2.add(item.getItemCode());
			}
		}
		list1 = itemService.getBarcodeSuggestion(kit.getAssignedWarehouse().getCode(), list1, kitNumber);
		list2 = itemService.getBarcodeSuggestion(kit.getRequestWarehouse().getCode(), list2, kitNumber);
		for (String suggestion : list1){
			loop1:for (SalesOrderItemLine item : kit.getItems()){
				if(ItemStatus.AVAILABLE.isEquals(item.getStatus()) ||
						ItemStatus.OUT_OF_STOCK.isEquals(item.getStatus())){
					if(kit.getAssignedWarehouse().getId() == item.getCurrentLocation().getId() 
							&& item.getSuggestion() == null){
						item.setSuggestion(suggestion);
						if(suggestion.equals("No available suggestion.")){
							item.setStatus(ItemStatus.OUT_OF_STOCK.value());
						}else{
							item.setStatus(ItemStatus.AVAILABLE.value());
						}
						break loop1;
					}
				}
			}
			loop2:for (Subkit subkit : kit.getSubkits()){
				for (SalesOrderItemLine item : subkit.getItems()){
					if(ItemStatus.AVAILABLE.isEquals(item.getStatus()) ||
							ItemStatus.OUT_OF_STOCK.isEquals(item.getStatus())){
						if(kit.getAssignedWarehouse().getId() == item.getCurrentLocation().getId() 
								&& item.getSuggestion() == null){
							item.setSuggestion(suggestion);
							if(suggestion.equals("No available suggestion.")){
								item.setStatus(ItemStatus.OUT_OF_STOCK.value());
							}else{
								item.setStatus(ItemStatus.AVAILABLE.value());
							}
							break loop2;
						}
					}
				}
			}
		}
		for (String suggestion : list2){
			loop3:for (SalesOrderItemLine item : kit.getItems()){
				if(ItemStatus.AVAILABLE.isEquals(item.getStatus()) ||
						ItemStatus.OUT_OF_STOCK.isEquals(item.getStatus())){
					if(kit.getRequestWarehouse().getId() == item.getCurrentLocation().getId()
							&& item.getSuggestion() == null){
						item.setSuggestion(suggestion);
						if(suggestion.equals("No available suggestion.")){
							item.setStatus(ItemStatus.OUT_OF_STOCK.value());
						}else{
							item.setStatus(ItemStatus.AVAILABLE.value());
						}
						break loop3;
					}
				}
			}
			loop4:for (Subkit subkit : kit.getSubkits()){
				for (SalesOrderItemLine item : subkit.getItems()){
					if(ItemStatus.AVAILABLE.isEquals(item.getStatus()) ||
							ItemStatus.OUT_OF_STOCK.isEquals(item.getStatus())){
						if(kit.getRequestWarehouse().getId() == item.getCurrentLocation().getId()
								&& item.getSuggestion() == null){
							item.setSuggestion(suggestion);
							if(suggestion.equals("No available suggestion.")){
								item.setStatus(ItemStatus.OUT_OF_STOCK.value());
							}else{
								item.setStatus(ItemStatus.AVAILABLE.value());
							}
							break loop4;
						}
					}
				}
			}
		}
		return kit;
	}

	@Override
	public void updateKitItemStatus(List<ItemRequestPair> requests) {
		for (ItemRequestPair request : requests) {
			kitRepository.updateKitItemStatusToAvailable(request.getItemCode(),
					warehouseService.getWarehouseByName(request.getWarehouse()).getId());
			kitRepository.updateSubkitItemStatusToAvailable(request.getItemCode(),
					warehouseService.getWarehouseByName(request.getWarehouse()).getId());
		}
	}

	@Override
	public KitConsolidatedReport getKitReport(Date fromDate, Date toDate, Long warehouseId) {
		List<Kit> listOfKits = null;
		if (warehouseId == null)
			listOfKits = kitRepository.findByDateRange(fromDate, toDate);
		else
			listOfKits = kitRepository.getByDateRangeAndWarehouseId(fromDate, toDate, warehouseId);
		KitConsolidatedReport report = new KitConsolidatedReport();
		report.setFromDate(fromDate);
		report.setToDate(toDate);
		for (Kit kit : listOfKits) {
			String statusText = KitStatus.getStatus(kit.getStatus()).text();
			double quantityDelivered, quantityOnTheFly, quantityInvoiced, quantityStockLoss, amountInvoiced,
					quantityRestocked, quantityReturnExpected;
			quantityDelivered = quantityOnTheFly = quantityStockLoss = amountInvoiced = 0.0;
			quantityInvoiced = quantityRestocked = quantityReturnExpected = 0.0;
			for (SalesOrderItemLine lineItem : kit.getItems()) {
				if (lineItem.getStatus() >= ItemStatus.RESERVED.value()
						&& lineItem.getStatus() <= ItemStatus.STOCK_LOSS.value()) {
					quantityDelivered++;
				}
				if (lineItem.getStatus() == ItemStatus.INVOICED.value()) {
					quantityInvoiced++;
					amountInvoiced += lineItem.getUnitPrice();
				}
				if (lineItem.getStatus() == ItemStatus.RETURN_RECEIVED_BY_WAREHOUSE.value()) {
					quantityRestocked++;
				}
				if (lineItem.getStatus() >= ItemStatus.RETURN_EXPECTED.value()
						&& lineItem.getStatus() <= ItemStatus.RETURN_DISPATCHED_BY_HUB.value()) {
					quantityReturnExpected++;
				}
				if (lineItem.getStatus() == ItemStatus.STOCK_LOSS.value()) {
					quantityStockLoss++;
				}
			}
			KitReportItem reportItem = new KitReportItem(kit.getKitNumber(), kit.getAssignedWarehouse().getName(),
					kit.getRequestWarehouse().getName(),
					quantityDelivered, quantityInvoiced, quantityOnTheFly, quantityRestocked, quantityStockLoss,
					quantityReturnExpected, 0.0, statusText, amountInvoiced, null, null);
			report.getKitItems().add(reportItem);
			report.setTotalQuantityDelivered(report.getTotalQuantityDelivered() + reportItem.getQuantityDelivered());
			report.setTotalQuantityOnTheFly(report.getTotalQuantityOnTheFly() + reportItem.getItemsOnTheFly());
			report.setTotalQuantityInvoiced(report.getTotalQuantityInvoiced() + reportItem.getQuantityInvoiced());
			report.setTotalQuantityReturnExpected(
					report.getTotalQuantityReturnExpected() + reportItem.getQuantityReturnExpected());
			report.setTotalQuantityRestocked(report.getTotalQuantityRestocked() + reportItem.getQuantityRestocked());
			report.setAmountInvoiced(report.getAmountInvoiced() + reportItem.getAmountInvoiced());
		}
		try {
			List<Object[]> count = kitRepository.findyCountByDateRange(fromDate, toDate);
			count.stream().forEach((record) -> {
				Date date = ((Date) record[0]);
				Double qtyDelivered = ((BigDecimal) record[1]).doubleValue();
				Double qtyReturnExpected = ((BigDecimal) record[2]).doubleValue();
				Double qtyRestocked = ((BigDecimal) record[3]).doubleValue();
				Double qtyOnTheFly = ((BigDecimal) record[4]).doubleValue();
				report.getCountByDate()
						.add(new CountByDate(qtyDelivered, qtyReturnExpected, qtyRestocked, qtyOnTheFly, date));
			});
		} catch (Exception ex) {
			logger.error("Exception thrown while firing native query!");
		}
		return report;
	}

	@Override
	public List<Kit> getKitsByWarehouse(String warehouseCode) {
		return kitRepository.findByAssignedWarehouse(warehouseService.getWarehouseByCode(warehouseCode));
	}

	@Override
	public List<OutOfStockKitItemsDTO> getOutOfStockItemsByWarehouse(Date fromDate, Date toDate, String warehouseCode) {
		Warehouse warehouse = warehouseService.getWarehouseByCode(warehouseCode);
		List<Object[]> items = kitRepository.getOutOfStockKitItemsByWarehouse(fromDate, toDate, warehouse.getId());
		List<OutOfStockKitItemsDTO> listOfItems = new ArrayList<>();
		if (items != null && items.size() > 0) {
			items.stream().forEach((record) -> {
				Item item = itemService.getItemByCode(((String) record[0]));
				listOfItems.add(new OutOfStockKitItemsDTO(item.getCode(), item.getDescription(),
						warehouseService.findById(((BigInteger) record[1]).longValue()).getName(), (Date) record[2],
						(String) record[3]));
			});
		}
		return listOfItems;
	}

	@Override
	public List<OutOfStockKitItemsDTO> getOutOfStockItems(Date fromDate, Date toDate) {
		List<Object[]> items = kitRepository.getOutOfStockKitItems(fromDate, toDate);
		List<OutOfStockKitItemsDTO> listOfItems = new ArrayList<>();
		if (items != null && items.size() > 0) {
			items.stream().forEach((record) -> {
				Item item = itemService.getItemByCode(((String) record[0]));
				listOfItems.add(new OutOfStockKitItemsDTO(item.getCode(), item.getDescription(),
						warehouseService.findById(((BigInteger) record[1]).longValue()).getName(), (Date) record[2],
						(String) record[3]));
			});
		}
		return listOfItems;
	}
	
	@Override
	public SalesConsolidatedReport getSalesReport(Date fromDate, Date toDate, Long warehouseId){
		List<Kit> listOfKits = null;
		if (warehouseId == null)
			listOfKits = kitRepository.findByDateRange(fromDate, toDate);
		else
			listOfKits = kitRepository.getByDateRangeAndWarehouseId(fromDate, toDate, warehouseId);
		SalesConsolidatedReport report = new SalesConsolidatedReport();
		report.setFromDate(fromDate);
		report.setToDate(toDate);
		for (Kit kit : listOfKits) {
			String statusText = KitStatus.getStatus(kit.getStatus()).text();
			double quantityDelivered, quantityOnTheFly, quantityInvoiced, quantityStockLoss, amountInvoiced,
					quantityRestocked, quantityReturnExpected, sales, expenses, discount, stockLoss;
			quantityDelivered = quantityOnTheFly = quantityStockLoss = amountInvoiced = 0.0;
			quantityInvoiced = quantityRestocked = quantityReturnExpected = 0.0;
			sales = expenses = discount = stockLoss = 0.0;
			for (SalesOrderItemLine lineItem : kit.getItems()) {
				if (lineItem.getStatus() >= ItemStatus.RESERVED.value()
						&& lineItem.getStatus() <= ItemStatus.STOCK_LOSS.value()) {
					quantityDelivered++;
				}
				if (lineItem.getStatus() == ItemStatus.INVOICED.value()) {
					quantityInvoiced++;
					amountInvoiced += lineItem.getUnitPrice();
					sales += lineItem.getUnitPrice();
					expenses += lineItem.getWholesalePrice();
				}
				if (lineItem.getStatus() == ItemStatus.RETURN_RECEIVED_BY_WAREHOUSE.value()) {
					quantityRestocked++;
				}
				if (lineItem.getStatus() >= ItemStatus.RETURN_EXPECTED.value()
						&& lineItem.getStatus() <= ItemStatus.RETURN_DISPATCHED_BY_HUB.value()) {
					quantityReturnExpected++;
				}
				if (lineItem.getStatus() == ItemStatus.STOCK_LOSS.value()) {
					quantityStockLoss++;
					stockLoss += lineItem.getUnitPrice();
				}
			}
			List<SubkitDTO> subkits = dtoWrapper.convertSubkitToSubkitDTO((List<Subkit>)kit.getSubkits());
			SalesReportItem reportItem = new SalesReportItem(kit.getKitNumber(), kit.getAssignedWarehouse().getName(),
					kit.getRequestWarehouse().getName(),
					quantityDelivered, quantityInvoiced, quantityOnTheFly, quantityRestocked, quantityStockLoss,
					quantityReturnExpected, 0.0, statusText, amountInvoiced, null, subkits);
			reportItem.setSales(sales);
			reportItem.setExpenses(expenses);
			reportItem.setDiscount(discount);
			reportItem.setStockLoss(stockLoss);
			report.getKitItems().add(reportItem);
			report.setTotalQuantityDelivered(report.getTotalQuantityDelivered() + reportItem.getQuantityDelivered());
			report.setTotalQuantityOnTheFly(report.getTotalQuantityOnTheFly() + reportItem.getItemsOnTheFly());
			report.setTotalQuantityInvoiced(report.getTotalQuantityInvoiced() + reportItem.getQuantityInvoiced());
			
			report.setTotalSales(report.getTotalSales() + reportItem.getSales());
			report.setTotalExpenses(report.getTotalExpenses() + reportItem.getExpenses());
			report.setBlendedDiscount(report.getBlendedDiscount() + reportItem.getDiscount());
			report.setTotalStockLoss(report.getTotalStockLoss() + reportItem.getStockLoss());
			
			report.setTotalQuantityInvoiced(report.getTotalQuantityInvoiced() + reportItem.getQuantityInvoiced());
			report.setTotalQuantityInvoiced(report.getTotalQuantityInvoiced() + reportItem.getQuantityInvoiced());
			report.setTotalQuantityReturnExpected(
					report.getTotalQuantityReturnExpected() + reportItem.getQuantityReturnExpected());
			report.setTotalQuantityRestocked(report.getTotalQuantityRestocked() + reportItem.getQuantityRestocked());
			report.setAmountInvoiced(report.getAmountInvoiced() + reportItem.getAmountInvoiced());
		}
		try {
			List<Object[]> count = kitRepository.findyCountByDateRange(fromDate, toDate);
			count.stream().forEach((record) -> {
				Date date = ((Date) record[0]);
				Double qtyDelivered = ((BigDecimal) record[1]).doubleValue();
				Double qtyReturnExpected = ((BigDecimal) record[2]).doubleValue();
				Double qtyRestocked = ((BigDecimal) record[3]).doubleValue();
				Double qtyOnTheFly = ((BigDecimal) record[4]).doubleValue();
				report.getCountByDate()
						.add(new CountByDate(qtyDelivered, qtyReturnExpected, qtyRestocked, qtyOnTheFly, date));
			});
		} catch (Exception ex) {
			logger.error("Exception thrown while firing native query!");
		}
		return report;
	}

	@SuppressWarnings("unchecked")
	@Override
	public DTResult<KitDTO> getConsolidatedKitReport(DTParameters params, String warehouseRef) {
		DTResult<KitDTO> result = new DTResult<>();
		Warehouse warehouse = null;
		if (warehouseRef != null) {
			warehouse = warehouseService.getWarehouseByCode(warehouseRef);
		}
		String orderBy = "", sortOrder = "";
		if (params.order.get(0).Dir != null) {
			sortOrder = (params.order.get(0).Dir.toString()).toUpperCase();
		} else {
			sortOrder = "DESC";
		}
		switch (params.order.get(0).Column) {
		case _ORDER_NUMBER_COL:
			orderBy = "kit_number";
			break;
		case _ORDER_WAREHOUSE_COL:
			orderBy = "assigned_warehouse";
			break;
		case _ORDER_STATUS_COL:
			orderBy = "status";
			break;
		case _ORDER_DATE_COL:
		default:
			orderBy = "date";
			break;
		}
		result.draw = params.draw;
		String countQuery = "", searchQuery = "";
		if (warehouse != null) {
			// NOT AN ADMIN, show only specific warehouse details
			if (params.search.value != null && !params.search.value.isEmpty()) {
				countQuery = String.format(_COUNT_ALL_QUERY_WITH_SEARCH, warehouse.getId(), warehouse.getId(),
						params.search.value, "< " + KitStatus.CLOSED.value());
				searchQuery = String.format(_GET_ALL_QUERY_WITH_SEARCH, warehouse.getId(), warehouse.getId(),
						params.search.value, "< " + KitStatus.CLOSED.value(), orderBy, sortOrder, params.length,
						params.start);
			} else {
				countQuery = String.format(_COUNT_ALL_QUERY, warehouse.getId(), warehouse.getId(),
						"< " + KitStatus.CLOSED.value());
				searchQuery = String.format(_GET_ALL_QUERY, warehouse.getId(), warehouse.getId(),
						"< " + KitStatus.CLOSED.value(), orderBy, sortOrder, params.length, params.start);
			}
		} else {
			// ADMIN view, use admin queries
			if (params.search.value != null && !params.search.value.isEmpty()) {
				countQuery = String.format(_COUNT_ALL_ADMIN_SEARCH_QUERY, params.search.value,
						"< " + KitStatus.CLOSED.value());
				searchQuery = String.format(_GET_ALL_ADMIN_SEARCH_QUERY, params.search.value,
						"< " + KitStatus.CLOSED.value(), orderBy, sortOrder, params.length, params.start);
			} else {
				countQuery = String.format(_COUNT_ALL_ADMIN_QUERY, "< " + KitStatus.CLOSED.value());
				searchQuery = String.format(_GET_ALL_ADMIN_QUERY, "< " + KitStatus.CLOSED.value(), orderBy, sortOrder,
						params.length, params.start);
			}
		}
		try {
			Query nativeCountQuery = em.createNativeQuery(countQuery);
			result.recordsTotal = ((BigInteger) nativeCountQuery.getSingleResult()).intValue();
			result.recordsFiltered = result.recordsTotal;
			Query nativeQuery = em.createNativeQuery(searchQuery, Kit.class);
			List<Kit> resultSet = new ArrayList<>();
			resultSet = nativeQuery.getResultList();
			result.data = dtoWrapper.quickConvertKitToKitDTO(resultSet);
			return result;
		} catch (Exception ex) {
			logger.error("Exception thrown while executing query : " + ex.getMessage());
			throw new RuntimeException(ex.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public DTResult<KitDTO> getOpenKits(DTParameters params, Date fromDate, Date toDate, Long requestWarehouseId,
			Long assignedWarehouseId, Integer status) {
		DTResult<KitDTO> result = new DTResult<>();
		String orderBy = "", sortOrder = "";
		if (params.order.get(0).Dir != null) {
			sortOrder = (params.order.get(0).Dir.toString()).toUpperCase();
		} else {
			sortOrder = "DESC";
		}
		switch (params.order.get(0).Column) {
		case _ORDER_NUMBER_COL:
			orderBy = "kit_number";
			break;
		case _ORDER_WAREHOUSE_COL:
			orderBy = "assigned_warehouse";
			break;
		case _ORDER_STATUS_COL:
			orderBy = "status";
			break;
		case _ORDER_DATE_COL:
		default:
			orderBy = "date";
			break;
		}
		result.draw = params.draw;
		String countQuery = "", searchQuery = "";
		String q = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		if (assignedWarehouseId != null)
			q += " AND assigned_warehouse = " + assignedWarehouseId;
		if (requestWarehouseId != null)
			q += " AND request_warehouse = " + requestWarehouseId;
		if (status != null)
			q += " AND status = " + status;
		if (fromDate != null)
			q += " AND Date(date) >= \'" + format.format(fromDate) + "\'";
		if (toDate != null)
			q += " AND Date(date) <= \'" + format.format(toDate) + "\'";
		if (params.search.value != null && !params.search.value.isEmpty()) {
			countQuery = String.format(_COUNT_ALL_QUERY_WITH_SEARCH, params.search.value,
					"< " + KitStatus.CLOSED.value(), q);
			searchQuery = String.format(_GET_ALL_QUERY_WITH_SEARCH, params.search.value,
					"< " + KitStatus.CLOSED.value(), q, orderBy, sortOrder, params.length, params.start);
		} else {
			countQuery = String.format(_COUNT_ALL_QUERY, "< " + KitStatus.CLOSED.value(), q);
			searchQuery = String.format(_GET_ALL_QUERY, "< " + KitStatus.CLOSED.value(), q, orderBy, sortOrder,
					params.length, params.start);
		}
		try {
			Query nativeCountQuery = em.createNativeQuery(countQuery);
			result.recordsTotal = ((BigInteger) nativeCountQuery.getSingleResult()).intValue();
			result.recordsFiltered = result.recordsTotal;
			Query nativeQuery = em.createNativeQuery(searchQuery, Kit.class);
			List<Kit> resultSet = new ArrayList<>();
			resultSet = nativeQuery.getResultList();
			result.data = dtoWrapper.quickConvertKitToKitDTO(resultSet);
			return result;
		} catch (Exception ex) {
			logger.error("Exception thrown while executing query : " + ex.getMessage());
			throw new RuntimeException(ex.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public DTResult<KitDTO> getClosedKits(DTParameters params, Date fromDate, Date toDate, Long requestWarehouseId,
			Long assignedWarehouseId, Integer status) {
		DTResult<KitDTO> result = new DTResult<>();
		String orderBy = "", sortOrder = "";
		if (params.order.get(0).Dir != null) {
			sortOrder = (params.order.get(0).Dir.toString()).toUpperCase();
		} else {
			sortOrder = "DESC";
		}
		switch (params.order.get(0).Column) {
		case _ORDER_NUMBER_COL:
			orderBy = "kit_number";
			break;
		case _ORDER_WAREHOUSE_COL:
			orderBy = "assigned_warehouse";
			break;
		case _ORDER_STATUS_COL:
			orderBy = "status";
			break;
		case _ORDER_DATE_COL:
		default:
			orderBy = "date";
			break;
		}
		result.draw = params.draw;
		String countQuery = "", searchQuery = "";
		String q = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		if (status != null)
			q += " AND status = " + status;
		if (assignedWarehouseId != null)
			q += " AND assigned_warehouse = " + assignedWarehouseId;
		if (requestWarehouseId != null)
			q += " AND request_warehouse = " + requestWarehouseId;
		if (fromDate != null)
			q += " AND Date(date) >= \'" + format.format(fromDate) + "\'";
		if (toDate != null)
			q += " AND Date(date) <= \'" + format.format(toDate) + "\'";
		if (params.search.value != null && !params.search.value.isEmpty()) {
			countQuery = String.format(_COUNT_ALL_QUERY_WITH_SEARCH, params.search.value,
					"= " + KitStatus.CLOSED.value(), q);
			searchQuery = String.format(_GET_ALL_QUERY_WITH_SEARCH, params.search.value,
					"= " + KitStatus.CLOSED.value(), q, orderBy, sortOrder, params.length, params.start);
		} else {
			countQuery = String.format(_COUNT_ALL_QUERY, "= " + KitStatus.CLOSED.value(), q);
			searchQuery = String.format(_GET_ALL_QUERY, "= " + KitStatus.CLOSED.value(), q, orderBy, sortOrder,
					params.length, params.start);
		}
		try {
			Query nativeCountQuery = em.createNativeQuery(countQuery);
			result.recordsTotal = ((BigInteger) nativeCountQuery.getSingleResult()).intValue();
			result.recordsFiltered = result.recordsTotal;
			Query nativeQuery = em.createNativeQuery(searchQuery, Kit.class);
			List<Kit> resultSet = new ArrayList<>();
			resultSet = nativeQuery.getResultList();
			result.data = dtoWrapper.quickConvertKitToKitDTO(resultSet);
			return result;
		} catch (Exception ex) {
			logger.error("Exception thrown while executing query : " + ex.getMessage());
			throw new RuntimeException(ex.getMessage());
		}
	}

	@Override
	public String getKitStatus(String kitNumber) {
		Kit kit = this.getByKitNumber(kitNumber);
		String status = null;
		if (kit != null) {
			status = KitStatus.getStatus(kit.getStatus()).text();
		}
		return status;
	}

	@Override
	public Subkit getSubkitBySubkitNumber(String subkitNumber) {
		return subkitRepository.findBySubkitNumber(subkitNumber);
	}

	@Override
	public Subkit changeSubkitLocation(Subkit subkit) {
		Kit kit = kitRepository.findByKitNumber(subkit.getKitNumber());
		Map<String, Integer> m = new HashMap<>();
		if (subkit.getStatus() == SubkitStatus.NEW.value()) {
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
			subkit = subkitRepository.save(subkit);
			for (SalesOrderItemLine salesOrderItemLine : subkit.getItems()) {
				updateKitItemsToOutOfStockAndAvailable(salesOrderItemLine.getItemCode(),
						kit.getRequestWarehouse().getId());
			}
			subkit = subkitRepository.save(subkit);
			kit = kitRepository.save(kit);
		}
		return subkit;
	}

	@Override
	public List<KitDTO> getReturnExpectedKits() {
		List<KitDTO> kits = dtoWrapper.quickConvertKitToKitDTO(kitRepository.getReturnExpectedKits());
		return kits;
	}
}
