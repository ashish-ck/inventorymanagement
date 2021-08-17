package com.drivojoy.inventory.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

/**
 * 
 * @author ashishsingh
 *
 */
@Service
@Transactional
public interface IBarcodeSerivce {

	/**
	 * Returns list of newly created barcodes based on seed and count
	 * @param seed Seed
	 * @param count number of codes
	 * @return barcodes
	 */
	public List<String> getBarcodeList(String seed, int count);
	
	/**
	 * Returns newly created barcode based on seed
	 * @param seed Seed
	 * @return Barcode
	 */
	public String getBarcode(String seed);
}
