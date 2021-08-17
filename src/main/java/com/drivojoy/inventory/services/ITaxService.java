package com.drivojoy.inventory.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.drivojoy.inventory.models.Tax;

/**
 * 
 * @author ashishsingh
 *
 */
@Service
@Transactional
public interface ITaxService {

	/**
	 * Fetches a list of all taxes
	 * @return List of tax
	 */
	List<Tax> getAllTaxes();

	/**
	 * Saves a new tax definition
	 * @param tax Tax
	 * @return Tax
	 */
	Tax saveTax(Tax tax);

	/**
	 * Finds a tax by tax code
	 * @param code Code
	 * @return Tax
	 */
	Tax getByCode(String code);
}
