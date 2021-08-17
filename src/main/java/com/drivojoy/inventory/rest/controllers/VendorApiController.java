package com.drivojoy.inventory.rest.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.drivojoy.inventory.dto.ApiResponse;
import com.drivojoy.inventory.dto.VendorDTO;
import com.drivojoy.inventory.services.IVendorService;
import com.drivojoy.inventory.utils.Constants;

/**
 * 
 * @author ashishsingh
 *
 */
@RestController
@RequestMapping("/api/vendors/")
public class VendorApiController {
	
	@Autowired
	private IVendorService vendorService;
	
	private final Logger logger = LoggerFactory.getLogger(VendorApiController.class);
	
	/**
	 * API End point to fetch list of all vendors
	 * @return List of vendors
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value="/get", method=RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<VendorDTO>>> findItems(){
		logger.debug("fetch all vendors api invoked");
		List<VendorDTO> vendorList = null;
		try{
			vendorList = vendorService.getVendors();
			//TO-DO: Need to change this
			for(VendorDTO vendor : vendorList){
				vendor.setItemsSold(null);
			}
			ApiResponse<List<VendorDTO>> response = new ApiResponse<List<VendorDTO>>(true, vendorList, "Vendors fetched successfully!");
			return new ResponseEntity<ApiResponse<List<VendorDTO>>>(response, HttpStatus.OK);
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getCause());
			ApiResponse<List<VendorDTO>> response = new ApiResponse<List<VendorDTO>>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<List<VendorDTO>>>(response, HttpStatus.OK);
		}
	}

	/**
	 * API End point to create a vendor
	 * @param vendor Vendor object
	 * @return Vendor
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value="/create", method=RequestMethod.POST)
	public ResponseEntity<ApiResponse<VendorDTO>> createVendor(@RequestBody VendorDTO vendor){
		logger.debug("createVendor api invoked : "+vendor.toString());
		try{
			vendor = vendorService.createUpdateVendor(vendor);
			ApiResponse<VendorDTO> response = new ApiResponse<VendorDTO>(true, vendor, "Vendor created successfully!");
			return new ResponseEntity<ApiResponse<VendorDTO>>(response, HttpStatus.OK);
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getCause());
			ApiResponse<VendorDTO> response = new ApiResponse<VendorDTO>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<VendorDTO>>(response, HttpStatus.OK);
		}
	}
	
	/**
	 * API End point to update an existing vendor
	 * @param id Existing vendor Id
	 * @param vendorDTO Updated copy of vendor
	 * @return Vendor
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value="/edit/{id}", method=RequestMethod.PUT,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse<VendorDTO>> editVendor(@PathVariable("id") long id, @RequestBody VendorDTO vendorDTO){
		logger.debug("edit vendor api invoked" + vendorDTO.toString());
		try{
			vendorDTO = vendorService.createUpdateVendor(vendorDTO);
			ApiResponse<VendorDTO> response = new ApiResponse<VendorDTO>(true, vendorDTO, "Vendor saved successfully!");
			return new ResponseEntity<ApiResponse<VendorDTO>>(response, HttpStatus.OK);
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getCause());
			ApiResponse<VendorDTO> response = new ApiResponse<VendorDTO>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<VendorDTO>>(response, HttpStatus.OK);
		}
	}
	
	/**
	 * API End point to find vendors that sell a particular item
	 * @param id Id of the item sold by vendors
	 * @return List of vendors
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value="/getVendorsByItem/{id}", method=RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<VendorDTO>>> getVendorsByItem(@PathVariable("id") long id){
		List<VendorDTO> vendorList = null;
		logger.debug("get vendors by item api invoked" + id);
		try{
			vendorList = vendorService.getVendorsByItem(id);
			ApiResponse<List<VendorDTO>> response = new ApiResponse<List<VendorDTO>>(true, vendorList, "Vendors fetched successfully!");
			return new ResponseEntity<ApiResponse<List<VendorDTO>>>(response, HttpStatus.OK);
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getCause());
			ApiResponse<List<VendorDTO>> response = new ApiResponse<List<VendorDTO>>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<List<VendorDTO>>>(response, HttpStatus.OK);
		}
	}	
}
