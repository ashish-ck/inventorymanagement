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
import com.drivojoy.inventory.dto.CrateDTO;
import com.drivojoy.inventory.dto.ItemCountDTO;
import com.drivojoy.inventory.dto.RackDTO;
import com.drivojoy.inventory.dto.ShelfDTO;
import com.drivojoy.inventory.helpers.UserAuthentication;
import com.drivojoy.inventory.models.Crate;
import com.drivojoy.inventory.models.Rack;
import com.drivojoy.inventory.models.Shelf;
import com.drivojoy.inventory.models.datatables.DTParameters;
import com.drivojoy.inventory.models.datatables.DTResult;
import com.drivojoy.inventory.services.IItemService;
import com.drivojoy.inventory.services.IShelfService;
import com.drivojoy.inventory.utils.Constants;
import com.drivojoy.inventory.utils.IDTOWrapper;

/**
 * 
 * @author ashish.singh
 *
 */
@RestController
@RequestMapping("/api/shelves/")
public class ShelfApiController {
	@Autowired
	private IShelfService shelfService;
	@Autowired
	private IItemService itemService;
	@Autowired
	private IDTOWrapper dtoWrapper;
	private final Logger logger = LoggerFactory.getLogger(ShelfApiController.class);

	/**API End point to update an existing shelf
	 * @param warehouseId Warehouse Id
	 * @param shelfDTO - Shelf
	 * @return Shelf
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/warehouse/{warehouseId}/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse<ShelfDTO>> addShelf(@PathVariable("warehouseId") long warehouseId,
			@RequestBody ShelfDTO shelfDTO) {
		logger.debug("add shelf api invoked");
		try {
			Shelf shelf = dtoWrapper.convert(shelfDTO);
			shelf = shelfService.addShelf(warehouseId, shelf);
			shelfDTO = dtoWrapper.convert(shelf);
			ApiResponse<ShelfDTO> response = new ApiResponse<ShelfDTO>(true, shelfDTO, "");
			return new ResponseEntity<ApiResponse<ShelfDTO>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Controller caught exception from service : " + ex.getCause());
			ApiResponse<ShelfDTO> response = new ApiResponse<ShelfDTO>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<ShelfDTO>>(response, HttpStatus.OK);
		}
	}

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
	public ResponseEntity<ApiResponse<ShelfDTO>> editShelf(@PathVariable("id") long id,
			@RequestBody ShelfDTO shelfDTO) {
		logger.debug("edit shelf api invoked" + shelfDTO.toString());
		try {
			Shelf shelf = dtoWrapper.convert(shelfDTO);
			shelf = shelfService.addUpdateShelf(shelf);
			shelfDTO = dtoWrapper.convert(shelf);
			ApiResponse<ShelfDTO> response = new ApiResponse<ShelfDTO>(true, shelfDTO, "Shelf updated successfully!");
			return new ResponseEntity<ApiResponse<ShelfDTO>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Controller caught exception from service : " + ex.getCause());
			ApiResponse<ShelfDTO> response = new ApiResponse<ShelfDTO>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<ShelfDTO>>(response, HttpStatus.OK);
		}
	}

	/*
	 * API end point to add rack
	 * 
	 * @param id - shelf id
	 * 
	 * @param rackDTO - rackDTO
	 * 
	 * @return rackDTO - added rackDTO
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/{id}/addRack", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse<RackDTO>> addRack(@PathVariable("id") long id, @RequestBody RackDTO rackDTO) {
		logger.debug("adding rack to shelf api invoked");
		try {
			Rack rack = dtoWrapper.convert(rackDTO);
			rack = shelfService.addRack(id, rack);
			rackDTO = dtoWrapper.convert(rack);
			ApiResponse<RackDTO> response = new ApiResponse<RackDTO>(true, rackDTO, "Rack added successfully!");
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
	 * API end point to get all racks
	 * 
	 * @param id - shelf id
	 * 
	 * @return list of rackDTO
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/{id}/getRacks", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<RackDTO>>> getRacks(@PathVariable("id") long id) {
		logger.debug("fetching racks from shelf api invoked");
		try {
			List<Rack> racks = shelfService.getRacks(id);
			List<RackDTO> rackDTOs = dtoWrapper.convertToRackDTO(racks);
			ApiResponse<List<RackDTO>> response = new ApiResponse<List<RackDTO>>(true, rackDTOs, "Racks data fetched!");
			return new ResponseEntity<ApiResponse<List<RackDTO>>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Controller caught exception from service : " + ex.getCause());
			ApiResponse<List<RackDTO>> response = new ApiResponse<List<RackDTO>>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<List<RackDTO>>>(response, HttpStatus.OK);
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
			List<Crate> crates = shelfService.getCrates(id);
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
	 * @param id - shelf id
	 * 
	 * @return list of ItemCount
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/{id}/getItems", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<ItemCountDTO>>> getItems(@PathVariable("id") long id) {
		logger.debug("fetching crates from shelf api invoked");
		try {
			List<ItemCountDTO> ItemCounts = itemService.getItemsInShelf(id);
			ApiResponse<List<ItemCountDTO>> response = new ApiResponse<List<ItemCountDTO>>(true, ItemCounts,
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
	
	/**API End point to retrieve all warehouse items in shelf
	 * @param id shelf id
	 * @param params DTTable params
	 * @return DT table item count
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
    @RequestMapping(value="/{id}/items", method=RequestMethod.POST)
    public ResponseEntity<DTResult<ItemCountDTO>> items(@PathVariable("id") long id, @RequestBody DTParameters params,
    		 @AuthenticationPrincipal UserAuthentication user){
    	logger.debug("Controller invoked to get open kits by pagination");
    	DTResult<ItemCountDTO> result = null;
    	try{
    		result = itemService.getItemCountDTO(params, 3L, 1L, 1L, 1L);
    		return new ResponseEntity<DTResult<ItemCountDTO>>(result, HttpStatus.OK);
    	}catch(Exception ex){
    		logger.error("Exception thrown while getting kits pagination");
    		return new ResponseEntity<DTResult<ItemCountDTO>>(HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
}
