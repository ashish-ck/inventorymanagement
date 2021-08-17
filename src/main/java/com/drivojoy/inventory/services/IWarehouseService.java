package com.drivojoy.inventory.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.drivojoy.inventory.dto.WarehouseDTO;
import com.drivojoy.inventory.dto.WarehouseNetworkDTO;
import com.drivojoy.inventory.models.Warehouse;

/**
 * 
 * @author ashishsingh
 *
 */
/**
 * @author Akshay
 *
 */
@Service
@Transactional
public interface IWarehouseService {

	/**
	 * Creates a new warehouse
	 * @param warehouseDTO Warehouse
	 * @return Warehouse
	 * @throws Exception Throws exception
	 */
	WarehouseDTO createWarehouse(WarehouseDTO warehouseDTO) throws Exception; 
	
	/**
	 * Updates an existing warehouse
	 * @param warehouseDTO Warehouse
	 * @return Warehouse
	 * @throws Exception Throws exceptions
	 */
	WarehouseDTO editWarehouse(WarehouseDTO warehouseDTO) throws Exception;
	
	/**
	 * Finds a warehouse by name
	 * @param name Name
	 * @return Warehouse
	 */
	Warehouse getWarehouseByName(String name);
	
	/**
	 * Finds a warehouse by code
	 * @param code Warehouse code
	 * @return Warehouse
	 */
	Warehouse getWarehouseByCode(String code);
	
	/**
	 * Gets the default warehouse
	 * @return Warehouse
	 */
	Warehouse getDefaultWarehouse();
	
	/**
	 * Finds a warehouse by id
	 * @param id Warehouse id
	 * @return Warehouse
	 */
	Warehouse findById(long id);
	
	/**
	 * Fetches a list of all warehouses
	 * @return List of warehouses
	 */
	List<Warehouse> getAllWarehouses();
	
	/**
	 * Finds the mother warehouse of a particular warehouse
	 * @param warehouseCode Warehouse code
	 * @return Warehouse
	 */
	Warehouse getMotherWarehouse(String warehouseCode);
	
	/**
	 * Gets a list of all warehouses view models
	 * @return List of warehouses
	 */
	List<WarehouseDTO> getAllWarehousesDTO();
	
	List<WarehouseNetworkDTO> getAllWarehouseNetworkDTO();
}
