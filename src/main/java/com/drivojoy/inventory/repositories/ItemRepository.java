package com.drivojoy.inventory.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.drivojoy.inventory.models.Item;

/**
 * 
 * @author ashishsingh
 *
 */
@Repository
public interface ItemRepository extends JpaRepository <Item, Long> {

	/**
	 * Finds an item by code
	 * @param code Item code
	 * @return Item
	 */
	Item findByCode(String code);

	/**
	 * Finds an item by id
	 * @param id Item id
	 * @return Item
	 */
	Item findById(long id);
	
	/**
	 * Finds all active items
	 * @return List of items
	 */
	@Query("SELECT p FROM Item p WHERE p.isActive = true")
	List<Item> findAllActiveItems();
	
	/**
	 * Finds an item that has barcode represendted by 'barcode'
	 * @param barcode Barcode
	 * @return Item
	 */
	@Query(value="SELECT item.* FROM item LEFT JOIN item_count ON item_count.item_id = item.id WHERE item_count.barcode = :barcode", nativeQuery=true)
	Item findItemByBarcode(@Param("barcode") String barcode);
	
	/**
	 * Fetches all the attributes of the item represented by id
	 * @param id Id
	 * @return Item
	 */
	@Query("SELECT p FROM Item p JOIN FETCH p.itemDetails WHERE p.id = :id")
	Item getItemDetails(@Param("id") long id);
	
	/**Returns count of item by item code.
	 * @param itemCode Item code
	 * @return Count
	 */
	@Query(value="select item.id, item.code, item.description, item.vendor_alias from item where item.code = :code", nativeQuery=true)
	Object getShortItem(@Param("code") String itemCode);
	
	/**Returns count of item count by barcode.
	 * @param barcode Barcode
	 * @return Count
	 */
	@Query(value="SELECT count(item_count.barcode) FROM item_count WHERE item_count.barcode = :barcode", nativeQuery=true)
	Integer doesBarcodeExists(@Param("barcode") String barcode);
	
	/**
	 * Finds all items below warning point and above reorder point for a particular warehouse
	 * @param warehouse Warehouse Id
	 * @return List of items
	 */
	@Query(value="SELECT item.* FROM item LEFT JOIN item_count ON item_count.item_id = item.id WHERE item_count.warehouse = :warehouse", nativeQuery=true)
	List<Item> getWarningItems(@Param("warehouse") long warehouse);
	
	/**
	 * Finds all items below reorder point for a particular warehouse
	 * @param warehouse Warehouse Id
	 * @return List of items
	 */
	@Query(value="SELECT item.* FROM item LEFT JOIN item_count ON item_count.item_id = item.id WHERE item_count.warehouse = :warehouse AND item_count.reorder_point > 0 AND item_count.reorder_point >= 1", nativeQuery=true)
	List<Item> getReorderItems(@Param("warehouse") long warehouse);
	
	/**
	 * Finds all items below warning point and above reorder point
	 * @return List of items
	 */
	@Query(value="SELECT item.* FROM item LEFT JOIN item_count ON item_count.item_id = item.id WHERE item_count.warning_point >= 1 AND 1 > item_count.reorder_point", nativeQuery=true)
	List<Item> getWarningItems();
	
	/**
	 * Finds all items below reorder point
	 * @return List of items
	 */
	@Query(value="SELECT item.* FROM item LEFT JOIN item_count ON item_count.item_id = item.id WHERE item_count.reorder_point > 0 AND item_count.reorder_point >= 1", nativeQuery=true)
	List<Item> getReorderItems();
	
	/**
	 * Gets all items that are currently in inventory of a warehouse
	 * @param warehouse Warehouse id
	 * @return List of items
	 */
	@Query(value="SELECT item.* FROM item LEFT JOIN item_count ON item_count.item_id = item.id WHERE item_count.warehouse = :warehouse GROUP BY item.code", nativeQuery=true)
	List<Item> getInventoryCountSheetByWarehouse(@Param("warehouse")long warehouse);
	
	/**
	 * Gets all items that are currently in inventory across all warehouses
	 * @return List of items
	 */
	@Query(value="SELECT item.* FROM item LEFT JOIN item_count ON item_count.item_id = item.id WHERE item_count.status > 200 AND item_count.status <= 1100 GROUP BY item.code", nativeQuery=true)
	List<Item> getInventoryCountSheet();
	
	/**
	 * Updates the status of an item identified by barcode in a particular warehouse
	 * @param barcode Barcode
	 * @param status Status
	 * @param warehouseId Warehouse id
	 */
	/**
	 * @param barcode Barcode
	 * @param status Status
	 * @param warehouseId Warehouse id
	 */
	@Modifying(clearAutomatically=true)
	@Query(value="Update item_details SET status = :status WHERE barcode = :barcode AND warehouse = :warehouse ", nativeQuery=true)
	void updateItemDetails(@Param("barcode") String barcode, @Param("status") int status, @Param("warehouse") long warehouseId);

	/**
	 * Updates the quantity and status of an item identified by barcode in a particular warehouse
	 * @param barcode Barcode
	 * @param status Status
	 * @param warehouseId Warehouse id
	 */
	@Modifying(clearAutomatically=true)
	@Query(value="Update item_details SET status = :status WHERE barcode = :barcode AND warehouse = :warehouse", nativeQuery=true)
	void updateItemDetailsCountLocationStatus(@Param("barcode")String barcode, @Param("status")int status, @Param("warehouse") long warehouseId);
	
	/**
	 * Updates the status of an item identified by barcode in a particular warehouse
	 * @param barcode Barcode
	 * @param status Status
	 * @param warehouseId Warehouse id
	 */
	@Modifying(clearAutomatically=true)
	@Query(value="Update item_details SET status = :status WHERE barcode = :barcode AND warehouse = :warehouse", nativeQuery=true)
	void updateItemStatusLocation(@Param("barcode")String barcode, @Param("status")int status, @Param("warehouse") long warehouseId);
	
	/**Gets a list of item count items by code and status
	 * @param itemCode Item code
	 * @param warehouse Warehouse id
	 * @param status Status
	 * @return List of item count row object
	 */
	@Query(value="SELECT item_count.warehouse, item_count.item_code, item_count.description, item_count.vendor_alias, item_count.barcode, item_count.status, item_count.reorder_point, item_count.unit_price, item_count.wholesale_price, item_count.shelf, item_count.rack, item_count.crate FROM item_count WHERE item_count.item_code = :item_code AND item_count.warehouse = :warehouse AND item_count.status = :status", nativeQuery=true)
	List<Object[]> getItemCountByItemCodeAndWarehouseAndStatus(@Param("item_code") String itemCode, @Param("warehouse") long warehouse, @Param("status") int status);
	
	/**
	 * Updates the status of an item identified by barcode in a particular warehouse
	 * @param barcode Barcode
	 * @param status Status
	 */
	@Modifying(clearAutomatically=true)
	@Query(value="Update item_count SET status = :status WHERE barcode = :barcode", nativeQuery=true)
	void updateItemCountStatusByBarcode(@Param("status")int status, @Param("barcode")String barcode);
	
	/**
	 * @param barcode Barcode
	 * @param shelfId Shelf id
	 * @param rackId Rack id
	 * @param crateId Crate id
	 */
	@Modifying(clearAutomatically=true)
	@Query(value="Update item_count SET shelf = :shelf_id, rack = :rack_id, crate = :crate_id WHERE barcode = :barcode", nativeQuery=true)
	void updateItemCountStorageLocationByBarcode(@Param("barcode")String barcode, @Param("shelf_id") long shelfId, @Param("rack_id") long rackId, @Param("crate_id") long crateId);
	
	/**Updates warehouse item warehouse by barcode.
	 * @param barcode Barcode
	 * @param warehouseId Warehouse id
	 */
	@Modifying(clearAutomatically=true)
	@Query(value="Update item_count SET warehouse = :warehouse_id WHERE barcode = :barcode", nativeQuery=true)
	void updateItemCountWarehouseLocation(@Param("barcode")String barcode, @Param("warehouse_id") long warehouseId);
	
	/**
	 * @param warehouseId Warehouse id
	 * @return List of item count row object
	 */
	@Query(value="SELECT item_count.warehouse, item_count.item_code, item_count.description, item_count.vendor_alias, item_count.barcode, item_count.status, item_count.reorder_point, item_count.unit_price, item_count.wholesale_price, item_count.shelf, item_count.rack, item_count.crate FROM item_count WHERE item_count.warehouse = :warehouse_id", nativeQuery=true)
	List<Object[]> findItemCountByWarehouse(@Param("warehouse_id") long warehouseId);
	
	/**
	 * @param shelfId Shelf id
	 * @return List of item count row object
	 */
	@Query(value="SELECT item_count.warehouse, item_count.item_code, item_count.description, item_count.vendor_alias, item_count.barcode, item_count.status, item_count.reorder_point, item_count.unit_price, item_count.wholesale_price, item_count.shelf, item_count.rack, item_count.crate FROM item_count WHERE item_count.shelf = :shelf_id", nativeQuery=true)
	List<Object[]> findItemCountByShelf(@Param("shelf_id") long shelfId);

	/**
	 * @param rackId Rack id
	 * @return List of item count row object
	 */
	@Query(value="SELECT item_count.warehouse, item_count.item_code, item_count.description, item_count.vendor_alias, item_count.barcode, item_count.status, item_count.reorder_point, item_count.unit_price, item_count.wholesale_price, item_count.shelf, item_count.rack, item_count.crate FROM item_count WHERE item_count.rack = :rack_id", nativeQuery=true)
	List<Object[]> findItemCountByRack(@Param("rack_id") long rackId);

	/**
	 * @param crateId Crate id
	 * @return List of item count row object
	 */
	@Query(value="SELECT item_count.warehouse, item_count.item_code, item_count.description, item_count.vendor_alias, item_count.barcode, item_count.status, item_count.reorder_point, item_count.unit_price, item_count.wholesale_price, item_count.shelf, item_count.rack, item_count.crate FROM item_count WHERE item_count.crate = :crate_id", nativeQuery=true)
	List<Object[]> findItemCountByCrate(@Param("crate_id") long crateId);

	/**
	 * Get warehouse item by shelf and rack.
	 * @param shelfId Shelf id
	 * @param rackId Rack id
	 * @return List of item count row object
	 */
	@Query(value="SELECT item_count.warehouse, item_count.item_code, item_count.description, item_count.vendor_alias, item_count.barcode, item_count.status, item_count.reorder_point, item_count.unit_price, item_count.wholesale_price, item_count.shelf, item_count.rack, item_count.crate FROM item_count WHERE item_count.shelf = :shelf_id AND item_count.rack = :rack_id", nativeQuery=true)
	List<Object[]> findItemCountByShelfAndRack(@Param("shelf_id") long shelfId, @Param("rack_id")  long rackId);

	/**
	 * Get warehouse item by shelf, rack and crate. 
	 * @param shelfId Shelf id
	 * @param rackId Rack id
	 * @param crateId Crate id
	 * @return List of item count row object
	 */
	@Query(value="SELECT item_count.warehouse, item_count.item_code, item_count.description, item_count.vendor_alias, item_count.barcode, item_count.status, item_count.reorder_point, item_count.unit_price, item_count.wholesale_price, item_count.shelf, item_count.rack, item_count.crate FROM item_count WHERE item_count.shelf = :shelf_id AND item_count.rack = :rack_id AND item_count.crate = :crate_id", nativeQuery=true)
	List<Object[]> findItemCountByShelfAndRackAndCrate(@Param("shelf_id") long shelfId, @Param("rack_id")  long rackId, @Param("crate_id")  long crateId);
	
	/**
	 * Get all warehouse items.
	 * @return List of item count row object
	 */
	@Query(value="SELECT item_count.warehouse, item_count.item_code, item_count.description, item_count.vendor_alias, item_count.barcode, item_count.status, item_count.reorder_point, item_count.unit_price, item_count.wholesale_price, item_count.shelf, item_count.rack, item_count.crate FROM item_count", nativeQuery=true)
	List<Object[]> getAllItemCount();

	/**
	 * Get warehouse item by barcode. 
	 * @param barcode Barcode
	 * @return List of item count row object
	 */
	@Query(value="SELECT item_count.warehouse, item_count.item_code, item_count.description, item_count.vendor_alias, item_count.barcode, item_count.status, item_count.reorder_point, item_count.unit_price, item_count.wholesale_price, item_count.shelf, item_count.rack, item_count.crate FROM item_count WHERE	item_count.barcode = :barcode", nativeQuery=true)
	Object[] getItemCountByBarcode(@Param("barcode")String barcode);
	
	/**Get warehouse item by status, warehouse and barcode. 
	 * @param status Status
	 * @param warehouseId Warehouse id
	 * @param barcode Barcode
	 * @return List of item count row object
	 */
	@Query(value="SELECT item_count.warehouse, item_count.item_code, item_count.description, item_count.vendor_alias, item_count.barcode, item_count.status, item_count.reorder_point, item_count.unit_price, item_count.wholesale_price, item_count.shelf, item_count.rack, item_count.crate FROM item_count WHERE item_count.status = :status AND item_count.warehouse = :warehouse_id AND item_count.barcode = :barcode", nativeQuery=true)
	List<Object[]> findBarcodeByStatusAndWarehouseAndBarcode(@Param("status") int status, @Param("warehouse_id") long warehouseId, @Param("barcode") String barcode);
	
	/**Get warehouse item by item code.
	 * @param code Item code
	 * @param warehouseId Warehouse id
	 * @return List of item count row object
	 */
	@Query(value="SELECT item_count.warehouse, item_count.item_code, item_count.description, item_count.vendor_alias, item_count.barcode, item_count.status, item_count.reorder_point, item_count.unit_price, item_count.wholesale_price, item_count.shelf, item_count.rack, item_count.crate FROM item_count WHERE (item_count.barcode LIKE %:code% OR item_count.item_code LIKE %:code%) AND item_count.warehouse = :warehouse AND item_count.status = 200", nativeQuery=true)
	List<Object[]> getItemCountsByBarcodeOrCode(@Param("code") String code, @Param("warehouse") long warehouseId);
	
	/**Get warehouse item by status, warehouse id, item code and has storage location defined.
	 * @param status Status
	 * @param warehouseId Warehouse id
	 * @param itemCode Item code
	 * @return List of item count row object
	 */
	@Query(value="SELECT item_count.warehouse, item_count.item_code, item_count.description, item_count.vendor_alias, item_count.barcode, item_count.status, item_count.reorder_point, item_count.unit_price, item_count.wholesale_price, item_count.shelf, item_count.rack, item_count.crate FROM item_count WHERE item_count.status = :status AND item_count.warehouse = :warehouse_id AND item_count.item_code = :item_code AND item_count.shelf IS NOT NULL AND item_count.rack IS NOT NULL AND item_count.crate IS NOT NULL", nativeQuery=true)
	List<Object[]> getBarcodesByStatusAndWarehouseAndItemCode( @Param("status") int status, @Param("warehouse_id") long warehouseId, @Param("item_code") String itemCode);

	/**Get warehouse item by item code.
	 * @param itemCode Item code
	 * @return List of item count row object
	 */
	@Query(value="SELECT item_count.warehouse, item_count.item_code, item_code.description, item_code.vendor_alias, item_count.description, item_count.vendor_alias, item_count.barcode, item_count.status, item_count.reorder_point, item_count.unit_price, item_count.wholesale_price, item_count.shelf, item_count.rack, item_count.crate FROM item_count WHERE item_count.item_code = :item_code AND item_count.status = 200", nativeQuery=true)
	List<Object[]> getAvailableItemCountByItemCode(@Param("item_code") String itemCode);
	
	/**Get warehouse item by item code.
	 * @param itemCode Item count
	 * @return Count
	 */
	@Query(value="SELECT count(item_count.barcode) FROM item_count WHERE item_count.item_code = :item_code AND item_count.status = 200", nativeQuery=true)
	Integer getAvailableItemCountCountByItemCode(@Param("item_code") String itemCode);
	
	/**Get warehouse item count by item code and warehouse.
	 * @param itemCode Item count
	 * @param warehouseId Warehouse Id
	 * @return Count
	 */
	@Query(value="SELECT count(item_count.barcode) FROM item_count WHERE item_count.item_code = :item_code AND item_count.warehouse = :warehouse AND item_count.status = 200", nativeQuery=true)
	Integer getAvailableItemCountCountByItemCodeAndWarehouseId(@Param("item_code") String itemCode, @Param("warehouse") Long warehouseId);
	
	/**Get warehouse item by warehouse id, item code and kit number.
	 * @param warehouseId Warehouse id
	 * @param itemCode Item code
	 * @param kitNumber Kit number
	 * @return List of item count row object
	 */
	@Query(value="select i.warehouse, i.item_code, i.description, i.vendor_alias, i.barcode, i.status, i.reorder_point, i.unit_price, i.wholesale_price, i.shelf, i.rack, i.crate from item_count i, order_item_details o, purchase_order po where i.barcode = o.barcode and i.status = 200 and i.warehouse = :warehouse_id and i.item_code = :item_code and (o.order_id = po.id and po.kit_number = :kit_number) and (i.shelf is not null and i.rack is not null and i.crate is not null)", nativeQuery=true)
	List<Object[]> getAvailableItemCountByItemCodeFromSelfPurchaseOrders(@Param("warehouse_id") long warehouseId, @Param("item_code") String itemCode, @Param("kit_number") String kitNumber);
	
	/**Get warehouse item by warehouse id, item code.
	 * @param warehouseId Warehouse id
	 * @param itemCode Item code
	 * @return List of item count row object
	 */
	@Query(value="select i.warehouse, i.item_code, i.description, i.vendor_alias, i.barcode, i.status, i.reorder_point, i.unit_price, i.wholesale_price, i.shelf, i.rack, i.crate from item_count i where i.status = 200 and i.warehouse = :warehouse_id and i.item_code = :item_code and (i.shelf is not null and i.rack is not null and i.crate is not null)", nativeQuery=true)
	List<Object[]> getAvailableItemCountByItemCodeFromNonDefault(@Param("warehouse_id") long warehouseId, @Param("item_code") String itemCode);
	
	/**Get warehouse item by warehouse id and item code.
	 * @param warehouseId Warehouse id
	 * @param itemCode Item code
	 * @return List of item count row object
	 */
	@Query(value="select i.warehouse, i.item_code, i.description, i.vendor_alias, i.barcode, i.status, i.reorder_point, i.unit_price, i.wholesale_price, i.shelf, i.rack, i.crate from item_count i, order_item_details o where i.barcode = o.barcode and i.status = 200 and i.warehouse = :warehouse_id and i.item_code = :item_code  and (i.shelf is not null and i.rack is not null and i.crate is not null)", nativeQuery=true)
	List<Object[]> getAvailableItemCountByItemCodeFromOtherPurchaseOrders(@Param("warehouse_id") long warehouseId, @Param("item_code") String itemCode);
	
	/**Find all item count by warehouse id, shelf id, rack id, crate id. Parameter not necessary limit and offset.
	 * @param warehouseId Warehouse Id
	 * @param shelfId Shelf Id
	 * @param rackId Rack Id
	 * @param crateId Crate Id
	 * @param limit Limit
	 * @param offset Offset
	 * @return List of item count row objects
	 */
	@Query(value="SELECT item_count.warehouse, item_count.item_code, item_count.description, item_count.vendor_alias, item_count.barcode, item_count.status, item_count.reorder_point, item_count.unit_price, item_count.wholesale_price, item_count.shelf, item_count.rack, item_count.crate FROM item_count WHERE item_count.warehouse = IFNULL(:warehouse ,item_count.warehouse) AND item_count.shelf = IFNULL(:shelf, item_count.shelf) AND item_count.rack = IFNULL(:rack, item_count.rack) AND item_count.crate = IFNULL(:crate, item_count.crate) LIMIT :lim OFFSET :off", nativeQuery=true)
	List<Object[]> getItemCount(@Param("warehouse") Long warehouseId, @Param("shelf") Long shelfId, @Param("rack") Long rackId, @Param("crate") Long crateId, @Param("lim") Integer limit, @Param("off") Integer offset);
	
	/**Find all item count's count by warehouse id, shelf id, rack id, crate id. Parameter not necessary limit and offset.
	 * @param warehouseId Warehouse Id
	 * @param shelfId Shelf Id
	 * @param rackId Rack Id
	 * @param crateId Crate Id
	 * @param limit Limit
	 * @param offset Offset
	 * @return List of item count row objects
	 */
	@Query(value="SELECT count(item_count.barcode) FROM item_count WHERE item_count.warehouse = IFNULL(:warehouse ,item_count.warehouse) AND item_count.shelf = IFNULL(:shelf, item_count.shelf) AND item_count.rack = IFNULL(:rack, item_count.rack) AND item_count.crate = IFNULL(:crate, item_count.crate) LIMIT :lim OFFSET :off", nativeQuery=true)
	Integer getItemCountCount(@Param("warehouse") Long warehouseId, @Param("shelf") Long shelfId, @Param("rack") Long rackId, @Param("crate") Long crateId, @Param("lim") Integer limit, @Param("off") Integer offset);
}
