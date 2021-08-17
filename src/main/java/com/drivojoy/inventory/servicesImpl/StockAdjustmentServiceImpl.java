package com.drivojoy.inventory.servicesImpl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.drivojoy.inventory.dto.ItemDTO;
import com.drivojoy.inventory.dto.ItemRequestPair;
import com.drivojoy.inventory.models.Item;
import com.drivojoy.inventory.models.ItemDetails;
import com.drivojoy.inventory.models.SalesOrderItemLine;
import com.drivojoy.inventory.models.StockAdjustment;
import com.drivojoy.inventory.models.Warehouse;
import com.drivojoy.inventory.models.datatables.DTParameters;
import com.drivojoy.inventory.models.datatables.DTResult;
import com.drivojoy.inventory.repositories.StockAdjustmentRepository;
import com.drivojoy.inventory.services.IDocumentSequencesService;
import com.drivojoy.inventory.services.IInventoryOperationsService;
import com.drivojoy.inventory.services.IItemService;
import com.drivojoy.inventory.services.IKitService;
import com.drivojoy.inventory.services.IStockAdjustmentService;
import com.drivojoy.inventory.services.IWarehouseService;
import com.drivojoy.inventory.utils.Constants;
import com.drivojoy.inventory.utils.Constants.ItemStatus;
import com.drivojoy.inventory.utils.IDTOWrapper;

@Component
public class StockAdjustmentServiceImpl implements IStockAdjustmentService{
	@Autowired
	private StockAdjustmentRepository stockAdjustmentRepository;
	@Autowired
	private IInventoryOperationsService inventoryService;
	@Autowired
	private IDocumentSequencesService documentService;
	@Autowired
	private IDTOWrapper dtoWrapperService;
	@Autowired
	private IItemService itemService;
	private IWarehouseService warehouseService;
	@Autowired
 	private IKitService kitService; 
	@PersistenceContext
	private EntityManager em;
	private String _GET_ALL_QUERY_WITH_SEARCH = "SELECT * FROM stock_adjustment WHERE warehouse = %d AND order_number LIKE \'%%%s%%\' ORDER BY %s %s LIMIT %d OFFSET %d";
	private String _GET_ALL_QUERY = "SELECT * FROM stock_adjustment WHERE warehouse = %d ORDER BY %s %s LIMIT %d OFFSET %d";
	private String _GET_ALL_ADMIN_QUERY = "SELECT * FROM stock_adjustment ORDER BY %s %s LIMIT %d OFFSET %d";
	private String _GET_ALL_ADMIN_SEARCH_QUERY = "SELECT * FROM stock_adjustment WHERE order_number LIKE \'%%%s%%\' ORDER BY %s %s LIMIT %d OFFSET %d";
	private String _COUNT_ALL_QUERY_WITH_SEARCH = "SELECT COUNT(*) FROM stock_adjustment WHERE (warehouse = %d) AND order_number LIKE %s";
	private String _COUNT_ALL_QUERY = "SELECT COUNT(*) FROM stock_adjustment WHERE (warehouse = %d)";
	private String _COUNT_ALL_ADMIN_QUERY = "SELECT COUNT(*) FROM stock_adjustment";
	private String _COUNT_ALL_ADMIN_SEARCH_QUERY = "SELECT COUNT(*) FROM stock_adjustment WHERE (order_number LIKE \'%%%s%%\')";
	private final int _ORDER_NUMBER_COL = 0;
	private final int _ORDER_DATE_COL = 1;
	private final int _ORDER_WAREHOUSE_COL = 2;
	private final Logger logger = LoggerFactory.getLogger(StockAdjustmentServiceImpl.class);
	@Override
	public List<StockAdjustment> getAll() {
		logger.debug("Fetching all stock adjustments");
		return stockAdjustmentRepository.findAll();
	}

	// Fixed
	@Override
	public StockAdjustment save(StockAdjustment order) {
		HashMap<String, ItemRequestPair> uniqueItems = new HashMap<>();
		List<ItemRequestPair> itemList = new ArrayList<>();
		order.setOrderNumber(documentService.getNextSequence(Constants._STOCK_ADJUSTMENT, Constants._STOCK_ADJUSTMENT_PREFIX));
		order.setStatus(Constants._STATUS_FULFILLED);
		order = stockAdjustmentRepository.saveAndFlush(order);
		for(SalesOrderItemLine request : order.getItems()){
			double quantity = 1;
			itemList.add(new ItemRequestPair(order.getWarehouse().getName(), request.getItemCode(), request.getBarcode(), 
					Calendar.getInstance().getTime(), null, null, 0, request.getBarcode(), 
					ItemStatus.AVAILABLE.value(), ItemStatus.AVAILABLE.text(), null, null, 0, 0));
			uniqueItems.put(request.getBarcode(), new ItemRequestPair(order.getWarehouse().getName(), request.getItemCode(), request.getBarcode(), 
					Calendar.getInstance().getTime(), null, null, 0, request.getBarcode(), 
					ItemStatus.AVAILABLE.value(), ItemStatus.AVAILABLE.text(), null, null, 0, 0));
			Item item = itemService.getItemByCode(request.getItemCode());
			ItemDTO itemDTO = dtoWrapperService.convert(item);
			if(itemDTO != null){
				if(!item.isSerialized()){
					itemService.updateItemDetails(request.getBarcode(), order.getWarehouse(), request.getStatus());
				}else{
					boolean isPresent = false;
					for(ItemRequestPair pair : itemDTO.getItemDetails()){
						if(pair.getBarcode().equals(request.getBarcode())){
							isPresent = true;
							itemService.updateItemDetails(request.getBarcode(), order.getWarehouse(), request.getStatus());
							break;
						}
					}
					if(!isPresent){
						if(quantity < 0){
							throw new RuntimeException("Item "+request.getBarcode()+" was not found!");
						}
						// Fixed
						item.getItemDetails().add(new ItemDetails(request.getBarcode(), Calendar.getInstance().getTime(), null, null, 0, 
								request.getBarcode(), 0, order.getWarehouse(), request.getItemCode(), 
								ItemStatus.AVAILABLE.value(), null, null, 0, 0));
						itemService.updateItem(item);
					}
				}
			}else{
				// Fixed
				item.getItemDetails().add(new ItemDetails(request.getBarcode(), Calendar.getInstance().getTime(), null, null, 0, request.getBarcode(), 
						0, order.getWarehouse(), request.getItemCode(), ItemStatus.AVAILABLE.value(), null, null, 0, 0));
				itemService.updateItem(item);
			}
		}
		kitService.updateKitItemStatus(itemList);
		inventoryService.fulfillStockAdjustment(uniqueItems, order.getWarehouse(), order.getOrderNumber());
		return order;
	}

	@Override
	public StockAdjustment getOrderByNumber(String orderNumber) {
		return stockAdjustmentRepository.findByOrderNumber(orderNumber);
	}

	@SuppressWarnings("unchecked")
	@Override
	public DTResult<StockAdjustment> getAllAdjustmentsPaginated(DTParameters params, String warehouseRef) {
		DTResult<StockAdjustment> result = new DTResult<>();
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
		 case _ORDER_WAREHOUSE_COL: orderBy = "warehouse"; break; 
		 case _ORDER_DATE_COL:
	     default: orderBy = "order_date"; break;
		}
		if(warehouse != null){
			//NOT AN ADMIN, show only specific warehouse details
			if(params.search.value != null && !params.search.value.isEmpty() ){
				countQuery = String.format(_COUNT_ALL_QUERY_WITH_SEARCH, warehouse.getId(), params.search.value);
				searchQuery = String.format(_GET_ALL_QUERY_WITH_SEARCH, warehouse.getId(), params.search.value, orderBy, sortOrder, params.length, params.start);
			}else{
				countQuery = String.format(_COUNT_ALL_QUERY, warehouse.getId());
				searchQuery = String.format(_GET_ALL_QUERY, warehouse.getId(), orderBy, sortOrder, params.length, params.start);
			}
		}else{
			//ADMIN view, use admin queries
			if(params.search.value != null && !params.search.value.isEmpty() ){
				countQuery = String.format(_COUNT_ALL_ADMIN_SEARCH_QUERY, params.search.value);
				searchQuery = String.format(_GET_ALL_ADMIN_SEARCH_QUERY, params.search.value, orderBy, sortOrder, params.length, params.start);
			}else{
				countQuery = String.format(_COUNT_ALL_ADMIN_QUERY);
				searchQuery = String.format(_GET_ALL_ADMIN_QUERY, orderBy, sortOrder, params.length, params.start);
			}
		}
		try{
			Query nativeCountQuery = em.createNativeQuery(countQuery);
			result.recordsTotal = ((BigInteger) nativeCountQuery.getSingleResult()).intValue();
			result.recordsFiltered = result.recordsTotal;		

			Query nativeQuery = em.createNativeQuery(searchQuery, StockAdjustment.class);
			List<StockAdjustment> resultSetFull = new ArrayList<>();
			resultSetFull = nativeQuery.getResultList();
			result.data = resultSetFull;
			return result;
		}catch(Exception ex){
			logger.error("Exception thrown while executing query : "+ex.getMessage());
			throw new RuntimeException(ex.getMessage());
		}
	}

	
	
}
