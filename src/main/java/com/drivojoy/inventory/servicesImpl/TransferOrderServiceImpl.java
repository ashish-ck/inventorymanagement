package com.drivojoy.inventory.servicesImpl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.drivojoy.inventory.dto.ItemOrderLineDTO;
import com.drivojoy.inventory.dto.ItemRequestPair;
import com.drivojoy.inventory.dto.TransferOrderDTO;
import com.drivojoy.inventory.models.Item;
import com.drivojoy.inventory.models.Kit;
import com.drivojoy.inventory.models.TransferOrder;
import com.drivojoy.inventory.models.Warehouse;
import com.drivojoy.inventory.models.datatables.DTParameters;
import com.drivojoy.inventory.models.datatables.DTResult;
import com.drivojoy.inventory.repositories.TransferOrderRepository;
import com.drivojoy.inventory.services.IDocumentSequencesService;
import com.drivojoy.inventory.services.IInventoryOperationsService;
import com.drivojoy.inventory.services.IItemService;
import com.drivojoy.inventory.services.IKitService;
import com.drivojoy.inventory.services.ITransferOrderService;
import com.drivojoy.inventory.services.IWarehouseService;
import com.drivojoy.inventory.utils.Constants;
import com.drivojoy.inventory.utils.Constants.ItemStatus;
import com.drivojoy.inventory.utils.Constants.KitStatus;
import com.drivojoy.inventory.utils.IDTOWrapper;

@Component
public class TransferOrderServiceImpl implements ITransferOrderService{
	@Autowired
	private TransferOrderRepository transferOrderRepository;
	@Autowired
	private IInventoryOperationsService inventoryService;
	@Autowired
	private IDocumentSequencesService documentService;
	@Autowired
	private IKitService kitService;
	@Autowired
	private IDTOWrapper dtoWrapper;
	@Autowired
	private IItemService itemService;
	@Autowired
	private IWarehouseService warehouseService;
	private String _GET_ALL_QUERY_WITH_SEARCH = "SELECT * FROM transfer_order WHERE (from_warehouse = %d OR to_warehouse = %d) AND (kit_number LIKE %s OR order_number LIKE \'%%%s%%\') ORDER BY %s %s LIMIT %d OFFSET %d";
	private String _GET_ALL_QUERY = "SELECT * FROM transfer_order WHERE (from_warehouse = %d OR to_warehouse = %d) ORDER BY %s %s LIMIT %d OFFSET %d";
	private String _GET_ALL_ADMIN_QUERY = "SELECT * FROM transfer_order ORDER BY %s %s LIMIT %d OFFSET %d";
	private String _GET_ALL_ADMIN_SEARCH_QUERY = "SELECT * FROM transfer_order WHERE (kit_number LIKE %s OR order_number LIKE \'%%%s%%\') ORDER BY %s %s LIMIT %d OFFSET %d";
	private String _COUNT_ALL_QUERY_WITH_SEARCH = "SELECT COUNT(*) FROM transfer_order WHERE (from_warehouse = %d OR to_warehouse = %d) AND (kit_number LIKE %s OR order_number LIKE \'%%%s%%\')";
	private String _COUNT_ALL_QUERY = "SELECT COUNT(*) FROM transfer_order WHERE (from_warehouse = %d OR to_warehouse = %d)";
	private String _COUNT_ALL_ADMIN_QUERY = "SELECT COUNT(*) FROM transfer_order";
	private String _COUNT_ALL_ADMIN_SEARCH_QUERY = "SELECT COUNT(*) FROM transfer_order WHERE (kit_number LIKE %s OR order_number LIKE \'%%%s%%\')";
	private final int _ORDER_NUMBER_COL = 0;
	private final int _ORDER_DATE_COL = 1;
	private final int _ORDER_STATUS_COL = 2;
	private final int _ORDER_FROM_WAREHOUSE_COL = 3;
	private final int _ORDER_TO_WAREHOUSE_COL = 4;
	@PersistenceContext
	private EntityManager em;
	private final Logger logger = LoggerFactory.getLogger(TransferOrderServiceImpl.class);
	
	@Override
	public List<TransferOrderDTO> getAllOrders(){
		logger.debug("Fetching all transfer orders till date");
		return dtoWrapper.convertToTransferOrderDTO(transferOrderRepository.findAll());
	}
	
	@Override
	public TransferOrderDTO createTransferOrderFromKit(List<ItemRequestPair> requests, String toWorkshop, String kitNumber){
		HashMap<String, ItemRequestPair> uniqueItems = new HashMap<>();
		Collection<ItemOrderLineDTO> items = new ArrayList<>();
		String selectedWarehouse = requests.get(0).getWarehouse();
		for(ItemRequestPair request: requests){
			uniqueItems.put(request.getBarcode(), request);
		}
		for(String barcode: uniqueItems.keySet()){
			Item currentItem = itemService.getItemByCode(uniqueItems.get(barcode).getItemCode());
			ItemOrderLineDTO item = new ItemOrderLineDTO(currentItem.getCode(), null, currentItem.getUnitOfMeasurement().getNotation(), 0, 0, currentItem.getDescription(), currentItem.isSerialized(), barcode);
			items.add(item);
		}
		TransferOrderDTO order = new TransferOrderDTO(0, null, Calendar.getInstance().getTime(), Constants._STATUS_OPEN, null, dtoWrapper.convert(warehouseService.getWarehouseByName(selectedWarehouse)), dtoWrapper.convert(warehouseService.getWarehouseByName(toWorkshop)), 0, items, null, null, kitNumber, null);
		order = saveOrder(order);
		order.setItemDetails(requests);
		order.setSendDate(Calendar.getInstance().getTime());
		order = sendItems(order);
		return order;
	}
	
	@Override
	public TransferOrderDTO saveOrder(TransferOrderDTO orderDTO) {
		if(orderDTO.getStatus() > Constants._STATUS_OPEN){
			logger.error("The transfer order already has been dispatched/delivered, cannot update it!");
			throw new RuntimeException("The transfer order already has been dispatched/delivered, cannot update it!");
		}
		/*Adding the logic to check if only correct items from a kit are transferrable
		if(orderDTO.getKitNumber() != null){
			Kit existingKit = kitService.getByKitNumber(orderDTO.getKitNumber());
			for(ItemOrderLineDTO item : orderDTO.getItems()){
				if(existingKit != null){
					//String existingStatus = existingKit.getLineItem(item.getItemCode()).getCurrentStatusText();
					if(KitStatus.CLOSED.isEquals(existingKit.getStatus()) && 
							!ItemStatus.AVAILABLE.isEquals(existingKit.getLineItem(item.getItemCode()).getStatus())){
						throw new RuntimeException("Bad Request!");
					}
				}
			}
		}*/
		for (ItemOrderLineDTO itemOrderLineDTO : orderDTO.getItems()){
			Item item = itemService.getItemByCode(itemOrderLineDTO.getItemCode());
			itemOrderLineDTO.setUnit(item.getUnitOfMeasurement().getNotation());
		}
		TransferOrder order = dtoWrapper.convert(orderDTO);
		try{
			if(order.getOrderNumber() == null || order.getOrderNumber().equals("")){
				order.setOrderNumber(documentService.getNextSequence(Constants._STOCK_TRANSFER, Constants._STOCK_TRANSFER_PREFIX));
				order.setStatus(Constants._STATUS_OPEN);
			}
			order = transferOrderRepository.save(order);
			if(order != null)
				documentService.incrementSequence(Constants._STOCK_TRANSFER, Constants._STOCK_TRANSFER_PREFIX);
			return dtoWrapper.convert(order);
		}catch(OptimisticLockException ex){
			logger.error("This is not the recent version of transfer order being updated!");
			throw new RuntimeException(ex.getMessage());
		}catch(Exception ex){
			logger.error("Exception thrown while updating transfer order!");
			throw new RuntimeException(Constants._GENERIC_ERROR_MESSAGE);
		}
	}
	
	@Override
	public TransferOrderDTO sendTransferOrderItems(TransferOrderDTO orderDTO){
		if(transferOrderRepository.findOne(orderDTO.getId()) == null){
			throw new RuntimeException("Invalid order!");
		}
		List<ItemRequestPair> listOfItems = new ArrayList<>();
		for(ItemRequestPair item : orderDTO.getItemDetails()){
			item.setWarehouse(orderDTO.getFromWarehouse().getName());
			item.setStatus(ItemStatus.AVAILABLE.value());
			// TODO item.setQuantityDelivered(item.getQuantityRequested());
			// item.setStatus(ItemStatus.ISSUED.value());
			listOfItems.add(item);
		}
		orderDTO.setItemDetails(listOfItems);
		//first call inventory operations and get the items removed from selected warehouse
		inventoryService.fulfillStockTransferSend(orderDTO.getSendDate(), orderDTO.getOrderNumber(), listOfItems, 
				orderDTO.getFromWarehouse().getName());		
		//once that is done set the order status to sent and update it
		TransferOrder order = dtoWrapper.convert(orderDTO);
		order.setStatus(Constants._DISPATCHED);		
		order = transferOrderRepository.saveAndFlush(order);
		return dtoWrapper.convert(order);
	}
	
	@Override
	public TransferOrderDTO sendItems(TransferOrderDTO orderDTO){
		if(transferOrderRepository.findOne(orderDTO.getId()) == null){
			throw new RuntimeException("Invalid order!");
		}
		Kit existingKit = null;
		if(orderDTO.getKitNumber() != null){
			existingKit = kitService.getByKitNumber(orderDTO.getKitNumber());
		}
		List<ItemRequestPair> listOfItems = new ArrayList<>();
		for(ItemRequestPair item : orderDTO.getItemDetails()){
			item.setWarehouse(orderDTO.getFromWarehouse().getName());
			if(existingKit != null){
				if(KitStatus.CLOSED.isEquals(existingKit.getStatus()) && 
						!ItemStatus.AVAILABLE.isEquals(existingKit.getLineItem(item.getItemCode()).getStatus())){
					throw new RuntimeException("Bad Request!");
				}else{
					item.setStatus(existingKit.getLineItem(item.getItemCode()).getStatus());
				}
			}else{
				item.setStatus(ItemStatus.AVAILABLE.value());
			}
			// TODO item.setQuantityDelivered(item.getQuantityRequested());
			item.setStatus(ItemStatus.ISSUED.value());
			listOfItems.add(item);
		}
		orderDTO.setItemDetails(listOfItems);
		//first call inventory operations and get the items removed from selected warehouse
		inventoryService.fulfillStockTransferSend(orderDTO.getSendDate(), orderDTO.getOrderNumber(), listOfItems, 
				orderDTO.getFromWarehouse().getName());
		//once that is done set the order status to sent and update it
		TransferOrder order = dtoWrapper.convert(orderDTO);
		order.setStatus(Constants._DISPATCHED);		
		order = transferOrderRepository.saveAndFlush(order);
		if(order.getKitNumber() != null){
			kitService.updateKitItemLocation(order.getKitNumber(), order.getToWarehouse().getName(), Constants._IN_TRANSIT, listOfItems);
		}
		return dtoWrapper.convert(order);
	}

	@Override
	public TransferOrderDTO receiveTransferOrderItems(TransferOrderDTO orderDTO) {
		if(transferOrderRepository.findOne(orderDTO.getId()) == null){
			throw new RuntimeException("Invalid order!");
		}
		//Warehouse warehouse = dtoWrapper.convert(orderDTO.getToWarehouse());
		List<ItemRequestPair> listOfItems = new ArrayList<>();		
		for(ItemRequestPair item : orderDTO.getRecvdItemDetails()){
			item.setWarehouse(orderDTO.getToWarehouse().getName());
			// TODO item.setQuantityDelivered(item.getQuantityRequested());
			// TODO item.setStatus(Constants._ISSUED_TEXT);
			//item.setStatus(ItemStatus.ISSUED.value());
			item.setStatus(ItemStatus.AVAILABLE.value());
			listOfItems.add(item);
			itemService.updateItemCountWarehouseLocation(item.getBarcode(), orderDTO.getToWarehouse().getId());
			//itemService.updateItemDetailsCountLocationStatus(item.getBarcode(), warehouse, Constants._AVAILABLE, item.getQuantityDelivered());
		}
		//Simply call the receive transfer from inventory operations
		inventoryService.fulfillStockTransferReceive(orderDTO.getReceiveDate(), orderDTO.getOrderNumber(), listOfItems, orderDTO.getToWarehouse().getName());
		//mark status as fulfilled
		for (ItemRequestPair item : orderDTO.getRecvdItemDetails()){
			kitService.updateKitItemsToOutOfStockAndAvailable(item.getItemCode(), orderDTO.getFromWarehouse().getId());
			kitService.updateKitItemsToOutOfStockAndAvailable(item.getItemCode(), orderDTO.getToWarehouse().getId());
		}
		TransferOrder order = dtoWrapper.convert(orderDTO);
		order.setStatus(Constants._STATUS_FULFILLED);
		order = transferOrderRepository.save(order);
		em.flush();
		return dtoWrapper.convert(order);
	}
	
	@Override
	public TransferOrderDTO receiveItems(TransferOrderDTO orderDTO) {
		if(transferOrderRepository.findOne(orderDTO.getId()) == null){
			throw new RuntimeException("Invalid order!");
		}
		//Warehouse warehouse = dtoWrapper.convert(orderDTO.getToWarehouse());
		List<ItemRequestPair> listOfItems = new ArrayList<>();		
		for(ItemRequestPair item : orderDTO.getRecvdItemDetails()){
			item.setWarehouse(orderDTO.getToWarehouse().getName());
			// TODO item.setQuantityDelivered(item.getQuantityRequested());
			// TODO item.setStatus(Constants._ISSUED_TEXT);
			item.setStatus(ItemStatus.ISSUED.value());
			listOfItems.add(item);
			//itemService.updateItemDetailsCountLocationStatus(item.getBarcode(), warehouse, Constants._AVAILABLE, item.getQuantityDelivered());
		}
		//Simply call the receive transfer from inventory operations
		inventoryService.fulfillStockTransferReceive(orderDTO.getReceiveDate(), orderDTO.getOrderNumber(), listOfItems, orderDTO.getToWarehouse().getName());
		//mark status as fulfilled
		TransferOrder order = dtoWrapper.convert(orderDTO);
		order.setStatus(Constants._STATUS_FULFILLED);
		order = transferOrderRepository.save(order);
		em.flush();
		if(order.getKitNumber() != null){
			for(ItemRequestPair item : listOfItems){
				// TODO item.setStatus(Constants._AVAILABLE_TEXT);
				item.setStatus(ItemStatus.AVAILABLE.value());
			}
			if(KitStatus.CLOSED.isEquals(kitService.getByKitNumber(order.getKitNumber()).getStatus())){
				kitService.updateKitItemLocation(order.getKitNumber(), order.getToWarehouse().getName(), ItemStatus.AVAILABLE.value(), listOfItems);
			}else{
				inventoryService.reserveItemsForOrder(orderDTO.getReceiveDate(), orderDTO.getKitNumber(), listOfItems, orderDTO.getToWarehouse().getName());
				kitService.updateKitItemLocation(order.getKitNumber(), order.getToWarehouse().getName(),  ItemStatus.RESERVED.value(), listOfItems);
			}
		}else{
			kitService.updateKitItemStatus(listOfItems);
		}
		return dtoWrapper.convert(order);
	}

	@Override
	public TransferOrderDTO getOrderByNumber(String orderNumber) {
		logger.debug("Fetching transfer order with order number : "+orderNumber);
		if(orderNumber != null)
			return dtoWrapper.convert(transferOrderRepository.findByOrderNumber(orderNumber));
		throw new RuntimeException("Order Number required!");
	}
	
	@Override
	public List<TransferOrderDTO> getOrdersByWarehouse(String warehouse) {
		Warehouse warehouseObj = warehouseService.getWarehouseByCode(warehouse);
		return dtoWrapper.convertToTransferOrderDTO(transferOrderRepository.findByFromWarehouseOrToWarehouse(warehouseObj, warehouseObj));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public DTResult<TransferOrderDTO> getAllOrdersPaginated(DTParameters params, String warehouseRef) {
		DTResult<TransferOrderDTO> result = new DTResult<>();
		Warehouse warehouse = null;
		if(warehouseRef != null){
			warehouse = warehouseService.getWarehouseByCode(warehouseRef);
		}
		
		result.draw = params.draw;
		String countQuery = "", searchQuery = "";
		String orderBy = "", sortOrder = "";
		if(params.order.get(0).Dir != null){
			sortOrder  = (params.order.get(0).Dir.toString()).toUpperCase();
		}else{
			sortOrder = "DESC";
		}
		switch(params.order.get(0).Column){
		 case _ORDER_NUMBER_COL: orderBy = "order_number"; break;
		 case _ORDER_FROM_WAREHOUSE_COL: orderBy = "from_warehouse"; break;
		 case _ORDER_TO_WAREHOUSE_COL: orderBy = "to_warehouse"; break;
		 case _ORDER_STATUS_COL: orderBy = "status"; break; 
		 case _ORDER_DATE_COL:
	     default: orderBy = "order_date"; break;
		}
		if(warehouse != null){
			//NOT AN ADMIN, show only specific warehouse details
			if(params.search.value != null && !params.search.value.isEmpty() ){
				countQuery = String.format(_COUNT_ALL_QUERY_WITH_SEARCH, warehouse.getId(), warehouse.getId(), params.search.value, params.search.value);
				searchQuery = String.format(_GET_ALL_QUERY_WITH_SEARCH, warehouse.getId(), warehouse.getId(), params.search.value, params.search.value, orderBy, sortOrder, params.length, params.start);
			}else{
				countQuery = String.format(_COUNT_ALL_QUERY, warehouse.getId(), warehouse.getId());
				searchQuery = String.format(_GET_ALL_QUERY, warehouse.getId(), warehouse.getId(), orderBy, sortOrder, params.length, params.start);
			}
		}else{
			//ADMIN view, use admin queries
			if(params.search.value != null && !params.search.value.isEmpty() ){
				countQuery = String.format(_COUNT_ALL_ADMIN_SEARCH_QUERY,params.search.value, params.search.value);
				searchQuery = String.format(_GET_ALL_ADMIN_SEARCH_QUERY, params.search.value, params.search.value, orderBy, sortOrder, params.length, params.start);
			}else{
				countQuery = String.format(_COUNT_ALL_ADMIN_QUERY);
				searchQuery = String.format(_GET_ALL_ADMIN_QUERY, orderBy, sortOrder, params.length, params.start);
			}
		}
		try{
			Query nativeCountQuery = em.createNativeQuery(countQuery);
			result.recordsTotal = ((BigInteger) nativeCountQuery.getSingleResult()).intValue();
			result.recordsFiltered = result.recordsTotal;		
			Query nativeQuery = em.createNativeQuery(searchQuery, TransferOrder.class);
			List<TransferOrder> resultSetFull = new ArrayList<>();
			resultSetFull = nativeQuery.getResultList();			
			List<TransferOrderDTO> resultSet = dtoWrapper.convertToTransferOrderDTO(resultSetFull);
			result.data = resultSet;
			return result;
		}catch(Exception ex){
			logger.error("Exception thrown while executing query : "+ex.getMessage());
			throw new RuntimeException(ex.getMessage());
		}
		
	}
	
	
}
