package com.drivojoy.inventory.repositories;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.drivojoy.inventory.models.InventoryBalances;
import com.drivojoy.inventory.models.Item;
import com.drivojoy.inventory.models.Warehouse;

/**
 * 
 * @author ashishsingh
 *
 */
@Repository
public interface InventoryBalancesRepository extends JpaRepository<InventoryBalances, Long>{

	/**
	 * Finds the opening and closing balance of an item in a warehouse on a particular date
	 * @param item Item
	 * @param warehouse Warehouse
	 * @param date Date
	 * @return InventoryBalances
	 */
	InventoryBalances findByItemAndWarehouseAndDate(Item item, Warehouse warehouse, Date date);
	
}
