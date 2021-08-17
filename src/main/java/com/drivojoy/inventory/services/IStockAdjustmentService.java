package com.drivojoy.inventory.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.drivojoy.inventory.models.StockAdjustment;
import com.drivojoy.inventory.models.datatables.DTParameters;
import com.drivojoy.inventory.models.datatables.DTResult;

/**
 * 
 * @author ashishsingh
 *
 */
@Service
@Transactional
public interface IStockAdjustmentService {

	/**
	 * Gets all stock adjustments
	 * @return List of stock adjustments
	 */
	List<StockAdjustment> getAll();
	
	/**
	 * Pagination service to get all stock adjustments by warehouse
	 * @param params DT params
	 * @param warehouse Warehouse
	 * @return DT result of stock adjustments
	 */
	DTResult<StockAdjustment> getAllAdjustmentsPaginated(DTParameters params, String warehouse);
	
	/**
	 * Saves a stock adjustment order
	 * @param order Stock Adjustment order
	 * @return Stock Adjustment
	 */
	StockAdjustment save(StockAdjustment order);
	
	/**
	 * Finds a stock adjustment by order number
	 * @param orderNumber Stock Adjustment order number
	 * @return Stock Adjustment
	 */
	StockAdjustment getOrderByNumber(String orderNumber);
}
