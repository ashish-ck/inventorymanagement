package com.drivojoy.inventory.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.drivojoy.inventory.models.PurchaseOrder;

/**
 * 
 * @author ashishsingh
 *
 */
@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long>{
	/**
	 * Finds an purchase order by order number
	 * @param orderNo Order no
	 * @return Purchase Order
	 */
	PurchaseOrder findByOrderNo(String orderNo);
	
	/**Find purchase orders by kit number
	 * @param kitNumber Kit number
	 * @return List of purchase orders
	 */
	List<PurchaseOrder> findByKitNumber(@Param("kit_number") String kitNumber);
	
	/**
	 * @param fromDate From date
	 * @param toDate To date
	 * @return List of purchase orders
	 */
	@Query("select k from PurchaseOrder k where k.date between :fromDate and :toDate")
	List<PurchaseOrder> findByDateRange(@Param("fromDate") Date fromDate, @Param("toDate")Date toDate);
	
	/**
	 * @param fromDate From date
	 * @param toDate To date
	 * @param warehouseId Warehouse id
	 * @return List of purchase orders
	 */
	@Query("select k from PurchaseOrder k where k.date between :fromDate and :toDate and k.shippingWarehouse.id = :warehouse")
	List<PurchaseOrder> getByDateRangeAndWarehouseId(@Param("fromDate") Date fromDate, @Param("toDate")Date toDate, @Param("warehouse")Long warehouseId);
	
	/**Find order item by barcode
	 * @param barcode Barcode 
	 * @return List of order item row objects
	 */
	@Query(value="SELECT order_items.order_id, order_items.barcode, order_items.description, order_items.is_item_serialized, order_items.item_code, order_items.unit, order_items.unit_price, order_items.vendor_alias, order_items.wholesale_price FROM order_items WHERE order_items.barcode = :barcode", nativeQuery=true)
	Object[] findOrderItemByBarcode(@Param("barcode") String barcode);
}
