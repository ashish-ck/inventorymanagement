package com.drivojoy.inventory.rest.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.drivojoy.inventory.services.IBarcodeSerivce;

/**
 * 
 * @author ashishsingh
 *
 */
@RestController
@RequestMapping("/api/barcodes")
public class BarcodeController {
	
	@Autowired
	private IBarcodeSerivce barcodeService;

	private final Logger logger = LoggerFactory.getLogger(BarcodeController.class);

	/**Creates a barcode based on seed
	 * @param seed - seed 
	 * @return - Generated barcode serial
	 */
	@RequestMapping(value="/create", method=RequestMethod.POST)
	public ResponseEntity<String> createBarcode(@RequestBody String seed){
		logger.debug("create barcode api invoked");
		String barcode = null;
		try{
			barcode = barcodeService.getBarcode(seed);
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getCause());
		}
		if(barcode != null)
			return new ResponseEntity<String>(barcode, HttpStatus.OK);
		
		return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
