package com.drivojoy.inventory.servicesImpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.drivojoy.inventory.models.UoM;
import com.drivojoy.inventory.repositories.UoMRepository;
import com.drivojoy.inventory.services.IUoMService;
import com.drivojoy.inventory.utils.Constants;

@Component
public class UoMServiceImpl implements IUoMService {
	
	@Autowired
	private UoMRepository uomRepository;
	
	private final Logger logger = LoggerFactory.getLogger(UoMServiceImpl.class);

	@Override
	public UoM getUnitByNotation(String notation) {
		logger.debug("fetching unit object for notation : "+notation);
		try{
			return uomRepository.findByNotation(notation);
		}catch(Exception ex){
			logger.error("Exception thrown while fetching unit object"+ex.getCause().getCause().getMessage());
			return null;
		}
	}

	@Override
	public UoM createUnit(UoM unit) 
			throws DataIntegrityViolationException, Exception {
		logger.debug("Preparing to create a new unit");
		try{
			return uomRepository.saveAndFlush(unit);
		}catch(DataIntegrityViolationException ex){
			throw new DataIntegrityViolationException("Unit with name '"+unit.getName()+"' "
					+ "and notation '"+unit.getNotation()+"' already exists!");
		}catch(Exception ex){
			logger.error("Exception thrown while fetching unit object"+ex.getMessage());
			throw new Exception("Something went wrong!");
		}
	}

	@Override
	public List<UoM> getAllUnits() {
		logger.debug("Fetching details for all Units");
		try{
			return uomRepository.findAll();
		}catch(Exception ex){
			logger.error("Exception thrown while fetching details for all units "+ex.getMessage());
			throw new RuntimeException(Constants._GENERIC_ERROR_MESSAGE);
		}
	}

	@Override
	public UoM getById(long id) {
		logger.debug("Fetching details for unit with id : "+id);
		try{
			return uomRepository.findOne(id);
		}catch(Exception ex){
			logger.error("Exception thrown while fetching details for unit with id '"+id+"' "+ex.getMessage());
			throw new RuntimeException(Constants._GENERIC_ERROR_MESSAGE);
		}
	}

	@Override
	public UoM getDefaultUnit() {
		UoM unit = uomRepository.findByNotation("UNIT");
		return unit;
	}

}
