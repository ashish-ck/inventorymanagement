package com.drivojoy.inventory.rest.controllers;

import java.util.Date;
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
import com.drivojoy.inventory.dto.KitConsolidatedReport;
import com.drivojoy.inventory.dto.KitDTO;
import com.drivojoy.inventory.dto.OutOfStockKitItemsDTO;
import com.drivojoy.inventory.dto.RequestParamsDTO;
import com.drivojoy.inventory.dto.SalesConsolidatedReport;
import com.drivojoy.inventory.dto.SalesOrderLineItemDTO;
import com.drivojoy.inventory.helpers.UserAuthentication;
import com.drivojoy.inventory.models.Kit;
import com.drivojoy.inventory.models.Subkit;
import com.drivojoy.inventory.models.datatables.DTResult;
import com.drivojoy.inventory.services.IKitService;
import com.drivojoy.inventory.utils.Constants;
import com.drivojoy.inventory.utils.IDTOWrapper;

/**
 * 
 * @author ashishsingh
 *
 */
@RestController
@RequestMapping(value = "/api/kits")
public class KitsApiController {

	@Autowired
	private IKitService kitService;
	@Autowired
	private IDTOWrapper dtoWrapper;
	private final Logger logger = LoggerFactory.getLogger(KitsApiController.class);

	/**
	 * API End point to retrieve all open kits in paginated form
	 * 
	 * @param params
	 *            DataTables parameters
	 * @param user
	 *            Currently logged in user
	 * @return DTTable Kit
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/getOpenKits", method = RequestMethod.POST)
	public ResponseEntity<DTResult<KitDTO>> getOpenKits(@RequestBody RequestParamsDTO requestParam,
			@AuthenticationPrincipal UserAuthentication user) {
		logger.debug("Controller invoked to get open kits by pagination");
		DTResult<KitDTO> result = null;
		try {
			if (user.hasRole("ROLE_ADMIN")) {
				result = kitService.getOpenKits(requestParam.getParams(), requestParam.getFromDate(), requestParam.getToDate(),
						requestParam.getRequestWarehouseId(), requestParam.getAssignedWarehouseId(), requestParam.getStatus());
			} else {
				result = kitService.getOpenKits(requestParam.getParams(), requestParam.getFromDate(), requestParam.getToDate(),
						requestParam.getRequestWarehouseId(), requestParam.getAssignedWarehouseId(), requestParam.getStatus());
			}
			return new ResponseEntity<DTResult<KitDTO>>(result, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Exception thrown while getting kits pagination");
			return new ResponseEntity<DTResult<KitDTO>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * API End point to retrieve all closed kits in paginated form
	 * 
	 * @param params DataTables parameters
	 * @param user Currently logged in user
	 * @return DT KIt result
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/getClosedKits", method = RequestMethod.POST)
	public ResponseEntity<DTResult<KitDTO>> getClosedKits(@RequestBody RequestParamsDTO requestParam,
			@AuthenticationPrincipal UserAuthentication user) {
		logger.debug("Controller invoked to get closed kits by pagination");
		DTResult<KitDTO> result = null;
		try {
			if (user.hasRole("ROLE_ADMIN")) {
				result = kitService.getClosedKits(requestParam.getParams(), requestParam.getFromDate(), requestParam.getToDate(),
						requestParam.getRequestWarehouseId(), requestParam.getAssignedWarehouseId(), requestParam.getStatus());
			} else {
				result = kitService.getClosedKits(requestParam.getParams(), requestParam.getFromDate(), requestParam.getToDate(),
						requestParam.getRequestWarehouseId(), requestParam.getAssignedWarehouseId(), requestParam.getStatus());
			}
			return new ResponseEntity<DTResult<KitDTO>>(result, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Exception thrown while getting kits pagination");
			return new ResponseEntity<DTResult<KitDTO>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * API Endpoint to get return expected kits.
	 * 
	 * @param user Logged in user
	 * @return List of kits
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/getReturnExpectedKits", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<KitDTO>>> getReturnExpectedKits(
			@AuthenticationPrincipal UserAuthentication user) {
		logger.debug("Controller invoked to get closed kits by pagination");
		ApiResponse<List<KitDTO>> response = null;
		try {
			if (user.hasRole("ROLE_ADMIN")) {
				response = new ApiResponse<List<KitDTO>>(true, kitService.getReturnExpectedKits(),
						"Fetched successfully!");
			} else {
				response = new ApiResponse<List<KitDTO>>(true, kitService.getReturnExpectedKits(),
						"Fetched successfully!");
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Exception thrown while getting return expected kits");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * API End point to retrieve a kit by sales order number
	 * 
	 * @param kitNumber
	 *            Sales Order/Request Id of the kit
	 * @param user
	 *            Currently logged in user
	 * @return Kit
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/get/{kitNumber}", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<KitDTO>> getByKitNumber(@PathVariable String kitNumber,
			@AuthenticationPrincipal UserAuthentication user) {
		logger.debug("Controller invoked on getByKitNumber");
		try {
			Kit kit = kitService.getByKitNumberWithSuggestion(kitNumber);
			if (kit != null) {
				kit.updateKitStatus();
				if ((kit.getAssignedWarehouse().getCode().equals(user.getWarehouse())
						|| kit.getRequestWarehouse().getCode().equals(user.getWarehouse()))
						|| user.hasRole("ROLE_ADMIN")) {
					ApiResponse<KitDTO> response = new ApiResponse<>(true, dtoWrapper.quickConvertNew(kit),
							"Kit retreived successfully!");
					kit = kitService.saveKit(kit);
					return new ResponseEntity<>(response, HttpStatus.OK);
				} else {
					kit = kitService.saveKit(kit);
					return new ResponseEntity<>(HttpStatus.FORBIDDEN);
				}
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

		} catch (Exception ex) {
			logger.error("Exception thrown while fetching kit for kit number " + kitNumber + " : " + ex.getMessage());
			ApiResponse<KitDTO> response = new ApiResponse<>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	/**
	 * API End point to update the item statuses and count in kit
	 * 
	 * @param request
	 *            Details of items to be updated in a kit
	 * @param user
	 *            Currently logged in user
	 * @return Kit
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/updateKitItemDetails", method = RequestMethod.POST)
	public ResponseEntity<ApiResponse<KitDTO>> updateKitItemDetails(@RequestBody InventoryRequest request,
			@AuthenticationPrincipal UserAuthentication user) {
		logger.debug("Controller invoked on updateKitItemDetails :" + request.toString());
		try {
			Subkit subkit = kitService.getSubkitBySubkitNumber(request.getVoucherRef());
			Kit kit = kitService.getByKitNumber(subkit.getKitNumber());
			if (kit.getAssignedWarehouse().getCode().equals(user.getWarehouse()) || user.hasRole("ROLE_ADMIN")) {
				kit = kitService.updateKitItemDetails(request);
				ApiResponse<KitDTO> response = new ApiResponse<>(true, dtoWrapper.convert(kit),
						"Kit updated successfully!");
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else if ((kit.getAssignedWarehouse().getCode().equals(user.getWarehouse())
					|| kit.getRequestWarehouse().getCode().equals(user.getWarehouse()))
					&& user.hasRole("ROLE_INVENTORYMANAGER")) {
				kit = kitService.updateKitItemDetails(request);
				ApiResponse<KitDTO> response = new ApiResponse<>(true, dtoWrapper.convert(kit),
						"Kit updated successfully!");
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else if ((subkit.getLocation().getCode().equals(user.getWarehouse())
					|| kit.getRequestWarehouse().getCode().equals(user.getWarehouse()))
					&& user.hasRole("ROLE_HUBMANAGER")) {
				kit = kitService.updateKitItemDetails(request);
				ApiResponse<KitDTO> response = new ApiResponse<>(true, dtoWrapper.convert(kit),
						"Kit updated successfully!");
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}
		} catch (Exception ex) {
			logger.error("Exception thrown while updating kit for kit number " + request.getVoucherRef() + " : "
					+ ex.getMessage());
			ApiResponse<KitDTO> response = new ApiResponse<>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	/**
	 * API End point to retrieve consolidated data of kits within a date range
	 * 
	 * @param fromDate
	 *            From date
	 * @param toDate
	 *            To date
	 * @param warehouseId
	 *            Warehouse Id           
	 * @return Kit Report
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/getByDateRange", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<KitConsolidatedReport>> getByDateRange(@RequestParam("fromDate") Date fromDate,
			@RequestParam("toDate") Date toDate, @RequestParam("warehouseId") Long warehouseId) {
		logger.debug("Controller invoked on getByDateRange");
		try {
			KitConsolidatedReport report = kitService.getKitReport(fromDate, toDate, warehouseId);
			ApiResponse<KitConsolidatedReport> response = new ApiResponse<KitConsolidatedReport>(true, report,
					"Report retrieved successfully!");
			return new ResponseEntity<ApiResponse<KitConsolidatedReport>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Exception thrown while fetching kits by date range :" + ex.getCause());
			ApiResponse<KitConsolidatedReport> response = new ApiResponse<KitConsolidatedReport>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<KitConsolidatedReport>>(response, HttpStatus.OK);
		}
	}
	
	/**Get sales report by date range and warehouse id
	 * @param fromDate from date
	 * @param toDate to date
	 * @param warehouseId warehouse id
	 * @return Sales consolidated report.
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/getSalesReportByDateRange", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<SalesConsolidatedReport>> getSalesReportByDateRange(@RequestParam("fromDate") Date fromDate,
			@RequestParam("toDate") Date toDate, @RequestParam("warehouseId") Long warehouseId) {
		logger.debug("Controller invoked on getByDateRange");
		try {
			SalesConsolidatedReport report = kitService.getSalesReport(fromDate, toDate, warehouseId);
			ApiResponse<SalesConsolidatedReport> response = new ApiResponse<SalesConsolidatedReport>(true, report,
					"Report retrieved successfully!");
			return new ResponseEntity<ApiResponse<SalesConsolidatedReport>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Exception thrown while fetching kits by date range :" + ex.getCause());
			ApiResponse<SalesConsolidatedReport> response = new ApiResponse<SalesConsolidatedReport>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<SalesConsolidatedReport>>(response, HttpStatus.OK);
		}
	}

	/**
	 * API End point to retrieve list of out of stock kit items within a date
	 * range
	 * 
	 * @param fromDate
	 *            From date
	 * @param toDate
	 *            To date
	 * @param user
	 *            Currently logged in user
	 * @return List of Out of stock kit items
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/getOutOfStockItems", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<OutOfStockKitItemsDTO>>> getOutOfStockItems(
			@RequestParam("fromDate") Date fromDate, @RequestParam("toDate") Date toDate,
			@AuthenticationPrincipal UserAuthentication user) {
		logger.debug("Controller invoked on getOutOfStockItems");
		try {
			List<OutOfStockKitItemsDTO> items = null;
			if (user != null) {
				if (user.hasRole("ROLE_ADMIN")) {
					items = kitService.getOutOfStockItems(fromDate, toDate);
				} else {
					items = kitService.getOutOfStockItemsByWarehouse(fromDate, toDate, user.getWarehouse());
				}
				ApiResponse<List<OutOfStockKitItemsDTO>> response = new ApiResponse<List<OutOfStockKitItemsDTO>>(true,
						items, "Items fetched successfully!");
				return new ResponseEntity<ApiResponse<List<OutOfStockKitItemsDTO>>>(response, HttpStatus.OK);
			} else {
				return new ResponseEntity<ApiResponse<List<OutOfStockKitItemsDTO>>>(HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception ex) {
			logger.error("Exception thrown while fetching out of stock items :" + ex.getCause());
			ApiResponse<List<OutOfStockKitItemsDTO>> response = new ApiResponse<List<OutOfStockKitItemsDTO>>(false,
					null, Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<List<OutOfStockKitItemsDTO>>>(response, HttpStatus.OK);
		}
	}

	/**
	 * API End point to retrieve the status of a kit
	 * 
	 * @param kitNumber
	 *            Sales Order Number/Request Id of the kit
	 * @return Kit status
	 */
	@RequestMapping(value = "/getKitStatus", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<String>> getKitStatus(@RequestParam("kitNumber") String kitNumber) {
		logger.debug("Controller invoked on getKitStatus");
		try {
			if (kitNumber == null || kitNumber == "") {
				return new ResponseEntity<ApiResponse<String>>(
						new ApiResponse<String>(false, "Invalid Kit Number", "Invalid Kit Number"), HttpStatus.OK);
			}
			String status = kitService.getKitStatus(kitNumber);
			if (status == null || status == "") {
				return new ResponseEntity<ApiResponse<String>>(
						new ApiResponse<String>(true, "Not requested!", "Not requested!"), HttpStatus.OK);
			}
			return new ResponseEntity<ApiResponse<String>>(new ApiResponse<String>(true, status, null), HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Exception thrown while fetching kits by date range ");
			return new ResponseEntity<ApiResponse<String>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * API end point to change subkit location from inventory to hub.
	 * 
	 * @param request Request
	 * @param user logged in user
	 * @return Kit
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/changeSubkitLocation", method = RequestMethod.POST)
	public ResponseEntity<ApiResponse<Kit>> changeSubkitLocation(@RequestBody InventoryRequest request,
			@AuthenticationPrincipal UserAuthentication user) {
		logger.debug("Controller invoked on updateKitItemDetails :" + request.toString());
		try {
			Subkit subkit = kitService.getSubkitBySubkitNumber(request.getVoucherRef());
			Kit kit = kitService.getByKitNumber(subkit.getKitNumber());
			if (kit.getAssignedWarehouse().getCode().equals(user.getWarehouse()) || user.hasRole("ROLE_ADMIN")) {
				subkit = kitService.changeSubkitLocation(subkit);
				kit = kitService.getByKitNumber(subkit.getKitNumber());
				ApiResponse<Kit> response = new ApiResponse<Kit>(true, kit, "Kit updated successfully!");
				return new ResponseEntity<ApiResponse<Kit>>(response, HttpStatus.OK);
			} else {
				return new ResponseEntity<ApiResponse<Kit>>(HttpStatus.FORBIDDEN);
			}
		} catch (Exception ex) {
			logger.error("Exception thrown while updating kit for kit number " + request.getVoucherRef() + " : "
					+ ex.getMessage());
			ApiResponse<Kit> response = new ApiResponse<Kit>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<Kit>>(response, HttpStatus.OK);
		}
	}

	/**
	 * API End point to update the status of a kit
	 * 
	 * @param kitNumber
	 *            Sales Order Number/Request Id of the kit
	 * @return List of kit items
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/updateKitStatus", method = RequestMethod.GET)
	public ResponseEntity<List<SalesOrderLineItemDTO>> updateKitStatus(@RequestParam("kitNumber") String kitNumber) {
		logger.debug("Controller invoked on updateKitStatus");
		Kit kit = null;
		try {
			if (kitNumber == null || kitNumber == "") {
				return new ResponseEntity<>(null, HttpStatus.OK);
			}
			kit = kitService.getByKitNumber(kitNumber);
			if (kit != null) {
				kit.updateKitStatus();
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
}
