package com.drivojoy.inventory.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.drivojoy.inventory.models.Item;
import com.drivojoy.inventory.models.Vendor;

/**
 * 
 * @author ashishsingh
 *
 */
@Repository
public interface VendorRespository extends JpaRepository<Vendor, Long>{

	/**
	 * Finds all the items sold by a vendor
	 * @param id Vendor id
	 * @return List of items
	 */
	@Query("select v.itemsSold from Vendor v where v.id = :id")
	List<Item> getVendorItems(@Param("id") long id);
	
	/**
	 * Find vendors that sell a particular item
	 * @param id Vendor id
	 * @return List of vendors
	 */
	@Query(value="SELECT id, name, address_line1, address_line2, city, country, location, pincode, state, contact_person, contact_no, created_dttm, last_mod_dttm, version, is_organised FROM vendor LEFT JOIN vendor_items_sold ON vendor.id = vendor_items_sold.vendor WHERE vendor_items_sold.items_sold = :id", nativeQuery=true)
	List<Vendor> getVendorsByItem(@Param("id")long id);
	
	//@Query("select count(vendor_items_sold.item_code) from vendor_items_sold where vendor_items_sold.vendor_id = :id and vendor_items_sold.item_code = :code")
	//Integer doesVendorSoldItem(@Param("id") long id, @Param("code") String itemCode);
}
