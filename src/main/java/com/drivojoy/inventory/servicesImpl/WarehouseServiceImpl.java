package com.drivojoy.inventory.servicesImpl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.drivojoy.inventory.dto.WarehouseDTO;
import com.drivojoy.inventory.dto.WarehouseNetworkDTO;
import com.drivojoy.inventory.models.Warehouse;
import com.drivojoy.inventory.repositories.WarehouseRepository;
import com.drivojoy.inventory.services.IWarehouseService;
import com.drivojoy.inventory.utils.Constants;
import com.drivojoy.inventory.utils.IDTOWrapper;

@Component
public class WarehouseServiceImpl implements IWarehouseService {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private IDTOWrapper dtoWrapper;
	private final Logger logger = LoggerFactory.getLogger(WarehouseServiceImpl.class);
	private final long _DEFAULT_WAREHOUSE = 1;

	@Override
	public WarehouseDTO createWarehouse(WarehouseDTO warehouseDTO) {
		Warehouse warehouse = dtoWrapper.convert(warehouseDTO);
		logger.debug("creating warehouse : "+warehouse.toString());
		try{
			warehouse = warehouseRepository.save(warehouse);

			/* The assumption here is that while creating a warehouse, 
			 * we are not going to add inventory items. A separate screen 
			 * will be provided wherein the user can import an excel 
			 * sheet/manually add items to inventory */
			
			return dtoWrapper.convert(warehouse);
		}catch(DataIntegrityViolationException ex){
			logger.error("Exception thrown while creating new warehouse : "
					+ex.getMessage());
			throw new DataIntegrityViolationException("Warehouse with name '"+warehouse.getName()+"' already exists!");
		}
	}

	@Override
	public WarehouseDTO editWarehouse(WarehouseDTO warehouseDTO) {
		Warehouse warehouse = dtoWrapper.convert(warehouseDTO);
		logger.debug("editing warehouse : "+warehouse.toString());
		try{
			warehouse = warehouseRepository.save(warehouse);
			em.flush();
			
			/* The assumption here is that editing a warehouse, 
			 * will involve editing the name/address only. A separate 
			 * screen will be provided wherein the user can import an  
			 * excel sheet/manually add items to inventory */
			
			return dtoWrapper.convert(warehouse);
		}catch(Exception ex){
			logger.error("Exception thrown while editing warehouse : "+ex.getCause());
			return null;
		}
	}

	@Override
	public Warehouse getWarehouseByName(String name) {
		return warehouseRepository.findByName(name);
	}
	
	@Override
	public Warehouse getWarehouseByCode(String code) {
		return warehouseRepository.findByCode(code);
	}

	@Override
	public Warehouse getDefaultWarehouse() {
		return warehouseRepository.findOne(_DEFAULT_WAREHOUSE);
	}

	@Override
	public List<WarehouseDTO> getAllWarehousesDTO() {
		logger.debug("Fetching details for all Warehouses");
		try{
			return dtoWrapper.convertToWarehouseDTO(warehouseRepository.findAll());
		}catch(Exception ex){
			logger.error("Exception thrown while fetching details for all warehouses "+ex.getMessage());
			throw new RuntimeException(Constants._GENERIC_ERROR_MESSAGE);
		}
	}
	
	@Override
	public List<Warehouse> getAllWarehouses() {
		logger.debug("Fetching details for all Warehouses");
		try{
			return warehouseRepository.findAll();
		}catch(Exception ex){
			logger.error("Exception thrown while fetching details for all warehouses "+ex.getMessage());
			throw new RuntimeException(Constants._GENERIC_ERROR_MESSAGE);
		}
	}

	@Override
	public Warehouse getMotherWarehouse(String warehouseCode) {
		Warehouse assignedWarehouse = warehouseRepository.findByCode(warehouseCode);
		if(assignedWarehouse == null)
			return null;
		if(assignedWarehouse.isParentWarehouse()){
			return assignedWarehouse;
		}else{
			if(assignedWarehouse.getMotherWarehouse() != null)
				return assignedWarehouse.getMotherWarehouse();
			else
				return getDefaultWarehouse();
		}
	}

	@Override
	public Warehouse findById(long id) {
		return warehouseRepository.findOne(id);
	}
	
	@Override
	public List<WarehouseNetworkDTO> getAllWarehouseNetworkDTO(){
		return dtoWrapper.convertObjectToWarehouseNetworkDTOList(warehouseRepository.getAllWarehouses());
	}
}
