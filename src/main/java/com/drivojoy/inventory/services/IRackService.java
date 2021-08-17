package com.drivojoy.inventory.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.drivojoy.inventory.dto.ItemCountDTO;
import com.drivojoy.inventory.models.Crate;
import com.drivojoy.inventory.models.Rack;

/**
 * 
 * @author ashish.singh
 *
 */
@Service
@Transactional
public interface IRackService {

	/**
	 * Adds a rack
	 * @param shelfId Shelf id
	 * @param rack Rack
	 * @return Rack
	 */
	Rack addRack(long shelfId, Rack rack);

	/**
	 * Returns the added updated rack
	 * @param rack Rack
	 * @return rack Rack
	 */
	Rack addUpdateRack(Rack rack);

	/**
	 * Finds a rack by id
	 * @param id Id
	 * @return Rack
	 */
	Rack findById(long id);

	/**
	 * Add crate to rack.
	 * @param id Id
	 * @param crate Crate
	 * @return Crate
	 */
	Crate addCrate(long id, Crate crate);

	/**
	 * Add crate to rack.
	 * @param id Id
	 * @return List of crates
	 */
	List<Crate> getCrates(long id);

	/**
	 * Get warehouse items by rack id.
	 * @param id Id
	 * @return List of item counts
	 */
	List<ItemCountDTO> getItemCounts(long id);
	
	/**
	 * Finds rack by rack name and shelf id.
	 * @param rackName Rack name
	 * @param shelfId Shelf id
	 * @return Rack
	 */
	Rack getRackByNameAndShelfId(String rackName, long shelfId);
}
