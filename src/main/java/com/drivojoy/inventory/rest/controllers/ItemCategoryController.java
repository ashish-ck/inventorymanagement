package com.drivojoy.inventory.rest.controllers;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.drivojoy.inventory.dto.ApiResponse;
import com.drivojoy.inventory.dto.ItemCategoryDTO;
import com.drivojoy.inventory.services.IItemCategoryService;
import com.drivojoy.inventory.utils.Constants;

/**
 * 
 * @author ashishsingh
 *
 */
@RestController
@RequestMapping(value="/api/categories")
public class ItemCategoryController {
	
	@Autowired
	private IItemCategoryService itemCategoryService;

	private final Logger logger = LoggerFactory.getLogger(ItemCategoryController.class);
	
	/**
	 * API End point to retrieve all item categories
	 * @return Item Category
	 */
	@PreAuthorize("hasAnyRole('ADMIN,INVENTORYMANAGER')")
	@RequestMapping(value="/get", method=RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<ItemCategoryDTO>>> getAllCategories(){
		logger.debug("fetch all categories api invoked");
		List<ItemCategoryDTO> categoryList = null;
		try{
			categoryList = itemCategoryService.getAllCategories();
			ApiResponse<List<ItemCategoryDTO>> response = new ApiResponse<List<ItemCategoryDTO>>(true, categoryList, "Categories fetched successfully!");
			return new ResponseEntity<ApiResponse<List<ItemCategoryDTO>>>(response, HttpStatus.OK);
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getCause());
			ApiResponse<List<ItemCategoryDTO>> response = new ApiResponse<List<ItemCategoryDTO>>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<List<ItemCategoryDTO>>>(response, HttpStatus.OK);
		}
	}
	
	/**
	 * API End point to get all categories that fall under a parent category.
	 * @param id Id of the parent category
	 * @return Item Category
	 */
	@PreAuthorize("hasAnyRole('ADMIN,INVENTORYMANAGER')")
	@RequestMapping(value="/get/{id}", method=RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<ItemCategoryDTO>>> getAllCategoriesWithParent(@PathVariable(value="id") long id){
		logger.debug("fetch categories with parent : "+id);
		List<ItemCategoryDTO> categoryList = null;
		try{
			categoryList = itemCategoryService.getAllCategories();
			ApiResponse<List<ItemCategoryDTO>> response = new ApiResponse<List<ItemCategoryDTO>>(true, categoryList, "Categories fetched successfully!");
			return new ResponseEntity<ApiResponse<List<ItemCategoryDTO>>>(response, HttpStatus.OK);
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getCause());
			ApiResponse<List<ItemCategoryDTO>> response = new ApiResponse<List<ItemCategoryDTO>>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<List<ItemCategoryDTO>>>(response, HttpStatus.OK);
		}
	}
	
	/**
	 * API End point to create a new category
	 * @param category Item Category view model
	 * @return Item Category
	 */
	@PreAuthorize("hasAnyRole('ADMIN,INVENTORYMANAGER')")
	@RequestMapping(value="/create", method=RequestMethod.POST)
	public ResponseEntity<ApiResponse<ItemCategoryDTO>> createCategory(@RequestBody ItemCategoryDTO category){
		logger.debug("creating new category : "+category.toString());

		try{
			category = itemCategoryService.createItemCategory(category);
			ApiResponse<ItemCategoryDTO> response = new ApiResponse<ItemCategoryDTO>(true, category, "Category saved successfully!");
			return new ResponseEntity<ApiResponse<ItemCategoryDTO>>(response, HttpStatus.OK);
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getCause());
			ApiResponse<ItemCategoryDTO> response = new ApiResponse<ItemCategoryDTO>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<ItemCategoryDTO>>(response, HttpStatus.OK);
		}
	}
}
