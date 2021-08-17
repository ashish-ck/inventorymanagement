package com.drivojoy.inventory.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.drivojoy.inventory.models.Crate;

/**
 * 
 * @author ashish.singh
 *
 */
@Repository
public interface CrateRepository extends JpaRepository<Crate, Long> {
	/**
	 * Finds a crate by name
	 * @param name Name
	 * @return Crate
	 */
	Crate findByName(String name);

	/**
	 * Finds a Crate by id
	 * @param id Id
	 * @return Crate
	 */
	Crate findById(long id);

	/**
	 * Finds all crates that contains the pattern
	 * @param pattern Patterns
	 * @return List of crates
	 */
	List<Crate> findByNameContaining(String pattern);
}
