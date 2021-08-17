package com.drivojoy.inventory.services;

import java.util.List;

import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.drivojoy.inventory.dto.AttributeDTO;

/**
 * 
 * @author ashishsingh
 *
 */
@Service
@Transactional
public interface IAttributeService {

	/**
	 * Finds attribute view model by its name
	 * @param name Name
	 * @return Attribute
	 */
	public AttributeDTO getAttributeByName(String name);
	
	/**
	 * Creates an attribute
	 * @param attribute Attribute
	 * @return Newly created attribute object
	 */
	public AttributeDTO createAttribute(AttributeDTO attribute);
	
	/**
	 * Find list of all attributes
	 * @return List of attribute
	 */
	public List<AttributeDTO> getAllAttributes();

}
