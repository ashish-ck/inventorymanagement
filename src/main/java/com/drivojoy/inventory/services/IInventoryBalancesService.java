package com.drivojoy.inventory.services;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.drivojoy.inventory.models.InventoryBalances;
import com.drivojoy.inventory.models.Item;
import com.drivojoy.inventory.models.Warehouse;

/**
 * 
 * @author ashishsingh
 *
 */
@Service
@Transactional
public interface IInventoryBalancesService {

	/**
	 * Finds the inventory balance of an item for a particular warehouse and date
	 * @param item Item
	 * @param warehouse Warehouse
	 * @param date Date
	 * @return InventoryBalances
	 */
	InventoryBalances findByItemAndWarehouseAndDate(Item item, Warehouse warehouse, Date date);
	
	/**
	 * Creates a new balance object
	 * @param balance Balance
	 * @return InventoryBalances
	 */
	InventoryBalances save(InventoryBalances balance);
}
