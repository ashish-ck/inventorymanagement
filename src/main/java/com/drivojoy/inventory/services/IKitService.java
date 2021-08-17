package com.drivojoy.inventory.services;

import java.util.Date;
import java.util.List;

//import javax.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.drivojoy.inventory.dto.InventoryRequest;
import com.drivojoy.inventory.dto.ItemRequestPair;
import com.drivojoy.inventory.dto.ItemSalesRequest;
import com.drivojoy.inventory.dto.KitConsolidatedReport;
import com.drivojoy.inventory.dto.KitDTO;
import com.drivojoy.inventory.dto.OutOfStockKitItemsDTO;
import com.drivojoy.inventory.dto.SalesConsolidatedReport;
import com.drivojoy.inventory.models.Kit;
import com.drivojoy.inventory.models.Subkit;
import com.drivojoy.inventory.models.Warehouse;
import com.drivojoy.inventory.models.datatables.DTParameters;
import com.drivojoy.inventory.models.datatables.DTResult;

/**
 * 
 * @author ashish.singh
 *
 */
@Service
public interface IKitService {

	/**
	 * Saves a kit
	 * @param kit Kit
	 * @return Kit
	 */
	Kit saveKit(Kit kit);

	/**
	 * Fetches all Kits
	 * @return List of kits
	 */
	List<Kit> getAllKits();

	/**
	 * Finds kit by warehouse
	 * @param warehouseCode Code of warehouse
	 * @return List of kits
	 */
	List<Kit> getKitsByWarehouse(String warehouseCode);

	/**
	 * This is re-written method for validateKitRequest.
	 * @param date Date
	 * @param voucherRef Voucher ref
	 * @param requests List of request items
	 * @param workshopRef Warehouse code
	 * @param status Status
	 * @return Kit
	 */
	@Transactional(readOnly = false, rollbackFor=Exception.class, propagation = Propagation.REQUIRED, isolation=Isolation.READ_UNCOMMITTED)
	Kit validateKitRequest(Date date, String voucherRef, List<ItemSalesRequest> requests, String workshopRef,
			int status);

	/**
	 * Updates the statuses and quantities of items in a kit. Makes necessary
	 * ledger entries and updates balances
	 * 
	 * @param itemDetails Request
	 * @return Kit
	 */
	@Transactional(readOnly = false, rollbackFor=Exception.class, propagation = Propagation.REQUIRED, isolation=Isolation.READ_UNCOMMITTED)
	Kit updateKitItemDetails(InventoryRequest itemDetails);

	/**
	 * Updates the location of items in particular kit
	 * 
	 * @param kitNumber Kit number
	 * @param warehouse Warehouse
	 * @param status Status
	 * @param listOfItems List of items
	 * @return Kit
	 */
	@Transactional(readOnly = false, rollbackFor=Exception.class, propagation = Propagation.REQUIRED, isolation=Isolation.READ_UNCOMMITTED)
	Kit updateKitItemLocation(String kitNumber, String warehouse, int status, List<ItemRequestPair> listOfItems);

	/**
	 * Finds a kit by sales order number
	 * @param kitNumber Kit number
	 * @return Kit
	 */
	Kit getByKitNumber(String kitNumber);

	/**
	 * Gets the status of a kit identified by sales order number
	 * 
	 * @param kitNumber Kit number
	 * @return Status
	 */
	String getKitStatus(String kitNumber);

	/**
	 * Updates the statuses of items in all kit belonging to a particular
	 * warehouse. Used when a stock adjustment or purchase order is fulfilled
	 * @param requests List of item request pairs
	 */
	@Transactional(readOnly = false, rollbackFor=Exception.class, propagation = Propagation.REQUIRED, isolation=Isolation.READ_UNCOMMITTED)
	void updateKitItemStatus(List<ItemRequestPair> requests);

	/**
	 * Gets a consolidated report of kits and items in a particular date range
	 * @param fromDate From date
	 * @param toDate To date
	 * @param warehouseId Warehouse id
	 * @return Kit consolidated report
	 */
	KitConsolidatedReport getKitReport(Date fromDate, Date toDate, Long warehouseId);
	
	/**Gets a consolidated report of kits and items in a particular date range
	 * @param fromDate from Date
	 * @param toDate to date
	 * @param warehouseId warehouse Id
	 * @return Sales Consolidated report.
	 */
	SalesConsolidatedReport getSalesReport(Date fromDate, Date toDate, Long warehouseId);

	/**
	 * Finds out of stock items in kits within a date range belonging to a
	 * particular warehouse 
	 * @param fromDate From date
	 * @param toDate To date
	 * @param warehouseCode Warehouse code 
	 * @return List of out of stock items
	 */
	List<OutOfStockKitItemsDTO> getOutOfStockItemsByWarehouse(Date fromDate, Date toDate, String warehouseCode);

	/**
	 * Finds out of stock items in kit within a date range
	 * @param fromDate From date
	 * @param toDate To date
	 * @return List of out of stock items
	 */
	List<OutOfStockKitItemsDTO> getOutOfStockItems(Date fromDate, Date toDate);

	/**Pagination service to find open kits
	 * @param params DT params
	 * @param fromDate From date
	 * @param toDate To date
	 * @param requestWarehouseId 
	 * @param assignedWarehouseId
	 * @param status
	 * @return
	 */
	DTResult<KitDTO> getOpenKits(DTParameters params, Date fromDate, Date toDate, Long requestWarehouseId, Long assignedWarehouseId, Integer status);

	/**
	 * Pagination service to find closed kits
	 * 
	 * @param params DT params
	 * @param fromDate From date
	 * @param toDate To date
	 * @param requestWarehouseId 
	 * @param assignedWarehouseId
	 * @param status
	 * @return
	 */
	DTResult<KitDTO> getClosedKits(DTParameters params, Date fromDate, Date toDate, Long requestWarehouseId, Long assignedWarehouseId, Integer status);

	/**
	 * Adds list of request items to subkit.
	 * @param kit Kit
	 * @param subkit Subkit
	 * @param requests List of item request pairs
	 * @param status Status
	 * @param warehouse Warehouse
	 * @return Subkit
	 */
	Subkit addToSubkitWithRequest(Kit kit, Subkit subkit, List<ItemRequestPair> requests, int status,
			Warehouse warehouse);
	
	/**
	 * Returns subkit by subkit number
	 * @param subkitNumber Subtkit number
	 * @return Subkit
	 */
	Subkit getSubkitBySubkitNumber(String subkitNumber);
	
	/**
	 * Update kit and subkit item details status to available or out of stock.
	 * @param itemCode Item code
	 * @param warehouseId Warehouse Id
	 */
	void updateKitItemsToOutOfStockAndAvailable(String itemCode, long warehouseId);
	
	/**
	 * Changes the location of subkit from inventory to hub.
	 * @param subkit Subkit
	 * @return Subkit
	 */
	@Transactional(readOnly = false, rollbackFor=Exception.class, propagation = Propagation.REQUIRED, isolation=Isolation.READ_UNCOMMITTED)
	Subkit changeSubkitLocation(Subkit subkit);
	
	/**
	 * Returns list of kits that have status return expected.
	 * @return List of kits
	 */
	List<KitDTO> getReturnExpectedKits();
	
	/**
	 * @param params DT paramas
	 * @param warehouseRef Warehouse code
	 * @return DT result of kit
	 */
	DTResult<KitDTO> getConsolidatedKitReport(DTParameters params, String warehouseRef);
	
	/**Re opens a closed or cancelled kit.
	 * @param kitNumber Kit Number
	 * @return Kit
	 */
	Kit reopenKit(String kitNumber);
	
	/**Get by kit number with barcode suggestion.
	 * @param kitNumber kit number
	 * @return Kit
	 */
	Kit getByKitNumberWithSuggestion(String kitNumber);
}
