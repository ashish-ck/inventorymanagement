package com.drivojoy.inventory.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.drivojoy.inventory.dto.ItemRequestPair;
import com.drivojoy.inventory.models.InventoryTransaction;
import com.drivojoy.inventory.models.Warehouse;

/**
 * 
 * @author ashishsingh
 *
 */
@Service
@Transactional
public interface IInventoryTransactionService {

	/**Records an inventory transaction
	 * @param transactionDate transaction date
	 * @param request item request pairs
	 * @param warehouse warehouse
	 * @param status status
	 * @param statusText status text
	 * @param transactionType Transaction type
	 * @param voucherReference voucher ref
	 * @return InventoryTransaction
	 */
	public InventoryTransaction recordTransaction(Date transactionDate, List<ItemRequestPair> request, Warehouse warehouse, int status,
			String statusText, String transactionType, String voucherReference);
	
	/**Records an inventory transaction
	 * @param transaction transaction
	 * @return InventoryTransaction
	 */
	public InventoryTransaction recordTransaction(InventoryTransaction transaction);
	
	/**Records an inventory transaction
	 * @param transactionDate Transaction date
	 * @param request Item request pairs
	 * @param warehouse Warehouse
	 * @param status Status
	 * @param statusText Status text
	 * @param transactionType Transaction type
	 * @param voucherReference Voucher ref
	 * @param version Version
	 * @return InventoryTransaction
	 */
	public InventoryTransaction recordTransaction(Date transactionDate, List<ItemRequestPair> request, Warehouse warehouse, int status,
			String statusText, String transactionType, String voucherReference, int version);

	/**Records an inventory transaction
	 * @param transactionDate transaction date
	 * @param request Item request pairs
	 * @param warehouse warehouse
	 * @param status status
	 * @param statusText status text
	 * @param transactionType transaction type
	 * @param voucherReference voucher ref
	 * @return InventoryTransaction
	 */
	public InventoryTransaction issueInventory(Date transactionDate, List<ItemRequestPair> request, Warehouse warehouse, 
			int status, String statusText, String transactionType, String voucherReference);
	
	/**Records an inventory transaction
	 * @param transactionDate transaction date
	 * @param request Item Request pairs
	 * @param warehouse warehouse
	 * @param status status 
	 * @param statusText status text
	 * @param transactionType transaction type
	 * @param voucherReference voucher ref
	 * @return InventoryTransaction
	 */
	public InventoryTransaction stockInventory(Date transactionDate, List<ItemRequestPair> request, Warehouse warehouse, 
			int status, String statusText, String transactionType, String voucherReference);

	/**Gets all transaction of a particular transaction type for a voucher
	 * @param voucherRef voucher ref
	 * @param transactionType transaction type
	 * @return List of InventoryTransaction
	 */
	public List<InventoryTransaction> getTransactionsByOrder(String voucherRef, String transactionType);
	
	/**Gets the items issued for a sales order identified by the voucherRef
	 * @param voucherRef voucher ref
	 * @return Map of issued for a sales order identified by the voucherRef
	 */
	public HashMap<String, String> getItemsIssuedForSalesOrder(String voucherRef); 

}
