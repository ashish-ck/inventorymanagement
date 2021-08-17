package com.drivojoy.inventory.servicesImpl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.drivojoy.inventory.services.IBarcodeSerivce;

@Component
public class BarcodeServiceImpl implements IBarcodeSerivce{

	private final Logger logger = LoggerFactory.getLogger(BarcodeServiceImpl.class);

	@Override
	public List<String> getBarcodeList(String seed, int count) {
		logger.info("Barcode sequence generation requested for seed : "+seed);
		List<String> listOfBarcodes = new ArrayList<>();
		for (int i = 0; i < count; i++){

			listOfBarcodes.add(getBarcode(seed)+"-"+i);
		}
		return listOfBarcodes;
	}

	@Override
	public String getBarcode(String seed) {
		String barcode = seed;
		synchronized (barcode) {
			barcode += ("-" + (System.currentTimeMillis() /1000L));
		}
		logger.debug("Returning barcode : "+barcode);
		writeBarcodeCode(barcode, "D:\\some.jpg");
		return barcode;
	}


	public void writeBarcodeCode(String code, String filePath){
		//Create the barcode bean
		Code39Bean bean = new Code39Bean();

		final int dpi = 150;

		//Configure the barcode generator
		bean.setModuleWidth(UnitConv.in2mm(1.0f / dpi)); //makes the narrow bar 
		                                                 //width exactly one pixel
		bean.setWideFactor(3);
		bean.doQuietZone(false);

		//Open output file
		File outputFile = new File(filePath);
		OutputStream out = null;
		try {
			out = new FileOutputStream(outputFile);
		    //Set up the canvas provider for monochrome PNG output 
		    BitmapCanvasProvider canvas = new BitmapCanvasProvider(
		            out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

		    //Generate the barcode
		    bean.generateBarcode(canvas, code);

		    //Signal end of generation
		    canvas.finish();
		    out.close();
		}catch(Exception ex){
			logger.error("Exception caught while creating barcodes "+ex.getMessage());
			throw new RuntimeException(ex.getMessage());
		}
		System.out.println("\n\nYou have successfully created Bar Code.");
	}


}
