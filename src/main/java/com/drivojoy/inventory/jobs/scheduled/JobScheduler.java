package com.drivojoy.inventory.jobs.scheduled;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.drivojoy.inventory.models.InventoryBalances;
import com.drivojoy.inventory.models.Item;
import com.drivojoy.inventory.models.ItemCount;
import com.drivojoy.inventory.services.IInventoryBalancesService;
import com.drivojoy.inventory.services.IItemService;
import com.drivojoy.inventory.utils.Constants.ItemStatus;

/**
 * This is the background job scheduler file
 * 
 * @author ashishsingh
 * 
 */

@Component
public class JobScheduler {
	@Autowired
	private IItemService itemService;
	@Autowired
	private IInventoryBalancesService inventoryBalanceService;
	private final int _PAGE_SIZE = 10;
	private Logger logger = LoggerFactory.getLogger(JobScheduler.class);
	/**
	 * Every night 11pm this function records the opening and closing balances for all items.
	 * 
	 */
	@Scheduled(cron="0 0 23 * * *")
	@Transactional
	public void recordOpeningClosingBalances(){
		logger.debug("Scheduled Job: Recording inventory opening/closing balances!");
		try{
			Calendar cal = Calendar.getInstance();
			Date today = cal.getTime();
			cal.add(Calendar.DATE, 1);
			Date tomorrow = cal.getTime();
			int pageNo = 1;
			Page<Item> listOfItems = null;
			do{
				PageRequest page = new PageRequest(pageNo++, _PAGE_SIZE);
				listOfItems = itemService.getAll(page);
				Iterator<Item> ir = listOfItems.iterator();
				while(ir.hasNext()){
					Item item = ir.next();
					for(ItemCount itemCount : item.getItemCount()){
						//update todays closing balance
						InventoryBalances closingBalance = inventoryBalanceService.findByItemAndWarehouseAndDate(item, itemCount.getWarehouse(), today);
						if(closingBalance != null){
							closingBalance.setClosingBalance(itemCount.getStatus());
							inventoryBalanceService.save(closingBalance);
						}else{
							/*
							(long id, Date date, Item item, int openingStatus, String openingStatusText,
									int closingStatus, String closingStatusText, Date entryDttm, int version, Date lastModDttm,
									Warehouse warehouse)
							*/
							closingBalance = new InventoryBalances(0, today, item, itemCount.getStatus(), ItemStatus.getStatus(itemCount.getStatus()).text(),
									itemCount.getStatus(), ItemStatus.getStatus(itemCount.getStatus()).text(), null, 0, null, itemCount.getWarehouse());
							inventoryBalanceService.save(closingBalance);
						}
						//update tomorrows opening balance
						InventoryBalances openingBalance = new InventoryBalances(0, tomorrow, item, itemCount.getStatus(), ItemStatus.getStatus(itemCount.getStatus()).text(),
								itemCount.getStatus(), ItemStatus.getStatus(itemCount.getStatus()).text(), null, 0, null, itemCount.getWarehouse());
						inventoryBalanceService.save(openingBalance);
					}
				}
			}
			while(!listOfItems.isLast());
		}catch(Exception ex){
			logger.error("Exception thrown while updating opening/closing balances "+ex.getMessage());
		}		
	}

}
