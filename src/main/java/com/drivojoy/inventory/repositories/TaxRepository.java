package com.drivojoy.inventory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.drivojoy.inventory.models.Tax;

/**
 * 
 * @author ashishsingh
 *
 */
@Repository
public interface TaxRepository extends JpaRepository<Tax, Long>{

	/**
	 * Finds a tax by tax code
	 * @param code Code
	 * @return Tax
	 */
	Tax findByCode(String code);
}
