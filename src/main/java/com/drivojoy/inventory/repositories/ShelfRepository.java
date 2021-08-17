package com.drivojoy.inventory.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.drivojoy.inventory.models.Shelf;

/**
 * 
 * @author ashish.singh
 *
 */
@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Long> {
	/**
	 * Finds a shelf by name
	 * @param name Name
	 * @return Shelf
	 */
	Shelf findByName(String name);

	/**
	 * Finds a shelf by id
	 * @param id Shelf id
	 * @return Shelf
	 */
	Shelf findById(long id);

	/**
	 * Finds all shelves that contains the pattern
	 * @param pattern Pattern
	 * @return List of shelves
	 */
	List<Shelf> findByNameContaining(String pattern);
}
