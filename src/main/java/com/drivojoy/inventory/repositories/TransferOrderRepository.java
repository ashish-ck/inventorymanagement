package com.drivojoy.inventory.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.drivojoy.inventory.models.TransferOrder;
import com.drivojoy.inventory.models.Warehouse;

/**
 * 
 * @author ashishsingh
 *
 */
@Repository
public interface TransferOrderRepository extends JpaRepository<TransferOrder, Long>{

	/**
	 * Finds a transfer order by order number
	 * @param orderNumber Order number
	 * @return Transfer order
	 */
	TransferOrder findByOrderNumber(String orderNumber);
	
	/**
	 * Finds list of transfer orders for a particular warehouse
	 * @param fromWarehouse From warehouse
	 * @param toWarehouse To warehouse
	 * @return List of transfer orders
	 */
	List<TransferOrder> findByFromWarehouseOrToWarehouse(Warehouse fromWarehouse, Warehouse toWarehouse);
}
