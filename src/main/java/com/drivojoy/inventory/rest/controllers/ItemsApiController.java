package com.drivojoy.inventory.rest.controllers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.drivojoy.inventory.dto.ApiResponse;
import com.drivojoy.inventory.dto.ItemCategoryDTO;
import com.drivojoy.inventory.dto.ItemCountDTO;
import com.drivojoy.inventory.dto.ItemCountQuantityDTO;
import com.drivojoy.inventory.dto.ItemDTO;
import com.drivojoy.inventory.dto.ItemReconcileReport;
import com.drivojoy.inventory.dto.RequestParamsDTO;
import com.drivojoy.inventory.helpers.UserAuthentication;
import com.drivojoy.inventory.models.Item;
import com.drivojoy.inventory.models.ItemCount;
import com.drivojoy.inventory.models.datatables.DTParameters;
import com.drivojoy.inventory.models.datatables.DTResult;
import com.drivojoy.inventory.services.IItemCategoryService;
import com.drivojoy.inventory.services.IItemService;
import com.drivojoy.inventory.utils.AsyncTasksComponent;
import com.drivojoy.inventory.utils.Constants;
import com.drivojoy.inventory.utils.KeyValuePair;

/**
 * 
 * @author ashishsingh
 *
 */
@RestController
@RequestMapping(value="/api/items")
@PropertySource("classpath:com/drivojoy/config/${spring.profiles.active}/app.properties")
public class ItemsApiController {
	@Value("${uploadFilePath}")
	private String uploadFilePath;
	@Autowired
	private IItemCategoryService itemCategoryService;
	@Autowired
	private IItemService itemService;
	@Autowired
	private AsyncTasksComponent asyncComp;
	private final Logger logger = LoggerFactory.getLogger(ItemsApiController.class);
	/**
	 * API End point to create a new item.
	 * @param itemDTO Item
	 * @return Item
	 */
	@PreAuthorize("hasAnyRole('ADMIN,INVENTORYMANAGER')")
	@RequestMapping(value="/create", method=RequestMethod.POST,
	consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse<ItemDTO>> createItem(@RequestBody ItemDTO itemDTO){
		logger.debug("createItem api invoked" + itemDTO.toString());
		ItemDTO createdItem= null;
		ApiResponse<ItemDTO> response = null;
		try{
			createdItem = itemService.createItem(itemDTO);
			response = new ApiResponse<ItemDTO>(true, createdItem, "Item Created Successfully!");
			return new ResponseEntity<ApiResponse<ItemDTO>>(response,HttpStatus.OK);
		}
		catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getMessage());
			response = new ApiResponse<ItemDTO>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<ItemDTO>>(response,HttpStatus.OK);
		}
		
	}

	/**
	 * API End point to update existing item
	 * @param id Id of existing item
	 * @param itemDTO Updated copy of the item
	 * @return Item
	 */
	@PreAuthorize("hasAnyRole('ADMIN,INVENTORYMANAGER')")
	@RequestMapping(value="/edit/{id}", method=RequestMethod.POST,
	consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse<ItemDTO>> editItem(@PathVariable("id") long id, @RequestBody ItemDTO itemDTO){
		logger.debug("editItem api invoked" + itemDTO.toString());
		ItemDTO editedItem= null;
		ApiResponse<ItemDTO> response = null;
		try{
			editedItem = itemService.editItem(itemDTO);
			response = new ApiResponse<ItemDTO>(true, editedItem, "Item updated Successfully!");
			return new ResponseEntity<ApiResponse<ItemDTO>>(response,HttpStatus.OK);
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getMessage());
			response = new ApiResponse<ItemDTO>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<ItemDTO>>(response,HttpStatus.OK);
		}
	}

	/**
	 * API End point to edit an item category
	 * @param id Id of the existing category
	 * @param itemCategory Update copy of the item category
	 * @return Item Category
	 */
	@PreAuthorize("hasAnyRole('ADMIN,INVENTORYMANAGER')")
	@RequestMapping(value="/categories/edit/{id}", method=RequestMethod.PUT,
	consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse<ItemCategoryDTO>> editItemCategory(@PathVariable("id") long id, @RequestBody ItemCategoryDTO itemCategory){
		logger.debug("editItemCategory api invoked" + itemCategory.toString());
		ItemCategoryDTO editedItemCategory = null;
		ApiResponse<ItemCategoryDTO> response = null;
		try{
			editedItemCategory = itemCategoryService.editItemCategory(itemCategory);
			response = new ApiResponse<ItemCategoryDTO>(true, editedItemCategory, "Category updated Successfully!");
			return new ResponseEntity<ApiResponse<ItemCategoryDTO>>(response,HttpStatus.OK);
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getMessage());
			response = new ApiResponse<ItemCategoryDTO>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<ItemCategoryDTO>>(response,HttpStatus.OK);
		}
	}

	/**
	 * API End point to find an item by id
	 * @param itemId id
	 * @return Item
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value="/get/{itemId}", method=RequestMethod.GET)
	public ResponseEntity<ApiResponse<ItemDTO>> findItemById(@PathVariable long itemId){
		logger.debug("Controller invoked to get item with id : "+itemId);
		ApiResponse<ItemDTO> response = null;
		try{
			ItemDTO item = itemService.getItemDTOById(itemId);
			response = new ApiResponse<ItemDTO>(true, item, "Item found Successfully!");
			return new ResponseEntity<ApiResponse<ItemDTO>>(response,HttpStatus.OK);
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getMessage());
			response = new ApiResponse<ItemDTO>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<ItemDTO>>(response,HttpStatus.OK);
		}
	}

	/**
	 * API End point to find an item by code.
	 * @param itemCode Code of the item to be found
	 * @return Item
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value="/getByCode", method=RequestMethod.GET)
	public ResponseEntity<ApiResponse<ItemDTO>> findItemByCode(@RequestParam("itemCode") String itemCode){
		logger.debug("Controller invoked to get item with code : "+itemCode);
		ApiResponse<ItemDTO> response = null;
		try{
			ItemDTO item = itemService.getItemDTOByCode(itemCode);
			response = new ApiResponse<ItemDTO>(true, item, "Item found Successfully!");
			return new ResponseEntity<ApiResponse<ItemDTO>>(response,HttpStatus.OK);
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getMessage());
			response = new ApiResponse<ItemDTO>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<ItemDTO>>(response,HttpStatus.OK);
		}
	}

	/**
	 * API End point to find items belonging to a category
	 * @param categoryId Id of the category from which items are to be retrieved.
	 * @return Item
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value="/getByCategory/{categoryId}" , method=RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<ItemDTO>>> findItemsByCategory(@PathVariable("categoryId") long categoryId){
		logger.debug("Controller invoked to get items with under category id : "+categoryId);
		try{
			List<ItemDTO> itemList = itemService.getItemsByCategory(categoryId);
			ApiResponse<List<ItemDTO>> response = new ApiResponse<>(true, itemList, "Items fetched Successfully!");
			return new ResponseEntity<ApiResponse<List<ItemDTO>>>(response, HttpStatus.OK);
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getMessage());
			ApiResponse<List<ItemDTO>> response = new ApiResponse<List<ItemDTO>>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<List<ItemDTO>>>(response,HttpStatus.OK);
		}
	}

	/**
	 * API End point to retrieve item view models that meet a set of criterias 
	 * @param criterias List of criterias
	 * @return List of items
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@CrossOrigin(origins="*")
	@RequestMapping(value="/getByCriteria" , method=RequestMethod.POST)
	public ResponseEntity<List<ItemDTO>> findItemsDTOByCriteria(@RequestBody List<KeyValuePair<String, List<String>>> criterias){
		logger.debug("Controller invoked to get items with under criterias : "+criterias.toString());
		try{
			return new ResponseEntity<List<ItemDTO>>(itemService.getItemsDTOByCriteria(criterias, Constants._ADD_MODE), HttpStatus.OK);
		}catch(Exception ex){
			return new ResponseEntity<List<ItemDTO>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * API End point to retrieve items that meet one among the set of criterias
	 * @param criterias List of criterias
	 * @return List of items
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value="/searchItemsByCriteria" , method=RequestMethod.POST)
	public ResponseEntity<ApiResponse<List<Item>>> findItemsByCriteria(@RequestBody List<KeyValuePair<String, List<String>>> criterias){
		logger.debug("Controller invoked to get items with under criterias : "+criterias.toString());
		try{
			List<Item> itemList = itemService.getItemsByCriteria(criterias, Constants._OR_MODE);
			ApiResponse<List<Item>> response = new ApiResponse<List<Item>>(true, itemList, "Items fetched successfully!");
			return new ResponseEntity<ApiResponse<List<Item>>>(response, HttpStatus.OK);
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getMessage());
			ApiResponse<List<Item>> response = new ApiResponse<List<Item>>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<List<Item>>>(response,HttpStatus.OK);
		}
	}
	
	/**
	 * API End point to retrieve consolidated data of items within a date range
	 * 
	 * @param fromDate
	 *            From date
	 * @param toDate
	 *            To date
	 * @param warehouseId
	 *            Warehouse Id
	 * @return Item report
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/getByDateRange", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<ItemReconcileReport>> getByDateRange(@RequestParam("fromDate") Date fromDate,
			@RequestParam("toDate") Date toDate, @RequestParam("warehouseId") Long warehouseId) {
		logger.debug("Controller invoked on getByDateRange");
		try {
			ItemReconcileReport report = itemService.getItemReport(fromDate, toDate, warehouseId);
			ApiResponse<ItemReconcileReport> response = new ApiResponse<ItemReconcileReport>(true, report,
					"Report retrieved successfully!");
			return new ResponseEntity<ApiResponse<ItemReconcileReport>>(response, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Exception thrown while fetching items by date range :" + ex.getCause());
			ApiResponse<ItemReconcileReport> response = new ApiResponse<ItemReconcileReport>(false, null,
					Constants._GENERIC_ERROR_MESSAGE);
			if (ex.getMessage() != null) {
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<ItemReconcileReport>>(response, HttpStatus.OK);
		}
	}

	/**
	 * API End point to retrieve item view models that meet one among the set of criterias
	 * @param criterias List of criterias
	 * @return Item
	 */
	@CrossOrigin(origins="*")
	@RequestMapping(value="/searchByCriteria" , method=RequestMethod.POST)
	public ResponseEntity<List<ItemDTO>> searchItemsByCriteria(@RequestBody List<KeyValuePair<String, List<String>>> criterias){
		logger.debug("Controller invoked to get items with under criterias : "+criterias.toString());
		try{
			return new ResponseEntity<List<ItemDTO>>(itemService.getItemsDTOByCriteria(criterias, Constants._OR_MODE), HttpStatus.OK);
		}catch(Exception ex){
			return new ResponseEntity<List<ItemDTO>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**API End point to retrieve warehouse item view models that meet one among the set of criterias
	 * @param criterias search criterias
	 * @param warehouseId warehouse id
	 * @return Item count
	 */
	@CrossOrigin(origins="*")
	@RequestMapping(value="/searchWarehouseItemsByCriteria/{warehouseId}" , method=RequestMethod.POST)
	public ResponseEntity<List<ItemCount>> searchItemCountsByCriteria(@RequestBody List<KeyValuePair<String, List<String>>> criterias, 
			@PathVariable("warehouseId") long warehouseId){
		logger.debug("Controller invoked to get items with under criterias : "+criterias.toString());
		try{
			return new ResponseEntity<List<ItemCount>>(itemService.getItemCountsByCriteria(criterias, Constants._OR_MODE, warehouseId), HttpStatus.OK);
		}catch(Exception ex){
			return new ResponseEntity<List<ItemCount>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	/**
	 * API End point to import new items from an excel
	 * @param file Excel file containing the item details
	 * @return List of Items
	 */
	@PreAuthorize("hasAnyRole('ADMIN,INVENTORYMANAGER')")
	@RequestMapping(value="/import", method=RequestMethod.POST)
	public ResponseEntity<List<ItemDTO>> importItems(@RequestParam("file") MultipartFile file){

		if (!file.isEmpty()) {
			try {
				String fileName = uploadFilePath + "temp.xls";
				byte[] bytes = file.getBytes();
				BufferedOutputStream stream =
						new BufferedOutputStream(new FileOutputStream(new File(fileName)));
				stream.write(bytes);
				stream.close();
				List<ItemDTO> createdItems = itemService.importItems(fileName);
				return new ResponseEntity<List<ItemDTO>>(createdItems, HttpStatus.OK);
			} catch (Exception ex) {
				logger.error("Exception thrown while importing items : "+ex.getMessage());
				return new ResponseEntity<List<ItemDTO>>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			return new ResponseEntity<List<ItemDTO>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * API End point to import new items from an excel
	 * @param file Excel file containing the item details
	 * @return List of items
	 */
	@PreAuthorize("hasAnyRole('ADMIN,INVENTORYMANAGER')")
	@RequestMapping(value="/importNew", method=RequestMethod.POST)
	public ResponseEntity<List<ItemDTO>> importItemsNew(@RequestParam("file") MultipartFile file){

		if (!file.isEmpty()) {
			try {
				String fileName = uploadFilePath + "temp.xls";
				byte[] bytes = file.getBytes();
				BufferedOutputStream stream =
						new BufferedOutputStream(new FileOutputStream(new File(fileName)));
				stream.write(bytes);
				stream.close();
				asyncComp.bulkUploadItemCount(fileName);
				return new ResponseEntity<List<ItemDTO>>(new ArrayList<>(), HttpStatus.OK);
			} catch (Exception ex) {
				logger.error("Exception thrown while importing items : "+ex.getMessage());
				return new ResponseEntity<List<ItemDTO>>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			return new ResponseEntity<List<ItemDTO>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * API End point to find an Item and all its details
	 * @param id Id of item to be retrieved
	 * @return Item
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value="/getItemDetails/{id}", method=RequestMethod.GET)
	public ResponseEntity<ApiResponse<ItemDTO>> getItemDetails(@PathVariable("id") long id){
		logger.debug("Controller invoked to get items details for id : "+id);
		ApiResponse<ItemDTO> response = null;
		try{
			ItemDTO item = itemService.getItemDetails(id);
			response = new ApiResponse<ItemDTO>(true, item, "Item found Successfully!");
			return new ResponseEntity<ApiResponse<ItemDTO>>(response,HttpStatus.OK);
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getMessage());
			response = new ApiResponse<ItemDTO>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<ItemDTO>>(response,HttpStatus.OK);
		}
	}

	/**
	 * API End point for pagination service to find all items
	 * @param params DTTables parameters
	 * @return List of Items
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value="/getItems", method=RequestMethod.POST)
	public ResponseEntity<DTResult<ItemDTO>> getItemsPagination(@RequestBody DTParameters params){
		logger.debug("Controller invoked to get items by pagination");
		try{
			return new ResponseEntity<DTResult<ItemDTO>>(itemService.getItemsPagination(params), HttpStatus.OK);
		}catch(Exception ex){
			logger.error("Exception thrown while getting items  pagination");
			return new ResponseEntity<DTResult<ItemDTO>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * API End point to retrieve the items having quantities less than warning point 
	 * @param user Currently logged in user
	 * @return List of Items
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value="/getWarningItems", method=RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<ItemDTO>>> getWarningItems(@AuthenticationPrincipal UserAuthentication user){
		logger.debug("Controller invoked to get items nearing warning point");
		try{
			List<ItemDTO> itemList = null;
			
			if(user.hasRole("ROLE_ADMIN")){
				itemList = itemService.getWarningPointItems(null);
			}else{
				itemList = itemService.getWarningPointItems(user.getWarehouse());
			}
			ApiResponse<List<ItemDTO>> response = new ApiResponse<>(true, itemList, "Items fetched Successfully!");
			return new ResponseEntity<ApiResponse<List<ItemDTO>>>(response, HttpStatus.OK);
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getMessage());
			ApiResponse<List<ItemDTO>> response = new ApiResponse<List<ItemDTO>>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<List<ItemDTO>>>(response,HttpStatus.OK);
		}
	}

	/**
	 * API End point to retrieve the items having quantities less than reorder point 
	 * @param user Currently logged in user
	 * @return List of items
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value="/getReorderItems", method=RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<ItemDTO>>> getReorderItems(@AuthenticationPrincipal UserAuthentication user){
		logger.debug("Controller invoked to get items nearing warning point");
		try{
			List<ItemDTO> itemList = null;
			if(user.hasRole("ROLE_ADMIN")){
				itemList = itemService.getReorderPointItems(null);
			}else{
				itemList = itemService.getReorderPointItems(user.getWarehouse());
			}
			ApiResponse<List<ItemDTO>> response = new ApiResponse<List<ItemDTO>>(true, itemList, "Items fetched Successfully!");
			return new ResponseEntity<ApiResponse<List<ItemDTO>>>(response, HttpStatus.OK);
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getMessage());
			ApiResponse<List<ItemDTO>> response = new ApiResponse<List<ItemDTO>>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<List<ItemDTO>>>(response,HttpStatus.OK);
		}
	}
	
	/**
	 * API End point to accept requests to create an inventory count sheet
	 * @param user Currently logged in user
	 * @param userRef Name of the report given by user
	 * @return message
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value="/getInventoryCountSheet", method=RequestMethod.GET)
	public ResponseEntity<ApiResponse<String>> getInventoryCountSheet(@AuthenticationPrincipal UserAuthentication user, @RequestParam("userRef")String userRef){
		logger.debug("Controller invoked to get inventory count sheet");
		try{
			String message = null;
			if(user.hasRole("ROLE_ADMIN")){
				message = itemService.getInventoryCountSheet(userRef);
			}else{
				message = itemService.getInventoryCountSheetByWarehouse(user.getWarehouse(), userRef);
			}
			ApiResponse<String> response = new ApiResponse<String>(true, message, "Items fetched Successfully!");
			return new ResponseEntity<ApiResponse<String>>(response, HttpStatus.OK);
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getMessage());
			ApiResponse<String> response = new ApiResponse<String>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<String>>(response,HttpStatus.OK);
		}
	}
	
	/**
	 * API End point to update the warning point, reorder point and reorder quantity for a particular item
	 * @param user Currently logged in user
	 * @param itemDTO New copy of item with updated details
	 * @return Item
	 */
	@PreAuthorize("hasAnyRole('ADMIN,INVENTORYMANAGER')")
	@RequestMapping(value="/updateInventoryLevels", method=RequestMethod.POST)
	public ResponseEntity<ApiResponse<ItemDTO>> updateInventoryLevels(@AuthenticationPrincipal UserAuthentication user, @RequestBody ItemDTO itemDTO){
		logger.debug("Controller invoked to update inventory levels");
		ApiResponse<ItemDTO> response = null;
		try{
			ItemDTO item = itemService.updateInventoryLevels(itemDTO);
			response = new ApiResponse<ItemDTO>(true, item, "Item updated Successfully!");
			return new ResponseEntity<ApiResponse<ItemDTO>>(response,HttpStatus.OK);
		}
		catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getMessage());
			response = new ApiResponse<ItemDTO>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<ItemDTO>>(response,HttpStatus.OK);
		}
	}

	/**
	 * API End point if a barcode belongs to a particular item
	 * @param barcode Barcode that needs to be verified
	 * @return true/false barcode valid
	 */
	@PreAuthorize("hasAnyRole('ADMIN,INVENTORYMANAGER')")
	@RequestMapping(value="/checkIfItemBarcodeValid", method=RequestMethod.GET)
	public ResponseEntity<ApiResponse<Boolean>> checkIfItemBarcodeIsValid(String barcode){
		logger.debug("Controller invoked to check if item barcode is valid");
		try{
			return new ResponseEntity<ApiResponse<Boolean>>(new ApiResponse<Boolean>(true, itemService.isBarcodeValid(barcode), null), HttpStatus.OK);
		}catch(Exception ex){
			return new ResponseEntity<ApiResponse<Boolean>>(new ApiResponse<Boolean>(false, false, ex.getMessage()), HttpStatus.OK);
		}
	}

	/**
	 * API End point to find all barcode items
	 * @return List of item counts
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value="/getAllBarcodeItems" , method=RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<ItemCountDTO>>> getAllBarcodeItems(){
		logger.debug("Controller all barcode items");
		try{
			List<ItemCountDTO> itemList = itemService.getAllBarcodeItems();
			ApiResponse<List<ItemCountDTO>> response = new ApiResponse<>(true, itemList, "Items fetched Successfully!");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getMessage());
			ApiResponse<List<ItemCountDTO>> response = new ApiResponse<>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<>(response,HttpStatus.OK);
		}
	}
	
	/**API End point to update item count storage location
	 * @param user logged in user
	 * @param itemCount Item count
	 * @return Item count
	 */
	@PreAuthorize("hasAnyRole('ADMIN,INVENTORYMANAGER')")
	@RequestMapping(value="/updateItemStorageLocationByBarcode", method=RequestMethod.POST)
	public ResponseEntity<ApiResponse<ItemCount>> updateItemStorageLocationByBarcode(@AuthenticationPrincipal UserAuthentication user, @RequestBody ItemCount itemCount){
		logger.debug("Controller invoked to update inventory levels");
		ApiResponse<ItemCount> response = null;
		try{
			itemCount = itemService.updateItemCountStorageLocationByBarcode(itemCount.getBarcode(), itemCount.getShelf().getId(),
					itemCount.getRack().getId(), itemCount.getCrate().getId());
			response = new ApiResponse<ItemCount>(true, itemCount, "Item updated Successfully!");
			return new ResponseEntity<ApiResponse<ItemCount>>(response,HttpStatus.OK);
		}
		catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getMessage());
			response = new ApiResponse<ItemCount>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<ItemCount>>(response,HttpStatus.OK);
		}
	}
	
	/**
	 * API End point to retrieve all open kits in paginated form
	 * 
	 * @param params
	 *            DataTables parameters
	 * @param user
	 *            Currently logged in user
	 * @return DTTable Kit
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/getFastMovingItems", method = RequestMethod.POST)
	public ResponseEntity<DTResult<ItemCountQuantityDTO>> getFastMovingItems(@RequestBody RequestParamsDTO request,
			@AuthenticationPrincipal UserAuthentication user) {
		logger.debug("Controller invoked to get open kits by pagination");
		DTResult<ItemCountQuantityDTO> result = null;
		try {
			if (user.hasRole("ROLE_ADMIN")) {
				result = itemService.getFastMovingItems(request);
			} else {
				result = itemService.getFastMovingItems(request);
			}
			return new ResponseEntity<DTResult<ItemCountQuantityDTO>>(result, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Exception thrown while getting kits pagination");
			return new ResponseEntity<DTResult<ItemCountQuantityDTO>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * API End point to retrieve all open kits in paginated form
	 * 
	 * @param params
	 *            DataTables parameters
	 * @param user
	 *            Currently logged in user
	 * @return DTTable Kit
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/getSlowMovingItems", method = RequestMethod.POST)
	public ResponseEntity<DTResult<ItemCountQuantityDTO>> getSlowMovingItems(@RequestBody RequestParamsDTO request,
			@AuthenticationPrincipal UserAuthentication user) {
		logger.debug("Controller invoked to get open kits by pagination");
		DTResult<ItemCountQuantityDTO> result = null;
		try {
			if (user.hasRole("ROLE_ADMIN")) {
				result = itemService.getSlowMovingItems(request);
			} else {
				result = itemService.getSlowMovingItems(request);
			}
			return new ResponseEntity<DTResult<ItemCountQuantityDTO>>(result, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Exception thrown while getting kits pagination");
			return new ResponseEntity<DTResult<ItemCountQuantityDTO>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
