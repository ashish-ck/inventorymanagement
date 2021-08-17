package com.drivojoy.inventory.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.drivojoy.inventory.dto.ItemCategoryDTO;
import com.drivojoy.inventory.models.ItemCategory;

/**
 * 
 * @author ashishsingh
 *
 */
@Service
@Transactional
public interface IItemCategoryService {

	/**
	 * Creates a category
	 * @param itemCategory item category
	 * @return Item category
	 */
	ItemCategoryDTO createItemCategory(ItemCategoryDTO itemCategory);
	
	/**
	 * Updates an existing category
	 * @param itemCategory item category
	 * @return Item category
	 */
	ItemCategoryDTO editItemCategory(ItemCategoryDTO itemCategory);
	
	/**
	 * Finds a category by id
	 * @param id id
	 * @return Item category
	 */
	ItemCategory getItemCategoryById(long id);
	ItemCategoryDTO getItemCategoryByName (String name);
	
	/**
	 * Finds all categories whose name contain the string
	 * @param name Name
	 * @return List of item category
	 */
	List<ItemCategoryDTO> getItemCategoryByNameContaining (String name);
	
	/**
	 * Fetches all categories
	 * @return List of item category
	 */
	List<ItemCategoryDTO> getAllCategories();
	
	/**
	 * Finds all categories belonging under a parent category
	 * @param id Id of the parent category
	 * @return List of item category
	 */
	List<ItemCategoryDTO> getCategoriesWithParent(long id);
	
}
