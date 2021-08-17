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
import com.drivojoy.inventory.dto.CrateDTO;
import com.drivojoy.inventory.dto.ItemCountDTO;
import com.drivojoy.inventory.dto.RackDTO;
import com.drivojoy.inventory.models.Crate;
import com.drivojoy.inventory.models.Rack;
import com.drivojoy.inventory.models.datatables.DTParameters;
import com.drivojoy.inventory.models.datatables.DTResult;
import com.drivojoy.inventory.services.IItemService;
import com.drivojoy.inventory.services.IRackService;
import com.drivojoy.inventory.utils.Constants;
import com.drivojoy.inventory.utils.IDTOWrapper;

/**
 * 
 * @author ashish.singh
 *
 */
@RestController
@RequestMapping("/api/racks/")
public class RackApiController {
	@Autowired
	IRackService rackService;
	@Autowired
	private IItemService itemService;
	@Autowired
	private IDTOWrapper dtoWrapper;
	private final Logger logger = LoggerFactory.getLogger(RackApiController.class);

	/*
	 * API end point to edit rack
	 * 
	 * @param id - rack id
	 * 
	 * @param rackDTO - rackDTO
	 * 
	 * @return rackDTO - updated rackDTO
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/edit/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse<RackDTO>> editRack(@PathVariable("id") long id, @RequestBody RackDTO rackDTO) {
		logger.debug("edit rack api invoked" + rackDTO.toString());
		try {
			Rack rack = dtoWrapper.convert(rackDTO);
			rack = rackService.addUpdateRack(rack);
			rackDTO = dtoWrapper.convert(rack);
			ApiResponse<RackDTO> response = new ApiResponse<RackDTO>(true, rackDTO, "Shelf updated successfully!");
			return new ResponseEntity<ApiResponse<RackDTO>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Controller caught exception from service : " + ex.getCause());
			ApiResponse<RackDTO> response = new ApiResponse<RackDTO>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<RackDTO>>(response, HttpStatus.OK);
		}
	}

	/*
	 * API end point to add crate
	 * 
	 * @param id - rack id
	 * 
	 * @param crateDTO - crateDTO
	 * 
	 * @return crateDTO - added crateDTO
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/{id}/addCrate", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse<CrateDTO>> addCrate(@PathVariable("id") long id, @RequestBody CrateDTO crateDTO) {
		logger.debug("adding crate to rack api invoked");
		try {
			Crate crate = dtoWrapper.convert(crateDTO);
			crate = rackService.addCrate(id, crate);
			crateDTO = dtoWrapper.convert(crate);
			ApiResponse<CrateDTO> response = new ApiResponse<CrateDTO>(true, crateDTO, "Crate added successfully!");
			return new ResponseEntity<ApiResponse<CrateDTO>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Controller caught exception from service : " + ex.getCause());
			ApiResponse<CrateDTO> response = new ApiResponse<CrateDTO>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<CrateDTO>>(response, HttpStatus.OK);
		}
	}

	/*
	 * API end point to get all crates
	 * 
	 * @param id - shelf id
	 * 
	 * @return list of crateDTO
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/{id}/getCrates", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<CrateDTO>>> getCrates(@PathVariable("id") long id) {
		logger.debug("fetching crates from shelf api invoked");
		try {
			List<Crate> crates = rackService.getCrates(id);
			List<CrateDTO> crateDTOs = dtoWrapper.convertToCrateDTO(crates);
			ApiResponse<List<CrateDTO>> response = new ApiResponse<List<CrateDTO>>(true, crateDTOs,
					"Crates data fetched!");
			return new ResponseEntity<ApiResponse<List<CrateDTO>>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Controller caught exception from service : " + ex.getCause());
			ApiResponse<List<CrateDTO>> response = new ApiResponse<List<CrateDTO>>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<List<CrateDTO>>>(response, HttpStatus.OK);
		}
	}

	/*
	 * API end point to get all warehouse items
	 * 
	 * @param id - rack id
	 * 
	 * @return list of ItemCount
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/{id}/getItems", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<ItemCountDTO>>> getItems(@PathVariable("id") long id) {
		logger.debug("fetching crates from shelf api invoked");
		try {
			List<ItemCountDTO> ItemCounts = itemService.getItemsInRack(id);
			ApiResponse<List<ItemCountDTO>> response = new ApiResponse<List<ItemCountDTO>>(true,
					ItemCounts, "Warehouse items data fetched!");
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

	/**API End point to retrieve all warehouse items by rack id
	 * @param id Rack id
	 * @param params DTTable params
	 * @return List of Item Count
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
    @RequestMapping(value="/{id}/items", method=RequestMethod.GET)
    public ResponseEntity<DTResult<ItemCountDTO>> items(@PathVariable("id") long id, @RequestBody DTParameters params){
    	logger.debug("Controller invoked to get open kits by pagination");
    	DTResult<ItemCountDTO> result = null;
    	try{
    		result = dtoWrapper.convert(itemService.getItemsInRack(id));
    		return new ResponseEntity<DTResult<ItemCountDTO>>(result, HttpStatus.OK);
    	}catch(Exception ex){
    		logger.error("Exception thrown while getting kits pagination");
    		return new ResponseEntity<DTResult<ItemCountDTO>>(HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
}
