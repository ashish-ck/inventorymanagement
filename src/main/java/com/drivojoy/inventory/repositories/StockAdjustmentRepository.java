package com.drivojoy.inventory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.drivojoy.inventory.models.StockAdjustment;

/**
 * 
 * @author ashishsingh
 *
 */
@Repository
public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, Long>{

	/**
	 * Finds a stock adjustment by orderNumber
	 * @param orderNumber Order number
	 * @return StockAdjustment
	 */
	StockAdjustment findByOrderNumber(String orderNumber);
}
