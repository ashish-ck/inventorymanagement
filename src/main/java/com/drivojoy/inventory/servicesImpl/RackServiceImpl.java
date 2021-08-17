package com.drivojoy.inventory.servicesImpl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.drivojoy.inventory.dto.ItemCountDTO;
import com.drivojoy.inventory.models.Crate;
import com.drivojoy.inventory.models.Rack;
import com.drivojoy.inventory.models.Shelf;
import com.drivojoy.inventory.repositories.RackRepository;
import com.drivojoy.inventory.services.ICrateService;
import com.drivojoy.inventory.services.IItemService;
import com.drivojoy.inventory.services.IRackService;
import com.drivojoy.inventory.services.IShelfService;

@Component
public class RackServiceImpl implements IRackService {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	RackRepository rackRepository;
	@Autowired
	IShelfService shelfService;
	@Autowired
	ICrateService crateService;
	@Autowired
	IItemService itemService;
	private final Logger logger = LoggerFactory.getLogger(RackServiceImpl.class);

	@Override
	public Rack addRack(long shelfId, Rack rack) {
		logger.debug("adding rack" + rack);
		try {
			rack = rackRepository.save(rack);
			em.flush();
			Shelf shelf = shelfService.findById(shelfId);
			List<Rack> racks = (List<Rack>) shelf.getRacks();
			racks.add(rack);
			shelfService.addUpdateShelf(shelf);
			return rack;
		} catch (Exception e) {
			logger.error("Exception thrown while adding rack to shelf " + e.getCause());
			return null;
		}
	}

	@Override
	public Rack addUpdateRack(Rack rack) {
		logger.debug("add update rack " + rack);
		try {
			rack = rackRepository.save(rack);
			em.flush();
			return rack;
		} catch (Exception e) {
			logger.error("Exception thrown while add update rack " + e.getCause());
			return null;
		}
	}

	@Override
	public Rack findById(long id) {
		logger.debug("fetching rack " + id);
		try {
			Rack rack = rackRepository.findById(id);
			return rack;
		} catch (Exception e) {
			logger.error("Exception thrown while fetching rack " + e.getCause());
			return null;
		}
	}

	@Override
	public Crate addCrate(long id, Crate crate) {
		logger.debug("adding crate" + crate);
		try {
			crate = crateService.addUpdateCrate(crate);
			Rack rack = rackRepository.findById(id);
			List<Crate> crates = (List<Crate>) rack.getCrates();
			crates.add(crate);
			rack = rackRepository.save(rack);
			em.flush();
			return crate;
		} catch (Exception e) {
			logger.error("Exception thrown while " + e.getCause());
			return null;
		}
	}

	@Override
	public List<Crate> getCrates(long id) {
		logger.debug("fetching crates " + id);
		try {
			Rack rack = rackRepository.findById(id);
			List<Crate> crates = (List<Crate>) rack.getCrates();
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
	public Rack getRackByNameAndShelfId(String rackName, long shelfId) {
		Shelf shelf = shelfService.findById(shelfId);
		if(shelf != null)
		for (Rack rack : shelf.getRacks()){
			if(rack.getName().equalsIgnoreCase(rackName)){
				return rack;
			}
		}
		return null;
	}
}
