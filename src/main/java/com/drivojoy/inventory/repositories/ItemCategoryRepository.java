package com.drivojoy.inventory.repositories;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.drivojoy.inventory.models.ItemCategory;

/**
 * 
 * @author ashishsingh
 *
 */
@Repository
public interface ItemCategoryRepository extends JpaRepository<ItemCategory, Long> {

	/**
	 * Finds a category by name
	 * @param name Name
	 * @return ItemCategory
	 */
	ItemCategory findByName(String name);
	
	/**
	 * Finds all categories that contains the pattern
	 * @param pattern Pattern
	 * @return List of item categories
	 */
	List<ItemCategory> findByNameContaining(String pattern);
	
	/**
	 * Finds all categories whose parent is represented by category
	 * @param category Item category
	 * @return List of item categories
	 */
	List<ItemCategory> findByParentCategory(ItemCategory category);
	
	/**
	 * Returns list of ids of categories with parent category has id represented by 'id'
	 * @param id category id
	 * @return List of numbers
	 */
	@Query(value="SELECT id FROM item_category WHERE parent_category = ?1", nativeQuery=true)
	List<BigInteger> getCategoryIdByParent(long id);
}
