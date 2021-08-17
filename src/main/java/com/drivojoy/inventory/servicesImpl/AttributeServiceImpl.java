package com.drivojoy.inventory.servicesImpl;

import java.util.List;

import javax.persistence.OptimisticLockException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.drivojoy.inventory.dto.AttributeDTO;
import com.drivojoy.inventory.models.Attribute;
import com.drivojoy.inventory.repositories.AttributeRepository;
import com.drivojoy.inventory.services.IAttributeService;
import com.drivojoy.inventory.services.IUoMService;
import com.drivojoy.inventory.utils.Constants;
import com.drivojoy.inventory.utils.IDTOWrapper;

@Component
public class AttributeServiceImpl implements IAttributeService{
	
	@Autowired
	private AttributeRepository attributeRepository;
	@Autowired
	private IUoMService uomService;
	
	@Autowired
	private IDTOWrapper dtoWrapper;
	
	private final Logger logger = LoggerFactory.getLogger(AttributeServiceImpl.class);

	@Override
	public AttributeDTO getAttributeByName(String name) {
		try{
			logger.debug("Fetching details for attribute with name : "+name);
			return dtoWrapper.convert(attributeRepository.findByName(name));
		}catch(Exception ex){
			logger.error("Exception thrown while fetching details for Attribute "+ex.getMessage());
			throw new RuntimeException(Constants._GENERIC_ERROR_MESSAGE);
		}
	}

	@Override
	public AttributeDTO createAttribute(AttributeDTO attributeDTO) {
		try{
			logger.debug("Saving new attribute : "+attributeDTO.toString());
			Attribute attribute = new Attribute(attributeDTO.getName(), 
					uomService.getUnitByNotation(attributeDTO.getUnit()),
					attributeDTO.getPossibleValues());
			attribute.setId(attributeDTO.getId());
			attribute = attributeRepository.saveAndFlush(attribute);
			attributeDTO.setId(attribute.getId());
			return attributeDTO;
		}
		catch(OptimisticLockException ex){
			logger.error("Optimistic Locking exception thrown while updating attribute "+ex.getMessage());
			throw new RuntimeException("Oops, looks like someone else has updated this attribute. Please reload to get the latest copy of the attribute");
		}
		catch(Exception ex){
			logger.error("Exception thrown while saving Attribute "+ex.getCause());
			throw new RuntimeException(ex.getMessage());
		}
	}

	@Override
	public List<AttributeDTO> getAllAttributes() {
		try{
			logger.debug("Fetching all attributes");
			return dtoWrapper.convertToAttributeDTO(attributeRepository.findAll());
		}catch(Exception ex){
			logger.error("Exception thrown while fetching all attributes");
			throw new RuntimeException(ex.getMessage());
		}
	}

	
}
