package com.drivojoy.inventory.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.drivojoy.inventory.dto.ItemRequestPair;
import com.drivojoy.inventory.dto.TransferOrderDTO;
import com.drivojoy.inventory.models.datatables.DTParameters;
import com.drivojoy.inventory.models.datatables.DTResult;

/**
 * 
 * @author ashishsingh
 *
 */
@Service
@Transactional
public interface ITransferOrderService {

	/**
	 * Saves a transfer order
	 * @param order Transfer order
	 * @return Transfer order
	 */
	TransferOrderDTO saveOrder(TransferOrderDTO order);

	/**
	 * Performs send operation for a particular transfer order
	 * @param orderDTO Transfer order
	 * @return Transfer order
	 */
	TransferOrderDTO sendItems(TransferOrderDTO orderDTO);

	/**
	 * Performs receive operation for a particular transfer order
	 * @param orderDTO Transfer order
	 * @return Transfer order
	 */
	TransferOrderDTO receiveItems(TransferOrderDTO orderDTO);
	
	/**
	 * Finds a transfer order by order number
	 * @param orderNumber Transfer order number
	 * @return Transfer order
	 */
	TransferOrderDTO getOrderByNumber(String orderNumber);

	/**
	 * Fetches all transfer orders
	 * @return List of transfer orders
	 */
	List<TransferOrderDTO> getAllOrders();
	
	/**
	 * Pagination service to find all transfer orders by warehouse
	 * @param params DT params
	 * @param wwarehouseRef Warehouse code
	 * @return DT result of transfer orders
	 */
	DTResult<TransferOrderDTO> getAllOrdersPaginated(DTParameters params, String wwarehouseRef);
	
	/**
	 * Finds transfer orders for a particular warehouse
	 * @param warehouse Warehouse
	 * @return List of transfer orders
	 */
	List<TransferOrderDTO> getOrdersByWarehouse(String warehouse);

	/**
	 * Auto-create transfer order from a kit
	 * @param requests List of item request pairs
	 * @param toWorkshop To Warehouse
	 * @param kitNumber Kit number
	 * @return Transfer order
	 */
	/**
	 * @param requests List of item request pairs
	 * @param toWorkshop To workshop
	 * @param kitNumber Kit number
	 * @return Transfer order
	 */
	TransferOrderDTO createTransferOrderFromKit(List<ItemRequestPair> requests, String toWorkshop, String kitNumber);
	
	/**
	 * @param orderDTO Transfer order
	 * @return Transfer order
	 */
	TransferOrderDTO sendTransferOrderItems(TransferOrderDTO orderDTO);
	
	/**
	 * @param orderDTO Transfer order
	 * @return Transfer order
	 */
	TransferOrderDTO receiveTransferOrderItems(TransferOrderDTO orderDTO);
}
