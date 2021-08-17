package com.drivojoy.inventory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.drivojoy.inventory.models.Attribute;

/**
 * @author ashishsingh
 *
 */
@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Long> {

	/**
	 * Finds an attribute by name
	 * @param name Attribute name
	 * @return Object of Attribute
	 */
	Attribute findByName(String name);
}
