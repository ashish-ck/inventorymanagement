package com.drivojoy.inventory.rest.controllers;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.drivojoy.inventory.dto.ApiResponse;
import com.drivojoy.inventory.dto.AttributeDTO;
import com.drivojoy.inventory.services.IAttributeService;
import com.drivojoy.inventory.utils.Constants;

/**
 * API Endpoint for Attributes
 * @author ashishsingh
 *
 */
@RestController
@RequestMapping(value="/api/attributes/")
public class AttributeController {

	@Autowired
	private IAttributeService attributeService;

	private final Logger logger = LoggerFactory.getLogger(AttributeController.class);
	
	/**
	 * API end point to get all attributes
	 * @return ApiResponse object containing the list of all attributes or error if any
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value="/get", method=RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<AttributeDTO>>> getAllAttributes(){
		logger.debug("fetch all attributes api invoked");
		List<AttributeDTO> attributeList = null;
		try{
			attributeList = attributeService.getAllAttributes();
			if(attributeList != null){
				ApiResponse<List<AttributeDTO>> response = new ApiResponse<>(true, attributeList, null);
				return new ResponseEntity<ApiResponse<List<AttributeDTO>>>(response, HttpStatus.OK);
			}else{
				return new ResponseEntity<ApiResponse<List<AttributeDTO>>>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getCause());
			ApiResponse<List<AttributeDTO>> response = new ApiResponse<>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<List<AttributeDTO>>>(response, HttpStatus.OK);
		}
	}
	
	/**
	 * API end point to create a new attribute
	 * @param attribute Attribute to be created
	 * @return ApiResponse object containing newly created attribute or error if any
	 */
	@PreAuthorize("hasAnyRole('ADMIN,INVENTORYMANAGER')")
	@RequestMapping(value="/create", method=RequestMethod.POST)
	public ResponseEntity<ApiResponse<AttributeDTO>> createAttribute(@RequestBody AttributeDTO attribute){
		logger.debug("create attribute api invoked : "+attribute.toString());
		try{
			
			attribute = attributeService.createAttribute(attribute);
			if(attribute != null){
				ApiResponse<AttributeDTO> response = new ApiResponse<AttributeDTO>(true, attribute, "Attribute Saved Successfully");
				return new ResponseEntity<ApiResponse<AttributeDTO>>(response, HttpStatus.OK);
			}else{
				return new ResponseEntity<ApiResponse<AttributeDTO>>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getCause());
			ApiResponse<AttributeDTO> response = new ApiResponse<AttributeDTO>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<AttributeDTO>>(response, HttpStatus.OK);
		}
	}

}
