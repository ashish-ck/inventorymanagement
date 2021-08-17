package com.drivojoy.inventory.servicesImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.drivojoy.inventory.dto.ItemAttributeDTO;
import com.drivojoy.inventory.dto.ItemCategoryDTO;
import com.drivojoy.inventory.dto.ItemCountDTO;
import com.drivojoy.inventory.dto.ItemCountQuantityDTO;
import com.drivojoy.inventory.dto.ItemDTO;
import com.drivojoy.inventory.dto.ItemNetworkDTO;
import com.drivojoy.inventory.dto.ItemReconcileReport;
import com.drivojoy.inventory.dto.ItemReport;
import com.drivojoy.inventory.dto.ItemRequestPair;
import com.drivojoy.inventory.dto.RequestParamsDTO;
import com.drivojoy.inventory.dto.WarehouseDTO;
import com.drivojoy.inventory.dto.WarehouseNetworkDTO;
import com.drivojoy.inventory.models.Crate;
import com.drivojoy.inventory.models.Item;
import com.drivojoy.inventory.models.ItemCount;
import com.drivojoy.inventory.models.ItemDetails;
import com.drivojoy.inventory.models.Kit;
import com.drivojoy.inventory.models.Rack;
import com.drivojoy.inventory.models.SalesOrderItemLine;
import com.drivojoy.inventory.models.Shelf;
import com.drivojoy.inventory.models.StockAdjustment;
import com.drivojoy.inventory.models.Tag;
import com.drivojoy.inventory.models.Tax;
import com.drivojoy.inventory.models.Warehouse;
import com.drivojoy.inventory.models.datatables.DTOrderDir;
import com.drivojoy.inventory.models.datatables.DTParameters;
import com.drivojoy.inventory.models.datatables.DTResult;
import com.drivojoy.inventory.repositories.ItemRepository;
import com.drivojoy.inventory.repositories.KitRepository;
import com.drivojoy.inventory.services.IAttributeService;
import com.drivojoy.inventory.services.ICrateService;
import com.drivojoy.inventory.services.IItemCategoryService;
import com.drivojoy.inventory.services.IItemService;
import com.drivojoy.inventory.services.IRackService;
import com.drivojoy.inventory.services.IShelfService;
import com.drivojoy.inventory.services.IStockAdjustmentService;
import com.drivojoy.inventory.services.ITagService;
import com.drivojoy.inventory.services.ITaxService;
import com.drivojoy.inventory.services.IUoMService;
import com.drivojoy.inventory.services.IWarehouseService;
import com.drivojoy.inventory.utils.AsyncTasksComponent;
import com.drivojoy.inventory.utils.Constants;
import com.drivojoy.inventory.utils.Constants.ItemStatus;
import com.drivojoy.inventory.utils.IDTOWrapper;
import com.drivojoy.inventory.utils.KeyValuePair;

@Component
public class ItemServiceImpl implements IItemService {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private ItemRepository itemRepository;
	@Autowired
	private KitRepository kitRepository;
	@Autowired
	private IDTOWrapper dtoWrapper;
	@Autowired
	private ITagService tagService;
	@Autowired
	private IAttributeService attributeService;
	@Autowired
	private IItemCategoryService itemCategoryService;
	@Autowired
	private IWarehouseService warehouseService;
	@Autowired
	private IStockAdjustmentService stockAdjustmentService;
	@Autowired
	private ITaxService taxService;
	@Autowired
	private IUoMService uomService;
	@Autowired
	private IShelfService shelfService;
	@Autowired
	private IRackService rackService;
	@Autowired
	private ICrateService crateService;
	@Autowired
	private AsyncTasksComponent asyncTasks;
	private final int _MAX = 100000;
	private final int _MIN = 1000;
	private final Logger logger = LoggerFactory.getLogger(ItemServiceImpl.class);
	@Override
	public ItemDTO createItem(ItemDTO itemDTO) {
		Item item = dtoWrapper.convert(itemDTO);
		item.setCreatedDttm(Calendar.getInstance().getTime());
		item.setLastModDttm(Calendar.getInstance().getTime());
		item.setActive(true);
		logger.debug("creating item : " + item.toString());
		// We dont want to save the item count details as of yet
		// Count details should be saved as a Stock adjustment entry
		// Lets create an item first with its count details
		Collection<ItemCount> countDetails = new ArrayList<>();
		for (ItemCount count : item.getItemCount()) {
			countDetails.add(count);
			count.setItemCode(item.getCode());
			item.getItemDetails().add(new ItemDetails(null, null, null, null, 0, count.getBarcode(), 0,
					count.getWarehouse(), item.getCode(), ItemStatus.AVAILABLE.value(), null, null, 0, 0));
		}
		item.getItemCount().clear();
		item = updateItem(item);
		if (item != null) {
			if (countDetails.size() > 0) {
				for (ItemCount count : countDetails) {
					count.setItemCode(item.getCode());
					Collection<SalesOrderItemLine> items = new ArrayList<>();
					items.add(new SalesOrderItemLine(item.getCode(), item.getDescription(), item.getVendorAlias(), 
							count.getWarehouse(), null, null, null, count.getBarcode(), null,
							ItemStatus.AVAILABLE.value(), ItemStatus.SCRAP_NOT_AVAILABLE.value(), count.getUnitPrice(),
							count.getWholesalePrice()));
					StockAdjustment order = new StockAdjustment(0, null, Calendar.getInstance().getTime(),
							Constants._STATUS_OPEN, items, null, null, 0, count.getWarehouse());
					stockAdjustmentService.save(order);
				}
			}
			updateItem(item);
			return dtoWrapper.convert(item);
		}
		throw new RuntimeException(Constants._GENERIC_ERROR_MESSAGE);
	}

	@Override
	public Item createRandomItem(String description) {
		ItemDTO item = new ItemDTO();
		item.setActive(true);
		item.setCategory(dtoWrapper.convert(itemCategoryService.getItemCategoryById(1)));
		item.setDescription(description);
		item.setUnitOfMeasurement(uomService.getDefaultUnit().getNotation());
		String itemCode = "sample";
		synchronized (itemCode) {
			Random r = new Random();
			long id = 0;
			boolean isUnique = false;
			while (!isUnique) {
				id = r.ints(_MIN, (_MAX + 1)).limit(1).findFirst().getAsInt();
				if (itemRepository.findOne(id) == null) {
					isUnique = true;
				}
			}
			itemCode = Long.toString(id);
		}
		item.setCode(itemCode);
		item.setBarcode(itemCode);
		item = this.createItem(item);
		return dtoWrapper.convert(item);
	}

	@Override
	public ItemDTO editItem(ItemDTO itemDTO) {
		Item updatedItem = dtoWrapper.convert(itemDTO);
		// Item oldItem = itemRepository.findOne(itemDTO.getId());
		updatedItem.setLastModDttm(Calendar.getInstance().getTime());
		logger.debug("editing item : " + updatedItem.toString());
		try {
			updatedItem = updateItem(updatedItem);
			if (updatedItem != null) {
				// No need to do this here. We wont be updating item quantity
				// from an item edit page
				// inventoryOperationsService.fulfillStockAdjustment(updatedItem,
				// oldItem.getItemCount());
				return dtoWrapper.convert(updatedItem);
			}
			return dtoWrapper.convert(updatedItem);
		} catch (Exception ex) {
			logger.error("Error thrown while editing item : " + ex.getMessage());
			return null;
		}
	}

	@Override
	public Item updateItem(Item item) {
		try {
			try {
				if (tagService.createTags(item.getTags()) != null) {
					item = itemRepository.save(item);
					em.flush();
					return item;
				}
				throw new RuntimeException("Item Was not saved!");
			} catch (Exception ex) {
				logger.error("Error thrown while creating item : " + ex.getMessage());
				throw new RuntimeException(ex.getMessage());
			}
		} catch (OptimisticLockException ex) {
			logger.error("concurrent access to this item is detected " + ex.getMessage());
			throw new ConcurrencyFailureException(
					"Item NOT save. This is not the latest copy of the item you are trying to edit, please refresh to get latest copy!");
		} catch (Exception ex) {
			logger.error("Exception thwon while updating item :" + ex.getMessage());
			throw new RuntimeException(Constants._GENERIC_ERROR_MESSAGE);
		}
	}

	@Override
	public ItemDTO deactivateItem(String itemCode) {
		try {
			Item item = getItemByCode(itemCode);
			if (item != null) {
				item.setActive(false);
				return dtoWrapper.convert(updateItem(item));
			} else
				return null;
		} catch (Exception ex) {
			logger.error("Exception thrown while deactivating item " + ex.getMessage());
			return null;
		}
	}

	@Override
	public ItemDTO reactivateItem(String itemCode) {
		try {
			Item item = getItemByCode(itemCode);
			if (item != null) {
				item.setActive(true);
				return dtoWrapper.convert(updateItem(item));
			} else
				return null;
		} catch (Exception ex) {
			logger.error("Exception thrown while deactivating item " + ex.getMessage());
			return null;
		}
	}

	@Override
	public Item getItemById(long itemId) {
		logger.debug("Fetching details for item with id :" + itemId);
		Item item = itemRepository.findById(itemId);
		logger.debug("Details fetched: \n" + item.toString());
		return item;
	}

	@Override
	public Item getItemByCode(String itemCode) {
		return itemRepository.findByCode(itemCode);
	}

	@Override
	public ItemDTO getItemDTOByCode(String itemCode) {
		Item item = itemRepository.findByCode(itemCode);
		if (item != null) {
			return dtoWrapper.convert(item);
		}
		return null;
	}

	@Deprecated
	@Override
	public Item getItemByBarcode(String barcode) {
		return itemRepository.findItemByBarcode(barcode);
	}

	@Override
	public double getQuantityInHandByWarehouse(Warehouse warehouse, Item item) {
		Collection<ItemCount> itemCount = item.getItemCount();
		double quantity = 0;
		for (ItemCount count : itemCount) {
			if (count.getWarehouse().equals(warehouse)) {
				quantity++;
			}
		}
		return quantity;
	}

	@Override
	public double getQuantityInHandByWarehouse(Warehouse warehouse, String itemCode) {
		Item item = getItemByCode(itemCode);
		return getQuantityInHandByWarehouse(warehouse, item);
	}

	@Override
	public double getQuantityAvailableByWarehouse(Warehouse warehouse, String itemCode) {
		Item item = getItemByCode(itemCode);
		return getQuantityAvailableByWarehouse(warehouse, item);
	}

	@Override
	public List<ItemDTO> getAllActiveItems() {
		try {
			logger.debug("Request received for all items");
			List<Item> itemList = itemRepository.findAll();
			return dtoWrapper.convertToItemDTOList(itemList);
		} catch (Exception ex) {
			return null;
		}
	}

	@Override
	public ItemDTO getItemDTOById(long itemId) {
		try {
			return dtoWrapper.convert(getItemById(itemId));
		} catch (Exception ex) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ItemDTO> getItemsByCategory(long id) {
		List<ItemCategoryDTO> itemCategories = itemCategoryService.getCategoriesWithParent(id);
		String ids = "";
		for (ItemCategoryDTO itemCategory : itemCategories) {
			ids = ids + "," + itemCategory.getId();
		}
		if (!"".equals(ids)) {
			ids = ids.substring(1);
			String selectQuery = "SELECT * FROM item WHERE category IN(" + ids + ")";
			Query nativeQuery = em.createNativeQuery(selectQuery, Item.class);
			List<Item> items = nativeQuery.getResultList();
			List<ItemDTO> resultSet = new ArrayList<>();
			for (Item item : items) {
				resultSet.add(dtoWrapper.convert(item));
			}
			return resultSet;
		}
		return null;
	}

	@Override
	public String generateBarcode() {
		Random r = new Random();
		String set = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String barcode = "";
		for (int i = 0; i < 6; i++) {
			barcode += set.charAt(r.nextInt(set.length()));
		}
		Integer count = 0;//itemRepository.doesBarcodeExists(barcode);
		if (count == null || count == 0) 
			return barcode;
		else
			return generateBarcode();
	}

	@Override
	public Item updateItemCountAndItemDetails(Warehouse warehouse, int status, Item item) {
		for (ItemCount count : item.getItemCount()) {
			if (count.getWarehouse().equals(warehouse)) {
				count.setStatus(status);
				// item = updateItem(item);
				break;
			}
		}
		for (ItemDetails itemDetails : item.getItemDetails()) {
			if (itemDetails.getWarehouse().equals(warehouse)) {
				itemDetails.setStatus(status);
				// item = updateItem(item);
				break;
			}
		}
		// String barcode = generateBarcode(warehouse);
		// item.getItemCount().add(new ItemCount(warehouse, item.getCode(),
		// barcode, status, 0, 0, 0, null, null, null));
		// item.getItemDetails().add(new ItemDetails(warehouse, item.getCode(),
		// barcode, status, 0, 0, 0, null, null, null));
		return updateItem(item);
	}

	@Override
	public List<ItemDTO> getWarningPointItems(String warehouse) {
		if (warehouse != null) {
			return dtoWrapper.convertToItemDTOList(
					itemRepository.getWarningItems(warehouseService.getWarehouseByCode(warehouse).getId()));
		} else {
			return dtoWrapper.convertToItemDTOList(itemRepository.getWarningItems());
		}
	}

	@Override
	public List<ItemDTO> getReorderPointItems(String warehouse) {
		if (warehouse != null) {
			return dtoWrapper.convertToItemDTOList(
					itemRepository.getReorderItems(warehouseService.getWarehouseByCode(warehouse).getId()));
		} else {
			return dtoWrapper.convertToItemDTOList(itemRepository.getReorderItems());
		}
	}

	@Override
	public String getInventoryCountSheetByWarehouse(String warehouse, String userRef) {
		try {
			asyncTasks.getInventoryCountSheetByWarehouse(userRef, warehouse);
			return "Report " + userRef + " will be generated shortly!";
		} catch (Exception ex) {
			logger.error("Exception thrown while generating inventory count sheet: " + ex.getMessage());
			throw new RuntimeException("Error: Cannot schedule report job. Please try again!");
		}
	}

	@Override
	public String getInventoryCountSheet(String userRef) {
		try {
			asyncTasks.getInventoryCountSheet(userRef);
			return "Report " + userRef + " will be generated shortly!";
		} catch (Exception ex) {
			logger.error("Exception thrown while generating inventory count sheet: " + ex.getMessage());
			throw new RuntimeException("Error: Cannot schedule report job. Please try again!");
		}
	}

	@Override
	public List<ItemDTO> getItemsDTOByCriteria(List<KeyValuePair<String, List<String>>> criterias, int mode) {
		return dtoWrapper.quickConvertToItemDTOList(getItemsByCriteria(criterias, mode));
	}

	@Override
	public List<ItemCount> getItemCountsByCriteria(List<KeyValuePair<String, List<String>>> criterias, int mode,
			long warehouseId) {
		String code = "";
		for (KeyValuePair<String, List<String>> criteria : criterias) {
			if (criteria.getKey().equalsIgnoreCase("barcode")) {
				code = criteria.getValue().get(0);
				break;
			}
			if (criteria.getKey().equalsIgnoreCase("code")) {
				code = criteria.getValue().get(0);
				break;
			}
		}
		List<ItemCount> itemCounts = dtoWrapper
				.convertObjectToItemCount(itemRepository.getItemCountsByBarcodeOrCode(code, warehouseId));
		return itemCounts;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Item> getItemsByCriteria(List<KeyValuePair<String, List<String>>> criterias, int mode) {
		String selectQuery = "Select ";
		String selectWhat = "item.* ";
		String from = "from item ";
		String where = "where ";
		String whereCriterias = "";
		String joinWith = "";
		String groupBy = "group by (item.id) ";
		String queryType = "";
		String query = "";
		switch (mode) {
		case Constants._ADD_MODE:
			queryType = "AND ";
			/* note that the space at the end is important */ break;
		case Constants._OR_MODE:
			queryType = "OR ";
			/* note that the space at the end is important */ break;
		default:
			queryType = "OR ";
			break;
		}
		try {
			for (KeyValuePair<String, List<String>> criteria : criterias) {
				if (criteria.getKey().equalsIgnoreCase("barcode")) {
					joinWith += "LEFT JOIN `item_details` ON item_details.item_id = item.id ";
					if (!whereCriterias.isEmpty()) {
						whereCriterias += queryType;
					}
					whereCriterias += "(item.barcode LIKE '%" + criteria.getValue().get(0) + "%' "
							+ "OR item_details.barcode = '" + criteria.getValue().get(0) + "') ";
					continue;
				}
				if (criteria.getKey().equalsIgnoreCase("code")) {
					if (!whereCriterias.isEmpty()) {
						whereCriterias += queryType;
					}
					whereCriterias += "item.code LIKE '%" + criteria.getValue().get(0) + "%' ";
					continue;
				}
				if (criteria.getKey().equalsIgnoreCase("alias")) {
					if (!whereCriterias.isEmpty()) {
						whereCriterias += queryType;
					}
					whereCriterias += "item.vendor_alias LIKE '%" + criteria.getValue().get(0) + "%' ";
					continue;
				}
				if (criteria.getKey().equalsIgnoreCase("category")) {
					List<String> categoryList = criteria.getValue();
					List<ItemCategoryDTO> categoryDTOs = null;
					String categoryIds = "";
					for (String categoryName : categoryList) {
						categoryDTOs = itemCategoryService.getItemCategoryByNameContaining(categoryName);
						for (ItemCategoryDTO category : categoryDTOs) {
							if (!categoryIds.isEmpty()) {
								categoryIds += ", ";
							}
							categoryIds += category.getId();
						}
					}
					if (!categoryIds.isEmpty()) {
						if (!whereCriterias.isEmpty()) {
							whereCriterias += queryType;
						}
						whereCriterias += "item.category IN (" + categoryIds + ") ";
					}
					continue;
				}
				if (criteria.getKey().equalsIgnoreCase("tags")) {
					List<String> tagList = criteria.getValue();
					String tagValues = "";
					for (String tag : tagList) {
						if (!tagValues.isEmpty())
							tagValues += ", ";
						tagValues += tag;
					}
					joinWith += "LEFT JOIN `item_tags` ON item_tags.item = item.id ";
					if (!whereCriterias.isEmpty()) {
						whereCriterias += queryType;
					}
					whereCriterias += "item_tags.tags IN (SELECT id from `tag` where name in ('" + tagValues + "')) ";
					continue;
				}
				if (criteria.getKey().equalsIgnoreCase("tags-like")) {
					String tagValue = criteria.getValue().get(0);
					joinWith += "LEFT JOIN `item_tags` ON item_tags.item = item.id ";
					if (!whereCriterias.isEmpty()) {
						whereCriterias += queryType;
					}
					whereCriterias += "item_tags.tags IN (SELECT id from `tag` where name LIKE ('%" + tagValue
							+ "%')) ";
					continue;
				}
				if (criteria.getKey().equalsIgnoreCase("warehouse")) {
					List<String> warehouseList = criteria.getValue();
					String warehouseNames = "";
					for (String warehouse : warehouseList) {
						if (!warehouseNames.isEmpty())
							warehouseNames += ", ";
						warehouseNames += ("'" + warehouse + "'");
					}

					if (!whereCriterias.isEmpty()) {
						whereCriterias += queryType;
					}
					whereCriterias += "item_count.warehouse IN (SELECT id from `warehouse` where name in ("
							+ warehouseNames + ") ) ";
					continue;
				}
				if (criteria.getKey().startsWith("attribute-")) {
					StringBuilder sb = new StringBuilder(criteria.getKey());
					String attributeName = sb.substring(10, criteria.getKey().length());
					List<String> attributeNames = Arrays.asList(attributeName.split(","));
					String attributeId = "";
					for (String attribute : attributeNames) {
						if (!attributeId.isEmpty()) {
							attributeId += ",";
						}
						attributeId += attributeService.getAttributeByName(attribute).getId();
					}
					List<String> attributeValues = criteria.getValue();
					String attributes = "";
					for (String value : attributeValues) {
						if (!attributes.isEmpty())
							attributes += ", ";
						attributes += ("'" + value + "'");
					}
					joinWith += "LEFT JOIN `item_attributes` ON item_attributes.item_id = item.id "
							+ "LEFT JOIN `item_attribute` ON item_attributes.attributes = item_attribute.id "
							+ "LEFT JOIN attribute ON item_attributes.attributes = attribute.id "
							+ "LEFT JOIN item_attribute_values ON item_attribute_values.item_attribute_id = item_attributes.attributes ";
					if (!whereCriterias.isEmpty()) {
						whereCriterias += "AND";
					}
					whereCriterias += "(attribute IN (" + attributeId + ") " + "AND (item_attribute_values.value IN ("
							+ attributes + ") OR item_attribute_values.value = 'all' )) ";
					continue;
				}
			}
			query = selectQuery + selectWhat + from;
			joinWith += "LEFT JOIN `item_count` ON item_count.item_id = item.id ";
			query += joinWith;
			if (!whereCriterias.isEmpty()) {
				query += (where + whereCriterias);
			}
			query += groupBy;
			query += " LIMIT 100";
			logger.info("Executing query : " + query);
			Query nativeQuery = em.createNativeQuery(query, Item.class);
			List<Item> resultSet = nativeQuery.getResultList();
			return resultSet;
		} catch (Exception ex) {
			logger.error("Exception thrown while executing query : " + query);
			logger.error("Exception thrown was : " + ex.getCause());
			throw new RuntimeException(ex.getMessage());
		}
	}

	@Override
	public List<ItemDTO> importItemsNew(String fileName) {
		File inputFile = null;
		FileInputStream inputStream = null;
		List<ItemDTO> items = new ArrayList<>();
		try {
			inputFile = new File(fileName);
			inputStream = new FileInputStream(inputFile);
			org.apache.poi.ss.usermodel.Workbook workbook = WorkbookFactory.create(inputStream);
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(i);
				Iterator<Row> iterator = sheet.iterator();
				Row row = null;
				while (iterator.hasNext()) {
					row = iterator.next();
					List<ItemCount> itemCounts = new ArrayList<>();
					Iterator<Cell> cellIterator = row.cellIterator();
					List<String> strings = new ArrayList<>();
					for (Cell cell = null; cellIterator.hasNext();) {
						cell = cellIterator.next();
						cell.setCellType(Cell.CELL_TYPE_STRING);
						strings.add(cell.getStringCellValue());
					}
					String itemCode = strings.get(0);
					Item item = itemRepository.findByCode(itemCode);
					Double unitPrice = Double.parseDouble(strings.get(1));
					Double wholesalePrice = Double.parseDouble(strings.get(2));
					String warehouseName = strings.get(3);
					Warehouse warehouse = warehouseService.getWarehouseByName(warehouseName);
					String shelfName = strings.get(4);
					String rackName = strings.get(5);
					String crateName = strings.get(6);
					Double quantity = Double.parseDouble(strings.get(7));
					Double reorderPoint = Double.parseDouble(strings.get(8));
					int status = ItemStatus.AVAILABLE.value();
					Shelf shelf = null;
					Rack rack = null;
					Crate crate = null;
					if (warehouse != null) {
						shelf = shelfService.getShelfByNameAndWarehouseId(shelfName, warehouse.getId());
						if (shelf == null)
							shelf = shelfService.addShelf(warehouse.getId(),
									new Shelf(0, shelfName, shelfName, new ArrayList<>()));
					}
					if (shelf != null) {
						rack = rackService.getRackByNameAndShelfId(rackName, shelf.getId());
						if (rack == null)
							rack = rackService.addRack(shelf.getId(),
									new Rack(0, rackName, rackName, new ArrayList<>()));
					}
					if (rack != null) {
						crate = crateService.getCrateByNameAndRackId(crateName, rack.getId());
						if (crate == null)
							crate = crateService.addCrate(rack.getId(), new Crate(0, crateName, crateName));
					}
					for (int j = 0; j < quantity; j++) {
						String barcode = generateBarcode();
						ItemCount itemCount = new ItemCount(warehouse, itemCode, item.getDescription(), item.getVendorAlias(), barcode, status, reorderPoint,
								unitPrice, wholesalePrice, shelf, rack, crate);
						itemCounts.add(itemCount);
					}
					if (item != null) {
						item.getItemCount().addAll(itemCounts);
						item = itemRepository.save(item);
						items.add(dtoWrapper.convert(item));
					} else {
						logger.error("Item code not present: " + itemCode);
					}
				}
			}
			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return items;
	}

	@Override
	public List<ItemDTO> importItems(String fileName) throws IOException {
		List<ItemDTO> createdItems = new ArrayList<>();
		File inputFile = null;
		FileInputStream inputStream = null;
		try {
			inputFile = new File(fileName);
			inputStream = new FileInputStream(inputFile);
		} catch (FileNotFoundException ex) {
			logger.debug("File not found " + ex.getMessage());
			throw new FileNotFoundException("The required file was not found!");
		}
		Workbook workbook = null;
		try {
			workbook = new XSSFWorkbook(inputStream);
		} catch (IOException ex) {
			logger.debug("Unable to open/read file " + ex.getMessage());
			throw new IOException("Unable to open/read file!");
		}
		Sheet firstSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = firstSheet.iterator();
		Row nextRow = iterator.next();
		int colNo = nextRow.getPhysicalNumberOfCells();
		int count = 0;
		while (iterator.hasNext()) {
			ItemDTO itemDTO = new ItemDTO();
			nextRow = iterator.next();
			if (nextRow.getCell(0) == null) {
				break;
			}
			ItemCategoryDTO category = null;
			if (nextRow.getCell(Constants._CATEGORY_COL) == null) {
				category = itemCategoryService.getItemCategoryByName("Default Category");
			} else {
				category = itemCategoryService
						.getItemCategoryByName(nextRow.getCell(Constants._CATEGORY_COL).getStringCellValue());
			}
			if (category == null) {
				break;
			}
			if (nextRow.getCell(Constants._QTY_COL).getNumericCellValue() > 0) {
				if (nextRow.getCell(Constants._WAREHOUSE_COL).getStringCellValue() != null) {
					String warehouseName = nextRow.getCell(Constants._WAREHOUSE_COL).getStringCellValue();
					Warehouse warehouse = warehouseService.getWarehouseByName(warehouseName);
					Collection<ItemCountDTO> itemCounts = new ArrayList<>();
					int qty = (int) nextRow.getCell(Constants._QTY_COL).getNumericCellValue();
					WarehouseDTO warehouseDTO = new WarehouseDTO();
					warehouseDTO.setId(warehouse.getId());
					warehouseDTO.setName(warehouse.getName());
					warehouseDTO.setCode(warehouse.getCode());
					warehouseDTO.setCity(warehouse.getAddress().getCity());
					warehouseDTO.setCountry(warehouse.getAddress().getCountry());
					warehouseDTO.setLocation(warehouse.getAddress().getLocation());
					warehouseDTO.setMotherWarehouse(warehouse.isParentWarehouse());
					warehouseDTO.setAddressLine1(warehouse.getAddress().getAddressLine1());
					warehouseDTO.setAddressLine2(warehouse.getAddress().getAddressLine2());
					warehouseDTO.setPincode(warehouse.getAddress().getPincode());
					warehouseDTO.setState(warehouse.getAddress().getState());
					warehouseDTO.setMotherWarehouseCode(warehouse.getMotherWarehouse().getCode());
					warehouseDTO.setVersion(warehouse.getVersion());
					WarehouseNetworkDTO warehouseNetworkDTO = new WarehouseNetworkDTO();
					warehouseNetworkDTO.setId(warehouseDTO.getId());
					warehouseNetworkDTO.setName(warehouse.getName());
					warehouseNetworkDTO.setCode(warehouse.getCode());
					for (int i = 0; i < qty; i++) {
						itemCounts.add(new ItemCountDTO(warehouseNetworkDTO,
								nextRow.getCell(Constants._CODE_COL).getStringCellValue(), itemDTO.getDescription(), itemDTO.getVendorAlias(), generateBarcode(),
								ItemStatus.AVAILABLE.value(), 0, 0, 0, null, null, null));
					}
					itemDTO.setItemCount(itemCounts);
				} else {
					workbook.close();
					inputStream.close();
					logger.error("Warehouse name is required if adding quantity!");
					throw new RuntimeException("Warehouse Name is needed!");
				}
			}
			nextRow.getCell(Constants._CODE_COL).setCellType(Cell.CELL_TYPE_STRING);
			nextRow.getCell(Constants._BARCODE_COL).setCellType(Cell.CELL_TYPE_STRING);
			nextRow.getCell(Constants._VENDORALIAS_COL).setCellType(Cell.CELL_TYPE_STRING);
			itemDTO.setId(0);
			itemDTO.setCode(nextRow.getCell(Constants._CODE_COL).getStringCellValue());
			itemDTO.setUnitOfMeasurement(nextRow.getCell(Constants._UNIT_COL).getStringCellValue());
			itemDTO.setCategory(category);
			itemDTO.setBarcode(nextRow.getCell(Constants._BARCODE_COL).getStringCellValue());
			itemDTO.setActive(true);
			if (nextRow.getCell(Constants._DESCRIPTION_COL) != null) {
				itemDTO.setDescription(nextRow.getCell(Constants._DESCRIPTION_COL).getStringCellValue());
			} else {
				itemDTO.setDescription("Common Part");
			}
			if (nextRow.getCell(Constants._VENDORALIAS_COL) != null) {
				try {
					itemDTO.setVendorAlias(nextRow.getCell(Constants._VENDORALIAS_COL).getStringCellValue());
				} catch (Exception ex) {
					logger.warn("Not a string value...trying to parse as a number!");
					itemDTO.setVendorAlias(
							Double.toString(nextRow.getCell(Constants._VENDORALIAS_COL).getNumericCellValue()));
				}
			}
			if (nextRow.getCell(Constants._WARNINGPT_COL) != null) {
				itemDTO.setWarningPoint(nextRow.getCell(Constants._WARNINGPT_COL).getNumericCellValue());
			}
			if (nextRow.getCell(Constants._REORDERPT_COL) != null) {
				itemDTO.setReorderPoint(nextRow.getCell(Constants._REORDERPT_COL).getNumericCellValue());
			}
			if (nextRow.getCell(Constants._REORDERQTY_COL) != null) {
				itemDTO.setReorderPoint(nextRow.getCell(Constants._REORDERQTY_COL).getNumericCellValue());
			}
			itemDTO.setTaxable(true);
			nextRow.getCell(Constants._PRICE_COL).setCellType(Cell.CELL_TYPE_NUMERIC);
			if (nextRow.getCell(Constants._PRICE_COL) != null
					|| nextRow.getCell(Constants._PRICE_COL).getNumericCellValue() > 0) {
				if (nextRow.getCell(Constants._TAX_COL) != null) {
					nextRow.getCell(Constants._TAX_COL).setCellType(Cell.CELL_TYPE_NUMERIC);
					Tax tax = taxService.getByCode("VAT" + nextRow.getCell(Constants._TAX_COL).getNumericCellValue());
					itemDTO.getTaxes().add(tax);
					itemDTO.setWholesalePrice((100 / (100 + nextRow.getCell(Constants._TAX_COL).getNumericCellValue()))
							* nextRow.getCell(Constants._PRICE_COL).getNumericCellValue());
				}
				itemDTO.setNormalPrice(nextRow.getCell(Constants._PRICE_COL).getNumericCellValue());
			}
			if (nextRow.getCell(Constants._TAGS_COL) != null) {
				String sb = new String(nextRow.getCell(Constants._TAGS_COL).getStringCellValue());
				List<String> tagValues = Arrays.asList(sb.split(","));
				Collection<Tag> tags = new ArrayList<>();
				for (String tag : tagValues) {
					Tag currentTag = tagService.getTagByValue(tag);
					if (currentTag == null) {
						currentTag = new Tag(0, tag, 0);
					}
					tags.add(currentTag);
				}
				itemDTO.setTags(tags);
			}
			try {
				for (int i = Constants._QTY_COL + 3; i < colNo; i++) {
					if (nextRow.getCell(i) != null) {
						String sb = new String(nextRow.getCell(i).getStringCellValue());
						ItemAttributeDTO attribute = new ItemAttributeDTO(0,
								attributeService
										.getAttributeByName(firstSheet.getRow(0).getCell(i).getStringCellValue()),
								Arrays.asList(sb.split(",")), 0);
						itemDTO.getItemAttributes().add(attribute);
					}
				}
			} catch (Exception ex) {
				logger.error("Exception thrown while setting attribute values : " + ex.getMessage());
				workbook.close();
				inputStream.close();
				throw new RuntimeException(ex.getMessage());
			}
			itemDTO.setCreatedDttm(Calendar.getInstance().getTime());
			itemDTO.setSerialized(nextRow.getCell(Constants._SERIALIZED_COL).getBooleanCellValue());
			logger.error("Saving item: " + itemDTO.getCode());
			createdItems.add(createItem(itemDTO));
			if (itemDTO.getCode().equals("99131K230950")) {
				logger.debug("Found!");
			}
			if (count == 15) {
				em.flush();
				count = 0;
			}
			count++;
		}
		workbook.close();
		inputStream.close();
		try {
			inputFile.delete();
		} catch (Exception ex) {
			logger.warn("File not deleted!");
		}
		return createdItems;
	}

	@Override
	public void reserveItems(List<ItemRequestPair> requests) {
		updateItemCount(requests, Constants._RESERVED);
	}

	@Override
	public void issueItems(List<ItemRequestPair> requests) {
		updateItemCount(requests, Constants._ISSUED);
	}

	@Override
	public void restockItems(List<ItemRequestPair> requests) {
		updateItemCount(requests, Constants._RESTOCK);
	}

	@Override
	public void invoiceItems(List<ItemRequestPair> requests) {
		updateItemCount(requests, Constants._INVOICED);
	}

	@Override
	public void receiveItems(List<ItemRequestPair> requests) {
		updateItemCount(requests, Constants._AVAILABLE);
	}

	@Override
	public ItemDTO getItemDetails(long id) {
		Item item = itemRepository.getItemDetails(id);
		return dtoWrapper.convert(item);
	}

	@Override
	public void updateItemDetailsCountLocationStatus(String barcode, Warehouse warehouse, int status) {
		itemRepository.updateItemDetailsCountLocationStatus(barcode, status, warehouse.getId());
	}

	@Override
	public void updateItemDetails(String barcode, Warehouse warehouse, int status) {
		itemRepository.updateItemDetails(barcode, status, warehouse.getId());
	}

	@Override
	public Page<Item> getAll(Pageable page) {
		return itemRepository.findAll(page);
	}

	@SuppressWarnings("unchecked")
	@Override
	public DTResult<ItemDTO> getItemsPagination(DTParameters param) {
		DTResult<ItemDTO> result = new DTResult<>();
		result.draw = param.draw;
		result.recordsTotal = itemRepository.count();
		String select = "SELECT * FROM item ";
		String count = "SELECT COUNT(*) FROM item ";
		String where = "";
		if (param.search.value != null && !param.search.value.isEmpty()) {
			where = "WHERE code LIKE '%" + param.search.value + "%' OR " + "description LIKE '%" + param.search.value
					+ "%' ";
		}
		String limit = "LIMIT " + param.length + " ";
		String offset = "OFFSET " + param.start + " ";
		List<Item> resultSet = new ArrayList<>();
		try {
			if (param.search.value != null && !param.search.value.isEmpty()) {
				String selectQuery = select + where + limit + offset;
				Query nativeQuery = em.createNativeQuery(selectQuery, Item.class);
				resultSet = nativeQuery.getResultList();
				String countQuery = count + where;
				Query nativeCountQuery = em.createNativeQuery(countQuery);
				result.recordsFiltered = ((BigInteger) nativeCountQuery.getSingleResult()).intValue();
			} else {
				String selectQuery = select + where + limit + offset;
				Query nativeQuery = em.createNativeQuery(selectQuery, Item.class);
				resultSet = nativeQuery.getResultList();
				result.recordsFiltered = result.recordsTotal;
			}
		} catch (Exception ex) {
			logger.error("Exception thrown while executing query : " + ex.getMessage());
			throw new RuntimeException(ex.getMessage());
		}
		result.data = dtoWrapper.convertToItemDTOList(resultSet);
		return result;
	}

	@Override
	public ItemDTO updateInventoryLevels(ItemDTO itemDTO) {
		Item newItem = dtoWrapper.convert(itemDTO);
		Item existingItem = getItemById(newItem.getId());
		if (newItem.getVersion() != existingItem.getVersion()) {
			throw new OptimisticLockException("Item has been edited by someone. This is not the latest copy!");
		}
		Collection<ItemCount> currentItemCount = existingItem.getItemCount();
		HashMap<Long, ItemCount> warehouseCountDict = new HashMap<>(currentItemCount.size());
		for (ItemCount count : currentItemCount) {
			warehouseCountDict.put(count.getWarehouse().getId(), count);
		}
		List<ItemCount> newItemCount = new ArrayList<>();
		for (ItemCount count : newItem.getItemCount()) {
			if (warehouseCountDict.containsKey(count.getWarehouse().getId())) {
				ItemCount existingCount = warehouseCountDict.get(count.getWarehouse().getId());
				existingCount.setReorderPoint(count.getReorderPoint());
				newItemCount.add(existingCount);
			} else {
				newItemCount.add(count);
			}
		}
		existingItem.setItemCount(newItemCount);
		updateItem(existingItem);
		return dtoWrapper.convert(existingItem);
	}

	@Override
	public boolean isBarcodeValid(String barcode) {
		if (getItemByBarcode(barcode) != null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isItemBarcodeValid(String itemCode, String barcode) {
		Item item = getItemByBarcode(barcode);
		if (item != null) {
			if (itemCode.equalsIgnoreCase(item.getCode())) {
				return true;
			}
		}
		return false;
	}

	/*************** PRIVATE METHODS ***************/

	private void updateItemCount(List<ItemRequestPair> requests, int status) {
		try {
			for (ItemRequestPair request : requests) {
				Item item = getItemByCode(request.getItemCode());
				ItemCount count = null;
				for (ItemCount cnt : item.getItemCount()) {
					if (cnt.getWarehouse().getName().equalsIgnoreCase(request.getWarehouse())
							&& cnt.getBarcode().equals(request.getBarcode())) {
						count = cnt;
						item.getItemCount().remove(cnt);
						break;
					}
				}
				Collection<ItemStatus> preconditons = new ArrayList<>();
				boolean satisfied = false;
				if (count != null) {
					switch (ItemStatus.getStatus(status)) {
					case AVAILABLE:
						satisfied = false;
						preconditons = ItemStatus.AVAILABLE.preConditions();
						for (ItemStatus itemStatus : preconditons) {
							if (itemStatus.value() == request.getStatus()) {
								satisfied = true;
								break;
							}
						}
						if (satisfied) {
							count.setStatus(ItemStatus.AVAILABLE.value());
						}
						break;
					case RESERVED:
						satisfied = false;
						preconditons = ItemStatus.RESERVED.preConditions();
						for (ItemStatus itemStatus : preconditons) {
							if (itemStatus.value() == request.getStatus()) {
								satisfied = true;
								break;
							}
						}
						if (satisfied) {
							count.setStatus(ItemStatus.RESERVED.value());
						}
						break;
					case ISSUED:
						satisfied = false;
						preconditons = ItemStatus.ISSUED.preConditions();
						for (ItemStatus itemStatus : preconditons) {
							if (itemStatus.value() == request.getStatus()) {
								satisfied = true;
								break;
							}
						}
						if (satisfied) {
							count.setStatus(ItemStatus.ISSUED.value());
						}
						break;
					case INVOICED:
						satisfied = false;
						preconditons = ItemStatus.INVOICED.preConditions();
						for (ItemStatus itemStatus : preconditons) {
							if (itemStatus.value() == request.getStatus()) {
								satisfied = true;
								break;
							}
						}
						if (satisfied) {
							count.setStatus(ItemStatus.INVOICED.value());
						}
						break;
					case OUT_OF_STOCK:
						break;
					default:
						break;
					}
					item.getItemCount().add(count);
					updateItem(item);
					if (item.isSerialized()) {
						itemRepository.updateItemStatusLocation(request.getBarcode(), status,
								warehouseService.getWarehouseByName(request.getWarehouse()).getId());
					}
					if (satisfied) {
						itemRepository.updateItemDetails(request.getBarcode(), request.getStatus(),
								warehouseService.getWarehouseByName(request.getWarehouse()).getId());
					}
				} else {
					throw new Exception("Bad Request");
				}
			}
			return;
		} catch (Exception ex) {
			logger.error("Exception thrown while updating item status : " + ex.getMessage());
			throw new RuntimeException(ex.getMessage());
		}
	}

	private double getQuantityAvailableByWarehouse(Warehouse warehouse, Item item) {
		Collection<ItemCount> itemCount = item.getItemCount();
		double quantity = 0;
		for (ItemCount count : itemCount) {
			if (count.getWarehouse().equals(warehouse)) {
				quantity++;
			}
		}
		return quantity;
	}

	public List<ItemCount> getAvailableItemCountByCodeAndWarehouse(String itemCode, long warehouseId) {
		List<Object[]> items = new ArrayList<>();
		try {
			items = itemRepository.getItemCountByItemCodeAndWarehouseAndStatus(itemCode, warehouseId,
					ItemStatus.AVAILABLE.value());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dtoWrapper.convertObjectToItemCount(items);
	}

	@Override
	public void setItemCountStatusByBarcode(int status, String barcode) {
		try {
			itemRepository.updateItemCountStatusByBarcode(status, barcode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateKitSubkitItemDetailsByBarcode(long kitId, int status, long subkitId, String barcode) {
		// itemRepository.updateItemKitItemDetailsByBarcode(kitId, status,
		// subkitId, barcode);
		// itemRepository.updateItemSubkitItemDetailsByBarcode(kitId, status,
		// subkitId, barcode);
	}

	@Override
	public List<ItemCountDTO> getItemsByWarehouseId(long id) {
		return dtoWrapper.convertObjectToItemCountDTO(itemRepository.findItemCountByWarehouse(id));
	}

	@Override
	public List<ItemCountDTO> getItemsInShelf(long id) {
		return dtoWrapper.convertObjectToItemCountDTO(itemRepository.findItemCountByShelf(id));
	}

	@Override
	public List<ItemCountDTO> getItemsInRack(long id) {
		return dtoWrapper.convertObjectToItemCountDTO(itemRepository.findItemCountByRack(id));
	}

	@Override
	public List<ItemCountDTO> getItemsInCrate(long id) {
		return dtoWrapper.convertObjectToItemCountDTO(itemRepository.findItemCountByCrate(id));
	}

	@Override
	public ItemCount updateItemCountStorageLocationByBarcode(String barcode, long shelfId, long rackId, long crateId) {
		itemRepository.updateItemCountStorageLocationByBarcode(barcode, shelfId, rackId, crateId);
		return getItemCountByBarcode(barcode);
	}

	@Override
	public List<ItemCount> getItemsInShelfRackCrate(long shelf, long rack, long crate) {
		return dtoWrapper
				.convertObjectToItemCount(itemRepository.findItemCountByShelfAndRackAndCrate(shelf, rack, crate));
	}

	@Override
	public List<ItemCount> getItemsInShelfRack(long shelf, long rack) {
		return dtoWrapper.convertObjectToItemCount(itemRepository.findItemCountByShelfAndRack(shelf, rack));
	}

	@Override
	public List<ItemCountDTO> getAllBarcodeItems() {
		return dtoWrapper.convertObjectToItemCountDTO(itemRepository.getAllItemCount());
	}

	@Override
	public ItemCount getItemCountByBarcode(String barcode) {
		Object records[] = itemRepository.getItemCountByBarcode(barcode);
		if (records == null || records.length == 0)
			return null;
		Object[] record = (Object[]) records[0];
		Warehouse warehouse = record[0] == null ? null
				: warehouseService.findById(((BigInteger) record[0]).longValue());
		String itemCode = record[1] == null ? null : (String) record[1];
		String description = record[2] == null ? null : (String) record[2];
		String vendorAlias = record[3] == null ? null : (String) record[3];
		int status = record[5] == null ? null : (Integer) record[5];
		Double reorderPoint = record[6] == null ? null : (Double) record[6];
		Double unitPrice = record[7] == null ? null : (Double) record[7];
		Double wholesalePrice = record[8] == null ? null : (Double) record[8];
		Shelf shelf = record[9] == null ? null : shelfService.findById(((BigInteger) record[9]).longValue());
		Rack rack = record[10] == null ? null : rackService.findById(((BigInteger) record[10]).longValue());
		Crate crate = record[11] == null ? null : crateService.findById(((BigInteger) record[11]).longValue());
		return new ItemCount(warehouse, itemCode, description, vendorAlias, barcode, status, reorderPoint, unitPrice, wholesalePrice, shelf, rack,
				crate);
	}

	@Override
	public boolean doesItemExists(String itemCode) {
		return true;
	}
	
	@Override
	public ItemNetworkDTO getItemNetworkByCode(String itemCode){
		Object object = itemRepository.getShortItem(itemCode);
		if(object != null){
			Object record[] = (Object[])object;
			if(record != null && record.length > 0){
				Long id = record[0] == null ? null :((BigInteger) record[0]).longValue();
				String description = record[2] == null ? null : (String) record[2];
				String vendorAlias = record[3] == null ? null : (String) record[3];
				return new ItemNetworkDTO(id, itemCode, description, vendorAlias);
			}
		}
		return null;
	}

	@Override
	public List<String> getBarcodeSuggestion(String warehouseCode, List<String> itemCodes, String voucherRef) {
		List<String> result = new ArrayList<>();
		Warehouse warehouse = warehouseService.getWarehouseByCode(warehouseCode);
		Map<String, Integer> map = new HashMap<>();
		for (String itemCode : itemCodes) {
			if (map.containsKey(itemCode)) {
				map.put(itemCode, map.get(itemCode) + 1);
			} else {
				map.put(itemCode, 1);
			}
		}
		Map<String, ItemCount> temp = null;
		List<ItemCount> itemCounts = null;
		Map<String, String> barcodes = new HashMap<>();
		Map<String, Map<String, ItemCount>> barcodes1 = new HashMap<>();
		Map<String, Map<String, ItemCount>> barcodes2 = new HashMap<>();
		Map<String, Map<String, ItemCount>> barcodes3 = new HashMap<>();
		for (String itemCode : map.keySet()) {
			itemCounts = dtoWrapper.convertObjectToItemCount(itemRepository
					.getAvailableItemCountByItemCodeFromSelfPurchaseOrders(warehouse.getId(), itemCode, voucherRef));
			temp = null;
			if (barcodes1.containsKey(itemCode)) {
				temp = barcodes1.get(itemCode);
			} else {
				temp = new HashMap<>();
			}
			if (itemCounts != null)
				l: for (ItemCount count : itemCounts) {
					if (map.get(itemCode) == 0)
						break l;
					if (!barcodes.containsKey(count.getBarcode())) {
						temp.put(count.getBarcode(), count);
						map.put(itemCode, map.get(itemCode) - 1);
						barcodes.put(count.getBarcode(), count.getBarcode());
					}
				}
			barcodes1.put(itemCode, temp);
		}
		for (String itemCode : map.keySet()) {
			itemCounts = dtoWrapper.convertObjectToItemCount(
					itemRepository.getAvailableItemCountByItemCodeFromNonDefault(warehouse.getId(), itemCode));
			temp = null;
			if (barcodes2.containsKey(itemCode)) {
				temp = barcodes2.get(itemCode);
			} else {
				temp = new HashMap<>();
			}
			if (itemCounts != null)
				l: for (ItemCount count : itemCounts) {
					if (map.get(itemCode) == 0)
						break l;
					if (!barcodes.containsKey(count.getBarcode())) {
						temp.put(count.getBarcode(), count);
						map.put(itemCode, map.get(itemCode) - 1);
						barcodes.put(count.getBarcode(), count.getBarcode());
					}
				}
			barcodes2.put(itemCode, temp);
		}
		for (String itemCode : map.keySet()) {
			itemCounts = dtoWrapper.convertObjectToItemCount(
					itemRepository.getAvailableItemCountByItemCodeFromOtherPurchaseOrders(warehouse.getId(), itemCode));
			temp = null;
			if (barcodes3.containsKey(itemCode)) {
				temp = barcodes3.get(itemCode);
			} else {
				temp = new HashMap<>();
			}
			if (itemCounts != null)
				l: for (ItemCount count : itemCounts) {
					if (map.get(itemCode) == 0)
						break l;
					if (!barcodes.containsKey(count.getBarcode())) {
						temp.put(count.getBarcode(), count);
						map.put(itemCode, map.get(itemCode) - 1);
						barcodes.put(count.getBarcode(), count.getBarcode());
					}
				}
			barcodes3.put(itemCode, temp);
		}
		p: for (String itemCode : itemCodes) {
			if (barcodes1.containsKey(itemCode)) {
				Map<String, ItemCount> value = barcodes1.get(itemCode);
				if (value.size() > 0) {
					for (String barcode : value.keySet()) {
						ItemCount itemCount = value.get(barcode);
						value.remove(barcode);
						barcodes1.put(itemCode, value);
						String suggestion = itemCount.getBarcode() + "(" + itemCount.getShelf().getName() + ", "
								+ itemCount.getRack().getName() + ", " + itemCount.getCrate().getName() + ")";
						result.add(suggestion);
						continue p;
					}
				}
			}
			if (barcodes2.containsKey(itemCode)) {
				Map<String, ItemCount> value = barcodes2.get(itemCode);
				if (value.size() > 0) {
					for (String barcode : value.keySet()) {
						ItemCount itemCount = value.get(barcode);
						value.remove(barcode);
						barcodes2.put(itemCode, value);
						String suggestion = itemCount.getBarcode() + "(" + itemCount.getShelf().getName() + ", "
								+ itemCount.getRack().getName() + ", " + itemCount.getCrate().getName() + ")";
						result.add(suggestion);
						continue p;
					}
				}
			}
			if (barcodes3.containsKey(itemCode)) {
				Map<String, ItemCount> value = barcodes3.get(itemCode);
				if (value.size() > 0) {
					for (String barcode : value.keySet()) {
						ItemCount itemCount = value.get(barcode);
						value.remove(barcode);
						barcodes3.put(itemCode, value);
						String suggestion = itemCount.getBarcode() + "(" + itemCount.getShelf().getName() + ", "
								+ itemCount.getRack().getName() + ", " + itemCount.getCrate().getName() + ")";
						result.add(suggestion);
						continue p;
					}
				}
			}
			result.add("No available suggestion.");
		}
		return result;
	}

	@Override
	public List<String> getAvailableBarcodesByBarcodesAndWarehouse(String code, List<String> barcodes) {
		List<String> result = new ArrayList<>();
		Warehouse warehouse = warehouseService.getWarehouseByCode(code);
		for (String barcode : barcodes) {
			String message = "";
			ItemCount itemCount = dtoWrapper.convertObjectListToItemCount(
					itemRepository.findBarcodeByStatusAndWarehouseAndBarcode(ItemStatus.AVAILABLE.value(),
							warehouse.getId(), barcode));
			if (itemCount != null) {
				if (itemCount.getShelf() != null && itemCount.getRack() != null && itemCount.getRack() != null) {
					message = "Barcode is valid.";
					result.add(barcode + " : " + message);
					continue;
				} else {
					message = "Barcode does not has storage location.";
					result.add(barcode + " : " + message);
					continue;
				}
			} else {
				itemCount = getItemCountByBarcode(barcode);
				if (itemCount != null) {
					if (itemCount.getWarehouse().getId() != warehouse.getId()
							&& ItemStatus.AVAILABLE.isEquals(itemCount.getStatus())) {
						message = "Barcode not available at this warehouse.";
						result.add(barcode + " : " + message);
						continue;
					} else if (ItemStatus.INVOICED.isEquals(itemCount.getStatus())) {
						message = "Barcode is invoiced.";
						result.add(barcode + " : " + message);
						continue;
					}
				} else {
					message = "Barcode invalid.";
					result.add(barcode + " : " + message);
					continue;
				}
			}
		}
		return result;
	}

	@Override
	public void updateKitItemsToOutOfStock(long warehouseId, String itemCode) {
		//itemRepository.updateKitItemsToOutOfStock(warehouseId, itemCode);
		//itemRepository.updateSubkitItemsToOutOfStock(warehouseId, itemCode);
	}

	@Override
	public void updateItemCountWarehouseLocation(String barcode, long warehouseId) {
		itemRepository.updateItemCountWarehouseLocation(barcode, warehouseId);
	}

	@Override
	public void updateItemCountWarehouseLocation(List<ItemRequestPair> requests, long warehouseId) {
		for (ItemRequestPair request : requests) {
			itemRepository.updateItemCountWarehouseLocation(request.getBarcode(), warehouseId);
		}
	}

	@Override
	public List<ItemCount> getAvailableItemCountByCode(String itemCode) {
		return dtoWrapper.convertObjectToItemCount(itemRepository.getAvailableItemCountByItemCode(itemCode));
	}

	@Override
	public void createDefaultStorageLocation(long warehouseId) {
		Shelf shelf = null;
		Rack rack = null;
		Crate crate = null;
		shelf = shelfService.getShelfByNameAndWarehouseId("default", warehouseId);
		if (shelf == null)
			shelf = shelfService.addShelf(warehouseId, new Shelf(0, "default", "default", new ArrayList<>()));
		if (shelf != null) {
			rack = rackService.getRackByNameAndShelfId("default", shelf.getId());
			if (rack == null)
				rack = rackService.addRack(shelf.getId(), new Rack(0, "default", "default", new ArrayList<>()));
		}
		if (rack != null) {
			crate = crateService.getCrateByNameAndRackId("default", rack.getId());
			if (crate == null)
				crate = crateService.addCrate(rack.getId(), new Crate(0, "default", "default"));
		}
	}

	@Override
	public ItemReconcileReport getItemReport(Date fromDate, Date toDate, Long warehouseId) {
		List<Kit> listOfKits = null;
		if(warehouseId == null)
			listOfKits = kitRepository.findByDateRange(fromDate, toDate);
		else
			listOfKits = kitRepository.getByDateRangeAndWarehouseId(fromDate, toDate, warehouseId);
		ItemReconcileReport report = new ItemReconcileReport();
		Map<String, ItemReport> m = new HashMap<>();
		for (Kit kit : listOfKits) {
			for (SalesOrderItemLine lineItem : kit.getItems()) {
				ItemReport itemReport = null;
				if(m.containsKey(lineItem.getItemCode())){
					itemReport = m.get(lineItem.getItemCode());
				} else{
					Integer itemCounts = itemRepository.getAvailableItemCountCountByItemCode(lineItem.getItemCode());
					//Integer itemCounts = 0;
					//if(p != null && p.size() != 0)
						//itemCounts = p.size();
					itemReport = new ItemReport(lineItem.getItemCode(), 0.0, 0.0, itemCounts);
				}
				if (lineItem.getStatus() >= ItemStatus.RESERVED.value()
						&& lineItem.getStatus() <= ItemStatus.STOCK_LOSS.value() && 
						lineItem.getStatus() != ItemStatus.RETURN_RECEIVED_BY_WAREHOUSE.value()) {
					itemReport.setQuantityOutgoing(itemReport.getQuantityOutgoing() + 1);
				}
				if (lineItem.getStatus() == ItemStatus.RETURN_RECEIVED_BY_WAREHOUSE.value()) {
					itemReport.setQuantityIncoming(itemReport.getQuantityIncoming() + 1);
				}
				m.put(lineItem.getItemCode(), itemReport);
			}
		}
		for(String itemCode : m.keySet()){
			report.getItems().add(m.get(itemCode));
		}
		return report;
	}
	
	@Override
	public Integer getAvailableCountByCode(String code){
		return itemRepository.getAvailableItemCountCountByItemCode(code);
	}
	
	public Integer getAvailableCountByCodeAndWarehouseId(String itemCode, long warehouseId){
		return itemRepository.getAvailableItemCountCountByItemCodeAndWarehouseId(itemCode, warehouseId);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public DTResult<ItemCountDTO> getItemCountDTO(DTParameters params, Long warehouseId, Long shelfId, Long rackId, Long crateId) {
		DTResult<ItemCountDTO> result = new DTResult<>();
		result.draw = params.draw;
		result.recordsTotal = itemRepository.count();
		String select = "SELECT item_count.warehouse, item_count.item_code, item_count.description, item_count.vendor_alias, item_count.barcode, item_count.status, item_count.reorder_point, item_count.unit_price, item_count.wholesale_price, item_count.shelf, item_count.rack, item_count.crate FROM item_count ";
		String count = "SELECT count(item_count.barcode) FROM item_count ";
		String where = " WHERE 1 = 1 ";
		if(warehouseId != 0){
			where += " AND item_count.warehouse = " + warehouseId + " ";
		}
		if(shelfId != 0){
			where += " AND item_count.shelf = " + shelfId + " ";
		}
		if(rackId != 0){
			where += " AND item_count.rack = " + rackId + " ";
		}
		if(crateId != 0){
			where += " AND item_count.crate = " + crateId + " ";
		}
		if (params.search.value != null && !params.search.value.isEmpty()) {
			where += " AND item_count.item_code LIKE '%" + params.search.value + "%' OR " + "item_count.barcode LIKE '%" + params.search.value
					+ "%' ";
		}
		String limit = " LIMIT " + params.length + " ";
		String offset = " OFFSET " + params.start + " ";
		List<Object[]> resultSet = new ArrayList<>();
		try {
			if (params.search.value != null && !params.search.value.isEmpty()) {
				String selectQuery = select + where + " ORDER BY item_count.status " + limit + offset;
				Query nativeQuery = em.createNativeQuery(selectQuery);
				resultSet = nativeQuery.getResultList();
				String countQuery = count + where + " ORDER BY item_count.status ";
				Query nativeCountQuery = em.createNativeQuery(countQuery);
				result.recordsFiltered = ((BigInteger) nativeCountQuery.getSingleResult()).intValue();
				result.recordsTotal = result.recordsFiltered; 
			} else {
				String selectQuery = select + where + " ORDER BY item_count.status " + limit + offset;
				Query nativeQuery = em.createNativeQuery(selectQuery);
				resultSet = nativeQuery.getResultList();
				String countQuery = count + where + " ORDER BY item_count.status ";
				Query nativeCountQuery = em.createNativeQuery(countQuery);
				result.recordsFiltered = ((BigInteger) nativeCountQuery.getSingleResult()).intValue();
				result.recordsTotal = result.recordsFiltered;
			}
		} catch (Exception ex) {
			logger.error("Exception thrown while executing query : " + ex.getMessage());
			throw new RuntimeException(ex.getMessage());
		}
		result.data = dtoWrapper.convertObjectToItemCountDTO(resultSet);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public DTResult<ItemCountQuantityDTO> getFastMovingItems(RequestParamsDTO request){
		DTParameters params = request.getParams();
		Date fromDate = request.getFromDate();
		Date toDate = request.getToDate();
		DTResult<ItemCountQuantityDTO> result = new DTResult<>();
		String orderBy = "", sortOrder = "";
		if (params.order.get(0).Dir != null) {
			sortOrder = (params.order.get(0).Dir.toString()).toUpperCase();
		} else {
			sortOrder = "DESC";
		}
		orderBy = " rate DESC";
		int i = 0;
		for (Field field : ItemCountQuantityDTO.class.getDeclaredFields()) {
		    field.setAccessible(true);
		    if(i == params.order.get(0).Column && params.order.get(0).getDir().equals(DTOrderDir.desc)){
		    	orderBy = " " + field.getName().replaceAll("([A-Z])", "_$1").toLowerCase() + " DESC";
		    	break;
		    }
		    if(i == params.order.get(0).Column && !params.order.get(0).getDir().equals(DTOrderDir.desc)){
		    	orderBy = " " + field.getName().replaceAll("([A-Z])", "_$1").toLowerCase();
		    	break;
		    }
		    i++;
		}
		result.draw = params.draw;
		String fromDateStr = "";
		String toDateStr = "";
		SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
		String loc = "";
		if(request.getAssignedWarehouseId() != null){
			loc = " AND current_location = " + request.getAssignedWarehouseId();
		}
		if(fromDate != null){
			fromDateStr = "\'" + format.format(fromDate) + "\'";
		}else{
			fromDateStr = "MIN(date)";
		}
		if(toDate != null){
			toDateStr = "\'" + format.format(toDate) + "\'";
		}else{
			toDateStr = "CURRENT_DATE()";
		}
		String countQuery = "", searchQuery = "";
		String date = toDateStr + ", " +  fromDateStr;
		if (params.search.value != null && !params.search.value.isEmpty()) {
			String q = String.format(" AND (item_code like \'%%%s%%\' OR barcode like \'%%%s%%\' OR description like \'%%%s%%\')", params.search.value, params.search.value, params.search.value);
			countQuery = "SELECT COUNT(*) AS rows FROM ((SELECT item_code, description, COUNT(barcode) AS quantity, IF(FLOOR(DATEDIFF(%s)) = 0, 0, COUNT(barcode) / FLOOR(DATEDIFF(%s))) AS rate FROM kit_item_details kid INNER JOIN kit k ON kid.kit_id = k.id WHERE barcode IS NOT NULL AND barcode <> item_code %s GROUP BY item_code) AS A)";
			searchQuery  = "SELECT item_code, description, COUNT(barcode) AS quantity, IF(FLOOR(DATEDIFF(%s)) = 0, 0, COUNT(barcode) / FLOOR(DATEDIFF(%s))) AS rate FROM kit_item_details kid INNER JOIN kit k ON kid.kit_id = k.id WHERE barcode IS NOT NULL AND barcode <> item_code %s GROUP BY item_code ORDER BY %s LIMIT %d OFFSET %d";
			q += loc;
			countQuery = String.format(countQuery, date, date, q);
			searchQuery = String.format(searchQuery, date, date, q, orderBy, params.length, params.start);
		} else {
			countQuery = "SELECT COUNT(*) AS rows FROM ((SELECT item_code, description, COUNT(barcode) AS quantity, IF(FLOOR(DATEDIFF(%s)) = 0, 0, COUNT(barcode) / FLOOR(DATEDIFF(%s))) AS rate FROM kit_item_details kid INNER JOIN kit k ON kid.kit_id = k.id WHERE barcode IS NOT NULL AND barcode <> item_code %s GROUP BY item_code) AS A)";
			searchQuery  = "SELECT item_code, description, COUNT(barcode) AS quantity, IF(FLOOR(DATEDIFF(%s)) = 0, 0, COUNT(barcode) / FLOOR(DATEDIFF(%s))) AS rate FROM kit_item_details kid INNER JOIN kit k ON kid.kit_id = k.id WHERE barcode IS NOT NULL AND barcode <> item_code %s GROUP BY item_code ORDER BY %s LIMIT %d OFFSET %d";
			countQuery = String.format(countQuery, date, date, loc);
			searchQuery = String.format(searchQuery, date, date, loc, orderBy, params.length, params.start);
		}
		try {
			Query nativeCountQuery = em.createNativeQuery(countQuery);
			result.recordsTotal = ((BigInteger) nativeCountQuery.getSingleResult()).intValue();
			result.recordsFiltered = result.recordsTotal;
			Query nativeQuery = em.createNativeQuery(searchQuery);
			List<Object[]> resultSet = new ArrayList<>();
			resultSet = nativeQuery.getResultList();
			result.data = dtoWrapper.convertObjectToItemCountQuantityDTO(resultSet);
			return result;
		} catch (Exception ex) {
			logger.error("Exception thrown while executing query : " + ex.getMessage());
			throw new RuntimeException(ex.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public DTResult<ItemCountQuantityDTO> getSlowMovingItems(RequestParamsDTO request){
		DTParameters params = request.getParams();
		Date fromDate = request.getFromDate();
		Date toDate = request.getToDate();
		DTResult<ItemCountQuantityDTO> result = new DTResult<>();
		String orderBy = "", sortOrder = "";
		if (params.order.get(0).Dir != null) {
			sortOrder = (params.order.get(0).Dir.toString()).toUpperCase();
		} else {
			sortOrder = "DESC";
		}
		orderBy = " rate ";
		int i = 0;
		for (Field field : ItemCountQuantityDTO.class.getDeclaredFields()) {
		    field.setAccessible(true);
		    if(i == params.order.get(0).Column && params.order.get(0).getDir().equals(DTOrderDir.desc)){
		    	orderBy = " " + field.getName().replaceAll("([A-Z])", "_$1").toLowerCase() + " DESC";
		    	break;
		    }
		    if(i == params.order.get(0).Column && !params.order.get(0).getDir().equals(DTOrderDir.desc)){
		    	orderBy = " " + field.getName().replaceAll("([A-Z])", "_$1").toLowerCase();
		    	break;
		    }
		    i++;
		}
		result.draw = params.draw;
		String fromDateStr = "";
		String toDateStr = "";
		SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
		String loc = "";
		if(request.getAssignedWarehouseId() != null){
			loc = " AND current_location = " + request.getAssignedWarehouseId();
		}
		if(fromDate != null){
			fromDateStr = "\'" + format.format(fromDate) + "\'";
		}else{
			fromDateStr = "MIN(date)";
		}
		if(toDate != null){
			toDateStr = "\'" + format.format(toDate) + "\'";
		}else{
			toDateStr = "CURRENT_DATE()";
		}
		String countQuery = "", searchQuery = "";
		String date = toDateStr + ", " +  fromDateStr;
		if (params.search.value != null && !params.search.value.isEmpty()) {
			String q = String.format(" AND (item_code like \'%%%s%%\' OR barcode like \'%%%s%%\' OR description like \'%%%s%%\')", params.search.value, params.search.value, params.search.value);
			countQuery = "SELECT COUNT(*) AS rows FROM ((SELECT item_code, description, COUNT(barcode) AS quantity, IF(FLOOR(DATEDIFF(%s)) = 0, 0, COUNT(barcode) / FLOOR(DATEDIFF(%s))) AS rate FROM kit_item_details kid INNER JOIN kit k ON kid.kit_id = k.id WHERE barcode IS NOT NULL AND barcode <> item_code %s GROUP BY item_code) AS A)";
			searchQuery  = "SELECT item_code, description, COUNT(barcode) AS quantity, IF(FLOOR(DATEDIFF(%s)) = 0, 0, COUNT(barcode) / FLOOR(DATEDIFF(%s))) AS rate FROM kit_item_details kid INNER JOIN kit k ON kid.kit_id = k.id WHERE barcode IS NOT NULL AND barcode <> item_code %s GROUP BY item_code ORDER BY %s LIMIT %d OFFSET %d";
			q += loc;
			countQuery = String.format(countQuery, date, date, q);
			searchQuery = String.format(searchQuery, date, date, q, orderBy, params.length, params.start);
		} else {
			countQuery = "SELECT COUNT(*) AS rows FROM ((SELECT item_code, description, COUNT(barcode) AS quantity, IF(FLOOR(DATEDIFF(%s)) = 0, 0, COUNT(barcode) / FLOOR(DATEDIFF(%s))) AS rate FROM kit_item_details kid INNER JOIN kit k ON kid.kit_id = k.id WHERE barcode IS NOT NULL AND barcode <> item_code %s GROUP BY item_code) AS A)";
			searchQuery  = "SELECT item_code, description, COUNT(barcode) AS quantity, IF(FLOOR(DATEDIFF(%s)) = 0, 0, COUNT(barcode) / FLOOR(DATEDIFF(%s))) AS rate FROM kit_item_details kid INNER JOIN kit k ON kid.kit_id = k.id WHERE barcode IS NOT NULL AND barcode <> item_code %s GROUP BY item_code ORDER BY %s LIMIT %d OFFSET %d";
			countQuery = String.format(countQuery, date, date, loc);
			searchQuery = String.format(searchQuery, date, date, loc, orderBy, params.length, params.start);
		}
		try {
			Query nativeCountQuery = em.createNativeQuery(countQuery);
			result.recordsTotal = ((BigInteger) nativeCountQuery.getSingleResult()).intValue();
			result.recordsFiltered = result.recordsTotal;
			Query nativeQuery = em.createNativeQuery(searchQuery);
			List<Object[]> resultSet = nativeQuery.getResultList();
			result.data = dtoWrapper.convertObjectToItemCountQuantityDTO(resultSet);
			return result;
		} catch (Exception ex) {
			logger.error("Exception thrown while executing query : " + ex.getMessage());
			throw new RuntimeException(ex.getMessage());
		}
	}
}
