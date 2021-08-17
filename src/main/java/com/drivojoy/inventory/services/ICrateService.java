package com.drivojoy.inventory.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.drivojoy.inventory.dto.ItemCountDTO;
import com.drivojoy.inventory.models.Crate;

/**
 * 
 * @author ashish.singh
 *
 */
@Service
@Transactional
public interface ICrateService {

	/**
	 * Adds a crate
	 * 
	 * @param rackId rack Id
	 * @param crate Crate
	 * @return Crate
	 */
	Crate addCrate(long rackId, Crate crate);

	/**
	 * return the added updated crate
	 * 
	 * @param crate Crate
	 * @return crate
	 */
	Crate addUpdateCrate(Crate crate);

	/**
	 * Finds a crate by id
	 * 
	 * @param id Id
	 * @return Crate
	 */
	Crate findById(long id);

	/**
	 * Get warehouse items by crate id.
	 * @param id Id
	 * @return List of Item count
	 */
	List<ItemCountDTO> getItemCounts(long id);
	
	/**
	 * Get crate by crate name and rack id.
	 * @param crateName Name
	 * @param rackId Rack Id
	 * @return Crate
	 */
	Crate getCrateByNameAndRackId(String crateName, long rackId);
}
