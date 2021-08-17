package com.drivojoy.inventory.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.drivojoy.inventory.models.Kit;
import com.drivojoy.inventory.models.Warehouse;

/**
 * 
 * @author ashishsingh
 *
 */
@Repository
public interface KitRepository extends JpaRepository<Kit, Long>{
	
	/**
	 * Finds a kit by its sales order # or kit number
	 * @param kitNumber Kit number
	 * @return Kit
	 */
	Kit findByKitNumber(String kitNumber);
	
	/**
	 * Finds all kits assigned to a particular warehouse
	 * @param warehouse Warehouse
	 * @return List of kits
	 */
	List<Kit> findByAssignedWarehouse(Warehouse warehouse);
	
	/**No where used.
	 * Update kit items status, kit id, subkit id by barcode.
	 * @param kitId Kit id
	 * @param status Status
	 * @param subkitId Subkit Id
	 * @param barcode Barcode
	 */
	@Modifying(clearAutomatically=true)
	@Query(value="Update kit_item_details SET kit_id = :kit_id, status = :status, subkit_id = :subkit_id WHERE barcode = :barcode", nativeQuery=true)
	void updateItemKitItemDetailsByBarcode(@Param("kit_id")long kitId, @Param("status")int status, @Param("subkit_id")long subkitId, @Param("barcode")String barcode);
	
	/**
	 * Update subkit item status subkit id, kit id by barcode.
	 * @param kitId Kit id
	 * @param status Status
	 * @param subkitId Subkit id
	 * @param barcode Barcode
	 */
	@Modifying(clearAutomatically=true)
	@Query(value="Update subkit_item_details SET kit_id = :kit_id, status = :status, subkit_id = :subkit_id WHERE barcode = :barcode", nativeQuery=true)
	void updateItemSubkitItemDetailsByBarcode(@Param("kit_id")long kitId, @Param("status")int status, @Param("subkit_id")long subkitId, @Param("barcode")String barcode);
	
	/**
	 * Update kit item to out of stock by warehouse id and item code.
	 * @param warehouseId Warehouse id
	 * @param itemCode Item code
	 */
	@Modifying(clearAutomatically=true)
	@Query(value="Update kit_item_details SET status = 100 WHERE current_location = :warehouse_id AND status = 200 AND item_code = :item_code AND barcode IS NULL", nativeQuery=true)
	void updateKitItemsToOutOfStock(@Param("item_code")String itemCode, @Param("warehouse_id")long warehouseId);
	
	/**
	 * Update subkit item to out of stock by warehouse id and item code.
	 * @param warehouseId Warehouse id
	 * @param itemCode Item code
	 */
	@Modifying(clearAutomatically=true)
	@Query(value="Update subkit_item_details SET status = 100 WHERE current_location = :warehouse_id AND status = 200 AND item_code = :item_code AND barcode IS NULL", nativeQuery=true)
	void updateSubkitItemsToOutOfStock(@Param("item_code")String itemCode, @Param("warehouse_id")long warehouseId);
	
	/**Updates the status of an item in the kit whose current location is identified by workshop
	 * @param itemCode Item code
	 * @param workshop Warehouse Id
	 */
	@Modifying(clearAutomatically=true)
	@Query(value="Update kit_item_details SET status = 200 WHERE item_code = :item_code AND current_location = :warehouse AND status = 100", nativeQuery=true)
	void updateKitItemStatusToAvailable(@Param("item_code")String itemCode, @Param("warehouse")long workshop);
	
	/**Updates the status of an item in the subkit whose current location is identified by workshop
	 * @param itemCode Item code
	 * @param workshop Warehouse id
	 */
	@Modifying(clearAutomatically=true)
	@Query(value="Update subkit_item_details SET status = 200 WHERE item_code = :item_code AND current_location = :warehouse AND status = 100", nativeQuery=true)
	void updateSubkitItemStatusToAvailable(@Param("item_code")String itemCode, @Param("warehouse")long workshop);
	
	/**Update kit item status by barcode.
	 * @param status Status
	 * @param barcode Barcode
	 */
	@Modifying(clearAutomatically=true)
	@Query(value="Update kit_item_details SET status = :status WHERE barcode = :barcode", nativeQuery=true)
	void updateKitItemStatusByBarcode(@Param("status")int status, @Param("barcode")String barcode);
	
	/**Update subkit item status by barcode.
	 * @param status Status
	 * @param barcode Barcode
	 */
	@Modifying(clearAutomatically=true)
	@Query(value="Update subkit_item_details SET status = :status WHERE barcode = :barcode", nativeQuery=true)
	void updateSubkitItemStatusByBarcode(@Param("status")int status, @Param("barcode")String barcode);
	/**
	 * Finds all kit in between a date range
	 * @param fromDate From date
	 * @param toDate To date
	 * @return List of kits
	 */
	@Query("select k from Kit k where k.date between :fromDate and :toDate")
	List<Kit> findByDateRange(@Param("fromDate") Date fromDate, @Param("toDate")Date toDate);

	/**
	 * Finds all kit in between a date range
	 * @param fromDate From date
	 * @param toDate To date
	 * @param warehouseId Warehouse id
	 * @return List of kits
	 */
	@Query("select k from Kit k where k.date between :fromDate and :toDate and (k.requestWarehouse.id = :warehouse OR k.assignedWarehouse.id = :warehouse)")
	List<Kit> getByDateRangeAndWarehouseId(@Param("fromDate") Date fromDate, @Param("toDate")Date toDate, @Param("warehouse")Long warehouseId);
	
	/**
	 * Finds the sum of quantities delivered, returned, restocked and on the fly in a particular date range
	 * @param fromDate From date
	 * @param toDate To date
	 * @return List of kit item row objects
	 */
	@Query(value="Select CAST(kit.date AS DATE) as date, "+
				"SUM(IF(kit_item_details.status >= 300 AND kit_item_details.status <= 700, 1, 0)) as qtyDelivered, "+ 
				"SUM(IF(kit_item_details.status = 1100, 1, 0)) as qtyReturnExpected, "+
				"SUM(IF(kit_item_details.status >= 800 AND kit_item_details.status <= 1000, 1, 0)) as qtyRestocked, "+ 
				"SUM(IF(kit_item_details.status = 1800, 1, 0)) as qtyOnTheFly FROM kit_item_details "+
				"left join kit on kit.id = kit_item_details.kit_id WHERE CAST(kit.date as Date) BETWEEN :fromDate AND :toDate "+
				"GROUP BY CAST(kit.date as DATE)", nativeQuery=true)
	List<Object[]> findyCountByDateRange(@Param("fromDate") Date fromDate, @Param("toDate")Date toDate);
	
	/**Gets a list of out of stock kit items in a particular date range
	 * @param fromDate From date
	 * @param toDate To date
	 * @return List of kit item row objects
	 */
	@Query(value="Select items.item_code, items.current_location, k.date, k.kit_number from kit k right join kit_item_details items on k.id = items.kit_id where items.status = 100 and k.date between :fromDate and :toDate", nativeQuery=true)
	List<Object[]> getOutOfStockKitItems(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);
	
	/**
	 * Gets a list of out of stock kit items in a particular date range belonging to a warehouse
	 * @param fromDate From date
	 * @param toDate To date
	 * @param warehouse Warehouse id
	 * @return List of kit item row objects
	 */
	@Query(value="Select items.item_code, items.current_location, k.date, k.kit_number from kit k right join kit_item_details items on k.id = items.kit_id where  items.current_location = :warehouse and items.status = 100 and k.date between :fromDate and :toDate", nativeQuery=true)
	List<Object[]> getOutOfStockKitItemsByWarehouse(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate, @Param("warehouse") long warehouse);
	
	/**Get return expected kits.
	 * @return List of kits
	 */
	@Query(value="select * from kit where status >= 400 and status <= 700", nativeQuery=true)
	List<Kit> getReturnExpectedKits();
}
