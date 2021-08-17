package com.drivojoy.inventory.servicesImpl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.OptimisticLockException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.drivojoy.inventory.dto.ItemCategoryDTO;
import com.drivojoy.inventory.models.ItemCategory;
import com.drivojoy.inventory.repositories.ItemCategoryRepository;
import com.drivojoy.inventory.services.IItemCategoryService;
import com.drivojoy.inventory.utils.IDTOWrapper;

@Component
public class ItemCategoryServiceImpl implements IItemCategoryService {
	
	@Autowired
	private ItemCategoryRepository itemCategoryRepository;
	@Autowired
	private IDTOWrapper dtoWrapper;
	private final int _DEFAULT_CATEGORY = 1;
	private final Logger logger = LoggerFactory.getLogger(ItemCategoryServiceImpl.class);

	@Override
	public ItemCategoryDTO createItemCategory(ItemCategoryDTO itemCategoryDTO) {
		ItemCategory itemCategory = dtoWrapper.convert(itemCategoryDTO);
		logger.debug("Creating item category: "+itemCategory.toString());
		ItemCategory createdCategory ;
		if(itemCategory.getParentCategory() == null){
			itemCategory.setParentCategory(getItemCategoryById(_DEFAULT_CATEGORY));
		}
		try{
			createdCategory = itemCategoryRepository.save(itemCategory);
			return dtoWrapper.convert(createdCategory);
		}catch(Exception ex){
			logger.error("Exception throw while creating new category : "+ex.getCause());
			throw new RuntimeException(ex.getMessage());
		}
	}

	@Override
	public ItemCategoryDTO editItemCategory(ItemCategoryDTO itemCategoryDTO) {
		ItemCategory itemCategory = dtoWrapper.convert(itemCategoryDTO);
		logger.debug("Editing item category: "+itemCategory.toString());
		ItemCategory editedCategory ;
		if(itemCategory.getParentCategory() == null){
			itemCategory.setParentCategory(getItemCategoryById(_DEFAULT_CATEGORY));
		}
		try{
			editedCategory = itemCategoryRepository.save(itemCategory);
			return dtoWrapper.convert(editedCategory);
		}catch(OptimisticLockException ex){
			logger.error("Category being edited is not the latest copy : "+ex.getCause());
			throw new RuntimeException("Category NOT save. This is not the latest copy of the category you are trying to edit, please refresh to get latest copy!");
		}
		catch(Exception ex){
			logger.error("Exception throw while editing category : "+ex.getCause());
			throw new RuntimeException(ex.getMessage());
		}
	}

	@Override
	public ItemCategory getItemCategoryById(long id) {
		return itemCategoryRepository.findOne(id);
	}
	@Override
	public ItemCategoryDTO getItemCategoryByName(String name){
		return dtoWrapper.convert(itemCategoryRepository.findByName(name));
	}
	
	@Override
	public List<ItemCategoryDTO> getItemCategoryByNameContaining(String name){
		
		List<ItemCategory> categories = itemCategoryRepository.findByNameContaining(name);
		List<ItemCategoryDTO> categoryDTOs = new ArrayList<>();
		for(ItemCategory category: categories){
			categoryDTOs.add(dtoWrapper.convert(category));
		}
		
		return categoryDTOs;
	}

	@Override
	public List<ItemCategoryDTO> getAllCategories() {
		List<ItemCategoryDTO> categoryDTOList = new ArrayList<>();
		List<ItemCategory> categories = itemCategoryRepository.findAll();
		
		for(ItemCategory category: categories){
			categoryDTOList.add(dtoWrapper.convert(category));
		}
		
		return categoryDTOList;
	}

	@Override
	public List<ItemCategoryDTO> getCategoriesWithParent(long id) {
		List<ItemCategoryDTO> categoryDTOList = new ArrayList<>();
		List<ItemCategory> categories = itemCategoryRepository.
				findByParentCategory(itemCategoryRepository.findOne(id));
		
		for(ItemCategory category: categories){
			categoryDTOList.add(dtoWrapper.convert(category));
		}
		
		return categoryDTOList;
	}
}
