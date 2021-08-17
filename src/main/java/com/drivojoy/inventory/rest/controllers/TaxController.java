package com.drivojoy.inventory.rest.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.drivojoy.inventory.models.Tax;
import com.drivojoy.inventory.services.ITaxService;

/**
 * 
 * @author ashishsingh
 *
 */
@RestController
@RequestMapping(value="/api/taxes/")
public class TaxController {

	@Autowired
	private ITaxService taxService;
	
	private final Logger logger = LoggerFactory.getLogger(TagController.class);
	
	/**
	 * API End point to get all taxes
	 * @return list of tax
	 */
	@PreAuthorize("hasAnyRole('ADMIN,INVENTORYMANAGER')")
	@RequestMapping(value="/get", method=RequestMethod.GET)
	public ResponseEntity<List<Tax>> getAllTaxes(){
		logger.debug("fetch all taxes api invoked");
		List<Tax> taxes = null;
		try{
			taxes = taxService.getAllTaxes();
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getCause());
		}
		if(taxes != null)
			return new ResponseEntity<List<Tax>>(taxes, HttpStatus.OK);
		
		return new ResponseEntity<List<Tax>>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@PreAuthorize("hasAnyRole('ADMIN,INVENTORYMANAGER')")
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public ResponseEntity<Tax> saveTax(Tax tax){
		logger.debug("fetch all taxes api invoked");
		try{
			tax = taxService.saveTax(tax);
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getCause());
		}
		if(tax != null)
			return new ResponseEntity<Tax>(tax, HttpStatus.OK);
		
		return new ResponseEntity<Tax>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * API End point to get a tax by code
	 * @param code Tax code of the tax to be retrieved
	 * @return Tax
	 */
	@PreAuthorize("hasAnyRole('ADMIN,INVENTORYMANAGER')")
	@RequestMapping(value="/get/{code}", method=RequestMethod.POST)
	public ResponseEntity<Tax> getTax(String code){
		logger.debug("fetch all taxes api invoked");
		try{
			Tax tax = taxService.getByCode(code);
			if(tax != null)
				return new ResponseEntity<Tax>(tax, HttpStatus.OK);
			else
				return new ResponseEntity<Tax>(HttpStatus.NOT_FOUND);
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getCause());
		}

		
		return new ResponseEntity<Tax>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}
