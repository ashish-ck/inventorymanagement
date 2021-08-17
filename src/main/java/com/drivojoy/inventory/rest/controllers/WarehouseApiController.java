package com.drivojoy.inventory.rest.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.drivojoy.inventory.dto.ApiResponse;
import com.drivojoy.inventory.dto.ItemCountDTO;
import com.drivojoy.inventory.dto.ShelfDTO;
import com.drivojoy.inventory.dto.WarehouseDTO;
import com.drivojoy.inventory.dto.WarehouseNetworkDTO;
import com.drivojoy.inventory.helpers.UserAuthentication;
import com.drivojoy.inventory.models.Shelf;
import com.drivojoy.inventory.models.Warehouse;
import com.drivojoy.inventory.models.datatables.DTParameters;
import com.drivojoy.inventory.models.datatables.DTResult;
import com.drivojoy.inventory.services.IItemService;
import com.drivojoy.inventory.services.IWarehouseService;
import com.drivojoy.inventory.utils.Constants;
import com.drivojoy.inventory.utils.IDTOWrapper;

/**
 * 
 * @author ashishsingh
 *
 */
@RestController
@RequestMapping(value = "/api/warehouses")
public class WarehouseApiController {

	@Autowired
	private IWarehouseService warehouseService;
	@Autowired
	private IDTOWrapper dtoWrapper;
	@Autowired
	private IItemService itemService;
	private final Logger logger = LoggerFactory.getLogger(WarehouseApiController.class);

	/**
	 * API End point to create a warehouse
	 * @param warehouseDTO Warehouse
	 * @return Warehouse
	 */
	@PreAuthorize("hasAnyRole('ADMIN,INVENTORYMANAGER')")
	@RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse<WarehouseDTO>> createWarehouse(@RequestBody WarehouseDTO warehouseDTO) {
		logger.debug("createWarehouse api invoked" + warehouseDTO.toString());
		WarehouseDTO createdWarehouse = null;
		try {
			createdWarehouse = warehouseService.createWarehouse(warehouseDTO);
			ApiResponse<WarehouseDTO> response = new ApiResponse<WarehouseDTO>(true, createdWarehouse,
					"Warehouse created successfully!");
			return new ResponseEntity<ApiResponse<WarehouseDTO>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Controller caught exception from service : " + ex.getCause());
			ApiResponse<WarehouseDTO> response = new ApiResponse<WarehouseDTO>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<WarehouseDTO>>(response, HttpStatus.OK);
		}
	}

	/**
	 * API End point to update an existing warehouse
	 * 
	 * @param id Id of existing warehouse
	 * @param warehouseDTO Updated copy of warehouse
	 * @return Warehouse
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/edit/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse<WarehouseDTO>> editWarehouse(@PathVariable("id") long id,
			@RequestBody WarehouseDTO warehouseDTO) {
		logger.debug("createItemCategory api invoked" + warehouseDTO.toString());
		WarehouseDTO editedWarehouse = null;
		try {
			editedWarehouse = warehouseService.editWarehouse(warehouseDTO);
			ApiResponse<WarehouseDTO> response = new ApiResponse<WarehouseDTO>(true, editedWarehouse,
					"Warehouse saved successfully!");
			return new ResponseEntity<ApiResponse<WarehouseDTO>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Controller caught exception from service : " + ex.getCause());
			ApiResponse<WarehouseDTO> response = new ApiResponse<WarehouseDTO>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<WarehouseDTO>>(response, HttpStatus.OK);
		}
	}

	/**
	 * API End point to fetch list of all warehouse view models
	 * @return List of warehouses
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<WarehouseDTO>>> getAllWarehousesDTO() {
		logger.debug("fetch all warehouses api invoked");
		List<WarehouseDTO> warehouseList = null;
		try {
			warehouseList = warehouseService.getAllWarehousesDTO();
			ApiResponse<List<WarehouseDTO>> response = new ApiResponse<List<WarehouseDTO>>(true, warehouseList, "");
			return new ResponseEntity<ApiResponse<List<WarehouseDTO>>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Controller caught exception from service : " + ex.getCause());
			ApiResponse<List<WarehouseDTO>> response = new ApiResponse<List<WarehouseDTO>>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<List<WarehouseDTO>>>(response, HttpStatus.OK);
		}
	}
	
	/**
	 * API End point to fetch list of all warehouse name and id
	 * @return List of warehouses
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/getWarehouseNames", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<WarehouseNetworkDTO>>> getAllWarehouseNames() {
		logger.debug("fetch all warehouses api invoked");
		List<WarehouseNetworkDTO> warehouseList = null;
		try {
			warehouseList = warehouseService.getAllWarehouseNetworkDTO();
			ApiResponse<List<WarehouseNetworkDTO>> response = new ApiResponse<>(true, warehouseList, "");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Controller caught exception from service : " + ex.getCause());
			ApiResponse<List<WarehouseNetworkDTO>> response = new ApiResponse<>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	/**
	 * API End point to fetch list of all warehouses
	 * @return List of warehouses
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/getAll", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<Warehouse>>> getAllWarehouses() {
		logger.debug("fetch all warehouses api invoked");
		List<Warehouse> warehouseList = null;
		try {
			warehouseList = warehouseService.getAllWarehouses();
			ApiResponse<List<Warehouse>> response = new ApiResponse<List<Warehouse>>(true, warehouseList, "");
			return new ResponseEntity<ApiResponse<List<Warehouse>>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Controller caught exception from service : " + ex.getCause());
			ApiResponse<List<Warehouse>> response = new ApiResponse<List<Warehouse>>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<List<Warehouse>>>(response, HttpStatus.OK);
		}
	}

	/**API End point to fetch list of all shelves in warehouse
	 * @param warehouseId Id
	 * @return List of shelves
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/{warehouseId}/getShelves", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<ShelfDTO>>> getShelves(@PathVariable("warehouseId") long warehouseId) {
		logger.debug("fetch all shelves in warehouse api invoked");
		try {
			Warehouse warehouse = warehouseService.findById(warehouseId);
			List<Shelf> shelfs = (List<Shelf>) warehouse.getShelves();
			List<ShelfDTO> shelfDTOs = dtoWrapper.convertToShelfDTO(shelfs);
			ApiResponse<List<ShelfDTO>> response = new ApiResponse<List<ShelfDTO>>(true, shelfDTOs,
					"Shelves data fetched!");
			return new ResponseEntity<ApiResponse<List<ShelfDTO>>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Controller caught exception from service : " + ex.getCause());
			ApiResponse<List<ShelfDTO>> response = new ApiResponse<List<ShelfDTO>>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<List<ShelfDTO>>>(response, HttpStatus.OK);
		}
	}

	/*
	 * API end point to get all warehouse items
	 * 
	 * @param id - warehouse id
	 * 
	 * @return list of ItemCountDTO
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/{id}/getItems", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<ItemCountDTO>>> getItems(@PathVariable("id") long id) {
		logger.debug("fetching crates from shelf api invoked");
		try {
			List<ItemCountDTO> itemCounts = itemService.getItemsByWarehouseId(id);
			ApiResponse<List<ItemCountDTO>> response = new ApiResponse<List<ItemCountDTO>>(true, itemCounts,
					"Warehouse items data fetched!");
			return new ResponseEntity<ApiResponse<List<ItemCountDTO>>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Controller caught exception from service : " + ex.getCause());
			ApiResponse<List<ItemCountDTO>> response = new ApiResponse<List<ItemCountDTO>>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<List<ItemCountDTO>>>(response, HttpStatus.OK);
		}
	}
	
	/**API End point to retrieve all warehouse items by warehouse
	 * @param id Id
	 * @param params DT parameters
	 * @return DT result item count
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value="/{id}/items", method=RequestMethod.POST)
	public ResponseEntity<DTResult<ItemCountDTO>> items(@PathVariable("id") long id, @RequestBody DTParameters params,
   		 @AuthenticationPrincipal UserAuthentication user){
		//@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	    //@RequestMapping(value="/{id}/items", method=RequestMethod.POST)
	    //public ResponseEntity<DTResult<ItemCountDTO>> items(@PathVariable("id") long id, @RequestBody DTParameters params,
	    		 //@AuthenticationPrincipal UserAuthentication user){
    	logger.debug("Controller invoked to get open kits by pagination");
    	DTResult<ItemCountDTO> result = null;
    	try{
    		result = itemService.getItemCountDTO(params, null, null, null, 1L);
    		return new ResponseEntity<DTResult<ItemCountDTO>>(result, HttpStatus.OK);
    	}catch(Exception ex){
    		logger.error("Exception thrown while getting kits pagination");
    		return new ResponseEntity<DTResult<ItemCountDTO>>(HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
	
	/**
	 * API End point to find an warehouse by id
	 * @param warehouseId Id
	 * @return Warehouse
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value="/get/{warehouseId}", method=RequestMethod.GET)
	public ResponseEntity<ApiResponse<WarehouseDTO>> findWarehouseById(@PathVariable long warehouseId){
		logger.debug("Controller invoked to get warehouse with id : "+warehouseId);
		ApiResponse<WarehouseDTO> response = null;
		try{
			WarehouseDTO warehouse = dtoWrapper.convert(warehouseService.findById(warehouseId));
			response = new ApiResponse<WarehouseDTO>(true, warehouse, "Warehouse found Successfully!");
			return new ResponseEntity<ApiResponse<WarehouseDTO>>(response,HttpStatus.OK);
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getMessage());
			response = new ApiResponse<WarehouseDTO>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<WarehouseDTO>>(response,HttpStatus.OK);
		}
	}
	
	/**API End point to find an warehouse by code
	 * @param code warehouse code
	 * @return Warehouse
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value="/getByCode/{code}", method=RequestMethod.GET)
	public ResponseEntity<ApiResponse<WarehouseDTO>> findWarehouseByCode(@PathVariable String code){
		logger.debug("Controller invoked to get warehouse with id : " + code);
		ApiResponse<WarehouseDTO> response = null;
		try{
			WarehouseDTO warehouse = dtoWrapper.convert(warehouseService.getWarehouseByCode(code));
			response = new ApiResponse<WarehouseDTO>(true, warehouse, "Warehouse found Successfully!");
			return new ResponseEntity<ApiResponse<WarehouseDTO>>(response,HttpStatus.OK);
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getMessage());
			response = new ApiResponse<WarehouseDTO>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<WarehouseDTO>>(response,HttpStatus.OK);
		}
	}
	
	/**API Endpoint to get all available barcodes that can be reserved at a warehouse/hub.
	 * @param workshopRef warehouse code
	 * @param barcodes list of barcodes
	 * @return List of barcodes
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/{workshopRef}/getAvailableBarcodesByBarcodes", method = RequestMethod.POST)
	public ResponseEntity<ApiResponse<List<String>>> getAvailableBarcodesByBarcodesAndWarehouseId(@PathVariable("workshopRef") String workshopRef,
			 @RequestBody List<String> barcodes) {
		logger.debug("fetching crates from shelf api invoked");
		try {
			List<String> result = itemService.getAvailableBarcodesByBarcodesAndWarehouse(workshopRef, barcodes);
			ApiResponse<List<String>> response = new ApiResponse<List<String>>(true, result,
					"Warehouse items data fetched!");
			return new ResponseEntity<ApiResponse<List<String>>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Controller caught exception from service : " + ex.getCause());
			ApiResponse<List<String>> response = new ApiResponse<List<String>>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<List<String>>>(response, HttpStatus.OK);
		}
	}
	
	/**Get barcode suggestions with storage locations by item codes.
	 * @param workshopRef Warehouse code
	 * @param voucherRef Kit Number
	 * @param itemCodes List of item codes.
	 * @return List of barcode suggestion with storage locations.
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
    @RequestMapping(value="/getBarcodeSuggestion/{workshopRef}/{voucherRef}", method=RequestMethod.POST)
    public ResponseEntity<ApiResponse<List<String>>> getBarcodeSuggestion(@PathVariable("workshopRef") String workshopRef, 
    		@PathVariable("voucherRef") String voucherRef,
    		@RequestBody List<String> itemCodes,
    		 @AuthenticationPrincipal UserAuthentication user) {
		logger.debug("fetching barcode suggestion api invoked");
		try {
			List<String> result = itemService.getBarcodeSuggestion(workshopRef, itemCodes, voucherRef);
			ApiResponse<List<String>> response = new ApiResponse<List<String>>(true, result,
					"Barcodess data fetched!");
			return new ResponseEntity<ApiResponse<List<String>>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Controller caught exception from service : " + ex.getCause());
			ApiResponse<List<String>> response = new ApiResponse<List<String>>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<List<String>>>(response, HttpStatus.OK);
		}
	}
		
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
    @RequestMapping(value="/warehouseItems/{warehouseId}/{shelfId}/{rackId}/{crateId}", method=RequestMethod.POST)
    public ResponseEntity<DTResult<ItemCountDTO>> items(@PathVariable("warehouseId") long warehouseId, 
    		@PathVariable("shelfId") long shelfId, @PathVariable("rackId") long rackId, 
    		@PathVariable("crateId") long crateId, 
    		@RequestBody DTParameters params,
    		 @AuthenticationPrincipal UserAuthentication user){
    	logger.debug("Controller invoked to get open kits by pagination");
    	DTResult<ItemCountDTO> result = null;
    	try{
    		result = itemService.getItemCountDTO(params, warehouseId, shelfId, rackId, crateId);
    		return new ResponseEntity<DTResult<ItemCountDTO>>(result, HttpStatus.OK);
    	}catch(Exception ex){
    		logger.error("Exception thrown while getting kits pagination");
    		return new ResponseEntity<DTResult<ItemCountDTO>>(HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
}
