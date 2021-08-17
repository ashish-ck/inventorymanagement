package com.drivojoy.inventory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.drivojoy.inventory.models.Tag;

/**
 * 
 * @author ashishsingh
 *
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Long>{

	/**
	 * Finds a tag by its name
	 * @param name Name
	 * @return Tag
	 */
	Tag findByName(String name);
}
