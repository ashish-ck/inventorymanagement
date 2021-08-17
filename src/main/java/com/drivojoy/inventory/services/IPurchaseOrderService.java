package com.drivojoy.inventory.services;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.drivojoy.inventory.dto.OutOfStockKitItemsDTO;
import com.drivojoy.inventory.dto.PurchaseOrderConsolidatedReport;
import com.drivojoy.inventory.dto.PurchaseOrderDTO;
import com.drivojoy.inventory.dto.RequestParamsDTO;
import com.drivojoy.inventory.models.ItemOrderLine;
import com.drivojoy.inventory.models.PurchaseOrder;
import com.drivojoy.inventory.models.datatables.DTResult;

/**
 * 
 * @author ashishsingh
 *
 */
@Service
public interface IPurchaseOrderService {

	/**
	 * Creates a new purchase order
	 * @param purchaseOrderDTO Purchase order
	 * @return Purchase order
	 */
	@Transactional(readOnly = false, rollbackFor=Exception.class, propagation = Propagation.REQUIRED, isolation=Isolation.READ_UNCOMMITTED)
	PurchaseOrderDTO createPurchaseOrder(PurchaseOrderDTO purchaseOrderDTO);
	
	/**
	 * Auto-creates a new purchase order from out of stock items
	 * @param items List of out of stock items
	 * @return Purchase order
	 */
	@Transactional(readOnly = false, rollbackFor=Exception.class, propagation = Propagation.REQUIRED, isolation=Isolation.READ_UNCOMMITTED)
	PurchaseOrderDTO autoCreatePurchaseOrder(List<OutOfStockKitItemsDTO> items);
	
	/**
	 * Updates an existing purchase order
	 * @param purchaseOrderDTO Purchase order
	 * @return Purchase order
	 */
	@Transactional(readOnly = false, rollbackFor=Exception.class, propagation = Propagation.REQUIRED, isolation=Isolation.READ_UNCOMMITTED)
	PurchaseOrderDTO updatePurchaseOrder(PurchaseOrderDTO purchaseOrderDTO);
	
	/**
	 * Gets a list of all purchase orders
	 * @return List of purchase order
	 */
	List<PurchaseOrderDTO> getPurchaseOrders();
	
	/**
	 * Pagination service to find all purchase orders
	 * @param params DT params
	 * @param warehouseRef Warehouse code
	 * @return DT result of purchase orders
	 */
	DTResult<PurchaseOrderDTO> getPurchaseOrdersPaginated(RequestParamsDTO requestParams);
	
	/**
	 * Finds a purchase order by id
	 * @param id PO id
	 * @return Purchase order
	 */
	PurchaseOrder getOrderById(long id);
	
	/**
	 * Finds a purchase order by order number
	 * @param orderNo Order no
	 * @return Purchase order
	 */
	PurchaseOrderDTO getOrderDTOByOrderNo(String orderNo);
	
	/**
	 * Receives items for a purchase order
	 * @param purchaseOrderDTO Purchase order
	 * @return Purchase order
	 */
	@Transactional(readOnly = false, rollbackFor=Exception.class, propagation = Propagation.REQUIRED, isolation=Isolation.READ_UNCOMMITTED)
	PurchaseOrderDTO receiveOrderItems(PurchaseOrderDTO purchaseOrderDTO);
	
	/**
	 * Returns items for a purchase order
	 * @param purchaseOrderDTO Purchase order
	 * @return Purchase order
	 */
	@Transactional(readOnly = false, rollbackFor=Exception.class, propagation = Propagation.REQUIRED, isolation=Isolation.READ_UNCOMMITTED)
	PurchaseOrderDTO returnItems(PurchaseOrderDTO purchaseOrderDTO);

	/**Finds purchase order by kit number
	 * @param kitNumber Kit number
	 * @return List of purchase orders
	 */
	List<PurchaseOrderDTO> getOrderDTOByKitNumber(String kitNumber);
	
	/**Get purchase order item by barcode
	 * @param barcode Barcode
	 * @return Purchase order item
	 */
	ItemOrderLine getOrderItemByBarcode(String barcode);

	/**Consolidated purchase order report
	 * @param fromDate From date
	 * @param toDate To date
	 * @param warehouseId Warehouse id
	 * @return Purchase order consolidated report
	 */
	PurchaseOrderConsolidatedReport getPurchaseOrderReport(Date fromDate, Date toDate, Long warehouseId);
}
