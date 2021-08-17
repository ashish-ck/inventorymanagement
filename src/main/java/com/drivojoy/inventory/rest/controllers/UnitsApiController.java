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
import com.drivojoy.inventory.models.UoM;
import com.drivojoy.inventory.services.IUoMService;
import com.drivojoy.inventory.utils.Constants;

/**
 * 
 * @author ashishsingh
 *
 */
@RestController
@RequestMapping(value="/api/units")
public class UnitsApiController {

	@Autowired
	private IUoMService uomService;
	
	private final Logger logger = LoggerFactory.getLogger(UnitsApiController.class);
	
	/**
	 * API End point to fetch all units of measurement
	 * @return List of UoM
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value="/get", method=RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<UoM>>> getAllUnits(){
		logger.debug("fetch all units api invoked");
		List<UoM> unitList = null;
		try{
			unitList = uomService.getAllUnits();
			ApiResponse<List<UoM>> response = new ApiResponse<List<UoM>>(true, unitList, "Units Fetched Successfully!");
			return new ResponseEntity<ApiResponse<List<UoM>>>(response, HttpStatus.OK);
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getCause());
			ApiResponse<List<UoM>> response = new ApiResponse<List<UoM>>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<List<UoM>>>(response, HttpStatus.OK);
		}
	}
	
	/**
	 * API End point to save a unit of measurement
	 * @param unit Unit of measurement object
	 * @return UoM
	 */
	@PreAuthorize("hasAnyRole('ADMIN,INVENTORYMANAGER')")
	@RequestMapping(value="/create", method=RequestMethod.POST)
	public ResponseEntity<ApiResponse<UoM>> createUnit(@RequestBody UoM unit){
		logger.debug("createUnit API invoked : "+unit.toString());
		try{
			unit = uomService.createUnit(unit);
			ApiResponse<UoM> response = new ApiResponse<UoM>(true, unit, "Units saved Successfully!");
			return new ResponseEntity<ApiResponse<UoM>>(response, HttpStatus.OK);
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getCause());
			ApiResponse<UoM> response = new ApiResponse<UoM>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<UoM>>(response, HttpStatus.OK);
		}
	}
}
