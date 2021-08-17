package com.drivojoy.inventory.servicesImpl;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.drivojoy.inventory.dto.ItemRequestPair;
import com.drivojoy.inventory.dto.OutOfStockKitItemsDTO;
import com.drivojoy.inventory.dto.PurchaseOrderConsolidatedReport;
import com.drivojoy.inventory.dto.PurchaseOrderDTO;
import com.drivojoy.inventory.dto.PurchaseOrderReportItem;
import com.drivojoy.inventory.dto.RequestParamsDTO;
import com.drivojoy.inventory.models.ItemDetails;
import com.drivojoy.inventory.models.ItemOrderLine;
import com.drivojoy.inventory.models.PurchaseOrder;
import com.drivojoy.inventory.models.datatables.DTParameters;
import com.drivojoy.inventory.models.datatables.DTResult;
import com.drivojoy.inventory.repositories.PurchaseOrderRepository;
import com.drivojoy.inventory.services.IDocumentSequencesService;
import com.drivojoy.inventory.services.IInventoryOperationsService;
import com.drivojoy.inventory.services.IInventoryTransactionService;
import com.drivojoy.inventory.services.IKitService;
import com.drivojoy.inventory.services.IPurchaseOrderService;
import com.drivojoy.inventory.utils.Constants;
import com.drivojoy.inventory.utils.Constants.ItemStatus;
import com.drivojoy.inventory.utils.IDTOWrapper;

@Component
public class PurchaseOrderServiceImpl implements IPurchaseOrderService {

	@Autowired
	private PurchaseOrderRepository orderRepository;
	@Autowired
	private IDTOWrapper dtoWrapper;
	@Autowired
	private IDocumentSequencesService documentService;
	@Autowired
	private IInventoryTransactionService inventoryTransactionService;
	@Autowired
	private IInventoryOperationsService inventoryOperationsService;
	@Autowired
	private IKitService kitService;
	@PersistenceContext
	private EntityManager em;

	private String _GROUP_BY = " GROUP BY id, amt_balance, amt_paid, contact_number, contact_person, created_dttm, date, invoice_date, invoice_no, last_mod_dttm, order_no, sub_total, taxes, total, version, shipping_warehouse, vendor, kit_number, vat_tax ";
	private String _GET_ALL_QUERY_WITH_SEARCH = "SELECT * FROM purchase_order LEFT JOIN order_status_changes ON order_status_changes.order_id = purchase_order.id WHERE 1 = 1 %s AND purchase_order.order_no LIKE \'%%%s%%\'  "
			+ _GROUP_BY + " ORDER BY %s %s LIMIT %d OFFSET %d";
	private String _GET_ALL_QUERY = "SELECT * FROM purchase_order LEFT JOIN order_status_changes ON order_status_changes.order_id = purchase_order.id WHERE 1 = 1 %s"
			+ _GROUP_BY + " ORDER BY %s %s LIMIT %d OFFSET %d";
	private String _GET_ALL_ADMIN_QUERY = "SELECT * FROM purchase_order LEFT JOIN order_status_changes ON order_status_changes.order_id = purchase_order.id "
			+ _GROUP_BY + "ORDER BY %s %s LIMIT %d OFFSET %d";
	private String _GET_ALL_ADMIN_SEARCH_QUERY = "SELECT * FROM purchase_order LEFT JOIN order_status_changes ON order_status_changes.order_id = purchase_order.id WHERE order_no LIKE \'%%%s%%\' "
			+ _GROUP_BY + "ORDER BY %s %s LIMIT %d OFFSET %d";
	private String _COUNT_ALL_QUERY_WITH_SEARCH = "SELECT COUNT(*) FROM purchase_order LEFT JOIN order_status_changes ON order_status_changes.order_id = purchase_order.id WHERE (1 = 1) %s AND order_no LIKE \'%%%s%%\'";
	private String _COUNT_ALL_QUERY = "SELECT COUNT(*) FROM purchase_order LEFT JOIN order_status_changes ON order_status_changes.order_id = purchase_order.id WHERE (1 = 1) %s";
	private String _COUNT_ALL_ADMIN_QUERY = "SELECT COUNT(*) FROM purchase_order";
	private String _COUNT_ALL_ADMIN_SEARCH_QUERY = "SELECT COUNT(*) FROM purchase_order WHERE (order_no LIKE \'%%%s%%\')";
	private final int _ORDER_NUMBER_COL = 0;
	private final int _ORDER_DATE_COL = 1;
	private final int _ORDER_STATUS_COL = 2;
	private final int _ORDER_VENDOR_COL = 3;
	private final int _ORDER_TOTAL_COL = 4;
	private final int _ORDER_PAID_COL = 5;

	private final Logger logger = LoggerFactory.getLogger(PurchaseOrderServiceImpl.class);

	@Override
	public PurchaseOrderDTO createPurchaseOrder(PurchaseOrderDTO purchaseOrderDTO) {
		logger.debug("Create Purchase Order service invoked");
		PurchaseOrder order = dtoWrapper.convert(purchaseOrderDTO);
		// order.setCreatedDttm(Calendar.getInstance().getTime());
		order.setOrderNo(documentService.getNextSequence(Constants._PURCHASE_ORDER, Constants._PURCHASE_ORDER_PREFIX));
		calculateOrderStatus(order);
		try {
			// We need to do three things here
			// First is save the order itself
			order = orderRepository.save(order);
			if (order != null) {
				// Second is to increment the sequence for respective document
				documentService.incrementSequence(Constants._PURCHASE_ORDER, Constants._PURCHASE_ORDER_PREFIX);
				// third is to check if vendor has a reference of items in his
				// list of items sold
				// if not then add a reference
				//List<String> listOfItems = new ArrayList<>();
				//for (ItemOrderLine item : order.getItems()) {
				//	listOfItems.add(item.getItemCode());
				//}
				//vendorService.checkAndAddItem(purchaseOrderDTO.getVendor().getId(), listOfItems);
			} else {
				throw new RuntimeException(Constants._GENERIC_ERROR_MESSAGE);
			}
			em.flush();
			return dtoWrapper.quickConvert(order);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception thrown while creating purchase order " + ex.getMessage());
			throw new RuntimeException(Constants._GENERIC_ERROR_MESSAGE);
		}
	}

	@Override
	public PurchaseOrderDTO updatePurchaseOrder(PurchaseOrderDTO purchaseOrderDTO) {
		PurchaseOrder order = dtoWrapper.convert(purchaseOrderDTO);
		try {
			calculateOrderStatus(order);
			order.setLastModDttm(Calendar.getInstance().getTime());
			order = orderRepository.save(order);
			return dtoWrapper.convert(order);
		} catch (Exception ex) {
			logger.error("Exception thrown while updating purchase order " + ex.getMessage());
			throw new RuntimeException(Constants._GENERIC_ERROR_MESSAGE);
		}
	}

	@Override
	public List<PurchaseOrderDTO> getPurchaseOrders() {
		return dtoWrapper.quickConvertToPurchaseOrderDTO(orderRepository.findAll());
	}

	@Override
	public PurchaseOrder getOrderById(long id) {
		return orderRepository.findOne(id);
	}

	@Override
	public PurchaseOrderDTO getOrderDTOByOrderNo(String orderNo) {
		PurchaseOrder order = getOrderByOrderNo(orderNo);
		return dtoWrapper.quickConvert(order);
	}

	@Override
	public PurchaseOrderDTO receiveOrderItems(PurchaseOrderDTO purchaseOrderDTO) {
		try {
			PurchaseOrder order =  inventoryOperationsService.fulfillPurchaseOrder(purchaseOrderDTO);
			Map<String, String> updatedItemCodes = new HashMap<>();
			for (ItemRequestPair request : purchaseOrderDTO.getItemDetails()) {
				if(!updatedItemCodes.containsKey(request.getItemCode()))
					updatedItemCodes.put(request.getItemCode(), request.getItemCode());
			}
			calculateOrderStatus(order);
			order = orderRepository.save(order);
			for (String itemCode : updatedItemCodes.keySet()) {
				kitService.updateKitItemsToOutOfStockAndAvailable(itemCode, purchaseOrderDTO.getShippingWarehouse().getId());
			}
			inventoryTransactionService.stockInventory(order.getDate(), 
					(List<ItemRequestPair>)purchaseOrderDTO.getItemDetails(), order.getShippingWarehouse(), ItemStatus.AVAILABLE.value(), ItemStatus.AVAILABLE.text(),
					Constants._PURCHASE_ORDER_FULFILLMENT, order.getOrderNo());
			return purchaseOrderDTO;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception thrown while receiving order items: " + ex.getMessage());
			throw new RuntimeException(Constants._GENERIC_ERROR_MESSAGE);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public DTResult<PurchaseOrderDTO> getPurchaseOrdersPaginated(RequestParamsDTO requestParams) {
		Long requestWarehouseId = requestParams.getRequestWarehouseId();
		DTParameters params = requestParams.getParams();
		Date fromDate = requestParams.getFromDate();
		Date toDate = requestParams.getToDate();
		Map<Integer, String> statusMap = new HashMap<>();
		statusMap.put(1, "\'Open\'");
		statusMap.put(2, "\'Received\'");
		statusMap.put(5, "\'Invoiced\'");
		statusMap.put(6, "\'Partially Paid\'");
		statusMap.put(7, "\'Paid\'");
		statusMap.put(11, "\'Fulfilled\'");
		DTResult<PurchaseOrderDTO> result = new DTResult<>();
		String orderBy = "", sortOrder = "";
		if (params.order.get(0).Dir != null) {
			sortOrder = (params.order.get(0).Dir.toString()).toUpperCase();
		} else {
			sortOrder = "DESC";
		}
		switch (params.order.get(0).Column) {
		case _ORDER_NUMBER_COL:
			orderBy = "purchase_order.order_no";
			break;
		case _ORDER_VENDOR_COL:
			orderBy = "purchase_order.vendor";
			break;
		case _ORDER_STATUS_COL:
			orderBy = "order_status_changes.status";
			break;
		case _ORDER_TOTAL_COL:
			orderBy = "purchase_order.total";
			break;
		case _ORDER_PAID_COL:
			orderBy = "purchase_order.amt_paid";
			break;
		case _ORDER_DATE_COL:
		default:
			orderBy = "date";
			break;
		}
		result.draw = params.draw;
		String q = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		if(requestParams.getStatus() != null){
			q += " AND order_status_changes.status = " + statusMap.get(requestParams.getStatus());
			for (Integer key : statusMap.keySet()){
				if(key != requestParams.getStatus()){
					q += " AND order_status_changes.status <> " + statusMap.get(key);
				}
			}
		}
		if(fromDate != null)
			q += " AND date >= \'" + format.format(fromDate) + "\'";
		if(toDate != null)
			q += " AND date <= \'" + format.format(toDate) + "\'";
		if(requestWarehouseId != null)
			q += " AND shipping_warehouse = " + requestWarehouseId;
		String countQuery = "", searchQuery = "";
		if (params.search.value != null && !params.search.value.isEmpty()) {
			countQuery = String.format(_COUNT_ALL_QUERY_WITH_SEARCH, q, params.search.value);
			searchQuery = String.format(_GET_ALL_QUERY_WITH_SEARCH, q, params.search.value, orderBy,
					sortOrder, params.length, params.start);
		} else {
			countQuery = String.format(_COUNT_ALL_QUERY, q);
			searchQuery = String.format(_GET_ALL_QUERY, q, orderBy, sortOrder, params.length,
					params.start);
		}
		try {
			Query nativeCountQuery = em.createNativeQuery(countQuery);
			result.recordsTotal = ((BigInteger) nativeCountQuery.getSingleResult()).intValue();
			result.recordsFiltered = result.recordsTotal;
			Query nativeQuery = em.createNativeQuery(searchQuery, PurchaseOrder.class);
			List<PurchaseOrder> resultSetFull = new ArrayList<>();
			resultSetFull = nativeQuery.getResultList();
			try {
				result.data = dtoWrapper.quickConvertToPurchaseOrderDTO(resultSetFull);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		} catch (Exception ex) {
			logger.error("Exception thrown while executing query : " + ex.getMessage());
			throw new RuntimeException(ex.getMessage());
		}
	}

	@Override
	public PurchaseOrderDTO autoCreatePurchaseOrder(List<OutOfStockKitItemsDTO> items) {
		logger.debug("Auto Create Purchase Order service invoked");
		return null;
	}

	// Fixed
	@Override
	public PurchaseOrderDTO returnItems(PurchaseOrderDTO purchaseOrderDTO) {
		try {
			inventoryOperationsService.fulfillPurchaseOrderReturn(purchaseOrderDTO.getShippingDate(),
					purchaseOrderDTO.getOrderNo(), (List<ItemRequestPair>) purchaseOrderDTO.getItemDetails(),
					purchaseOrderDTO.getShippingWarehouse().getName());
			PurchaseOrder order = dtoWrapper.convert(purchaseOrderDTO);
			calculateOrderStatus(order);
			order.setLastModDttm(Calendar.getInstance().getTime());
			order = orderRepository.saveAndFlush(order);
			return dtoWrapper.convert(order);
		} catch (Exception ex) {
			logger.error("Exception thrown while receiving order items: " + ex.getMessage());
			throw new RuntimeException(Constants._GENERIC_ERROR_MESSAGE);
		}
	}

	/* PRIVATE METHODS */

	private PurchaseOrder getOrderByOrderNo(String orderNo) {
		return orderRepository.findByOrderNo(orderNo);
	}

	private void calculateOrderStatus(PurchaseOrder order) {
		// need to implement rules to set the purchase order status
		// Criterias to be tested, status is empty, add open to it.
		if (order.getStatus() == null) {
			order.setStatus(new ArrayList<>());
		}
		logger.debug("Status: " + order.getStatus().toString() + " Size: " + order.getStatus().size());
		if (order.getStatus().size() == 0) {
			order.getStatus().add("Open");
		}
		// Check the delivery status
		if (!order.getStatus().contains("Received") && order.getItemDetails().size() != 0) {
			double requestedQty = 0, quantityReceived = 0;
			requestedQty = order.getItems().size();
			quantityReceived = order.getItemDetails().size();
			if (requestedQty == quantityReceived) {
				order.getStatus().remove("Partially Received");
				order.getStatus().add("Received");
			} else {
				if (!order.getStatus().contains("Partially Received") && quantityReceived > 0) {
					order.getStatus().add("Partially Received");
				}
			}
		}
		// Check if the order is having invoiceNo, if so, it has to be invoiced!
		if (!order.getStatus().contains("Invoiced")
				&& !(order.getInvoiceNo() == null || order.getInvoiceNo().isEmpty())) {
			order.getStatus().add("Invoiced");
		}
		// Check if the order is paid for or not
		if (!order.getStatus().contains("Paid") && order.getAmtPaid() > 0) {
			if (order.getAmtBalance() == 0) {
				order.getStatus().remove("Partially Paid");
				order.getStatus().add("Paid");
			} else {
				if (!order.getStatus().contains("Partially Paid")) {
					order.getStatus().add("Partially Paid");
				}
			}
		}
		// lastly check if invoice is received, invoiced and paid. If so, remove
		// all status and mark as fulfilled
		if (order.getStatus().contains("Received") && order.getStatus().contains("Invoiced")
				&& order.getStatus().contains("Paid")) {
			order.getStatus().clear();
			order.getStatus().add("Fulfilled");
		}
	}

	@Override
	public List<PurchaseOrderDTO> getOrderDTOByKitNumber(String kitNumber) {
		return dtoWrapper.convertToPurchaseOrderDTONew(orderRepository.findByKitNumber(kitNumber));
	}

	@Override
	public ItemOrderLine getOrderItemByBarcode(String barcode) {
		return dtoWrapper.convertObjectToItemOrderLine(orderRepository.findOrderItemByBarcode(barcode));
	}

	@Override
	public PurchaseOrderConsolidatedReport getPurchaseOrderReport(Date fromDate, Date toDate, Long warehouseId) {
		List<PurchaseOrder> listOfPurchaseOrders = null;
		if(warehouseId == null)
			listOfPurchaseOrders = orderRepository.findByDateRange(fromDate, toDate);
		else
			listOfPurchaseOrders = orderRepository.getByDateRangeAndWarehouseId(fromDate, toDate, warehouseId);
		PurchaseOrderConsolidatedReport report = new PurchaseOrderConsolidatedReport();
		report.setToDate(toDate);
		report.setFromDate(fromDate);
		for (PurchaseOrder order : listOfPurchaseOrders) {
			double totalWholesalePrice, totalUnitPrice;
			totalWholesalePrice = totalUnitPrice = 0.0;
			List<ItemRequestPair> items = new ArrayList<>();
			for (ItemDetails lineItem : order.getItemDetails()) {
				items.add(dtoWrapper.convert(lineItem));
				totalWholesalePrice += lineItem.getWholesalePrice();
				totalUnitPrice += lineItem.getUnitPrice();
			}
			List<String> statusus = (List<String>)order.getStatus();
			String status = statusus.get(statusus.size() - 1);
			PurchaseOrderReportItem purchaseOrderReportItem = new PurchaseOrderReportItem(order.getOrderNo(), order.getKitNumber(), order.getShippingWarehouse().getName(), order.getVendor().getName(),
					items, totalWholesalePrice, totalUnitPrice, order.getItemDetails().size(), order.getVatTax(), status, order.getAmtBalance());
			report.getOrders().add(purchaseOrderReportItem);
			report.setTotalQuantityDelivered(report.getTotalQuantityDelivered() + order.getItemDetails().size());
			report.setTotalUnitPrice(report.getTotalUnitPrice() + totalUnitPrice);
			report.setTotalWholesalePrice(report.getTotalWholesalePrice() + totalWholesalePrice);
			report.setVatTax(report.getVatTax() + order.getVatTax());
		}
		return report;
	}
}
