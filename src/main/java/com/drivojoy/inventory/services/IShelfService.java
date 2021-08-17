package com.drivojoy.inventory.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.drivojoy.inventory.dto.ItemCountDTO;
import com.drivojoy.inventory.models.Crate;
import com.drivojoy.inventory.models.Rack;
import com.drivojoy.inventory.models.Shelf;

/**
 * @author ashish.singh
 *
 */
@Service
@Transactional
public interface IShelfService {

	/**
	 * Adds a shelf
	 * @param warehouseId Warehouse id
	 * @param shelf Shelf
	 * @return Shelf
	 */
	Shelf addShelf(long warehouseId, Shelf shelf);

	/**
	 * Finds a list of shelves
	 * @param id Warehouse id
	 * @return List of shelves
	 */
	List<Shelf> getShelvesByWarehouseId(long id);

	/**
	 * Returns the added updated shelf
	 * @param shelf Shelf
	 * @return shelf Shelf
	 */
	Shelf addUpdateShelf(Shelf shelf);

	/**
	 * Finds a shelf by id
	 * @param id Shelf id
	 * @return Shelf
	 */
	Shelf findById(long id);

	/**
	 * Add rack to shelf.
	 * @param id Shelf id
	 * @param rack Rack
	 * @return Rack
	 */
	Rack addRack(long id, Rack rack);

	/**
	 * Get racks by shelf id.
	 * @param id Shelf id
	 * @return List of racks
	 */
	List<Rack> getRacks(long id);

	/**
	 * Get crates by shelf id
	 * @param id Shelf id
	 * @return List of crates
	 */
	List<Crate> getCrates(long id);

	/**
	 * Returns list of warehouse items by shelf id
	 * @param id Shelf id
	 * @return List of item counts
	 */
	List<ItemCountDTO> getItemCounts(long id);

	/**
	 * Returns shelf by shelf name and warehouse id
	 * 
	 * @param shelfName Shelf name
	 * @param id Warehouse Id
	 * @return Shelf
	 */
	Shelf getShelfByNameAndWarehouseId(String shelfName, long id);
}