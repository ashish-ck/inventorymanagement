package com.drivojoy.inventory.rest.controllers;

import java.util.Date;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.drivojoy.inventory.dto.ApiResponse;
import com.drivojoy.inventory.dto.OutOfStockKitItemsDTO;
import com.drivojoy.inventory.dto.PurchaseOrderConsolidatedReport;
import com.drivojoy.inventory.dto.PurchaseOrderDTO;
import com.drivojoy.inventory.dto.RequestParamsDTO;
import com.drivojoy.inventory.helpers.UserAuthentication;
import com.drivojoy.inventory.models.datatables.DTResult;
import com.drivojoy.inventory.services.IPurchaseOrderService;
import com.drivojoy.inventory.utils.Constants;

/**
 * 
 * @author ashishsingh
 *
 */
@RestController
@RequestMapping("/api/purchaseOrders/")
public class PurchaseOrderApiController {
	@Autowired
	private IPurchaseOrderService orderService;

	private final Logger logger = LoggerFactory.getLogger(PurchaseOrderApiController.class);

	/**
	 * API End point to create a purchase order
	 * 
	 * @param purchaseOrderDTO
	 *            Purchase order view model
	 * @return Purchase Order
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse<PurchaseOrderDTO>> createPurchaseOrder(
			@RequestBody PurchaseOrderDTO purchaseOrderDTO) {
		logger.debug("createPurchaseOrder api invoked" + purchaseOrderDTO.toString());
		PurchaseOrderDTO createdOrder = null;
		try {
			createdOrder = orderService.createPurchaseOrder(purchaseOrderDTO);
			ApiResponse<PurchaseOrderDTO> response = new ApiResponse<PurchaseOrderDTO>(true, createdOrder,
					"Order Created Successfully!");
			return new ResponseEntity<ApiResponse<PurchaseOrderDTO>>(response, HttpStatus.OK);

		} catch (Exception ex) {
			logger.error("Controller caught exception from service : " + ex.getCause());
			ApiResponse<PurchaseOrderDTO> response = new ApiResponse<PurchaseOrderDTO>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<PurchaseOrderDTO>>(response, HttpStatus.OK);
		}
	}

	/**
	 * API End point to update an existing purchase order
	 * 
	 * @param purchaseOrderDTO
	 *            Update purchase order view model
	 * @return Purchase Order
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse<PurchaseOrderDTO>> updatePurchaseOrder(
			@RequestBody PurchaseOrderDTO purchaseOrderDTO) {
		logger.debug("updatePurchaseOrder api invoked" + purchaseOrderDTO.toString());
		PurchaseOrderDTO updatedOrder = null;
		try {
			updatedOrder = orderService.updatePurchaseOrder(purchaseOrderDTO);
			ApiResponse<PurchaseOrderDTO> response = new ApiResponse<PurchaseOrderDTO>(true, updatedOrder,
					"Order Updated Successfully!");
			return new ResponseEntity<ApiResponse<PurchaseOrderDTO>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Controller caught exception from service : " + ex.getCause());
			ApiResponse<PurchaseOrderDTO> response = new ApiResponse<PurchaseOrderDTO>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<PurchaseOrderDTO>>(response, HttpStatus.OK);
		}
	}

	/**
	 * API End point to receive purchase order items
	 * 
	 * @param purchaseOrderDTO
	 *            Purchase order view model
	 * @return Purchase Order
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/receive/{orderNo}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse<PurchaseOrderDTO>> receiveOrderItems(
			@RequestBody PurchaseOrderDTO purchaseOrderDTO) {
		logger.debug("receiveOrderItems api invoked" + purchaseOrderDTO.toString());
		PurchaseOrderDTO updatedOrder = null;
		try {
			updatedOrder = orderService.receiveOrderItems(purchaseOrderDTO);
			ApiResponse<PurchaseOrderDTO> response = new ApiResponse<PurchaseOrderDTO>(true, updatedOrder,
					"Items Received Successfully!");
			return new ResponseEntity<ApiResponse<PurchaseOrderDTO>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Controller caught exception from service : " + ex.getCause());
			ApiResponse<PurchaseOrderDTO> response = new ApiResponse<PurchaseOrderDTO>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<PurchaseOrderDTO>>(response, HttpStatus.OK);
		}
	}

	/**
	 * API End point to mark the status of a purchase order as invoiced
	 * 
	 * @param purchaseOrderDTO
	 *            Purchase order view model
	 * @return Purchase Order
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/invoice/{orderNo}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse<PurchaseOrderDTO>> markOrderInvoiced(
			@RequestBody PurchaseOrderDTO purchaseOrderDTO) {
		logger.debug("receiveOrderItems api invoked" + purchaseOrderDTO.toString());
		PurchaseOrderDTO updatedOrder = null;
		try {
			updatedOrder = orderService.updatePurchaseOrder(purchaseOrderDTO);
			ApiResponse<PurchaseOrderDTO> response = new ApiResponse<PurchaseOrderDTO>(true, updatedOrder,
					"Items Received Successfully!");
			return new ResponseEntity<ApiResponse<PurchaseOrderDTO>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Controller caught exception from service : " + ex.getCause());
			ApiResponse<PurchaseOrderDTO> response = new ApiResponse<PurchaseOrderDTO>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<PurchaseOrderDTO>>(response, HttpStatus.OK);
		}
	}

	/**
	 * API End point to fetch all purchase order view models
	 * 
	 * @return Purchase Order
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<PurchaseOrderDTO>>> getAllPurchaseOrders() {
		logger.debug("fetch all PO api invoked");
		List<PurchaseOrderDTO> purchaseOrders = null;
		try {
			purchaseOrders = orderService.getPurchaseOrders();
			ApiResponse<List<PurchaseOrderDTO>> response = new ApiResponse<List<PurchaseOrderDTO>>(true, purchaseOrders,
					"Orders Fetched Successfully!");
			return new ResponseEntity<ApiResponse<List<PurchaseOrderDTO>>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Controller caught exception from service : " + ex.getCause());
			ApiResponse<List<PurchaseOrderDTO>> response = new ApiResponse<List<PurchaseOrderDTO>>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<List<PurchaseOrderDTO>>>(response, HttpStatus.OK);
		}
	}

	/**
	 * API End point to fetch all purchase orders in pagination form
	 * 
	 * @param params
	 *            DataTables params
	 * @param user
	 *            Currently logged in user
	 * @return DTTable Purchase Order
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/getPurchaseOrdersPaginated", method = RequestMethod.POST)
	public ResponseEntity<DTResult<PurchaseOrderDTO>> getAllPurchaseOrdersPaginated(@RequestBody RequestParamsDTO requestParam,
			@AuthenticationPrincipal UserAuthentication user) {
		logger.debug("fetch all PO api invoked");
		try {
			if (user.hasRole("ROLE_ADMIN")) {
				return new ResponseEntity<DTResult<PurchaseOrderDTO>>(
						orderService.getPurchaseOrdersPaginated(requestParam), HttpStatus.OK);
			} else {
				return new ResponseEntity<DTResult<PurchaseOrderDTO>>(
						orderService.getPurchaseOrdersPaginated(requestParam), HttpStatus.OK);
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return new ResponseEntity<DTResult<PurchaseOrderDTO>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * API End point to find a purchase order by order number
	 * 
	 * @param orderNo
	 *            Purchase order number
	 * @return Purchase Order
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/get/{orderNo}", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<PurchaseOrderDTO>> getOrderByOrderNo(@PathVariable("orderNo") String orderNo) {
		logger.debug("fetch all PO api invoked");
		PurchaseOrderDTO purchaseOrder = null;
		try {
			purchaseOrder = orderService.getOrderDTOByOrderNo(orderNo);
			ApiResponse<PurchaseOrderDTO> response = new ApiResponse<PurchaseOrderDTO>(true, purchaseOrder,
					"Order Found Successfully!");
			return new ResponseEntity<ApiResponse<PurchaseOrderDTO>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Controller caught exception from service : " + ex.getCause());
			ApiResponse<PurchaseOrderDTO> response = new ApiResponse<PurchaseOrderDTO>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<PurchaseOrderDTO>>(response, HttpStatus.OK);
		}
	}

	/**
	 * API End point to auto-create purchase order from out of stock items.
	 * 
	 * @param outOfStockItems
	 *            List of items to be included in the purchase order to be
	 *            created
	 * @return Purchase Order
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/autoCreatePO", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<PurchaseOrderDTO>> autoCreatePO(
			@RequestBody List<OutOfStockKitItemsDTO> outOfStockItems) {
		logger.debug("autoCreatePurchaseOrder api invoked" + outOfStockItems.toString());
		PurchaseOrderDTO createdOrder = null;
		try {
			createdOrder = orderService.autoCreatePurchaseOrder(outOfStockItems);
			ApiResponse<PurchaseOrderDTO> response = new ApiResponse<PurchaseOrderDTO>(true, createdOrder,
					"Order Created Successfully!");
			return new ResponseEntity<ApiResponse<PurchaseOrderDTO>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Controller caught exception from service : " + ex.getCause());
			ApiResponse<PurchaseOrderDTO> response = new ApiResponse<PurchaseOrderDTO>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<PurchaseOrderDTO>>(response, HttpStatus.OK);
		}
	}

	/**
	 * API End point to find a purchase order by order number
	 * 
	 * @param kitNumber Kit number
	 * @return List of purchase orders
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/getOrdersByKitNumber/{kitNumber}", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<PurchaseOrderDTO>>> getOrderByKitNumber(
			@PathVariable("kitNumber") String kitNumber) {
		logger.debug("fetch all PO api invoked");
		List<PurchaseOrderDTO> purchaseOrders = null;
		try {
			purchaseOrders = orderService.getOrderDTOByKitNumber(kitNumber);
			ApiResponse<List<PurchaseOrderDTO>> response = new ApiResponse<List<PurchaseOrderDTO>>(true, purchaseOrders,
					"Order Found Successfully!");
			return new ResponseEntity<ApiResponse<List<PurchaseOrderDTO>>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Controller caught exception from service : " + ex.getCause());
			ApiResponse<List<PurchaseOrderDTO>> response = new ApiResponse<List<PurchaseOrderDTO>>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<List<PurchaseOrderDTO>>>(response, HttpStatus.OK);
		}
	}

	/**
	 * API End point to retrieve consolidated data of po within a date range
	 * 
	 * @param fromDate
	 *            From date
	 * @param toDate
	 *            To date
	 * @param warehouseId
	 *            Warehouse Id
	 * @return Purchase order report
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/getByDateRange", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<PurchaseOrderConsolidatedReport>> getByDateRange(
			@RequestParam("fromDate") Date fromDate, @RequestParam("toDate") Date toDate,
			@RequestParam("warehouseId") Long warehouseId) {
		logger.debug("Controller invoked on getByDateRange");
		try {
			PurchaseOrderConsolidatedReport report = orderService.getPurchaseOrderReport(fromDate, toDate, warehouseId);
			ApiResponse<PurchaseOrderConsolidatedReport> response = new ApiResponse<PurchaseOrderConsolidatedReport>(
					true, report, "Report retrieved successfully!");
			return new ResponseEntity<ApiResponse<PurchaseOrderConsolidatedReport>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Exception thrown while fetching items by date range :" + ex.getCause());
			ApiResponse<PurchaseOrderConsolidatedReport> response = new ApiResponse<PurchaseOrderConsolidatedReport>(
					false, null, Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<PurchaseOrderConsolidatedReport>>(response, HttpStatus.OK);
		}
	}
}
