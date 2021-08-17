package com.drivojoy.inventory.servicesImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.drivojoy.inventory.dto.ItemCountDTO;
import com.drivojoy.inventory.models.Crate;
import com.drivojoy.inventory.models.Rack;
import com.drivojoy.inventory.models.Shelf;
import com.drivojoy.inventory.models.Warehouse;
import com.drivojoy.inventory.repositories.ShelfRepository;
import com.drivojoy.inventory.services.IItemService;
import com.drivojoy.inventory.services.IRackService;
import com.drivojoy.inventory.services.IShelfService;
import com.drivojoy.inventory.services.IWarehouseService;
import com.drivojoy.inventory.utils.IDTOWrapper;

@Component
public class ShelfServiceImpl implements IShelfService {

	@PersistenceContext
	private EntityManager em;
	@Autowired
	IWarehouseService warehouseService;
	@Autowired
	IDTOWrapper dtoWrapper;
	@Autowired
	ShelfRepository shelfRepository;
	@Autowired
	IRackService rackService;
	@Autowired
	IItemService itemService;
	private final Logger logger = LoggerFactory.getLogger(ShelfServiceImpl.class);

	@Transactional
	@Override
	public Shelf addShelf(long warehouseId, Shelf shelf) {
		logger.debug("adding shelf in warehouse " + shelf.toString());
		try {
			shelf = addUpdateShelf(shelf);
			Warehouse warehouse = warehouseService.findById(warehouseId);
			warehouse.getShelves().add(shelf);
			warehouseService.editWarehouse(dtoWrapper.convert(warehouse));
			return shelf;
		} catch (Exception ex) {
			logger.error("Exception thrown while adding shelf : " + ex.getCause());
			return null;
		}
	}

	@Override
	public Shelf addUpdateShelf(Shelf shelf) {
		logger.debug("editing shelf : " + shelf.toString());
		try {
			shelf = shelfRepository.save(shelf);
			em.flush();
			return shelf;
		} catch (Exception ex) {
			logger.error("Exception thrown while add update shelf : " + ex.getCause());
			return null;
		}
	}

	@Override
	public List<Shelf> getShelvesByWarehouseId(long id) {
		logger.debug("fetching shelves in warehouse " + id);
		try {
			Warehouse warehouse = warehouseService.findById(id);
			Collection<Shelf> shelves = warehouse.getShelves();
			if (shelves instanceof List) {
				return (List<Shelf>) shelves;
			} else {
				return new ArrayList<Shelf>(shelves);
			}
		} catch (Exception e) {
			logger.error("Exception thrown whiles fetching shelves from warehouse " + e.getCause());
			return null;
		}
	}

	@Override
	public Shelf findById(long id) {
		logger.debug("fetching shelf " + id);
		try {
			return shelfRepository.findById(id);
		} catch (Exception e) {
			logger.error("Exception thrown while fetching shelf " + e.getCause());
			return null;
		}
	}

	@Override
	public Rack addRack(long id, Rack rack) {
		logger.debug("adding rack in shelf " + rack.toString());
		try {
			rack = rackService.addUpdateRack(rack);
			Shelf shelf = shelfRepository.findById(id);
			List<Rack> racks = (List<Rack>) shelf.getRacks();
			if (racks == null)
				racks = new ArrayList<>();
			racks.add(rack);
			shelf.setRacks(racks);
			shelf = shelfRepository.save(shelf);
			em.flush();
			return rack;
		} catch (Exception e) {
			logger.error("Exception thrown while adding rack " + e.getCause());
			return null;
		}
	}

	@Override
	public List<Rack> getRacks(long id) {
		logger.debug("fetching racks from shelf " + id);
		try {
			Shelf shelf = shelfRepository.findById(id);
			Collection<Rack> racks = shelf.getRacks();
			if (racks instanceof List) {
				return (List<Rack>) racks;
			} else {
				return new ArrayList<Rack>(racks);
			}
		} catch (Exception e) {
			logger.error("Exception thrown while fetching racks " + e.getCause());
			return null;
		}
	}

	@Override
	public List<Crate> getCrates(long id) {
		logger.debug("fetching crates from shelf " + id);
		try {
			Shelf shelf = shelfRepository.findById(id);
			List<Crate> crates = new ArrayList<>();
			List<Rack> racks = (List<Rack>) shelf.getRacks();
			for (Rack rack : racks) {
				crates.addAll(rack.getCrates());
			}
			return crates;
		} catch (Exception e) {
			logger.error("Exception thrown while fetching crates " + e.getCause());
			return null;
		}
	}

	@Override
	public List<ItemCountDTO> getItemCounts(long id) {
		return itemService.getItemsInShelf(id);
	}

	@Override
	public Shelf getShelfByNameAndWarehouseId(String shelfName, long id) {
		Warehouse warehouse = warehouseService.findById(id);
		if(warehouse != null)
		for (Shelf shelf : warehouse.getShelves()){
			if(shelf.getName().equalsIgnoreCase(shelfName)){
				return shelf;
			}
		}
		return null;
	}
}
