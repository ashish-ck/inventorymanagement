package com.drivojoy.inventory.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.drivojoy.inventory.models.Rack;

/**
 * 
 * @author ashish.singh
 *
 */
@Repository
public interface RackRepository extends JpaRepository<Rack, Long> {
	/**
	 * Finds a rack by name
	 * @param name Name
	 * @return Rack
	 */
	Rack findByName(String name);

	/**
	 * Finds a rack by id
	 * @param id Id
	 * @return Rack
	 */
	Rack findById(long id);

	/**
	 * Finds all racks that contains the pattern
	 * @param pattern Pattern
	 * @return List of racks
	 */
	List<Rack> findByNameContaining(String pattern);
}
