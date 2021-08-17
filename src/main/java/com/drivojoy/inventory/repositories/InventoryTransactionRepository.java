package com.drivojoy.inventory.repositories;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.drivojoy.inventory.models.InventoryTransaction;

/**
 * 
 * @author ashishsingh
 *
 */
@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, BigInteger> {

	/**
	 * Finds all transactions for a voucher reference and transaction type
	 * @param voucherRef Voucher ref
	 * @param transactionType Transaction type
	 * @return List of transactions
	 */
	@Query("select t from InventoryTransaction t where t.voucherReference = :voucherRef and t.transactionType = :transactionType")
	List<InventoryTransaction> findByVoucherReferenceAndTransactionType(@Param("voucherRef") String voucherRef, @Param("transactionType") String transactionType);
}
