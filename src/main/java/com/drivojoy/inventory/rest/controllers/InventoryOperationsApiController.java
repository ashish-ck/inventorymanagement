package com.drivojoy.inventory.rest.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.drivojoy.inventory.dto.ApiResponse;
import com.drivojoy.inventory.dto.InventoryRequest;
import com.drivojoy.inventory.dto.ItemDTO;
import com.drivojoy.inventory.dto.ItemRequestPair;
import com.drivojoy.inventory.dto.ItemStatusDTO;
import com.drivojoy.inventory.dto.SalesInventoryRequest;
import com.drivojoy.inventory.dto.SalesOrderLineItemDTO;
import com.drivojoy.inventory.helpers.UserAuthentication;
import com.drivojoy.inventory.models.ItemCount;
import com.drivojoy.inventory.models.Kit;
import com.drivojoy.inventory.models.SalesOrderItemLine;
import com.drivojoy.inventory.services.IInventoryOperationsService;
import com.drivojoy.inventory.services.IKitService;
import com.drivojoy.inventory.utils.Constants;
import com.drivojoy.inventory.utils.Constants.ItemStatus;
import com.drivojoy.inventory.utils.IDTOWrapper;

/**
 * 
 * @author ashishsingh
 *
 */
@RestController
@RequestMapping(value = "/api/inventory/")
public class InventoryOperationsApiController {

	@Autowired
	private IDTOWrapper dtoWrapper;
	@Autowired
	private IInventoryOperationsService invOperations;
	@Autowired
	private IKitService kitService;
	private final Logger logger = LoggerFactory.getLogger(InventoryOperationsApiController.class);

	/**API End point to get logged in user details
	 * @param user - User
	 * @return - User details
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/getUserDetails", method = RequestMethod.GET)
	public ResponseEntity<UserAuthentication> getUserDetails(@AuthenticationPrincipal UserAuthentication user) {
		try {
			if (user != null) {
				if (user.isAuthenticated())
					return new ResponseEntity<UserAuthentication>(user, HttpStatus.OK);
				else
					return new ResponseEntity<UserAuthentication>(HttpStatus.UNAUTHORIZED);
			} else {
				return new ResponseEntity<UserAuthentication>(HttpStatus.UNAUTHORIZED);
			}

		} catch (Exception ex) {
			return new ResponseEntity<UserAuthentication>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**API End point for issuing items for an order
	 * @param request - Request
	 * @return - List of Item
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/issue", method = RequestMethod.POST)
	public ResponseEntity<ApiResponse<List<ItemDTO>>> issueItemsForOrder(@RequestBody InventoryRequest request) {
		logger.debug("issuing items for sales order : " + request.toString());
		try {
			invOperations.issueItemsForSalesOrder(request.getDate(), request.getVoucherRef(), request.getListOfItems(),
					request.getWorkshopRef());
			ApiResponse<List<ItemDTO>> response = new ApiResponse<List<ItemDTO>>(true, null,
					"Items Issued Successfully!");
			return new ResponseEntity<ApiResponse<List<ItemDTO>>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ApiResponse<List<ItemDTO>> response = new ApiResponse<List<ItemDTO>>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			logger.error("Exception thrown while issuing items for order " + ex.getMessage());
			return new ResponseEntity<ApiResponse<List<ItemDTO>>>(response, HttpStatus.OK);
		}
	}

	/**API End point for re stocking items for order
	 * @param request - Request
	 * @return - Item list
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/restock", method = RequestMethod.POST)
	public ResponseEntity<ApiResponse<List<ItemDTO>>> restockItemsForOrder(@RequestBody InventoryRequest request) {
		logger.debug("initiating restocking items for sales order : " + request.toString());
		try {
			invOperations.restockItems(request.getDate(), request.getVoucherRef(), request.getListOfItems(),
					request.getWorkshopRef());
			ApiResponse<List<ItemDTO>> response = new ApiResponse<List<ItemDTO>>(true, null,
					"Items Restocked Successfully!");
			return new ResponseEntity<ApiResponse<List<ItemDTO>>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ApiResponse<List<ItemDTO>> response = new ApiResponse<List<ItemDTO>>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			logger.error("Exception thrown while restocking items for order :" + ex.getMessage());
			return new ResponseEntity<ApiResponse<List<ItemDTO>>>(response, HttpStatus.OK);
		}
	}

	/**API End point to fulfill sales order
	 * @param request - Request
	 * @return - List of Items
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/invoiced", method = RequestMethod.POST)
	public ResponseEntity<ApiResponse<List<ItemDTO>>> fulfillSalesOrder(@RequestBody InventoryRequest request) {
		logger.debug("issuing items for sales order : " + request.toString());
		List<ItemDTO> itemList = new ArrayList<>();
		try {
			itemList = invOperations.fulfillSalesOrder(request.getDate(), request.getVoucherRef(),
					request.getListOfItems(), request.getWorkshopRef());
			ApiResponse<List<ItemDTO>> response = new ApiResponse<List<ItemDTO>>(true, itemList,
					"Items issued Successfully!");
			return new ResponseEntity<ApiResponse<List<ItemDTO>>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ApiResponse<List<ItemDTO>> response = new ApiResponse<List<ItemDTO>>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			logger.error("Exception thrown while fulfilling sales order :" + ex.getMessage());
			return new ResponseEntity<ApiResponse<List<ItemDTO>>>(response, HttpStatus.OK);
		}
	}

	/**API End point to reserve items for an order
	 * @param request - Request
	 * @return - List of Items
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/reserve", method = RequestMethod.POST)
	public ResponseEntity<ApiResponse<List<ItemDTO>>> reserveItems(@RequestBody InventoryRequest request) {
		logger.debug("Reserve items for order : " + request.toString());
		try {
			invOperations.reserveItemsForOrder(request.getDate(), request.getVoucherRef(), request.getListOfItems(),
					request.getWorkshopRef());
			ApiResponse<List<ItemDTO>> response = new ApiResponse<List<ItemDTO>>(true, null,
					"Items reserved Successfully!");
			return new ResponseEntity<ApiResponse<List<ItemDTO>>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ApiResponse<List<ItemDTO>> response = new ApiResponse<List<ItemDTO>>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			logger.error("Exception thrown while reserving items for order :" + ex.getMessage());
			return new ResponseEntity<ApiResponse<List<ItemDTO>>>(response, HttpStatus.OK);
		}
	}
	
	/**API End point to fulfill a purchase order
	 * @param request - Request
	 * @return - List of Item Request pairs
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	public ResponseEntity<ApiResponse<List<ItemRequestPair>>> fulfillPurchaseOrder(
			@RequestBody InventoryRequest request) {
		logger.debug("Purchase Order Fulfillment requested for : " + request.toString());
		try {
			List<ItemRequestPair> itemList = invOperations.fulfillPurchaseOrder(request.getDate(),
					request.getVoucherRef(), request.getListOfItems(), request.getWorkshopRef(), false);
			ApiResponse<List<ItemRequestPair>> response = new ApiResponse<List<ItemRequestPair>>(true, itemList,
					"Items Received Successfully!");
			return new ResponseEntity<ApiResponse<List<ItemRequestPair>>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ApiResponse<List<ItemRequestPair>> response = new ApiResponse<List<ItemRequestPair>>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			logger.error("Exception thrown while ful-filling purchase order :" + ex.getMessage());
			return new ResponseEntity<ApiResponse<List<ItemRequestPair>>>(response, HttpStatus.OK);
		}
	}

	/**
	 * API End point to fulfill Sales Order Re stock
	 * @param request - InventoryRequest object with details of items
	 * @return - List of Items
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/restock/fulfill", method = RequestMethod.POST)
	public ResponseEntity<ApiResponse<List<ItemDTO>>> fulfillSalesOrderRestock(@RequestBody InventoryRequest request) {
		logger.debug("Sales Order Restock requested for : " + request.toString());
		try {
			invOperations.receiveItems(request.getDate(), request.getVoucherRef(), request.getListOfItems(),
					request.getWorkshopRef());
			ApiResponse<List<ItemDTO>> response = new ApiResponse<List<ItemDTO>>(true, null,
					"Items Restocked Successfully!");
			return new ResponseEntity<ApiResponse<List<ItemDTO>>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ApiResponse<List<ItemDTO>> response = new ApiResponse<List<ItemDTO>>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			logger.error("Exception thrown while restocking items for order :" + ex.getMessage());
			return new ResponseEntity<ApiResponse<List<ItemDTO>>>(response, HttpStatus.OK);
		}
	}

	/**
	 * API End point to fulfill Purchase Order Returns
	 * @param request InventoryRequest object with details of items
	 * @return List of items
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/return", method = RequestMethod.POST)
	public ResponseEntity<ApiResponse<List<ItemDTO>>> fulfillPurchaseOrderReturn(
			@RequestBody InventoryRequest request) {
		logger.debug("Purchase Order Return requested for : " + request.toString());
		try {
			List<ItemDTO> itemList = invOperations.fulfillPurchaseOrderReturn(request.getDate(),
					request.getVoucherRef(), request.getListOfItems(), request.getWorkshopRef());
			ApiResponse<List<ItemDTO>> response = new ApiResponse<List<ItemDTO>>(true, itemList,
					"Items Returned Successfully!");
			return new ResponseEntity<ApiResponse<List<ItemDTO>>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ApiResponse<List<ItemDTO>> response = new ApiResponse<List<ItemDTO>>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			logger.error("Exception thrown while returnig items for order :" + ex.getMessage());
			return new ResponseEntity<ApiResponse<List<ItemDTO>>>(response, HttpStatus.OK);
		}
	}

	/**
	 * API End point to send items of a transfer order
	 * @param request - InventoryRequest object with details of items
	 * @return - List of Item count
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/transfer/send", method = RequestMethod.POST)
	public ResponseEntity<ApiResponse<List<ItemCount>>> fulfillStockTransferSend(
			@RequestBody InventoryRequest request) {
		logger.debug("Stock transfer send requested for : " + request.toString());
		try {
			List<ItemCount> itemList = invOperations.fulfillStockTransferSend(request.getDate(),
					request.getVoucherRef(), request.getListOfItems(), request.getWorkshopRef());
			ApiResponse<List<ItemCount>> response = new ApiResponse<List<ItemCount>>(true, itemList,
					"Items Sent Successfully!");
			return new ResponseEntity<ApiResponse<List<ItemCount>>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ApiResponse<List<ItemCount>> response = new ApiResponse<List<ItemCount>>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			logger.error("Exception thrown while sending items for stock transfer :" + ex.getMessage());
			return new ResponseEntity<ApiResponse<List<ItemCount>>>(response, HttpStatus.OK);
		}
	}

	/**
	 * API End point to receive items of a transfer order
	 * 
	 * @param request InventoryRequest object with details of items
	 * @return List of Item count
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/transfer/receive", method = RequestMethod.POST)
	public ResponseEntity<ApiResponse<List<ItemCount>>> fulfillStockTransferReceive(
			@RequestBody InventoryRequest request) {
		logger.debug("Stock transfer receive requested for : " + request.toString());
		try {
			List<ItemCount> itemList = invOperations.fulfillStockTransferReceive(request.getDate(),
					request.getVoucherRef(), request.getListOfItems(), request.getWorkshopRef());
			ApiResponse<List<ItemCount>> response = new ApiResponse<List<ItemCount>>(true, itemList,
					"Items Received Successfully!");
			return new ResponseEntity<ApiResponse<List<ItemCount>>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ApiResponse<List<ItemCount>> response = new ApiResponse<List<ItemCount>>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			logger.error("Exception thrown while received items for stock transfer :" + ex.getMessage());
			return new ResponseEntity<ApiResponse<List<ItemCount>>>(response, HttpStatus.OK);
		}
	}

	/**
	 * API End point to request items for a sales order. This is an integration
	 * call that is made from the sales dashboard
	 * @param request InventoryRequest object with details of items
	 * @return List of kit items
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/kit/update", method = RequestMethod.POST)
	public ResponseEntity<List<SalesOrderLineItemDTO>> updateKitRequest(@RequestBody SalesInventoryRequest request) {
		logger.debug("Request to update a kit");
		logger.debug("Request Data : " + request.toString());
		try {
			Collection<SalesOrderItemLine> itemList = invOperations.updateKitRequest(request.getDate(),
					request.getVoucherRef(), request.getListOfItems(), request.getWorkshopRef(), request.getStatus());
			return new ResponseEntity<>(dtoWrapper.quickConvertSalesOrderItemLineToSalesOrderItemLineDTO(itemList),
					HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Exception thrown while updating kit request" + ex.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**API End point to cancel the kit
	 * @param kitNumber Kit Number
	 * @param status Status
	 * @return List of kit items
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cancelKit/{kitNumber}", method = RequestMethod.GET)
	public ResponseEntity<List<SalesOrderLineItemDTO>> cancelKit(@PathVariable String kitNumber,
			@RequestParam("status") int status) {
		logger.debug("Controller invoked on updateKitStatus");
		Kit kit = null;
		try {
			if (kitNumber == null || kitNumber == "") {
				return new ResponseEntity<>(null, HttpStatus.OK);
			}
			kit = kitService.getByKitNumber(kitNumber);
			if (kit != null) {
				kit = kitService.validateKitRequest(null, kitNumber, new ArrayList<>(), null, status);
				return new ResponseEntity<>(
						dtoWrapper.quickConvertSalesOrderItemLineToSalesOrderItemLineDTO(kit.getItems()),
						HttpStatus.OK);
			} else {
				return new ResponseEntity<>(null, HttpStatus.OK);
			}
		} catch (Exception ex) {
			logger.error("Exception thrown while fetching kits by date range ");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**API End point to re-open a kit
	 * @param kitNumber Kit Number
	 * @param status Status
	 * @return List of kit items
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/reopenKit/{kitNumber}", method = RequestMethod.GET)
	public ResponseEntity<List<SalesOrderLineItemDTO>> reopenKit(@PathVariable String kitNumber) {
		logger.debug("Controller invoked on updateKitStatus");
		Kit kit = null;
		try {
			if (kitNumber == null || kitNumber == "") {
				return new ResponseEntity<>(null, HttpStatus.OK);
			}
			kit = kitService.reopenKit(kitNumber);
			if (kit != null) {
				return new ResponseEntity<>(
						dtoWrapper.quickConvertSalesOrderItemLineToSalesOrderItemLineDTO(kit.getItems()),
						HttpStatus.OK);
			} else {
				return new ResponseEntity<>(null, HttpStatus.OK);
			}
		} catch (Exception ex) {
			logger.error("Exception thrown while fetching kits by date range ");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**API End point to get list of item status
	 * @return List of item Status
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/item/status", method = RequestMethod.GET)
	public ResponseEntity<List<ItemStatusDTO>> getItemStatus() {
		logger.debug("Request to getting item status");
		try {
			List<ItemStatusDTO> itemStatusList = dtoWrapper
					.convertItemStatusEnumToItemStatusDTO(Arrays.asList(ItemStatus.values()));
			return new ResponseEntity<List<ItemStatusDTO>>(itemStatusList, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Exception thrown while getting item status" + ex.getMessage());
			return new ResponseEntity<List<ItemStatusDTO>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}