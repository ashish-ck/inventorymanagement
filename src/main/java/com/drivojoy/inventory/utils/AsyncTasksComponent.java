package com.drivojoy.inventory.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.drivojoy.inventory.models.Item;
import com.drivojoy.inventory.models.ItemCount;
import com.drivojoy.inventory.models.ItemDetails;
import com.drivojoy.inventory.models.UserReports;
import com.drivojoy.inventory.repositories.ItemRepository;
import com.drivojoy.inventory.repositories.UserReportsRepository;
import com.drivojoy.inventory.repositories.WarehouseRepository;
import com.drivojoy.inventory.services.IItemService;

/**
 * 
 * @author ashishsingh
 *
 */
@Component
@PropertySource("classpath:com/drivojoy/config/${spring.profiles.active}/app.properties")
public class AsyncTasksComponent {

	@Value("${reportsPath}")
	private String reportsPath;
	@Autowired
	private ItemRepository itemRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private UserReportsRepository reportsRepository;
	@Autowired
	private IItemService itemService;
	private final int _COL_ITEM_CODE = 0;
	private final int _COL_DESCRIPTION = 1;
	private final int _COL_CATEGORY = 2;
	private final int _COL_BRAND = 3;
	private final int _COL_BARCODE = 4;
	private final int _COL_QUANTITY = 5;
	private final int _COL_WAREHOUSE = 6;


	private final Logger logger = LoggerFactory.getLogger(AsyncTasksComponent.class);

	/**
	 * An asynchronous service to create inventory count sheet by warehouse
	 * @param userRef Name of the report to be generated
	 * @param warehouse Warehouse code for which count sheet is to be generated
	 */
	@Async
	public void getInventoryCountSheetByWarehouse(String userRef, String warehouse){
		logger.debug("Executing async task: getInventoryCountSheetByWarehouse");
		List<Item>  itemDetails = itemRepository.getInventoryCountSheetByWarehouse(warehouseRepository.findByCode(warehouse).getId());
		createExcelReport(userRef, itemDetails);
	}

	/**
	 * An asynchronous service to create inventory count sheet for all warehouses
	 * @param userRef Name of the report to be generated
	 */
	@Async
	public void getInventoryCountSheet(String userRef){
		logger.debug("Executing async task: getInventoryCountSheet");
		List<Item>  itemDetails = itemRepository.getInventoryCountSheet();
		createExcelReport(userRef, itemDetails);
	}
	
	@Async
	public void bulkUploadItemCount(String fileName){
		logger.debug("Executing async task: bulkUploadItemCount");
		itemService.importItemsNew(fileName);
	}

	private void createExcelReport(String userRef, List<Item> itemDetails){
		userRef = userRef.replaceAll(".xlsx", "");
		userRef = userRef.replaceAll(".xls", "");
		//Create Blank workbook
		XSSFWorkbook workbook = new XSSFWorkbook(); 
		try{
			//Create a blank spreadsheet
			XSSFSheet spreadsheet = workbook.createSheet(userRef);
			int index = 0;
			XSSFRow heading = spreadsheet.createRow(index);
			index++;
			Cell cell = heading.createCell(_COL_ITEM_CODE);
			cell.setCellValue((String)"Item Code");
			Cell cell1 = heading.createCell(_COL_CATEGORY);
			cell1.setCellValue((String)"Category");
			Cell cell2 = heading.createCell(_COL_BRAND);
			cell2.setCellValue((String)"Brand");
			Cell cell3 = heading.createCell(_COL_BARCODE);
			cell3.setCellValue((String)"Barcode");
			Cell cell4 = heading.createCell(_COL_QUANTITY);
			cell4.setCellValue((String)"Quantity");
			Cell cell5 = heading.createCell(_COL_WAREHOUSE);
			cell5.setCellValue((String)"Warehouse");
			Cell cell6 = heading.createCell(_COL_DESCRIPTION);
			cell6.setCellValue((String)"Description");

			for(Item item : itemDetails){
				double qtyInHand = 0;
				for(ItemCount count : item.getItemCount()){
					qtyInHand += 1; //count.getQuantityInHand();
				}
				if(qtyInHand == 0)
					continue;

				Collection<ItemDetails> details = itemRepository.getItemDetails(item.getId()).getItemDetails();
				if(details != null){
					for(ItemDetails count: details){
						/*
						if(count.getQtyRequested() > 0){
							XSSFRow row = spreadsheet.createRow(index);
							Cell codeCell = row.createCell(_COL_ITEM_CODE);
							codeCell.setCellValue(item.getCode());
							Cell descriptionCell = row.createCell(_COL_DESCRIPTION);
							descriptionCell.setCellValue(item.getDescription());
							Cell categoryCell = row.createCell(_COL_CATEGORY);
							categoryCell.setCellValue(item.getCategory().getName());
							Cell brandCell = row.createCell(_COL_BRAND);
							brandCell.setCellValue("Not Available");
							for(ItemAttribute attribute : item.getAttributes()){
								if(attribute.getAttribute().getName().equalsIgnoreCase("brand")){
									brandCell.setCellValue("");
									for(String value :  attribute.getValue()){
										if(brandCell.getStringCellValue().isEmpty())
											brandCell.setCellValue(value);
										else
											brandCell.setCellValue(","+value);
									}
									break;
								}
							}
							Cell barcodeCell = row.createCell(_COL_BARCODE);
							barcodeCell.setCellValue(count.getBarcode());
							Cell quantityCell = row.createCell(_COL_QUANTITY);
							quantityCell.setCellValue(count.getQtyRequested());
							Cell warehouseCell = row.createCell(_COL_WAREHOUSE);
							warehouseCell.setCellValue(count.getWarehouse().getName());
							index++;
						}
						*/
					}					
				}
			}
			//Write the workbook in file system
			String date = Calendar.getInstance().getTime().toString().replaceAll(" ", "_");
			date = date.replaceAll(":", "_");
			String fileName = userRef+"_"+date+".xlsx";
			String filePath = reportsPath+userRef+"_"+date+".xlsx";
			File file = new File(filePath);
			FileOutputStream out = new FileOutputStream(file);
			workbook.write(out);
			out.close();
			workbook.close();
			//Once done, we need to add a new record in the UserReports table
			UserReports report = new UserReports(0, fileName, file.getPath());
			reportsRepository.save(report);

		}catch(Exception ex){
			logger.error("Exception thrown while creating excel file for report "+userRef+" :"+ex.getMessage());
			throw new RuntimeException(ex.getMessage());
		}

	}
}
