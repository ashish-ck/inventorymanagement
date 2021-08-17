package com.drivojoy.inventory.servicesImpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.drivojoy.inventory.models.Tax;
import com.drivojoy.inventory.repositories.TaxRepository;
import com.drivojoy.inventory.services.ITaxService;


@Component
public class TaxServiceImpl implements ITaxService{

	@Autowired
	private TaxRepository taxRepository;
	
	private final Logger logger = LoggerFactory.getLogger(TaxServiceImpl.class);

	@Override
	public List<Tax> getAllTaxes(){
		logger.debug("Fetching all taxes");
		return taxRepository.findAll();
	}

	@Override
	public Tax saveTax(Tax tax) {
		logger.debug("Saving tax : "+tax.toString());
		return taxRepository.save(tax);
	}

	@Override
	public Tax getByCode(String code) {
		logger.debug("Fetching details for tax with code : "+code);
		return taxRepository.findByCode(code);
	}

	
	
}
