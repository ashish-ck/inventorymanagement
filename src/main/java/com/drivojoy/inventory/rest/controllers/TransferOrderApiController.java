package com.drivojoy.inventory.rest.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.drivojoy.inventory.dto.ApiResponse;
import com.drivojoy.inventory.dto.InventoryRequest;
import com.drivojoy.inventory.dto.TransferOrderDTO;
import com.drivojoy.inventory.helpers.UserAuthentication;
import com.drivojoy.inventory.models.datatables.DTParameters;
import com.drivojoy.inventory.models.datatables.DTResult;
import com.drivojoy.inventory.services.ITransferOrderService;
import com.drivojoy.inventory.utils.Constants;

/**
 * 
 * @author ashishsingh
 *
 */
@RestController
@RequestMapping("/api/transferOrders")
public class TransferOrderApiController {
	@Autowired
	private ITransferOrderService transferOrderService;

	private final Logger logger = LoggerFactory.getLogger(TransferOrderApiController.class);

	/**API End point to get all transfer orders in paginated form
	 * @param params DTTable params
	 * @param user Logged in user
	 * @return TransferOrderDTO
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/getTransferOrdersPaginated", method = RequestMethod.POST)
	public ResponseEntity<DTResult<TransferOrderDTO>> getTransferOrdersPaginated(@RequestBody DTParameters params,
			@AuthenticationPrincipal UserAuthentication user) {
		logger.debug("fetch all PO api invoked");
		try {
			if (user.hasRole("ROLE_ADMIN")) {
				return new ResponseEntity<DTResult<TransferOrderDTO>>(
						transferOrderService.getAllOrdersPaginated(params, null), HttpStatus.OK);
			} else {
				return new ResponseEntity<DTResult<TransferOrderDTO>>(
						transferOrderService.getAllOrdersPaginated(params, user.getWarehouse()), HttpStatus.OK);
			}
		} catch (Exception ex) {
			return new ResponseEntity<DTResult<TransferOrderDTO>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**API End point to find a transfer order by order number
	 * @param orderNumber Order number
	 * @param user logged in user
	 * @return Transfer order
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/get/{orderNumber}", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<TransferOrderDTO>> getOrderByNumber(@PathVariable String orderNumber,
			@AuthenticationPrincipal UserAuthentication user) {
		TransferOrderDTO orderDTO;
		try {

			orderDTO = transferOrderService.getOrderByNumber(orderNumber);

			if (orderDTO.getFromWarehouse().getCode().equals(user.getWarehouse())
					|| orderDTO.getToWarehouse().getCode().equals(user.getWarehouse()) || user.hasRole("ROLE_ADMIN")) {
				ApiResponse<TransferOrderDTO> response = new ApiResponse<TransferOrderDTO>(true, orderDTO,
						"Order Found!");
				return new ResponseEntity<ApiResponse<TransferOrderDTO>>(response, HttpStatus.OK);
			} else {
				return new ResponseEntity<ApiResponse<TransferOrderDTO>>(HttpStatus.FORBIDDEN);
			}

		} catch (Exception ex) {
			logger.error("Exception thrown while fetching transfer order : " + ex.getCause());
			ApiResponse<TransferOrderDTO> response = new ApiResponse<TransferOrderDTO>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<TransferOrderDTO>>(response, HttpStatus.OK);
		}
	}

	/**API End point to save a transfer order
	 * @param orderDTO Transfer Order
	 * @return Transfer Order
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ResponseEntity<ApiResponse<TransferOrderDTO>> saveOrder(@RequestBody TransferOrderDTO orderDTO) {
		logger.debug("Saving order : " + orderDTO.toString());
		try {
			orderDTO = transferOrderService.saveOrder(orderDTO);
			ApiResponse<TransferOrderDTO> response = new ApiResponse<TransferOrderDTO>(true, orderDTO,
					"Order Saved Successfully!");
			return new ResponseEntity<ApiResponse<TransferOrderDTO>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Exception thrown while saving transfer order : " + ex.getCause());
			ApiResponse<TransferOrderDTO> response = new ApiResponse<TransferOrderDTO>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<TransferOrderDTO>>(response, HttpStatus.OK);
		}
	}

	/**API End point to send transfer order items from source warehouse
	 * @param orderDTO Transfer Order
	 * @return Transfer Order
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/send", method = RequestMethod.POST)
	public ResponseEntity<ApiResponse<TransferOrderDTO>> sendItems(@RequestBody TransferOrderDTO orderDTO) {
		logger.debug("Sending items for order : " + orderDTO.toString());
		try {
			orderDTO = transferOrderService.sendTransferOrderItems(orderDTO);
			ApiResponse<TransferOrderDTO> response = new ApiResponse<TransferOrderDTO>(true, orderDTO,
					"Items Sent Successfully!");
			return new ResponseEntity<ApiResponse<TransferOrderDTO>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Exception thrown while sending items for transfer order : " + ex.getCause());
			ApiResponse<TransferOrderDTO> response = new ApiResponse<TransferOrderDTO>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<TransferOrderDTO>>(response, HttpStatus.OK);
		}
	}

	/**API End point to receive transfer order items to destination warehouse
	 * @param orderDTO Transfer Order
	 * @return Transfer Order
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/receive", method = RequestMethod.POST)
	public ResponseEntity<ApiResponse<TransferOrderDTO>> receiveItems(@RequestBody TransferOrderDTO orderDTO) {
		logger.debug("Receiving items for order : " + orderDTO.toString());
		try {
			orderDTO = transferOrderService.receiveTransferOrderItems(orderDTO);
			ApiResponse<TransferOrderDTO> response = new ApiResponse<TransferOrderDTO>(true, orderDTO,
					"Items Received Successfully!");
			return new ResponseEntity<ApiResponse<TransferOrderDTO>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Exception thrown while receiving items for transfer order : " + ex.getCause());
			ApiResponse<TransferOrderDTO> response = new ApiResponse<TransferOrderDTO>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<TransferOrderDTO>>(response, HttpStatus.OK);
		}
	}

	/**
	 * API End point to auto create transfer order from Kit Items
	 * 
	 * @param request Request
	 * @return Transfer Order
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/autoCreate", method = RequestMethod.POST)
	public ResponseEntity<ApiResponse<TransferOrderDTO>> autoCreate(@RequestBody InventoryRequest request) {
		logger.debug("Auto-creating ST for request : " + request.toString());
		try {
			TransferOrderDTO orderDTO;
			orderDTO = transferOrderService.createTransferOrderFromKit(request.getListOfItems(),
					request.getWorkshopRef(), request.getVoucherRef());
			ApiResponse<TransferOrderDTO> response = new ApiResponse<TransferOrderDTO>(true, orderDTO,
					"Order created Successfully!");
			return new ResponseEntity<ApiResponse<TransferOrderDTO>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Exception thrown while auto creating transfer order : " + ex.getCause());
			ApiResponse<TransferOrderDTO> response = new ApiResponse<TransferOrderDTO>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<TransferOrderDTO>>(response, HttpStatus.OK);
		}
	}
}
