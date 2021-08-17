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
import com.drivojoy.inventory.helpers.UserAuthentication;
import com.drivojoy.inventory.models.StockAdjustment;
import com.drivojoy.inventory.models.datatables.DTParameters;
import com.drivojoy.inventory.models.datatables.DTResult;
import com.drivojoy.inventory.services.IStockAdjustmentService;
import com.drivojoy.inventory.utils.Constants;

/**
 * 
 * @author ashishsingh
 *
 */
@RestController
@RequestMapping(value="/api/adjustments/")
public class StockAdjustmentApiController {

	@Autowired
	private IStockAdjustmentService stockAdjustmentService;

	private final Logger logger = LoggerFactory.getLogger(StockAdjustmentApiController.class);
	
	/**
	 * API End point to fetch a list of all stock adjustments
	 * @return Stock Adjustment
	 */
	@PreAuthorize("hasAnyRole('ADMIN,INVENTORYMANAGER')")
	@RequestMapping(value="/get", method=RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<StockAdjustment>>> getAllAdjustments(){
		logger.debug("Controller invoked on getting all adjustments ");
		try{
			List<StockAdjustment> orders = stockAdjustmentService.getAll();
			ApiResponse<List<StockAdjustment>> response = new ApiResponse<List<StockAdjustment>>(true, orders, "Orders Fetched Successfully!");
			return new ResponseEntity<ApiResponse<List<StockAdjustment>>>(response, HttpStatus.OK);
		}catch(Exception ex){
			logger.error("Exception thrown while fetching all stock adjustments "+ex.getCause());
			ApiResponse<List<StockAdjustment>> response = new ApiResponse<List<StockAdjustment>>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<List<StockAdjustment>>>(response, HttpStatus.OK);
		}
	}
	
	/**
	 * API End point to fetch stock adjustments in paginated form
	 * @param params DataTables parameters
	 * @param user Currently logged in user
	 * @return Stock Adjustment
	 */
	@PreAuthorize("hasAnyRole('ADMIN,INVENTORYMANAGER')")
	@RequestMapping(value="/getStockAdjustmentsPaginated", method=RequestMethod.POST)
	public ResponseEntity<DTResult<StockAdjustment>> getStockAdjustmentsPaginated(@RequestBody DTParameters params, @AuthenticationPrincipal UserAuthentication user){
		logger.debug("fetch all PO api invoked");
		try{
			if(user.hasRole("ROLE_ADMIN")){
				return new ResponseEntity<DTResult<StockAdjustment>>(stockAdjustmentService.getAllAdjustmentsPaginated(params, null), HttpStatus.OK);
			}else{
				return new ResponseEntity<DTResult<StockAdjustment>>(stockAdjustmentService.getAllAdjustmentsPaginated(params, user.getWarehouse()), HttpStatus.OK);
			}
		}catch(Exception ex){
			return new ResponseEntity<DTResult<StockAdjustment>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * API End point to find Stock Adjustment order by order number
	 * @param orderNumber Stock adjustment order number
	 * @return Stock Adjustment
	 */
	@PreAuthorize("hasAnyRole('ADMIN,INVENTORYMANAGER')")
	@RequestMapping(value="/get/{orderNumber}", method=RequestMethod.GET)
	public ResponseEntity<ApiResponse<StockAdjustment>> getOrderByNumber(@PathVariable("orderNumber") String orderNumber){
		logger.debug("Controller invoked on getting all adjustments ");
		try{
			StockAdjustment order = stockAdjustmentService.getOrderByNumber(orderNumber);
			ApiResponse<StockAdjustment> response = new ApiResponse<StockAdjustment>(true, order, "Order Found!");
			return new ResponseEntity<ApiResponse<StockAdjustment>>(response, HttpStatus.OK);
		}catch(Exception ex){
			logger.error("Exception thrown while fetching order "+orderNumber+" :"+ex.getCause());
			ApiResponse<StockAdjustment> response = new ApiResponse<StockAdjustment>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<StockAdjustment>>(response, HttpStatus.OK);
		}
	}
	
	/**
	 * API End point to save a stock adjustment order
	 * @param order Stock adjustment order to be saved
	 * @return Stock Adjustment
	 */
	@PreAuthorize("hasAnyRole('ADMIN,INVENTORYMANAGER')")
	@RequestMapping(value="/save", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse<StockAdjustment>> save(@RequestBody StockAdjustment order){
		logger.debug("Controller invoked on getting all adjustments ");
		try{
			order = stockAdjustmentService.save(order);
			ApiResponse<StockAdjustment> response = new ApiResponse<StockAdjustment>(true, order, "Order Found!");
			return new ResponseEntity<ApiResponse<StockAdjustment>>(response, HttpStatus.OK);
		}catch(Exception ex){
			logger.error("Exception thrown while saving order "+order.toString()+" :"+ex.getCause());
			ApiResponse<StockAdjustment> response = new ApiResponse<StockAdjustment>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<StockAdjustment>>(response, HttpStatus.OK);
		}
	}
	
}
