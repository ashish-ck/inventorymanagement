package com.drivojoy.inventory.servicesImpl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.drivojoy.inventory.dto.ItemDTO;
import com.drivojoy.inventory.dto.ItemRequestPair;
import com.drivojoy.inventory.dto.ItemSalesRequest;
import com.drivojoy.inventory.dto.PurchaseOrderDTO;
import com.drivojoy.inventory.models.Crate;
import com.drivojoy.inventory.models.Item;
import com.drivojoy.inventory.models.ItemCount;
import com.drivojoy.inventory.models.ItemDetails;
import com.drivojoy.inventory.models.ItemOrderLine;
import com.drivojoy.inventory.models.Kit;
import com.drivojoy.inventory.models.PurchaseOrder;
import com.drivojoy.inventory.models.Rack;
import com.drivojoy.inventory.models.SalesOrderItemLine;
import com.drivojoy.inventory.models.Shelf;
import com.drivojoy.inventory.models.Warehouse;
import com.drivojoy.inventory.services.ICrateService;
import com.drivojoy.inventory.services.IDocumentSequencesService;
import com.drivojoy.inventory.services.IInventoryOperationsService;
import com.drivojoy.inventory.services.IInventoryTransactionService;
import com.drivojoy.inventory.services.IItemService;
import com.drivojoy.inventory.services.IKitService;
import com.drivojoy.inventory.services.IPurchaseOrderService;
import com.drivojoy.inventory.services.IRackService;
import com.drivojoy.inventory.services.IShelfService;
import com.drivojoy.inventory.services.IWarehouseService;
import com.drivojoy.inventory.utils.Constants;
import com.drivojoy.inventory.utils.Constants.ItemStatus;
import com.drivojoy.inventory.utils.IDTOWrapper;

@Component
public class InventoryOperationServiceImpl implements IInventoryOperationsService {
	@Autowired
	private IItemService itemService;
	@Autowired
	private IWarehouseService warehouseService;
	@Autowired
	private IInventoryTransactionService inventoryTransactionService;
	@Autowired
	private IPurchaseOrderService orderService;
	@Autowired
	private IDocumentSequencesService docSequenceService;
	@Autowired
	private IDTOWrapper dtoWrapper;
	@Autowired
	private IKitService kitService;
	@Autowired
	private IShelfService shelfService;
	@Autowired
	private IRackService rackService;
	@Autowired
	private ICrateService crateService;
	@PersistenceContext
	private EntityManager em;
	private final Logger logger = LoggerFactory.getLogger(InventoryOperationServiceImpl.class);
	
	@Override
	public boolean isRequestPossible(ItemRequestPair request) {
		logger.debug("Checking if the request is possible : "+request.toString());
		double availableQuantity = 0;
		//First we need to check here if the item is available or reserved.
		if(request.getWarehouse() != null){
			availableQuantity = itemService.getQuantityAvailableByWarehouse(warehouseService.getWarehouseByName(request.getWarehouse())
					, request.getItemCode());
		}else{
			availableQuantity = itemService.getQuantityAvailableByWarehouse(warehouseService.getDefaultWarehouse()
					, request.getItemCode());
		}
		logger.debug("Quantity available for item "+request.getItemCode()+" :"+availableQuantity);
		double quantity = 1;
		if(quantity > availableQuantity){
			logger.warn("Order cannot be fulfilled");
			return false;
		}else{
			logger.debug("Order can be fulfilled");
			return true;
		}
	}
	
	@Override
	public List<ItemDTO> fulfillSalesOrder(Date date, String voucherRef, List<ItemRequestPair> requests, String workshop) {
		//need to determine which items are invoiced and which are expected back to inventory
		List<ItemDTO> itemList = new ArrayList<>();
		HashMap<String, ItemRequestPair> uniqueItems = new HashMap<>();
		for(ItemRequestPair request: requests){
			uniqueItems.put(request.getBarcode(), request);
		}
		for (String barcode : uniqueItems.keySet()){
			itemList.add(dtoWrapper.convert(itemService.getItemByCode(uniqueItems.get(barcode).getItemCode())));
		}
		invoiceItems(date, voucherRef, requests);
		return itemList;
	}

	@Override
	public List<ItemRequestPair> fulfillPurchaseOrder(Date date, String voucherRef, List<ItemRequestPair> listOfItems,
			String workshopRef, boolean isOnTheFly){
		Warehouse warehouse = warehouseService.getWarehouseByName(workshopRef);
		return fulfillPurchaseOrder(date, voucherRef, listOfItems, warehouse, isOnTheFly);
	}
	
	@Override
	public PurchaseOrder fulfillPurchaseOrder(PurchaseOrderDTO order){
		logger.debug("Checking if order exists in the system");
		PurchaseOrder purchaseOrder = orderService.getOrderById(order.getId());
		Map<String, Item> itemMap = new HashMap<>();
		Shelf shelf = null;
		Rack rack = null;
		Crate crate = null;
		Warehouse warehouse = purchaseOrder.getShippingWarehouse();
		itemService.createDefaultStorageLocation(warehouse.getId());
		shelf = shelfService.getShelfByNameAndWarehouseId("default", warehouse.getId());
		rack = rackService.getRackByNameAndShelfId("default", shelf.getId());
		crate = crateService.getCrateByNameAndRackId("default", rack.getId());
		Item item = null;
		Iterator<ItemOrderLine> iterator = purchaseOrder.getItems().iterator();
		for(ItemRequestPair request: order.getItemDetails()){
			ItemDetails updatedDetails = null;
			if(request.getBarcode() == null || "".equals(request.getBarcode())){
				request.setBarcode(itemService.generateBarcode());
				iterator.next().setBarcode(request.getBarcode());
			}
			if(itemMap.containsKey(request.getItemCode()))
				item = itemMap.get(request.getItemCode());
			else{
				item = itemService.getItemByCode(request.getItemCode());
				itemMap.put(request.getItemCode(), item);
			}
			if(item == null){
				throw new RuntimeException("Item with code "+request.getItemCode()+ " was not found!");
			}
			if(item.isSerialized()){
				updatedDetails = new ItemDetails(request.getVendorProductId(), request.getReceiveDate(), request.getBestBefore(), null, request.getWarrantyInMonths(), 
						request.getBarcode(), 0, warehouse, request.getItemCode(), ItemStatus.AVAILABLE.value(), 
						null, order.getOrderNo(), request.getUnitPrice(), request.getWholesalePrice());
				item.getItemDetails().add(updatedDetails);
				item.getItemCount().add(new ItemCount(warehouse, request.getItemCode(), item.getDescription(), item.getVendorAlias(), request.getBarcode(), ItemStatus.AVAILABLE.value(), 
						0, request.getUnitPrice(), request.getWholesalePrice(), shelf, rack, crate));
			}else{
				if(item.getItemDetails() != null && item.getItemDetails().size() > 0){
					updatedDetails = new ItemDetails(request.getVendorProductId(), request.getReceiveDate(), request.getBestBefore(), null, 
							request.getWarrantyInMonths(), request.getBarcode(), 0, 
							warehouse, request.getItemCode(), ItemStatus.AVAILABLE.value(), 
							null, order.getOrderNo(), request.getUnitPrice(), request.getWholesalePrice());
					item.getItemDetails().add(updatedDetails);
					item.getItemCount().add(new ItemCount(warehouse, request.getItemCode(), item.getDescription(), item.getVendorAlias(), request.getBarcode(), ItemStatus.AVAILABLE.value(), 
							0, request.getUnitPrice(), request.getWholesalePrice(), shelf, rack, crate));
				}else{
					updatedDetails = new ItemDetails(request.getVendorProductId(), request.getReceiveDate(), 
							request.getBestBefore(), null, request.getWarrantyInMonths(), 
							request.getBarcode(), 0, warehouse, request.getItemCode(), ItemStatus.AVAILABLE.value(), null, order.getOrderNo(),
							request.getUnitPrice(), request.getWholesalePrice());
					item.getItemDetails().add(updatedDetails);
					item.getItemCount().add(new ItemCount(warehouse, request.getItemCode(), item.getDescription(), item.getVendorAlias(), request.getBarcode(), ItemStatus.AVAILABLE.value(), 
							0, request.getUnitPrice(), request.getWholesalePrice(), shelf, rack, crate));
				}
			}
			purchaseOrder.getItemDetails().add(updatedDetails);
		}
		return purchaseOrder;			
	}
		
	@Override
	public List<ItemRequestPair> fulfillPurchaseOrder(Date date, String voucherRef, List<ItemRequestPair> requests, Warehouse warehouse, boolean isOnTheFly) {
		logger.debug("Checking if order exists in the system");
		PurchaseOrderDTO order = orderService.getOrderDTOByOrderNo(voucherRef);
		Map<String, Item> itemMap = new HashMap<>();
		Shelf shelf = null;
		Rack rack = null;
		Crate crate = null;
		itemService.createDefaultStorageLocation(warehouse.getId());
		shelf = shelfService.getShelfByNameAndWarehouseId("default", warehouse.getId());
		rack = rackService.getRackByNameAndShelfId("default", shelf.getId());
		crate = crateService.getCrateByNameAndRackId("default", rack.getId());
		if(order != null){
			Item item = null;
			Collection<ItemRequestPair> updatedItemDetails = new ArrayList<>();
			for(ItemRequestPair request: requests){
				ItemDetails updatedDetails = null;
				if(request.getBarcode() == null || "".equals(request.getBarcode())){
					request.setBarcode(itemService.generateBarcode());
				}
				if(itemMap.containsKey(request.getItemCode()))
					item = itemMap.get(request.getItemCode());
				else{
					item = itemService.getItemByCode(request.getItemCode());
					itemMap.put(request.getItemCode(), item);
				}
				if(item == null){
					throw new RuntimeException("Item with code "+request.getItemCode()+ " was not found!");
				}
				if(item.isSerialized()){
					updatedDetails = new ItemDetails(request.getVendorProductId(), request.getReceiveDate(), request.getBestBefore(), null, request.getWarrantyInMonths(), 
							request.getBarcode(), 0, warehouse, request.getItemCode(), ItemStatus.AVAILABLE.value(), 
							null, voucherRef, request.getUnitPrice(), request.getWholesalePrice());
					item.getItemDetails().add(updatedDetails);
					updatedItemDetails.add(dtoWrapper.convert(updatedDetails));
					item.getItemCount().add(new ItemCount(warehouse, request.getItemCode(), item.getDescription(), item.getVendorAlias(), request.getBarcode(), ItemStatus.AVAILABLE.value(), 
							0, request.getUnitPrice(), request.getWholesalePrice(), shelf, rack, crate));
				}else{
					if(item.getItemDetails() != null && item.getItemDetails().size() > 0){
						updatedDetails = new ItemDetails(request.getVendorProductId(), request.getReceiveDate(), request.getBestBefore(), null, 
								request.getWarrantyInMonths(), request.getBarcode(), 0, 
								warehouse, request.getItemCode(), ItemStatus.AVAILABLE.value(), 
								null, voucherRef, request.getUnitPrice(), request.getWholesalePrice());
						item.getItemDetails().add(updatedDetails);
						updatedItemDetails.add(dtoWrapper.convert(updatedDetails));
						item.getItemCount().add(new ItemCount(warehouse, request.getItemCode(), item.getDescription(), item.getVendorAlias(), request.getBarcode(), ItemStatus.AVAILABLE.value(), 
								0, request.getUnitPrice(), request.getWholesalePrice(), shelf, rack, crate));
					}else{
						updatedDetails = new ItemDetails(request.getVendorProductId(), request.getReceiveDate(), 
								request.getBestBefore(), null, request.getWarrantyInMonths(), 
								request.getBarcode(), 0, warehouse, request.getItemCode(), ItemStatus.AVAILABLE.value(), null, voucherRef,
								request.getUnitPrice(), request.getWholesalePrice());
						item.getItemDetails().add(updatedDetails);
						updatedItemDetails.add(dtoWrapper.convert(updatedDetails));
						item.getItemCount().add(new ItemCount(warehouse, request.getItemCode(), item.getDescription(), item.getVendorAlias(), request.getBarcode(), ItemStatus.AVAILABLE.value(), 
								0, request.getUnitPrice(), request.getWholesalePrice(), shelf, rack, crate));
					}
				}
			}
			inventoryTransactionService.stockInventory(date, 
					(List<ItemRequestPair>)updatedItemDetails, warehouse, ItemStatus.AVAILABLE.value(), ItemStatus.AVAILABLE.text(),
					Constants._PURCHASE_ORDER_FULFILLMENT, voucherRef);
			return (List<ItemRequestPair>)updatedItemDetails;			
		}else{
			logger.error("Order with orderNo '"+voucherRef+"' does not exist!");
			throw new RuntimeException("Order with orderNo '"+voucherRef+"' does not exist!");
		}
	}
	
	@Override
	public List<ItemDTO> fulfillPurchaseOrderReturn(Date date, String voucherRef, List<ItemRequestPair> requests, String workshop) {
		List<ItemDTO> itemList = new ArrayList<>();
		Map<String, ItemRequestPair> uniqueItems = new HashMap<>();
		for(ItemRequestPair request: requests){
			uniqueItems.put(request.getBarcode(), request);
		}
		try{
			for (String barcode: uniqueItems.keySet()){
				itemList.add(dtoWrapper.convert(itemService.getItemByCode(uniqueItems.get(barcode).getItemCode())));
				ItemRequestPair request = uniqueItems.get(barcode);
				request.setStatusText(ItemStatus.getStatus(request.getStatus()).text());
			}
			inventoryTransactionService.issueInventory(date, requests, warehouseService.getWarehouseByName(workshop), 
					Constants._STATUS_RETURN, Constants._PURCHASE_ORDER_RETURN, Constants._PURCHASE_ORDER_RETURN, voucherRef);
			itemService.issueItems(requests);
		}catch(Exception ex){
			logger.error("Exception thrown while issuing items "+ex.getMessage());
			throw new RuntimeException(ex.getMessage());
		}
		return itemList;
	}
	
	public List<ItemDTO> fulfillSalesOrderRestock(Date date, String voucherRef, List<ItemRequestPair> requests, String workshop){
		List<ItemDTO> itemList = new ArrayList<>();
		HashMap<String, ItemRequestPair> uniqueItems = new HashMap<>();
		for(ItemRequestPair request: requests){
			uniqueItems.put(request.getBarcode(), request);
		}
		for (String barcode: uniqueItems.keySet()){
			Item item = itemService.getItemByCode(uniqueItems.get(barcode).getItemCode());
			ItemRequestPair request = uniqueItems.get(barcode);
			request.setStatusText(ItemStatus.getStatus(request.getStatus()).text());
			itemList.add(dtoWrapper.convert(item));
		}
		inventoryTransactionService.stockInventory(date, requests, warehouseService.getWarehouseByName(workshop), 
				Constants._RESTOCK, Constants._SALES_ORDER_RESTOCK, Constants._SALES_ORDER_RESTOCK, voucherRef);
		restockItems(date, voucherRef, requests, workshop);
		return itemList;
	}

	@Override
	public List<ItemCount> fulfillStockTransferSend(Date date, String voucherRef, List<ItemRequestPair> requests,
			String workshop){
		List<ItemCount> itemList = new ArrayList<>();
		for(ItemRequestPair request: requests){
			try{
				/* deduct the stock from inventory but do not commit the transaction just yet */
				request.setStatusText(ItemStatus.getStatus(request.getStatus()).text());
				itemService.updateItemCountWarehouseLocation(request.getBarcode(), warehouseService.getWarehouseByName(workshop).getId());
				ItemCount itemCount = itemService.getItemCountByBarcode(request.getBarcode());
				itemList.add(itemCount);
			}catch(Exception ex){
				logger.error("Exception thrown while recording transaction");
				throw new RuntimeException(ex.getMessage());
			}
		}
		//inventoryTransactionService.issueInventory(date, requests,
			//	warehouseService.getWarehouseByName(workshop), ItemStatus.AVAILABLE.value(), ItemStatus.AVAILABLE.text(), Constants._STOCK_TRANSFER_SEND,
				//voucherRef);
		em.flush();
		//itemService.invoiceItems(requests);
		return itemList;
	}
	
	@Override
	public List<ItemCount> fulfillStockTransferReceive(Date date, String voucherRef, List<ItemRequestPair> requests, String workshop){
		List<ItemCount> itemList = new ArrayList<>();
		//first need to check here if the transfer operation was initiated for the same voucher ref
		Warehouse warehouse = warehouseService.getWarehouseByName(workshop);
		for(ItemRequestPair request: requests){
			try{
				request.setStatusText(ItemStatus.getStatus(request.getStatus()).text());
				///Item tem = itemService.getItemByCode(request.getItemCode());
				/* deduct the stock from inventory but do not commit the transaction just yet */
				//if(item.isSerialized()){
					
				//}else{
					//itemService.updateItemDetails(request.getBarcode(), warehouse, request.getStatus());
				//}
				itemService.updateItemDetailsCountLocationStatus(request.getBarcode(), warehouse, ItemStatus.AVAILABLE.value());
			}catch(Exception ex){
				logger.error("Exception thrown while recording transaction");
				return null;
			}
		}
		//inventoryTransactionService.stockInventory(date, requests, warehouse, Constants._STATUS_RECEIVED,
				//Constants._STOCK_TRANSFER_RECEIVE, Constants._STOCK_TRANSFER_RECEIVE, voucherRef);
		return itemList;
	}

	@Override
	public boolean fulfillStockAdjustment(HashMap<String, ItemRequestPair> uniqueItems, Warehouse warehouse, String voucherRef){
		try{
			List<ItemRequestPair> request = new ArrayList<>();
			for (String barcode : uniqueItems.keySet()){
				request.add(uniqueItems.get(barcode));
			}
			inventoryTransactionService.recordTransaction(Calendar.getInstance().getTime(), 
					request, warehouse, Constants._STATUS_FULFILLED, Constants._STOCK_ADJUSTMENT, 
					Constants._STOCK_ADJUSTMENT, voucherRef);
			return true;
		}catch(Exception ex){
			logger.error("Exception thrown while updating stock adjustment entry "+ex.getMessage());
			return false;
		}
	}
	
	@Override
	public boolean fulfillNewItemStockAdjustment(Item item, ItemCount countDetails) {
		try{
			String statusText = ItemStatus.getStatus(countDetails.getStatus()).text();
			List<ItemRequestPair> request = new ArrayList<>();
			ItemRequestPair pair = new ItemRequestPair();
			pair.setItemCode(item.getCode());
			pair.setBarcode(countDetails.getBarcode());
			request.add(pair);
			inventoryTransactionService.recordTransaction(Calendar.getInstance().getTime(), request, countDetails.getWarehouse(), 
					countDetails.getStatus(), statusText, Constants._STOCK_ADJUSTMENT, 
					docSequenceService.getNextSequence(Constants._STOCK_ADJUSTMENT, Constants._STOCK_ADJUSTMENT_PREFIX));
			return true;
		}catch(Exception ex){
			logger.error("Exception thrown while updating stock adjustment entry "+ex.getMessage());
			return false;
		}
	}

	@Override
	public void reserveItemsForOrder(Date date, String voucherRef, List<ItemRequestPair> requests, String workshop) {
		try{
			boolean isRequestPossible = false;
			//check if booking is possible
			logger.debug("Checking if requests are possible to be fulfilled");
			for(ItemRequestPair request: requests){
				request.setStatusText(ItemStatus.getStatus(request.getStatus()).text());
				if(request.getAssignedSalesOrder() == null){
					request.setAssignedSalesOrder(voucherRef);
				}
				if(isRequestPossible(request)){
					logger.info("Way to go...current inventory suffices for the request");
					isRequestPossible = true;
				}else{
					logger.info("Request is not possible : "+request.toString());
					logger.info("Check if negative inventory accounting is allowed");
				}
				if(!isRequestPossible){
					logger.error("Request cannot be fulfilled");
					throw new RuntimeException("Request cannot be fulfilled!");
				}
			}
			itemService.reserveItems(requests);
		}catch(Exception ex){
			logger.error("Exception thrown while reserving items "+ex.getMessage());
			throw new RuntimeException(ex.getMessage());
		}
	}

	@Override
	public void issueItemsForOrder(Date date, String voucherRef, List<ItemRequestPair> requests, String workshop) {
		try{
			itemService.issueItems(requests);
		}catch(Exception ex){
			logger.error("Exception thrown while reserving items "+ex.getMessage());
			throw new RuntimeException(ex.getMessage());
		}
	}
	
	@Override
	public void restockItems(Date date, String voucherRef, List<ItemRequestPair> requests, String workshop){
		try{
			itemService.restockItems(requests);
		}catch(Exception ex){
			logger.error("Exception thrown while reserving items "+ex.getMessage());
			throw new RuntimeException(ex.getMessage());
		}
	}
	
	@Override
	public void receiveItems(Date date, String voucherRef, List<ItemRequestPair> requests, String workshop){
		HashMap<String, ItemRequestPair> uniqueItems = new HashMap<>();
		for(ItemRequestPair request: requests){
			uniqueItems.put(request.getBarcode(), request);
		}
		for (String barcode: uniqueItems.keySet()){
			ItemRequestPair request = uniqueItems.get(barcode);
			request.setStatusText(ItemStatus.getStatus(request.getStatus()).text());
		}
		inventoryTransactionService.stockInventory(date, requests, warehouseService.getWarehouseByName(workshop), 
				Constants._RESTOCK, Constants._SALES_ORDER_RESTOCK, Constants._SALES_ORDER_RESTOCK, voucherRef);
		try{
			itemService.receiveItems(requests);
		}catch(Exception ex){
			logger.error("Exception thrown while reserving items "+ex.getMessage());
			throw new RuntimeException(ex.getMessage());
		}
	}
	
	public void issueItemsForSalesOrder(Date date, String voucherRef, List<ItemRequestPair> requests, String workshop) {
		List<ItemDTO> itemList = new ArrayList<>();
		HashMap<String, ItemRequestPair> uniqueItems = new HashMap<>();
		for(ItemRequestPair request: requests){
			uniqueItems.put(request.getBarcode(), request);
		}
		try{
			for (String barcode : uniqueItems.keySet()){
				Item item = itemService.getItemByCode(uniqueItems.get(barcode).getItemCode());
				itemList.add(dtoWrapper.convert(item));
				ItemRequestPair request = uniqueItems.get(barcode);
				request.setStatusText(ItemStatus.getStatus(request.getStatus()).text());
			}
			inventoryTransactionService.issueInventory(date, requests, warehouseService.getWarehouseByName(workshop),
					Constants._STATUS_FULFILLED, Constants._SALES_ORDER_FULFILLMENT, Constants._SALES_ORDER_FULFILLMENT, voucherRef);
			itemService.issueItems(requests);
		}catch(Exception ex){
			logger.error("Exception thrown while issuing items "+ex.getMessage());
			throw new RuntimeException(ex.getMessage());
		}
	}
	
	public void invoiceItems(Date date, String voucherRef, List<ItemRequestPair> requests){
		try{
			itemService.invoiceItems(requests);
		}catch(Exception ex){
			logger.error("Exception thrown while invoicing items "+ex.getMessage());
			throw new RuntimeException(ex.getMessage());
		}
	}
		
	@Override
	public Collection<SalesOrderItemLine> updateKitRequest(Date date, String voucherRef, List<ItemSalesRequest> requests, String workshopRef, int status) {
		try{
			Kit updatedKit = kitService.validateKitRequest(date, voucherRef, requests, workshopRef, status);
			if(updatedKit != null){
				return updatedKit.getItems();
			}else{
				throw new Exception("Something went wrong!");
			}
		}catch(Exception ex){
			logger.debug("Exception thrown while saving kit : "+ex.getMessage());
			throw new RuntimeException(Constants._GENERIC_ERROR_MESSAGE);
		}
	}
}
