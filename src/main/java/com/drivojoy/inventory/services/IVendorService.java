package com.drivojoy.inventory.services;

import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.drivojoy.inventory.dto.VendorDTO;
import com.drivojoy.inventory.models.Item;
import com.drivojoy.inventory.models.Vendor;

/**
 * 
 * @author ashishsingh
 *
 */
@Service
@Transactional
public interface IVendorService {

	/**
	 * Saves a vendor
	 * @param vendorDTO Vendor
	 * @return Vendor
	 */
	VendorDTO createUpdateVendor(VendorDTO vendorDTO);
	
	/**
	 * Fetches a list of all vendors
	 * @return List of vendors
	 */
	List<VendorDTO> getVendors();
	
	/**
	 * Finds a list of vendors selling a particular item identified by id
	 * @param id Id of item
	 * @return List of vendors
	 */
	List<VendorDTO> getVendorsByItem(long id);
	
	/**
	 * Service that checks if a vendor sells the items, if not, add them
	 * @param vendorId Vendor id
	 * @param items List of items
	 * @return Collection of items
	 */
	Collection<Item> checkAndAddItem(long vendorId, List<String> items);
	
	/**
	 * Finds a vendor by id
	 * @param id Vendor id
	 * @return Vendor
	 */
	Vendor getVendorById(long id);
}
