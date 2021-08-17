package com.drivojoy.inventory.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.drivojoy.inventory.models.Warehouse;

/**
 * 
 * @author ashishsingh
 *
 */
@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long>{

	/**
	 * Finds a warehouse by its name
	 * @param name Name
	 * @return Warehouse
	 */
	Warehouse findByName(String name);
	
	/**
	 * Finds a warehouse by its code
	 * @param code Warehouse code
	 * @return Warehouse
	 */
	Warehouse findByCode(String code);
	
	/**Finds a all warehouse shorter format.
	 * @return List of warehouse network object row.
	 */
	@Query(value="select warehouse.id, warehouse.name, warehouse.code, warehouse.is_parent_warehouse from warehouse", nativeQuery=true)
	List<Object[]> getAllWarehouses();
}
