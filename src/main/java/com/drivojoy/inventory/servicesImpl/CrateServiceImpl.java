package com.drivojoy.inventory.servicesImpl;

import java.util.Collection;
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
import com.drivojoy.inventory.repositories.CrateRepository;
import com.drivojoy.inventory.services.ICrateService;
import com.drivojoy.inventory.services.IItemService;
import com.drivojoy.inventory.services.IRackService;
import com.drivojoy.inventory.utils.IDTOWrapper;

@Component
public class CrateServiceImpl implements ICrateService {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	IRackService rackService;
	@Autowired
	CrateRepository crateRepository;
	@Autowired
	IDTOWrapper dtoWrapper;
	@Autowired
	IItemService itemService;
	private final Logger logger = LoggerFactory.getLogger(CrateServiceImpl.class);

	@Override
	public Crate addCrate(long rackId, Crate crate) {
		logger.debug("adding a crate : " + crate.toString());
		try {
			crate = crateRepository.save(crate);
			Rack rack = rackService.findById(rackId);
			Collection<Crate> crates = rack.getCrates();
			crates.add(crate);
			rack.setCrates(crates);
			rack = rackService.addUpdateRack(rack);
			return crate;
		} catch (Exception ex) {
			logger.error("Exception thrown while adding crate : " + ex.getCause());
			return null;
		}
	}

	@Override
	public Crate addUpdateCrate(Crate crate) {
		logger.debug("editing a crate : " + crate.toString());
		try {
			crate = crateRepository.save(crate);
			em.flush();
			return crate;
		} catch (Exception ex) {
			logger.error("Exception thrown while editing crate : " + ex.getCause());
			return null;
		}
	}

	@Override
	public Crate findById(long id) {
		try {
			return crateRepository.findById(id);
		} catch (Exception e) {
			logger.error("Exception thrown while fetching crate " + e.getCause());
			return null;
		}
	}

	@Override
	public List<ItemCountDTO> getItemCounts(long id) {
		return itemService.getItemsInCrate(id);
	}

	@Override
	public Crate getCrateByNameAndRackId(String crateName, long rackId) {
		Rack rack = rackService.findById(rackId);
		if(rack != null)
		for (Crate crate : rack.getCrates()){
			if(crate.getName().equalsIgnoreCase(crateName)){
				return crate;
			}
		}
		return null;
	}
}
