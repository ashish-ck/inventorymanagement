package com.drivojoy.inventory.servicesImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.drivojoy.inventory.dto.VendorDTO;
import com.drivojoy.inventory.models.Item;
import com.drivojoy.inventory.models.Vendor;
import com.drivojoy.inventory.repositories.VendorRespository;
import com.drivojoy.inventory.services.IItemService;
import com.drivojoy.inventory.services.IVendorService;
import com.drivojoy.inventory.utils.Constants;
import com.drivojoy.inventory.utils.IDTOWrapper;

@Component
public class VendorServiceImpl implements IVendorService{
	@Autowired
	private VendorRespository vendorRepository;
	@Autowired
	private IItemService itemService;
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private IDTOWrapper dtoWrapper;
	private final Logger logger = LoggerFactory.getLogger(VendorServiceImpl.class);
	@Override
	public VendorDTO createUpdateVendor(VendorDTO vendorDTO) {
		Vendor vendor = dtoWrapper.convert(vendorDTO);
		logger.debug("Vendor to be saved : "+vendor.toString());
		
		try{
			vendor = vendorRepository.saveAndFlush(vendor);
			return dtoWrapper.convert(vendor);
		}catch(DataIntegrityViolationException ex){
			logger.error("Vendor with name "+vendor.getName()+" already exists!");
			throw new DataIntegrityViolationException("Vendor with name "+vendor.getName()+" already exists!");
		}
		catch(Exception ex){
			logger.error("Exception thrown while creating vendor : "+ex.getMessage());
			throw new RuntimeException(Constants._GENERIC_ERROR_MESSAGE);
		}
	}

	@Override
	public List<VendorDTO> getVendors() {
		return dtoWrapper.convertToVendorDTO(vendorRepository.findAll());
	}

	@Override
	public List<VendorDTO> getVendorsByItem(long id) {
		return dtoWrapper.convertToVendorDTO(vendorRepository.getVendorsByItem(id));
	}

	@Override
	public Collection<Item> checkAndAddItem(long vendorId, List<String> items) {
		Vendor vendor = vendorRepository.findOne(vendorId);
		try{
			List<Item> vendorItems = vendorRepository.getVendorItems(vendor.getId());
			List<Item> itemsToBeAdded = new ArrayList<>();
			boolean itemPresent = false;
			for(String itemCode: items){
				itemPresent = false;
				for(Item vendorItem: vendorItems){
					if(vendorItem.equals(itemCode)){
						logger.debug("Item "+itemCode+" already present!");
						itemPresent = true;
						break;
					}
				}
				if(!itemPresent){
					logger.debug("Adding item "+itemCode+" to vendors list!");
					itemsToBeAdded.add(itemService.getItemByCode(itemCode));
				}
			}
			if(itemsToBeAdded.size() >0){
				vendor.getItemsSold().addAll(itemsToBeAdded);
				vendor = vendorRepository.save(vendor);
				em.flush();
				return vendor.getItemsSold();
			}
			return vendorItems;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception thrown while checkAndAddItem : "+ex.getMessage());
			throw new RuntimeException(Constants._GENERIC_ERROR_MESSAGE);
		}
	}

	@Override
	public Vendor getVendorById(long id) {
		return vendorRepository.findOne(id);
	}

}
