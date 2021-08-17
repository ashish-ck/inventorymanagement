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
import com.drivojoy.inventory.models.Crate;
import com.drivojoy.inventory.models.datatables.DTParameters;
import com.drivojoy.inventory.models.datatables.DTResult;
import com.drivojoy.inventory.services.ICrateService;
import com.drivojoy.inventory.services.IItemService;
import com.drivojoy.inventory.utils.Constants;
import com.drivojoy.inventory.utils.IDTOWrapper;

/**
 * 
 * @author ashish.singh
 *
 */
@RestController
@RequestMapping("/api/crates/")
public class CrateApiController {
	@Autowired
	ICrateService crateService;
	@Autowired
	private IItemService itemService;
	@Autowired
	private IDTOWrapper dtoWrapper;
	private final Logger logger = LoggerFactory.getLogger(CrateApiController.class);

	/*
	 * API end point to edit shelf
	 * 
	 * @param id - shelf id
	 * 
	 * @param shelfDTO - shelfDTO
	 * 
	 * @return shelfDTO - updated shelfDTO
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/edit/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse<CrateDTO>> editCrate(@PathVariable("id") long id,
			@RequestBody CrateDTO crateDTO) {
		logger.debug("edit shelf api invoked" + crateDTO.toString());
		try {
			Crate crate = dtoWrapper.convert(crateDTO);
			crate = crateService.addUpdateCrate(crate);
			crateDTO = dtoWrapper.convert(crate);
			ApiResponse<CrateDTO> response = new ApiResponse<CrateDTO>(true, crateDTO, "Shelf updated successfully!");
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
	 * API end point to get all warehouse items
	 * 
	 * @param id - shelf id
	 * 
	 * @return list of ItemCount
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/{id}/getItems", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<ItemCountDTO>>> getItems(@PathVariable("id") long id) {
		logger.debug("fetching crates from shelf api invoked");
		try {
			List<ItemCountDTO> ItemCounts = itemService.getItemsInCrate(id);
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
	
	/**API End point to retrieve all warehouse items by crate id
	 * @param id - crate id
	 * @param params - DT params
	 * @return - Item count
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
    @RequestMapping(value="/{id}/items", method=RequestMethod.GET)
    public ResponseEntity<DTResult<ItemCountDTO>> items(@PathVariable("id") long id, @RequestBody DTParameters params){
    	logger.debug("Controller invoked to get open kits by pagination");
    	DTResult<ItemCountDTO> result = null;
    	try{
    		result = dtoWrapper.convert(itemService.getItemsInCrate(id));
    		return new ResponseEntity<DTResult<ItemCountDTO>>(result, HttpStatus.OK);
    	}catch(Exception ex){
    		logger.error("Exception thrown while getting kits pagination");
    		return new ResponseEntity<DTResult<ItemCountDTO>>(HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
}
