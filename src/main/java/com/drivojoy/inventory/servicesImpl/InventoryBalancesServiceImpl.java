package com.drivojoy.inventory.servicesImpl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.drivojoy.inventory.models.InventoryBalances;
import com.drivojoy.inventory.models.Item;
import com.drivojoy.inventory.models.Warehouse;
import com.drivojoy.inventory.repositories.InventoryBalancesRepository;
import com.drivojoy.inventory.services.IInventoryBalancesService;

@Component
public class InventoryBalancesServiceImpl implements IInventoryBalancesService{

	@Autowired
	private InventoryBalancesRepository inventoryBalancesRepository;
	
	private Logger logger = LoggerFactory.getLogger(InventoryBalancesServiceImpl.class);

	@Override
	public InventoryBalances findByItemAndWarehouseAndDate(Item item, Warehouse warehouse, Date date) {
		logger.debug("Fetching inventory balance for Item "+item.getCode()+" and warehouse "+warehouse.getName()+" on date "+date.toString());
		return inventoryBalancesRepository.findByItemAndWarehouseAndDate(item, warehouse, date);
	}

	@Override
	public InventoryBalances save(InventoryBalances balance) {
		logger.debug("Saving inventory balance : "+balance);
		return inventoryBalancesRepository.save(balance);
	}
	
	
}
