package com.drivojoy.inventory.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.drivojoy.inventory.models.UoM;

/**
 * 
 * @author ashishsingh
 *
 */
@Service
@Transactional
public interface IUoMService {

	/**
	 * Finds a unit by its notation
	 * @param notation Notation
	 * @return UoM
	 */
	UoM getUnitByNotation(String notation);
	
	/**
	 * Creates a new unit of measurement
	 * @param unit Unit
	 * @return UoM
	 * @throws Exception Throws generic exception
	 */
	UoM createUnit(UoM unit) throws Exception;
	
	/**
	 * Finds the default unit of measurement
	 * @return UoM
	 */
	UoM getDefaultUnit();
	
	/**
	 * Fetches all units of measurements
	 * @return List of uoms
	 */
	List<UoM> getAllUnits();
	
	/**
	 * Finds a unit of measurement by id
	 * @param id Id
	 * @return UoM
	 */
	UoM getById(long id);
}
