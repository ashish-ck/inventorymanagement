package com.drivojoy.inventory.servicesImpl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.drivojoy.inventory.dto.ItemRequestPair;
import com.drivojoy.inventory.models.InventoryTransaction;
import com.drivojoy.inventory.models.Warehouse;
import com.drivojoy.inventory.repositories.InventoryTransactionRepository;
import com.drivojoy.inventory.services.IDocumentSequencesService;
import com.drivojoy.inventory.services.IInventoryTransactionService;
import com.drivojoy.inventory.utils.Constants;

@Component
public class InventoryTransactionServiceImpl implements IInventoryTransactionService {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private IDocumentSequencesService docSeqService;
	@Autowired
	private InventoryTransactionRepository inventoryTransactionRepository;
	private final Logger logger = LoggerFactory.getLogger(InventoryTransactionServiceImpl.class);
	@Override
	public InventoryTransaction recordTransaction(Date transactionDate, List<ItemRequestPair> request, 
			Warehouse warehouse,  int status, String statusText, String transactionType, 
			String voucherReference) {
		return recordTransaction(transactionDate, request, warehouse, status, statusText, 
				transactionType, voucherReference, 1);
	}

	@Override
	public InventoryTransaction recordTransaction(Date transactionDate, List<ItemRequestPair> request, Warehouse warehouse, 
			 int status, String statusText, String transactionType, String voucherReference, int version) {
		logger.debug("Record Transaction service invoked");
		InventoryTransaction transaction = new InventoryTransaction();
		transaction.setTransactionType(transactionType);
		transaction.setTransactionDate(transactionDate);
		transaction.setStatus(status);
		transaction.setStatusText(statusText);
		transaction.setVoucherReference(voucherReference);
		transaction.setStatus(status);
		transaction.setStatusText(statusText);
		transaction.setEntryDttm(Calendar.getInstance().getTime());
		transaction.setWarehouse(warehouse);
		transaction.setEmail(null);
		transaction = saveTransaction(transaction);
		if(transaction != null){
			try{
				docSeqService.incrementSequence(transaction.getTransactionType(), 
						transaction.getVoucherReference().split("-")[0]);
			}catch(Exception ex){
				logger.error("Exception thrown while incrementing sequence : "+transaction);
				return null;
			}
			//item = itemService.updateItemCountAndItemDetails(transaction.getWarehouse(), status, item);
		}
		//if(item != null)
			return transaction;
		//else
			//return null;
	}

	@Override
	public InventoryTransaction issueInventory(Date transactionDate, List<ItemRequestPair> requests, Warehouse warehouse,
			 int status, String statusText, String transactionType, String voucherReference) {
		InventoryTransaction transaction = new InventoryTransaction();
		transaction.setTransactionType(transactionType);
		transaction.setTransactionDate(transactionDate);
		transaction.setStatus(status);
		transaction.setStatusText(statusText);
		transaction.setVoucherReference(voucherReference);
		transaction.setEntryDttm(Calendar.getInstance().getTime());
		transaction.setWarehouse(warehouse);
		for(ItemRequestPair request : requests){
			transaction.getItem().add(request.getItemCode() + ", " + request.getBarcode());
		}
		transaction = saveTransaction(transaction);
		return transaction;
	}

	@Override
	public InventoryTransaction stockInventory(Date transactionDate, List<ItemRequestPair> request, Warehouse warehouse,
			 int status, String statusText, String transactionType, String voucherReference) {
		InventoryTransaction transaction = new InventoryTransaction();
		transaction.setTransactionType(transactionType);
		transaction.setTransactionDate(transactionDate);
		transaction.setStatus(status);
		transaction.setStatusText(statusText);
		transaction.setVoucherReference(voucherReference);
		transaction.setEntryDttm(Calendar.getInstance().getTime());
		transaction.setWarehouse(warehouse);
		transaction = saveTransaction(transaction);
		if(transaction != null && (transactionType.equals(Constants._PURCHASE_ORDER_FULFILLMENT) || transactionType.equals(Constants._STOCK_TRANSFER_RECEIVE))){
			em.flush();
		}
		return transaction;
	}


	@Override
	public List<InventoryTransaction> getTransactionsByOrder(String voucherRef, String transactionType) {
		return inventoryTransactionRepository.findByVoucherReferenceAndTransactionType(voucherRef, transactionType);
	}

	@Override
	public HashMap<String, String> getItemsIssuedForSalesOrder(String voucherRef) {
		HashMap<String, String> itemList = new HashMap<>();
		return itemList;
	}
	
	/* private methods */

	private InventoryTransaction saveTransaction(InventoryTransaction transaction){
		try{
			logger.debug("Saving transaction : "+transaction.toString());
			transaction.setLastModDttm(Calendar.getInstance().getTime());
			InventoryTransaction savedTransaction  = null;
			savedTransaction = inventoryTransactionRepository.save(transaction);
			//em.flush();
			return savedTransaction;
		}catch(Exception ex){
			logger.error("Exception thrown while saving transaction : "+transaction);
			throw new RuntimeException(Constants._GENERIC_ERROR_MESSAGE);
		}
	}

	@Override
	public InventoryTransaction recordTransaction(InventoryTransaction transaction) {
		transaction = saveTransaction(transaction);
		if(transaction != null){
			try{
				docSeqService.incrementSequence(transaction.getTransactionType(), 
						transaction.getVoucherReference().split("-")[0]);
			}catch(Exception ex){
				logger.error("Exception thrown while incrementing sequence : "+transaction);
				return null;
			}
		}
		return transaction;
	}
}
