package com.drivojoy.inventory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.drivojoy.inventory.models.UoM;

/**
 * 
 * @author ashishsingh
 *
 */
@Repository
public interface UoMRepository extends JpaRepository<UoM, Long>{
	
	/**
	 * Finds a unit by its notation
	 * @param notation Notation
	 * @return UoM
	 */
	UoM findByNotation(String notation);
}
