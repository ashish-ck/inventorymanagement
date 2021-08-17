package com.drivojoy.inventory.services;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.drivojoy.inventory.dto.ItemDTO;
import com.drivojoy.inventory.dto.ItemRequestPair;
import com.drivojoy.inventory.dto.ItemSalesRequest;
import com.drivojoy.inventory.dto.PurchaseOrderDTO;
import com.drivojoy.inventory.models.Item;
import com.drivojoy.inventory.models.ItemCount;
import com.drivojoy.inventory.models.PurchaseOrder;
import com.drivojoy.inventory.models.SalesOrderItemLine;
import com.drivojoy.inventory.models.Warehouse;

/**
 * 
 * @author ashishsingh
 *
 */
@Service
@Transactional
public interface IInventoryOperationsService {

	/**
	 * Checks if a request can be fulfilled
	 * @param request Request
	 * @return returns true if request is possible
	 */
	boolean isRequestPossible(ItemRequestPair request);
	
	/**
	 * This service fulfills the sales order by updating inventory balances, item statuses and transaction ledger
	 * @param date Voucher Date
	 * @param voucherRef Voucher Reference
	 * @param requests Request Details
	 * @param workshop Assigned warehouse
	 * @return List of item
	 */
	List<ItemDTO> fulfillSalesOrder(Date date, String voucherRef, List<ItemRequestPair> requests, String workshop);

	/**
	 * This service fulfills the purchase order by updating inventory balances, item statuses and transaction ledger
	 * @param date Voucher Date
	 * @param voucherRef Voucher Reference
	 * @param requests Request Details
	 * @param workshop Assigned warehouse
	 * @param isOnTheFly Is on the fly
	 * @return List of item request pairs 
	 */
	List<ItemRequestPair> fulfillPurchaseOrder(Date date, String voucherRef, List<ItemRequestPair> requests, Warehouse warehouse, boolean isOnTheFly);

	/**
	 * This service fulfills the purchase order returns by updating inventory balances, item statuses and transaction ledger
	 * @param date Voucher Date
	 * @param voucherRef Voucher Reference
	 * @param requests Request Details
	 * @param workshop Assigned warehouse
	 * @return List of items
	 */
	List<ItemDTO> fulfillPurchaseOrderReturn(Date date, String voucherRef, List<ItemRequestPair> requests, String workshop);
	
	/**
	 * This service fulfills the sales order restock by updating inventory balances, item statuses and transaction ledger
	 * @param date Voucher Date
	 * @param voucherRef Voucher Reference
	 * @param requests Request Details
	 * @param workshop Assigned warehouse
	 * @return List of items
	 */
	List<ItemDTO> fulfillSalesOrderRestock(Date date, String voucherRef, List<ItemRequestPair> requests, String workshop);
	
	/**
	 * This service fulfills stock transfer send operation by updating inventory balances, item statuses and transaction ledger
	 * @param date Voucher Date
	 * @param voucherRef Voucher Reference
	 * @param requests Request Details
	 * @param workshop Assigned warehouse
	 * @return List of item counts
	 */
	List<ItemCount> fulfillStockTransferSend(Date date, String voucherRef, List<ItemRequestPair> requests, String workshop);
	
	/**
	 * This service fulfill stock transfer receive operation by updating inventory balances, item statuses and transaction ledger
	 * @param date Voucher Date
	 * @param voucherRef Voucher Reference
	 * @param requests Request Details
	 * @param workshop Assigned warehouse
	 * @return List of item counts
	 */
	List<ItemCount> fulfillStockTransferReceive(Date date, String voucherRef, List<ItemRequestPair> requests, String workshop);
	
	/**
	 * Issues items for sales order
	 * @param date Voucher Date
	 * @param voucherRef Voucher Reference
	 * @param requests Request Details
	 * @param workshop Assigned warehouse
	 */
	void issueItemsForSalesOrder(Date date, String voucherRef, List<ItemRequestPair> requests, String workshop);
	
	/**
	 * Reserves items for sales order
	 * @param date Voucher Date
	 * @param voucherRef Voucher Reference
	 * @param requests Request Details
	 * @param workshop Assigned warehouse
	 */
	void reserveItemsForOrder(Date date, String voucherRef, List<ItemRequestPair> requests, String workshop);
	
	/**
	 * Service to deduct items from inventory
	 * @param date Voucher Date
	 * @param voucherRef Voucher Reference
	 * @param requests Request Details
	 * @param workshop Assigned warehouse
	 */
	void issueItemsForOrder(Date date, String voucherRef, List<ItemRequestPair> requests, String workshop);
	
	/**
	 * Service to restock items
	 * @param date Voucher Date
	 * @param voucherRef Voucher Reference
	 * @param requests Request Details
	 * @param workshop Assigned warehouse
	 */
	void restockItems(Date date, String voucherRef, List<ItemRequestPair> requests, String workshop);
	
	/**
	 * Service to receive items
	 * @param date Voucher Date
	 * @param voucherRef Voucher Reference
	 * @param requests Request Details
	 * @param workshop Assigned warehouse
	 */
	void receiveItems(Date date, String voucherRef, List<ItemRequestPair> requests, String workshop);
	
	/**
	 * Fulfills stock adjustment entries
	 * @param uniqueItems Unique items list with item code and quantity
	 * @param warehouse Assigned warehouse
	 * @param voucherRef Voucher Reference
	 * @return true if stock adjustment is done successfully.
	 */
	boolean fulfillStockAdjustment(HashMap<String, ItemRequestPair> uniqueItems, Warehouse warehouse, String voucherRef);
	
	/**
	 * Short hand method to fulfill stock adjustment of a new item
	 * @param item Item
	 * @param countDetails new count details
	 * @return true if stock adjustment is done successfully.
	 */
	boolean fulfillNewItemStockAdjustment(Item item, ItemCount countDetails);
	
	Collection<SalesOrderItemLine> updateKitRequest(Date date, String voucherRef, List<ItemSalesRequest> requests, String workshopRef, int status);

	List<ItemRequestPair> fulfillPurchaseOrder(Date date, String voucherRef, List<ItemRequestPair> listOfItems,
			String workshopRef, boolean isOnTheFly);
	
	PurchaseOrder fulfillPurchaseOrder(PurchaseOrderDTO order);
}
