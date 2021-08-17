package com.drivojoy.inventory.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.drivojoy.inventory.dto.ItemCountDTO;
import com.drivojoy.inventory.dto.ItemCountQuantityDTO;
import com.drivojoy.inventory.dto.ItemDTO;
import com.drivojoy.inventory.dto.ItemNetworkDTO;
import com.drivojoy.inventory.dto.ItemReconcileReport;
import com.drivojoy.inventory.dto.ItemRequestPair;
import com.drivojoy.inventory.dto.RequestParamsDTO;
import com.drivojoy.inventory.models.Item;
import com.drivojoy.inventory.models.ItemCount;
import com.drivojoy.inventory.models.Warehouse;
import com.drivojoy.inventory.models.datatables.DTParameters;
import com.drivojoy.inventory.models.datatables.DTResult;
import com.drivojoy.inventory.utils.KeyValuePair;

/**
 * @author ashish.singh
 *
 */
@Service
@Transactional
public interface IItemService {

	/**Fetch all items.
	 * @param page Page
	 * @return Page of item type
	 */
	Page<Item> getAll(Pageable page);

	/**Create an item.
	 * @param itemDTO item
	 * @return Item
	 */
	ItemDTO createItem(ItemDTO itemDTO);

	/**Update an item.
	 * @param itemDTO Item
	 * @return Item
	 */
	ItemDTO editItem(ItemDTO itemDTO);

	/**
	 * @param description Description
	 * @return Item
	 */
	Item createRandomItem(String description);

	/**Deactivate an item.
	 * @param itemCode Item code
	 * @return Item
	 */
	ItemDTO deactivateItem(String itemCode);

	/**
	 * Reactivate an item.
	 * @param itemCode Item code
	 * @return Item
	 */
	ItemDTO reactivateItem(String itemCode);

	/**
	 * Update an item.
	 * @param item Item
	 * @return Item
	 */
	Item updateItem(Item item);

	/**
	 * Fetch item dto by item id.
	 * @param itemId Id
	 * @return Item
	 */
	ItemDTO getItemDTOById(long itemId);

	/**
	 * Fetch item by item id.
	 * @param itemId Id
	 * @return Item
	 */
	Item getItemById(long itemId);

	/**
	 * Fetch item by item code.
	 * @param itemCode Item code
	 * @return Item	
	 */
	Item getItemByCode(String itemCode);
	
	ItemNetworkDTO getItemNetworkByCode(String itemCode);

	/**Fetch list of item dto that are active.
	 * @return List of Items
	 */
	List<ItemDTO> getAllActiveItems();

	/**
	 * Get quantity in hand in a warehouse for an item.
	 * @param warehouse Warehouse
	 * @param item Item
	 * @return Quantity in hand
	 */
	double getQuantityInHandByWarehouse(Warehouse warehouse, Item item);

	/**
	 * Fetch quantity in hand in a warehouse by item code.
	 * @param warehouse Warehouse
	 * @param itemCode item code
	 * @return Quantity in hand by warehouse
	 */
	double getQuantityInHandByWarehouse(Warehouse warehouse, String itemCode);

	/**
	 * Get item details.
	 * @param id Id
	 * @return Item
	 */
	ItemDTO getItemDetails(long id);

	/** Deprecated
	 * @param barcode Barcode
	 * @return Item
	 */
	Item getItemByBarcode(String barcode);

	/**
	 * Get list of items by category.
	 * @param id Id
	 * @return List of Items
	 */
	List<ItemDTO> getItemsByCategory(long id);

	/**
	 * Get list of items by search criteria.
	 * @param criterias search criteria
	 * @param mode Mode
	 * @return List of  Items
	 */
	List<Item> getItemsByCriteria(List<KeyValuePair<String, List<String>>> criterias, int mode);

	/**
	 * Get list of item dto by search criteria.
	 * @param criterias Search criterias
	 * @param mode Mode
	 * @return List of Items
	 */
	List<ItemDTO> getItemsDTOByCriteria(List<KeyValuePair<String, List<String>>> criterias, int mode);

	/**
	 * Import item from xls spreadsheet file.
	 * @param fileName File name
	 * @return List of items
	 * @throws FileNotFoundException File not found exception
	 * @throws IOException IO exception
	 */
	List<ItemDTO> importItems(String fileName) throws FileNotFoundException, IOException;

	/**
	 * Updates item count and item details for a warehouse with given status and item.
	 * @param warehouse Warehouse
	 * @param status Status
	 * @param item Item
	 * @return Item
	 */
	Item updateItemCountAndItemDetails(Warehouse warehouse, int status, Item item);
	
	/**
	 * Updates item count, location and status for a given barcode and warehouse.
	 * @param barcode Barcode
	 * @param warehouse Warehouse
	 * @param status Status
	 */
	void updateItemDetailsCountLocationStatus(String barcode, Warehouse warehouse, int status);

	/**
	 * @param requests List of item request pairs
	 */
	void reserveItems(List<ItemRequestPair> requests);

	/**
	 * @param requests List of item request pairs
	 */
	void issueItems(List<ItemRequestPair> requests);

	/**
	 * @param requests List of item request pairs
	 */
	void restockItems(List<ItemRequestPair> requests);

	/**
	 * @param requests List of item request pairs
	 */
	void invoiceItems(List<ItemRequestPair> requests);

	/**
	 * @param requests List of item request pairs
	 */
	void receiveItems(List<ItemRequestPair> requests);

	/**
	 * @param params DT params
	 * @return DT result of item
	 */
	DTResult<ItemDTO> getItemsPagination(DTParameters params);

	/**
	 * @param barcode Barcode
	 * @param warehouse Warehouse
	 * @param status Status
	 */
	void updateItemDetails(String barcode, Warehouse warehouse, int status);

	/**
	 * @param itemCode Item code
	 * @return Item
	 */
	ItemDTO getItemDTOByCode(String itemCode);

	/**
	 * @param warehouse Warehouse
	 * @return List of items
	 */
	List<ItemDTO> getWarningPointItems(String warehouse);

	/**
	 * @param warehouse Warehouse
	 * @return List of items
	 */
	List<ItemDTO> getReorderPointItems(String warehouse);

	/**
	 * @param warehouse Warehouse
	 * @param userRef User ref
	 * @return Inventory count sheet
	 */
	String getInventoryCountSheetByWarehouse(String warehouse, String userRef);

	/**
	 * @param userRef User ref
	 * @return Inventory count sheet
	 */
	String getInventoryCountSheet(String userRef);

	/**
	 * @param itemDTO item
	 * @return Item
	 */
	ItemDTO updateInventoryLevels(ItemDTO itemDTO);

	/**
	 * Deprecated.
	 * @param barcode Barcode
	 * @return true if barcode is valid
	 */
	boolean isBarcodeValid(String barcode);

	/**
	 * @param itemCode Item code
	 * @param barcode Barcode
	 * @return true is barcode is valid
	 */
	boolean isItemBarcodeValid(String itemCode, String barcode);

	/**
	 * @param warehouse Warehouse
	 * @param itemCode Item code
	 * @return Quantity available by Warehouse
	 */
	double getQuantityAvailableByWarehouse(Warehouse warehouse, String itemCode);

	/**Generate barcode.
	 * @return Barcode
	 */
	String generateBarcode();

	/**
	 * Returns a list of available warehouse items by item code and warehouse id.
	 * @param itemCode Item code
	 * @param warehouseId Warehouse id
	 * @return List of item counts
	 */
	List<ItemCount> getAvailableItemCountByCodeAndWarehouse(String itemCode, long warehouseId);

	/**
	 * Updates item count status by barcode.
	 * @param status Status
	 * @param barcode Barcode
	 */
	void setItemCountStatusByBarcode(int status, String barcode);

	/**
	 * Updates subkit item details status by barcode.
	 * @param kitId Kit id
	 * @param status Status
	 * @param subkitId Subkit id
	 * @param barcode Barcode
	 */
	void updateKitSubkitItemDetailsByBarcode(long kitId, int status, long subkitId, String barcode);

	/**
	 * Returns list of warehouse item by warehouse id.
	 * @param id Id
	 * @return List of item count
	 */
	List<ItemCountDTO> getItemsByWarehouseId(long id);

	/**
	 * Return list of warehouse items by shelf id.
	 * @param id Id
	 * @return List of item count
	 */
	List<ItemCountDTO> getItemsInShelf(long id);

	/**
	 * Returns list of warehouse items by shelf id.
	 * @param id Id
	 * @return List of item count
	 */
	List<ItemCountDTO> getItemsInRack(long id);

	/**
	 * Returns list of warehouse items by rack id.
	 * @param id Id
	 * @return List of item count
	 */
	List<ItemCountDTO> getItemsInCrate(long id);

	/**
	 * Returns list of warehouse items by crate id.
	 * @param shelf shelf id
	 * @param rack rack id
	 * @param crate crate id
	 * @return List of item count
	 */
	List<ItemCount> getItemsInShelfRackCrate(long shelf, long rack, long crate);

	/**
	 * Returns list of warehouse items by shelf, rack and crate ids.
	 * @param shelf shelf id
	 * @param rack rack id
	 * @return List of item count
	 */
	List<ItemCount> getItemsInShelfRack(long shelf, long rack);
	
	/**
	 * Imports a spreadsheet to warehouse items.
	 * @param fileName File name
	 * @return List of items
	 */
	List<ItemDTO> importItemsNew(String fileName);

	/**
	 * Returns list of all warehouse items.
	 * @return List of item count
	 */
	List<ItemCountDTO> getAllBarcodeItems();
	
	/**
	 * Returns warehouse item by barcode.
	 * @param barcode Barcode
	 * @return Item count
	 */
	ItemCount getItemCountByBarcode(String barcode);
	
	/**
	 * Returns list of available barcodes by list of barcode and warehouse code.
	 * @param code Code
	 * @param barcodes List of barcodes
	 * @return List of available barcodes
	 */
	List<String> getAvailableBarcodesByBarcodesAndWarehouse(String code, List<String> barcodes);
	
	/**
	 * Updates kit items to out of stock.
	 * @param warehouseId Warehouse id
	 * @param itemCode Item code
	 */
	void updateKitItemsToOutOfStock(long warehouseId, String itemCode);
	
	/**
	 * Updates shelf, rack, crate value by barcode.
	 * @param barcode Barcode 
	 * @param shelfId Shelf id
	 * @param rackId Rack id
	 * @param crateId Crate id
	 * @return Item count
	 */
	ItemCount updateItemCountStorageLocationByBarcode(String barcode, long shelfId, long rackId, long crateId);
	
	/**
	 * Return list of items by search criteria.
	 * @param criterias search criteria
	 * @param mode Mode
	 * @param warehouseId Warehouse id
	 * @return List of item count
	 */
	List<ItemCount> getItemCountsByCriteria(List<KeyValuePair<String, List<String>>> criterias, int mode, long warehouseId);
	
	/**
	 * Updates warehouse location by barcode.
	 * @param barcode Barcode
	 * @param warehouseId Warehouse Id
	 */
	void updateItemCountWarehouseLocation(String barcode, long warehouseId);
	
	/**
	 * Update warehouse item location for a list of request.
	 * @param requests List of item request pair
	 * @param warehouseId Id
	 */
	void updateItemCountWarehouseLocation(List<ItemRequestPair> requests, long warehouseId);
	
	/**
	 * Gets list of barcode suggestion with warehouse code and list of item codes.
	 * @param warehouseCode Warehouse code
	 * @param itemCodes List of item codes
	 * @param voucherRef Voucher ref
	 * @return List of barcode suggestion
	 */
	List<String> getBarcodeSuggestion(String warehouseCode, List<String> itemCodes, String voucherRef);

	/**
	 * Gets the list of available item count with given item code.
	 * @param itemCode Item code
	 * @return List of item count
	 */
	List<ItemCount> getAvailableItemCountByCode(String itemCode);
	
	/**Create default shelf, default rack and default crate in warehouse.
	 * @param warehouseId Id
	 */
	void createDefaultStorageLocation(long warehouseId);
	
	/**Finds if an item exists.
	 * @param itemCode Item code
	 * @return true if item exists
	 */
	boolean doesItemExists(String itemCode);

	/**Item reconcile report
	 * @param fromDate From date
	 * @param toDate To date
	 * @param warehouseId Warehouse id
	 * @return Item reconcile report
	 */
	ItemReconcileReport getItemReport(Date fromDate, Date toDate, Long warehouseId);
	
	Integer getAvailableCountByCode(String code);

	Integer getAvailableCountByCodeAndWarehouseId(String itemCode, long warehouseId);
	
	DTResult<ItemCountDTO> getItemCountDTO(DTParameters params, Long warehouseId, Long shelfId, Long rackId, Long crateId);

	DTResult<ItemCountQuantityDTO> getFastMovingItems(RequestParamsDTO request);

	DTResult<ItemCountQuantityDTO> getSlowMovingItems(RequestParamsDTO request);
}