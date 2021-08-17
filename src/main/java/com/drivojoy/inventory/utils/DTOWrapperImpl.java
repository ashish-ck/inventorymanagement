package com.drivojoy.inventory.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.drivojoy.inventory.dto.AttributeDTO;
import com.drivojoy.inventory.dto.CrateDTO;
import com.drivojoy.inventory.dto.ItemAttributeDTO;
import com.drivojoy.inventory.dto.ItemCategoryDTO;
import com.drivojoy.inventory.dto.ItemCountDTO;
import com.drivojoy.inventory.dto.ItemCountQuantityDTO;
import com.drivojoy.inventory.dto.ItemDTO;
import com.drivojoy.inventory.dto.ItemNetworkDTO;
import com.drivojoy.inventory.dto.ItemOrderLineDTO;
import com.drivojoy.inventory.dto.ItemRequestPair;
import com.drivojoy.inventory.dto.ItemSalesRequest;
import com.drivojoy.inventory.dto.ItemStatusDTO;
import com.drivojoy.inventory.dto.ItemStorageLocationDTO;
import com.drivojoy.inventory.dto.KitDTO;
import com.drivojoy.inventory.dto.PurchaseOrderDTO;
import com.drivojoy.inventory.dto.RackDTO;
import com.drivojoy.inventory.dto.SalesOrderLineItemDTO;
import com.drivojoy.inventory.dto.ShelfDTO;
import com.drivojoy.inventory.dto.SubkitDTO;
import com.drivojoy.inventory.dto.TransferOrderDTO;
import com.drivojoy.inventory.dto.VendorDTO;
import com.drivojoy.inventory.dto.WarehouseDTO;
import com.drivojoy.inventory.dto.WarehouseNetworkDTO;
import com.drivojoy.inventory.models.Address;
import com.drivojoy.inventory.models.Attribute;
import com.drivojoy.inventory.models.Crate;
import com.drivojoy.inventory.models.Item;
import com.drivojoy.inventory.models.ItemAttribute;
import com.drivojoy.inventory.models.ItemCategory;
import com.drivojoy.inventory.models.ItemCount;
import com.drivojoy.inventory.models.ItemDetails;
import com.drivojoy.inventory.models.ItemOrderLine;
import com.drivojoy.inventory.models.ItemStorageLocation;
import com.drivojoy.inventory.models.Kit;
import com.drivojoy.inventory.models.PurchaseOrder;
import com.drivojoy.inventory.models.Rack;
import com.drivojoy.inventory.models.SalesOrderItemLine;
import com.drivojoy.inventory.models.Shelf;
import com.drivojoy.inventory.models.Subkit;
import com.drivojoy.inventory.models.TransferOrder;
import com.drivojoy.inventory.models.UoM;
import com.drivojoy.inventory.models.Vendor;
import com.drivojoy.inventory.models.Warehouse;
import com.drivojoy.inventory.models.datatables.DTResult;
import com.drivojoy.inventory.services.IAttributeService;
import com.drivojoy.inventory.services.ICrateService;
import com.drivojoy.inventory.services.IItemCategoryService;
import com.drivojoy.inventory.services.IItemService;
import com.drivojoy.inventory.services.IRackService;
import com.drivojoy.inventory.services.IShelfService;
import com.drivojoy.inventory.services.IUoMService;
import com.drivojoy.inventory.services.IWarehouseService;
import com.drivojoy.inventory.utils.Constants.ItemStatus;

@Component
public class DTOWrapperImpl implements IDTOWrapper {

	@Autowired
	private IItemCategoryService itemCategoryService;
	@Autowired
	private IUoMService uomService;
	@Autowired
	private IItemService itemService;
	@Autowired
	private IWarehouseService warehouseService;
	@Autowired
	private IAttributeService attributeService;
	@Autowired
	private IShelfService shelfService;
	@Autowired
	private IRackService rackService;
	@Autowired
	private ICrateService crateService;

	private final Logger logger = LoggerFactory.getLogger(DTOWrapperImpl.class);

	@Override
	public ItemCategory convert(ItemCategoryDTO itemCategoryDTO) {
		try {
			ItemCategory convertedObject = new ItemCategory();
			convertedObject.setName(itemCategoryDTO.getName());
			convertedObject.setDescription(itemCategoryDTO.getDescription());
			if (itemCategoryDTO.getId() != 0) {
				convertedObject.setId(itemCategoryDTO.getId());
			}

			if (itemCategoryDTO.getParentCategory() != 0) {
				convertedObject.setParentCategory(
						itemCategoryService.getItemCategoryById(itemCategoryDTO.getParentCategory()));
			}
			convertedObject.setVersion(itemCategoryDTO.getVersion());
			return convertedObject;
		} catch (Exception ex) {
			return null;
		}
	}

	@Override
	public ItemCategoryDTO convert(ItemCategory itemCategory) {
		if (itemCategory != null) {
			ItemCategoryDTO itemCategoryDTO = new ItemCategoryDTO(itemCategory.getId(), itemCategory.getName(),
					itemCategory.getDescription(),
					itemCategory.getParentCategory() != null ? itemCategory.getParentCategory().getId() : 0,
					itemCategory.getVersion());
			return itemCategoryDTO;
		}

		return null;

	}

	@Override
	public WarehouseDTO convert(Warehouse warehouse) {
		WarehouseDTO warehouseDTO = new WarehouseDTO();
		warehouseDTO.setId(warehouse.getId());
		if (warehouse.getAddress() != null) {
			warehouseDTO.setAddressLine1(warehouse.getAddress().getAddressLine1());
			warehouseDTO.setAddressLine2(warehouse.getAddress().getAddressLine2());
			warehouseDTO.setCity(warehouse.getAddress().getCity());
			warehouseDTO.setCountry(warehouse.getAddress().getCountry());
			warehouseDTO.setLocation(warehouse.getAddress().getLocation());
			warehouseDTO.setState(warehouse.getAddress().getState());
			warehouseDTO.setPincode(warehouse.getAddress().getPincode());
			warehouseDTO.setShelves(convertToShelfDTO(warehouse.getShelves()));
		}
		warehouseDTO.setName(warehouse.getName());
		warehouseDTO.setCode(warehouse.getCode());
		warehouseDTO.setVersion(warehouse.getVersion());
		warehouseDTO.setMotherWarehouse(warehouse.isParentWarehouse());
		if (warehouse.getMotherWarehouse() != null) {
			warehouseDTO.setMotherWarehouseCode(warehouse.getMotherWarehouse().getCode());
		}
		return warehouseDTO;
	}

	@Override
	public Warehouse convert(WarehouseDTO warehouseDTO) {
		Warehouse warehouse = new Warehouse();
		if (warehouseDTO.getId() != 0) {
			warehouse.setId(warehouseDTO.getId());
		}
		warehouse.setName(warehouseDTO.getName());
		warehouse.setAddress(new Address(warehouseDTO.getAddressLine1(), warehouseDTO.getAddressLine2(),
				warehouseDTO.getCity(), warehouseDTO.getLocation(), warehouseDTO.getState(), warehouseDTO.getCountry(),
				warehouseDTO.getPincode()));
		warehouse.setCode(warehouseDTO.getCode());
		warehouse.setVersion(warehouseDTO.getVersion());
		warehouse.setParentWarehouse(warehouseDTO.isMotherWarehouse());
		warehouse.setShelves(convertToShelf(warehouseDTO.getShelves()));
		if (!warehouse.isParentWarehouse()) {
			warehouse.setMotherWarehouse(warehouseService.getWarehouseByCode(warehouseDTO.getMotherWarehouseCode()));
		}
		/*
		 * Revision may be required here as we may not be always want to
		 * initialize all item quantities with quantity in hand
		 */

		return warehouse;
	}

	@Override
	public ItemDTO convert(Item item) {
		try {
			if (item != null) {
				double availableQuantity = itemService.getAvailableCountByCode(item.getCode());
				ItemDTO itemDTO = new ItemDTO(item.getId(), item.getCode(), item.getDescription(),
						item.getUnitOfMeasurement().getNotation(), convert(item.getCategory()), item.getTags(),
						item.isActive(), item.getVendorAlias(), convertToItemAttributeDTO(item.getAttributes()),
						item.getBarcode(), item.getWarningPoint(), item.getReorderPoint(), item.getReorderQuantity(),
						availableQuantity, convertToItemCountDTO(item.getItemCount()), item.getNormalPrice(),
						item.getWholesalePrice(), item.getCreatedDttm(),
						convertToItemRequestPair(item.getItemDetails()), item.isSerialized(), item.getTaxes(),
						item.isTaxable(), convertToItemStorageLocationDTO(item.getItemStorageLocations()));
				itemDTO.setVersion(item.getVersion());
				return itemDTO;
			}
			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.debug("exception caught in dto wrapper : " + ex.getMessage());
			throw new RuntimeException(Constants._GENERIC_ERROR_MESSAGE);
		}

	}

	@Override
	public Item convert(ItemDTO itemDTO) {
		Item item = new Item(itemDTO.getId(), itemDTO.getCode(), itemDTO.getVendorAlias(), itemDTO.getDescription(),
				uomService.getUnitByNotation(itemDTO.getUnitOfMeasurement()),
				convertToItemAttribute(itemDTO.getItemAttributes()),
				convert(itemCategoryService.getItemCategoryByName(itemDTO.getCategory().getName())),
				itemDTO.getBarcode(), itemDTO.getWarningPoint(), itemDTO.getReorderPoint(),
				itemDTO.getReorderQuantity(), itemDTO.getNormalPrice(), itemDTO.getWholesalePrice(),
				Calendar.getInstance().getTime(), itemDTO.getCreatedDttm(), itemDTO.isActive(),
				convertToItemCount(itemDTO.getItemCount()), convertToItemDetails(itemDTO.getItemDetails()),
				itemDTO.getTags(), itemDTO.isSerialized(), itemDTO.getVersion(), itemDTO.getTaxes(),
				itemDTO.isTaxable(), convertToItemStorageLocation(itemDTO.getItemStorageLocations()));
		// item.setVersion(itemDTO.getVersion());
		return item;
	}

	@Override
	public List<ItemCount> convertToItemCount(Collection<ItemCountDTO> itemCountDTO) {
		List<ItemCount> itemCount = new ArrayList<>();
		for (ItemCountDTO item : itemCountDTO) {
			itemCount.add(new ItemCount(warehouseService.getWarehouseByCode(item.getWarehouse().getCode()),
					item.getItemCode(), item.getDescription(), item.getVendorAlias(), item.getBarcode(), item.getStatus(), item.getReorderPoint(),
					item.getUnitPrice(), item.getWholesalePrice(), convert(item.getShelf()), convert(item.getRack()),
					convert(item.getCrate())));
		}
		return itemCount;
	}

	@Override
	public List<ItemCountDTO> convertToItemCountDTO(Collection<ItemCount> itemCount) {
		List<ItemCountDTO> itemCountDTO = new ArrayList<>();
		for (ItemCount count : itemCount) {
			itemCountDTO.add(new ItemCountDTO(convertToWarehouseNetworkDTO(count.getWarehouse()), count.getItemCode(),
					count.getDescription(), count.getVendorAlias(), count.getBarcode(), count.getStatus(), count.getReorderPoint(), count.getUnitPrice(),
					count.getWholesalePrice(), convert(count.getShelf()), convert(count.getRack()),
					convert(count.getCrate())));
		}
		return itemCountDTO;
	}

	@Override
	public List<ItemDTO> convertToItemDTOList(Collection<Item> items) {
		List<ItemDTO> itemList = new ArrayList<>();
		if (items != null && items.size() > 0) {
			for (Item item : items) {
				itemList.add(convert(item));
			}
		}
		return itemList;
	}

	@Override
	public List<ItemDTO> quickConvertToItemDTOList(Collection<Item> items) {
		List<ItemDTO> itemList = new ArrayList<>();
		if (items != null && items.size() > 0) {
			for (Item item : items) {
				try {
					if (item != null) {
						ItemDTO itemDTO = new ItemDTO(item.getId(), item.getCode(), item.getDescription(),
								item.getUnitOfMeasurement().getNotation(), convert(item.getCategory()), item.getTags(),
								item.isActive(), item.getVendorAlias(), null, item.getBarcode(), item.getWarningPoint(),
								item.getReorderPoint(), item.getReorderQuantity(), 0.0, null, item.getNormalPrice(),
								item.getWholesalePrice(), item.getCreatedDttm(), null, item.isSerialized(),
								item.getTaxes(), item.isTaxable(), null);
						itemDTO.setVersion(item.getVersion());
						itemList.add(itemDTO);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					logger.debug("exception caught in dto wrapper : " + ex.getMessage());
					throw new RuntimeException(Constants._GENERIC_ERROR_MESSAGE);
				}
			}
		}
		return itemList;
	}

	@Override
	public List<Item> convertToItemList(Collection<ItemDTO> items) {
		List<Item> itemList = new ArrayList<>();
		if (items != null) {
			for (ItemDTO item : items) {
				itemList.add(convert(item));
			}
		}
		return itemList;
	}

	@Override
	public Attribute convert(AttributeDTO attributeDTO) {
		Attribute attribute = new Attribute(attributeDTO.getName(),
				uomService.getUnitByNotation(attributeDTO.getUnit()), attributeDTO.getPossibleValues());
		if (attributeDTO.getId() != 0)
			attribute.setId(attributeDTO.getId());
		return attribute;
	}

	@Override
	public AttributeDTO convert(Attribute itemAttribute) {
		try {
			AttributeDTO attributeDTO = new AttributeDTO(itemAttribute.getName(), itemAttribute.getUnit().getNotation(),
					(List<String>) itemAttribute.getPossibleValues());
			if (itemAttribute.getId() != 0)
				attributeDTO.setId(itemAttribute.getId());
			return attributeDTO;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public VendorDTO convert(Vendor vendor) {
		try {
			if (vendor.getOutletAddress() != null) {
				VendorDTO vendorDTO = new VendorDTO(vendor.getId(), vendor.getName(),
						vendor.getOutletAddress().getAddressLine1(), vendor.getOutletAddress().getAddressLine2(),
						vendor.getOutletAddress().getCity(), vendor.getOutletAddress().getLocation(),
						vendor.getOutletAddress().getState(), vendor.getOutletAddress().getCountry(),
						vendor.getOutletAddress().getPincode(), vendor.getContactNo(), vendor.getContactPerson(),
						convertToItemDTOList(new ArrayList<>()), vendor.getVersion(), vendor.getIsOrganised());
				return vendorDTO;
			} else {
				VendorDTO vendorDTO = new VendorDTO();
				vendorDTO.setId(vendor.getId());
				vendorDTO.setName(vendor.getName());
				vendorDTO.setItemsSold(new ArrayList<>());
				vendorDTO.setContactNo(vendor.getContactNo());
				vendorDTO.setContactPerson(vendor.getContactPerson());
				vendorDTO.setIsOrganised(vendor.getIsOrganised());
				return vendorDTO;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Vendor convert(VendorDTO vendorDTO) {
		Vendor vendor = new Vendor(vendorDTO.getId(), vendorDTO.getName(),
				new Address(vendorDTO.getAddressLine1(), vendorDTO.getAddressLine2(), vendorDTO.getCity(),
						vendorDTO.getLocation(), vendorDTO.getState(), vendorDTO.getCountry(), vendorDTO.getPincode()),
				vendorDTO.getContactNo(), vendorDTO.getContactPerson(), convertToItemList(vendorDTO.getItemsSold()),
				vendorDTO.getVersion(), vendorDTO.getIsOrganised());
		return vendor;
	}

	@Override
	public ShelfDTO convert(Shelf shelf) {
		if (shelf == null)
			return null;
		Collection<RackDTO> rackDTOs = convertToRackDTO(shelf.getRacks());
		ShelfDTO shelfDTO = new ShelfDTO(shelf.getId(), shelf.getName(), shelf.getDescription(), rackDTOs);
		return shelfDTO;
	}

	@Override
	public RackDTO convert(Rack rack) {
		if (rack == null)
			return null;
		Collection<CrateDTO> crateDTOs = convertToCrateDTO(rack.getCrates());
		RackDTO rackDTO = new RackDTO(rack.getId(), rack.getName(), rack.getDescription(), crateDTOs);
		return rackDTO;
	}

	@Override
	public CrateDTO convert(Crate crate) {
		if (crate == null)
			return null;
		CrateDTO crateDTO = new CrateDTO(crate.getId(), crate.getName(), crate.getDescription());
		return crateDTO;
	}

	@Override
	public Shelf convert(ShelfDTO shelfDTO) {
		if (shelfDTO == null)
			return null;
		Collection<Rack> racks = convertToRack(shelfDTO.getRacks());
		Shelf shelf = new Shelf(shelfDTO.getId(), shelfDTO.getName(), shelfDTO.getDescription(), racks);
		return shelf;
	}

	@Override
	public Rack convert(RackDTO rackDTO) {
		if (rackDTO == null)
			return null;
		Collection<Crate> crates = convertToCrate(rackDTO.getCrates());
		Rack rack = new Rack(rackDTO.getId(), rackDTO.getName(), rackDTO.getDescription(), crates);
		return rack;
	}

	@Override
	public Crate convert(CrateDTO crateDTO) {
		if (crateDTO == null)
			return null;
		Crate crate = new Crate(crateDTO.getId(), crateDTO.getName(), crateDTO.getDescription());
		return crate;
	}

	@Override
	public WarehouseNetworkDTO convertToWarehouseNetworkDTO(Warehouse warehouse) {
		if (warehouse == null)
			return null;
		WarehouseNetworkDTO warehouseNetworkDTO = new WarehouseNetworkDTO(warehouse.getId(), warehouse.getName(),
				warehouse.getCode(), warehouse.isParentWarehouse());
		return warehouseNetworkDTO;
	}

	@Override
	public WarehouseNetworkDTO convertWarehouseDTOToWarehouseNetworkDTO(WarehouseDTO warehouse) {
		if (warehouse == null)
			return null;
		WarehouseNetworkDTO warehouseNetworkDTO = new WarehouseNetworkDTO(warehouse.getId(), warehouse.getName(),
				warehouse.getCode(), warehouse.isMotherWarehouse());
		return warehouseNetworkDTO;
	}

	@Override
	public List<VendorDTO> convertToVendorDTO(Collection<Vendor> vendors) {
		List<VendorDTO> vendorList = new ArrayList<>();
		for (Vendor vendor : vendors) {
			vendorList.add(convert(vendor));
		}
		return vendorList;
	}

	@Override
	public PurchaseOrder convert(PurchaseOrderDTO purchaseOrderDTO) {
		Map<String, Warehouse> warehousesMap = new HashMap<>();
		Map<String, UoM> uomsMap = new HashMap<>();
		try {
			List<ItemDetails> itemDetails = new ArrayList<>();
			List<ItemOrderLine> itemOrderLines = new ArrayList<>();
			List<ItemDetails> returnItemDetails = new ArrayList<>();
			if(!warehousesMap.containsKey(purchaseOrderDTO.getShippingWarehouse().getName())){
				warehousesMap.put(purchaseOrderDTO.getShippingWarehouse().getName(), warehouseService.getWarehouseByName(purchaseOrderDTO.getShippingWarehouse().getName()));
			}
			for (ItemRequestPair details : purchaseOrderDTO.getItemDetails()){
				Warehouse warehouse = null;
				if(warehousesMap.containsKey(details.getWarehouse())){
					warehouse = warehousesMap.get(details.getWarehouse());
				}else{
					warehouse = warehouseService.getWarehouseByName(details.getWarehouse());
					warehousesMap.put(details.getWarehouse(), warehouse);
				}
				ItemDetails itemDetail = new ItemDetails(details.getVendorProductId(), details.getReceiveDate(),
						details.getBestBefore(), details.getSellingDate(), details.getWarrantyInMonths(), details.getBarcode(),
						0, warehouse, details.getItemCode(),
						details.getStatus(), details.getAssignedSalesOrder(), details.getPurchaseOrder(),
						details.getUnitPrice(), details.getWholesalePrice());
				itemDetail.setItemCode(details.getItemCode());
				itemDetails.add(itemDetail);
			}
			for (ItemOrderLineDTO orderLineItemDTO : purchaseOrderDTO.getItems()){
				UoM uom = null;
				if(uomsMap.containsKey(orderLineItemDTO.getUnit())){
					uom = uomsMap.get(orderLineItemDTO.getUnit());
				}else{
					uom = uomService.getUnitByNotation(orderLineItemDTO.getUnit());
					uomsMap.put(orderLineItemDTO.getUnit(), uom);
				}
				ItemOrderLine itemOrderLine = new ItemOrderLine(orderLineItemDTO.getItemCode(), orderLineItemDTO.getVendorAlias(),
						uom, orderLineItemDTO.getUnitPrice(),
						orderLineItemDTO.getWholesalePrice(), orderLineItemDTO.getDescription(),
						orderLineItemDTO.isItemSerialized(), orderLineItemDTO.getBarcode());
				itemOrderLines.add(itemOrderLine);
			}
			for (ItemRequestPair details : purchaseOrderDTO.getReturnItemDetails()){
				Warehouse warehouse = null;
				if(warehousesMap.containsKey(details.getWarehouse())){
					warehouse = warehousesMap.get(details.getWarehouse());
				}else{
					warehouse = warehouseService.getWarehouseByName(details.getWarehouse());
					warehousesMap.put(details.getWarehouse(), warehouse);
				}
				ItemDetails itemDetail = new ItemDetails(details.getVendorProductId(), details.getReceiveDate(),
						details.getBestBefore(), details.getSellingDate(), details.getWarrantyInMonths(), details.getBarcode(),
						0, warehouse, details.getItemCode(),
						details.getStatus(), details.getAssignedSalesOrder(), details.getPurchaseOrder(),
						details.getUnitPrice(), details.getWholesalePrice());
				itemDetail.setItemCode(details.getItemCode());
				returnItemDetails.add(itemDetail);
			}
			VendorDTO vendorDTO = purchaseOrderDTO.getVendor();
			Vendor vendor = new Vendor(vendorDTO.getId(), vendorDTO.getName(),
					new Address(vendorDTO.getAddressLine1(), vendorDTO.getAddressLine2(), vendorDTO.getCity(),
							vendorDTO.getLocation(), vendorDTO.getState(), vendorDTO.getCountry(), vendorDTO.getPincode()),
					vendorDTO.getContactNo(), vendorDTO.getContactPerson(), convertToItemList(vendorDTO.getItemsSold()),
					vendorDTO.getVersion(), vendorDTO.getIsOrganised());
			PurchaseOrder order = new PurchaseOrder(purchaseOrderDTO.getId(), purchaseOrderDTO.getOrderNo(),
					vendor, itemOrderLines,
					purchaseOrderDTO.getStatus(), purchaseOrderDTO.getDate(), null, 0,
					itemDetails, purchaseOrderDTO.getTaxes(),
					purchaseOrderDTO.getTotal(), purchaseOrderDTO.getAmtPaid(), purchaseOrderDTO.getAmtBalance(),
					purchaseOrderDTO.getPaymentDetails(), purchaseOrderDTO.getTaxDetails(),
					warehousesMap.get(purchaseOrderDTO.getShippingWarehouse().getName()), purchaseOrderDTO.getContactPerson(),
					purchaseOrderDTO.getContactNumber(), purchaseOrderDTO.getSubTotal(),
					purchaseOrderDTO.getInvoiceNo(), purchaseOrderDTO.getInvoiceDate(),
					purchaseOrderDTO.getLastModDttm(), returnItemDetails,
					purchaseOrderDTO.getKitNumber(), purchaseOrderDTO.getVatTax(), purchaseOrderDTO.getIsOnTheFly());
			order.setVersion(purchaseOrderDTO.getVersion());
			return order;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return null;
		}
	}

	@Override
	public PurchaseOrderDTO convert(PurchaseOrder purchaseOrder) {
		try {
			List<ItemRequestPair> itemDetails = new ArrayList<>();
			List<ItemOrderLineDTO> itemOrderLines = new ArrayList<>();
			List<ItemRequestPair> returnItemDetails = new ArrayList<>();
			itemOrderLines = (List<ItemOrderLineDTO>)convertToItemLineDTO(purchaseOrder.getItems());
			itemDetails = (List<ItemRequestPair>)convertToItemRequestPair(purchaseOrder.getItemDetails());
			returnItemDetails = (List<ItemRequestPair>)convertToItemRequestPair(purchaseOrder.getReturnItemDetails());
			VendorDTO vendorDTO = convert(purchaseOrder.getVendor());
			PurchaseOrderDTO order = new PurchaseOrderDTO(purchaseOrder.getId(), purchaseOrder.getOrderNo(), vendorDTO,
					itemOrderLines, purchaseOrder.getStatus(), purchaseOrder.getDate(),
					itemDetails, purchaseOrder.getTaxes(),
					purchaseOrder.getTotal(), purchaseOrder.getAmtPaid(), purchaseOrder.getAmtBalance(),
					purchaseOrder.getPaymentDetails(), purchaseOrder.getTaxDetails(),
					convertToWarehouseNetworkDTO(purchaseOrder.getShippingWarehouse()),
					purchaseOrder.getContactPerson(), purchaseOrder.getContactNumber(), purchaseOrder.getSubTotal(),
					purchaseOrder.getInvoiceNo(), purchaseOrder.getInvoiceDate(), purchaseOrder.getVersion(),
					purchaseOrder.getCreatedDttm(), purchaseOrder.getLastModDttm(),
					returnItemDetails, purchaseOrder.getKitNumber(),
					purchaseOrder.getVatTax(), purchaseOrder.getIsOnTheFly());
			return order;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return null;
		}
		/*PurchaseOrderDTO order = new PurchaseOrderDTO(purchaseOrder.getId(), purchaseOrder.getOrderNo(),
				convert(purchaseOrder.getVendor()), convertToItemLineDTO(purchaseOrder.getItems()),
				purchaseOrder.getStatus(), purchaseOrder.getDate(),
				convertToItemRequestPair(purchaseOrder.getItemDetails()), purchaseOrder.getTaxes(),
				purchaseOrder.getTotal(), purchaseOrder.getAmtPaid(), purchaseOrder.getAmtBalance(),
				purchaseOrder.getPaymentDetails(), purchaseOrder.getTaxDetails(),
				convert(purchaseOrder.getShippingWarehouse()), purchaseOrder.getContactPerson(),
				purchaseOrder.getContactNumber(), purchaseOrder.getSubTotal(), purchaseOrder.getInvoiceNo(),
				purchaseOrder.getInvoiceDate(), purchaseOrder.getVersion(), purchaseOrder.getCreatedDttm(),
				purchaseOrder.getLastModDttm(), convertToItemRequestPair(purchaseOrder.getReturnItemDetails()),
				purchaseOrder.getKitNumber(), purchaseOrder.getVatTax(), purchaseOrder.getIsOnTheFly());
		return order;*/
	}

	@Override
	public PurchaseOrderDTO quickConvert(PurchaseOrder purchaseOrder) {
		Vendor vendor = purchaseOrder.getVendor();
		VendorDTO vendorDTO = new VendorDTO(vendor.getId(), vendor.getName(),
				vendor.getOutletAddress().getAddressLine1(), vendor.getOutletAddress().getAddressLine2(),
				vendor.getOutletAddress().getCity(), vendor.getOutletAddress().getLocation(),
				vendor.getOutletAddress().getState(), vendor.getOutletAddress().getCountry(),
				vendor.getOutletAddress().getPincode(), vendor.getContactNo(), vendor.getContactPerson(),
				new ArrayList<>(), vendor.getVersion(), vendor.getIsOrganised());
		PurchaseOrderDTO order = new PurchaseOrderDTO(purchaseOrder.getId(), purchaseOrder.getOrderNo(), vendorDTO,
				convertToItemLineDTO(purchaseOrder.getItems()), purchaseOrder.getStatus(), purchaseOrder.getDate(),
				convertToItemRequestPair(purchaseOrder.getItemDetails()), purchaseOrder.getTaxes(),
				purchaseOrder.getTotal(), purchaseOrder.getAmtPaid(), purchaseOrder.getAmtBalance(),
				purchaseOrder.getPaymentDetails(), purchaseOrder.getTaxDetails(),
				convertToWarehouseNetworkDTO(purchaseOrder.getShippingWarehouse()),
				purchaseOrder.getContactPerson(), purchaseOrder.getContactNumber(), purchaseOrder.getSubTotal(),
				purchaseOrder.getInvoiceNo(), purchaseOrder.getInvoiceDate(), purchaseOrder.getVersion(),
				purchaseOrder.getCreatedDttm(), purchaseOrder.getLastModDttm(),
				convertToItemRequestPair(purchaseOrder.getReturnItemDetails()), purchaseOrder.getKitNumber(),
				purchaseOrder.getVatTax(), purchaseOrder.getIsOnTheFly());
		return order;
	}

	@Override
	public List<PurchaseOrderDTO> convertToPurchaseOrderDTO(Collection<PurchaseOrder> orders) {
		List<PurchaseOrderDTO> listOfOrders = new ArrayList<>();
		for (PurchaseOrder order : orders) {
			listOfOrders.add(convert(order));
		}
		return listOfOrders;
	}

	@Override
	public List<PurchaseOrderDTO> convertToPurchaseOrderDTONew(List<PurchaseOrder> orders) {
		List<PurchaseOrderDTO> listOfOrders = new ArrayList<>();
		for (PurchaseOrder purchaseOrder : orders) {
			PurchaseOrderDTO order = new PurchaseOrderDTO(purchaseOrder.getId(), purchaseOrder.getOrderNo(),
					convert(purchaseOrder.getVendor()), convertToItemLineDTO(purchaseOrder.getItems()),
					purchaseOrder.getStatus(), purchaseOrder.getDate(),
					convertToItemRequestPair(purchaseOrder.getItemDetails()), purchaseOrder.getTaxes(),
					purchaseOrder.getTotal(), purchaseOrder.getAmtPaid(), purchaseOrder.getAmtBalance(),
					purchaseOrder.getPaymentDetails(), purchaseOrder.getTaxDetails(),
					convertToWarehouseNetworkDTO(purchaseOrder.getShippingWarehouse()), purchaseOrder.getContactPerson(),
					purchaseOrder.getContactNumber(), purchaseOrder.getSubTotal(), purchaseOrder.getInvoiceNo(),
					purchaseOrder.getInvoiceDate(), purchaseOrder.getVersion(), purchaseOrder.getCreatedDttm(),
					purchaseOrder.getLastModDttm(), convertToItemRequestPair(purchaseOrder.getReturnItemDetails()),
					purchaseOrder.getKitNumber(), purchaseOrder.getVatTax(), purchaseOrder.getIsOnTheFly());
			listOfOrders.add(order);
		}
		return listOfOrders;
	}

	@Override
	public List<WarehouseDTO> convertToWarehouseDTO(List<Warehouse> warehouses) {
		List<WarehouseDTO> warehouseDTO = new ArrayList<>();
		if (warehouses != null) {
			for (Warehouse warehouse : warehouses) {
				warehouseDTO.add(convert(warehouse));
			}
			return warehouseDTO;
		}
		return warehouseDTO;
	}

	@Override
	public List<AttributeDTO> convertToAttributeDTO(List<Attribute> attributes) {
		List<AttributeDTO> attributeDTO = new ArrayList<>();
		for (Attribute attribute : attributes) {
			attributeDTO.add(convert(attribute));
		}
		return attributeDTO;
	}

	@Override
	public ItemOrderLineDTO convert(ItemOrderLine orderLineItem) {
		String unitNotation = null;
		if (orderLineItem.getUnit() != null)
			unitNotation = orderLineItem.getUnit().getNotation();
		return new ItemOrderLineDTO(orderLineItem.getItemCode(), null, unitNotation, orderLineItem.getUnitPrice(),
				orderLineItem.getTotalPrice(), orderLineItem.getDescription(), orderLineItem.isItemSerialized(),
				orderLineItem.getBarcode());
	}

	@Override
	public ItemOrderLine convert(ItemOrderLineDTO orderLineItemDTO) {
		return new ItemOrderLine(orderLineItemDTO.getItemCode(), orderLineItemDTO.getVendorAlias(),
				uomService.getUnitByNotation(orderLineItemDTO.getUnit()), orderLineItemDTO.getUnitPrice(),
				orderLineItemDTO.getWholesalePrice(), orderLineItemDTO.getDescription(),
				orderLineItemDTO.isItemSerialized(), orderLineItemDTO.getBarcode());
	}

	@Override
	public ItemRequestPair convert(ItemDetails details) {
		String statusText = ItemStatus.getStatus(details.getStatus()).text();
		ItemRequestPair pair = new ItemRequestPair(details.getWarehouse().getName(), details.getItemCode(),
				details.getBarcode(), details.getReceiveDate(), details.getBestBefore(), details.getSellingDate(),
				details.getWarrantyInMonths(), details.getVendorProductId(), details.getStatus(), statusText,
				details.getAssignedSalesOrder(), details.getPurchaseOrder(), details.getUnitPrice(),
				details.getWholesalePrice());
		return pair;
	}

	@Override
	public ItemDetails convert(ItemRequestPair details) {
		ItemDetails itemDetails = new ItemDetails(details.getVendorProductId(), details.getReceiveDate(),
				details.getBestBefore(), details.getSellingDate(), details.getWarrantyInMonths(), details.getBarcode(),
				0, warehouseService.getWarehouseByName(details.getWarehouse()), details.getItemCode(),
				details.getStatus(), details.getAssignedSalesOrder(), details.getPurchaseOrder(),
				details.getUnitPrice(), details.getWholesalePrice());
		itemDetails.setItemCode(details.getItemCode());
		return itemDetails;
	}

	@Override
	public TransferOrder convert(TransferOrderDTO orderDTO) {
		TransferOrder order = new TransferOrder(orderDTO.getId(), orderDTO.getOrderNumber(), orderDTO.getOrderDate(),
				orderDTO.getStatus(), convertToItemLine(orderDTO.getItems()), convert(orderDTO.getFromWarehouse()),
				convert(orderDTO.getToWarehouse()), orderDTO.getVersion(),
				convertToItemDetails(orderDTO.getItemDetails()), orderDTO.getSendDate(), orderDTO.getReceiveDate(),
				orderDTO.getKitNumber(), convertToItemDetails(orderDTO.getRecvdItemDetails()));
		return order;
	}

	@Override
	public TransferOrderDTO convert(TransferOrder order) {
		try {
			TransferOrderDTO orderDTO = new TransferOrderDTO(order.getId(), order.getOrderNumber(),
					order.getOrderDate(), order.getStatus(), convertToItemRequestPair(order.getItemDetails()),
					convert(order.getFromWarehouse()), convert(order.getToWarehouse()), order.getVersion(),
					convertToItemLineDTO(order.getItems()), order.getSendDate(), order.getReceiveDate(),
					order.getKitNumber(), convertToItemRequestPair(order.getRecvdItemDetails()));
			return orderDTO;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<TransferOrderDTO> convertToTransferOrderDTO(Collection<TransferOrder> orders) {
		List<TransferOrderDTO> orderDTOs = new ArrayList<>();
		for (TransferOrder order : orders) {
			orderDTOs.add(convert(order));
		}
		return orderDTOs;
	}

	@Override
	public List<TransferOrder> convertToTransferOrder(Collection<TransferOrderDTO> orderDTOs) {
		List<TransferOrder> orders = new ArrayList<>();
		for (TransferOrderDTO order : orderDTOs) {
			orders.add(convert(order));
		}
		return orders;
	}

	@Override
	public List<Kit> convertToKits(Collection<KitDTO> kitDTO) {
		List<Kit> kits = new ArrayList<>();
		if (kitDTO != null && kitDTO.size() > 0) {
			for (KitDTO kit : kitDTO) {
				kits.add(this.convert(kit));
			}
		}
		return kits;
	}

	@Override
	public List<KitDTO> convertToKitDTO(Collection<Kit> kits) {
		List<KitDTO> kitDTOs = new ArrayList<>();
		if (kits != null && kits.size() > 0) {
			for (Kit kit : kits) {
				kitDTOs.add(this.convert(kit));
			}
		}
		return kitDTOs;
	}

	@Override
	public Kit convert(KitDTO kitDTO) {
		Kit kit = new Kit(kitDTO.getId(), kitDTO.getKitNumber(),
				convertToSalesOrderItemLineCustomized(kitDTO.getItems()), convertToSubkit(kitDTO.getSubkits()),
				kitDTO.getLastModDttm(), kitDTO.getCreatedDttm(), kitDTO.getKitReopenTimes() , kitDTO.getVersion(),
				this.convert(kitDTO.getAssignedWarehouse()), kitDTO.getStatus(), kitDTO.getDate(),
				this.convert(kitDTO.getRequestWarehouse()));
		return kit;
	}

	private Collection<Subkit> convertToSubkit(Collection<SubkitDTO> subkits) {
		return null;
	}

	@Override
	public KitDTO convert(Kit kit) {
		KitDTO kitDTO = new KitDTO(kit.getId(), kit.getKitNumber(),
				this.convertToSalesOrderLineItemDTOCustomized(kit.getItems()), convertToSubkitDTO(kit.getSubkits()),
				kit.getLastModDttm(), kit.getCreatedDttm(), kit.getDate(), kit.getStatus(), kit.getKitReopenTimes(), kit.getVersion(),
				this.convert(kit.getAssignedWarehouse()), this.convert(kit.getRequestWarehouse()));
		return kitDTO;
	}

	@Override
	public KitDTO quickConvert(Kit kit) {
		KitDTO kitDTO = new KitDTO();
		WarehouseDTO assignedWarehouse = new WarehouseDTO();
		assignedWarehouse.setName(kit.getAssignedWarehouse().getName());
		assignedWarehouse.setCode(kit.getAssignedWarehouse().getCode());
		if(kit.getAssignedWarehouse().getMotherWarehouse() != null)
			assignedWarehouse.setMotherWarehouseCode((kit.getAssignedWarehouse().getMotherWarehouse().getCode()));
		kitDTO.setAssignedWarehouse(assignedWarehouse);
		WarehouseDTO requestWarehouse = new WarehouseDTO();
		requestWarehouse.setName(kit.getRequestWarehouse().getName());
		requestWarehouse.setCode(kit.getRequestWarehouse().getCode());
		if(kit.getAssignedWarehouse().getMotherWarehouse() != null)
			requestWarehouse.setMotherWarehouseCode((kit.getRequestWarehouse().getMotherWarehouse().getCode()));
		kitDTO.setRequestWarehouse(requestWarehouse);
		kitDTO.setDate(kit.getDate());
		kitDTO.setKitNumber(kit.getKitNumber());
		kitDTO.setStatus(kit.getStatus());
		kitDTO.setItems(new ArrayList<>());
		kitDTO.setSubkits(new ArrayList<>());
		return kitDTO;
	}

	@Override
	public KitDTO quickConvertNew(Kit kit) {
		KitDTO kitDTO = new KitDTO();
		WarehouseDTO assignedWarehouse = new WarehouseDTO();
		assignedWarehouse.setId(kit.getAssignedWarehouse().getId());
		assignedWarehouse.setName(kit.getAssignedWarehouse().getName());
		assignedWarehouse.setCode(kit.getAssignedWarehouse().getCode());
		kitDTO.setAssignedWarehouse(assignedWarehouse);
		WarehouseDTO requestWarehouse = new WarehouseDTO();
		requestWarehouse.setId(kit.getRequestWarehouse().getId());
		requestWarehouse.setName(kit.getRequestWarehouse().getName());
		requestWarehouse.setCode(kit.getRequestWarehouse().getCode());
		if(kit.getRequestWarehouse().getMotherWarehouse() != null)
			requestWarehouse.setMotherWarehouseCode((kit.getRequestWarehouse().getMotherWarehouse().getCode()));
		kitDTO.setRequestWarehouse(requestWarehouse);
		kitDTO.setDate(kit.getDate());
		kitDTO.setKitNumber(kit.getKitNumber());
		kitDTO.setStatus(kit.getStatus());
		kitDTO.setLastModDttm((kit.getLastModDttm()));
		kitDTO.setItems(quickConvertSalesOrderItemLineToSalesOrderItemLineDTO(kit.getItems()));
		List<SubkitDTO> subkits = new ArrayList<>();
		for (Subkit sk : kit.getSubkits()) {
			List<SalesOrderLineItemDTO> items = quickConvertSalesOrderItemLineToSalesOrderItemLineDTO(sk.getItems());
			WarehouseDTO location = new WarehouseDTO();
			location.setId(sk.getLocation().getId());
			location.setName(sk.getLocation().getName());
			location.setCode(sk.getLocation().getCode());
			if (kit.getAssignedWarehouse().getMotherWarehouse() != null)
				location.setMotherWarehouseCode((kit.getAssignedWarehouse().getMotherWarehouse().getCode()));
			SubkitDTO subkit = new SubkitDTO(sk.getId(), sk.getSubkitNumber(), sk.getKitNumber(), items,
					sk.getLastModDttm(), sk.getCreatedDttm(), sk.getDate(), sk.getStatus(), location);
			subkits.add(subkit);
		}
		kitDTO.setSubkits(subkits);
		return kitDTO;
	}

	/*
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * * * * * * PRIVATE METHODS START FROM
	 * HERE * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */

	private Collection<SubkitDTO> convertToSubkitDTO(Collection<Subkit> subkits) {
		if (subkits == null)
			return null;
		Collection<SubkitDTO> subkitDTOs = new ArrayList<>();
		for (Subkit subkit : subkits) {
			subkitDTOs.add(convert(subkit));
		}
		return subkitDTOs;
	}

	private Collection<ItemAttributeDTO> convertToItemAttributeDTO(Collection<ItemAttribute> itemAttributes) {
		Collection<ItemAttributeDTO> itemAttributesDTO = new ArrayList<>();
		for (ItemAttribute itemAttribute : itemAttributes)
			itemAttributesDTO.add(new ItemAttributeDTO(itemAttribute.getId(), convert(itemAttribute.getAttribute()),
					itemAttribute.getValue(), itemAttribute.getVersion()));
		return itemAttributesDTO;
	}

	private Collection<ItemAttribute> convertToItemAttribute(Collection<ItemAttributeDTO> itemAttribueDTO) {
		Collection<ItemAttribute> itemAttributes = new ArrayList<>();
		for (ItemAttributeDTO entry : itemAttribueDTO) {
			itemAttributes.add(new ItemAttribute(entry.getId(),
					convert(attributeService.getAttributeByName(entry.getAttribute().getName())), entry.getValue(),
					entry.getVersion()));
		}
		return itemAttributes;
	}

	private Collection<ItemRequestPair> convertToItemRequestPair(Collection<ItemDetails> itemDetails) {
		Collection<ItemRequestPair> list = new ArrayList<>();
		if (itemDetails != null) {
			Iterator<ItemDetails> ir = itemDetails.iterator();
			while (ir.hasNext()) {
				list.add(convert(ir.next()));
			}
		}
		return list;
	}

	private Collection<ItemDetails> convertToItemDetails(Collection<ItemRequestPair> itemDetailsDTO) {
		Collection<ItemDetails> list = new ArrayList<>();
		if (itemDetailsDTO != null) {
			Iterator<ItemRequestPair> ir = itemDetailsDTO.iterator();
			while (ir.hasNext()) {
				list.add(convert(ir.next()));
			}
		}
		return list;
	}

	private Collection<ItemOrderLineDTO> convertToItemLineDTO(Collection<ItemOrderLine> orderLines) {
		Collection<ItemOrderLineDTO> list = new ArrayList<>();
		for (ItemOrderLine orderLine : orderLines) {
			list.add(convert(orderLine));
		}
		return list;
	}

	private Collection<ItemOrderLine> convertToItemLine(Collection<ItemOrderLineDTO> orderLinesDTO) {
		Collection<ItemOrderLine> list = new ArrayList<>();
		if (orderLinesDTO != null) {
			for (ItemOrderLineDTO orderLine : orderLinesDTO) {
				list.add(convert(orderLine));
			}
		}
		return list;
	}

	private Collection<ItemStorageLocation> convertToItemStorageLocation(
			Collection<ItemStorageLocationDTO> itemStorageLocationsDTO) {
		Collection<ItemStorageLocation> itemStorageLocations = new ArrayList<>();
		if (itemStorageLocationsDTO != null && itemStorageLocationsDTO.size() > 0) {
			for (ItemStorageLocationDTO location : itemStorageLocationsDTO) {
				itemStorageLocations
						.add(new ItemStorageLocation(warehouseService.getWarehouseByName(location.getWarehouse()),
								convert(location.getShelf()), convert(location.getRack()), convert(location.getCrate())));
			}
		}
		return itemStorageLocations;
	}

	private Collection<ItemStorageLocationDTO> convertToItemStorageLocationDTO(
			Collection<ItemStorageLocation> itemStorageLocations) {
		Collection<ItemStorageLocationDTO> itemStorageLocationsDTO = new ArrayList<>();
		if (itemStorageLocations != null && itemStorageLocations.size() > 0) {
			for (ItemStorageLocation location : itemStorageLocations) {
				itemStorageLocationsDTO.add(new ItemStorageLocationDTO(location.getWarehouse().getName(),
						convert(location.getShelf()), convert(location.getRack()), convert(location.getCrate())));
			}
		}
		return itemStorageLocationsDTO;
	}

	private Collection<SalesOrderLineItemDTO> convertToSalesOrderLineItemDTOCustomized(
			Collection<SalesOrderItemLine> salesOrderItemLines) {
		Collection<SalesOrderLineItemDTO> salesOrderLineItemDTO = new ArrayList<>();
		if (salesOrderItemLines != null && salesOrderItemLines.size() > 0) {
			for (SalesOrderItemLine salesOrderLineItem : salesOrderItemLines) {
				SalesOrderLineItemDTO lineItem = convert(salesOrderLineItem);
				salesOrderLineItemDTO.add(lineItem);
			}
		}
		return salesOrderLineItemDTO;
	}

	private SalesOrderLineItemDTO convert(SalesOrderItemLine salesOrderLineItem) {
		WarehouseNetworkDTO currentLocation = convertToWarehouseNetworkDTO(salesOrderLineItem.getCurrentLocation());
		ItemNetworkDTO item = new ItemNetworkDTO();
		item.setDescription(salesOrderLineItem.getDescription());
		item.setVendorAlias(salesOrderLineItem.getVendorAlias());
		item.setCode(salesOrderLineItem.getItemCode());
		return new SalesOrderLineItemDTO(item, currentLocation, salesOrderLineItem.getBarcode(),
				salesOrderLineItem.getSerialNumber(), salesOrderLineItem.getStatus(),
				salesOrderLineItem.getScrapStatus(), salesOrderLineItem.getUnitPrice(),
				salesOrderLineItem.getWholesalePrice(), salesOrderLineItem.getKitNumber(),
				salesOrderLineItem.getSubkitNumber(), salesOrderLineItem.getOrderNo());
	}

	private Collection<SalesOrderItemLine> convertToSalesOrderItemLineCustomized(
			Collection<SalesOrderLineItemDTO> salesOrderItemLineDTO) {
		Collection<SalesOrderItemLine> salesOrderLineItems = new ArrayList<>();
		if (salesOrderItemLineDTO != null && salesOrderItemLineDTO.size() > 0) {
			for (SalesOrderLineItemDTO salesOrderLineItem : salesOrderItemLineDTO) {
				SalesOrderItemLine lineItem = convert(salesOrderLineItem);
				salesOrderLineItems.add(lineItem);
			}
		}
		return salesOrderLineItems;
	}

	private SalesOrderItemLine convert(SalesOrderLineItemDTO salesOrderLineItem) {
		Warehouse currentLocation = new Warehouse();
		currentLocation.setId(salesOrderLineItem.getCurrentLocation().getId());
		currentLocation.setName(salesOrderLineItem.getCurrentLocation().getName());
		currentLocation.setCode(salesOrderLineItem.getCurrentLocation().getCode());
		return new SalesOrderItemLine(salesOrderLineItem.getItem().getCode(), salesOrderLineItem.getItem().getDescription(), 
				salesOrderLineItem.getItem().getVendorAlias(), currentLocation, null, null, null, 
				salesOrderLineItem.getBarcode(), salesOrderLineItem.getSerialNumber(), salesOrderLineItem.getStatus(),
				salesOrderLineItem.getScrapStatus(), salesOrderLineItem.getUnitPrice(),
				salesOrderLineItem.getWholesalePrice());
	}

	public List<ShelfDTO> convertToShelfDTO(Collection<Shelf> shelfs) {
		if (shelfs == null)
			return null;
		List<ShelfDTO> shelfDTOs = new ArrayList<>();
		for (Shelf shelf : shelfs) {
			shelfDTOs.add(convert(shelf));
		}
		return shelfDTOs;
	}

	public List<RackDTO> convertToRackDTO(Collection<Rack> racks) {
		if (racks == null)
			return null;
		List<RackDTO> rackDTOs = new ArrayList<>();
		for (Rack rack : racks) {
			rackDTOs.add(convert(rack));
		}
		return rackDTOs;
	}

	public List<CrateDTO> convertToCrateDTO(Collection<Crate> crates) {
		if (crates == null)
			return null;
		List<CrateDTO> crateDTOs = new ArrayList<>();
		for (Crate crate : crates) {
			crateDTOs.add(convert(crate));
		}
		return crateDTOs;
	}

	public List<Shelf> convertToShelf(Collection<ShelfDTO> shelfDTOs) {
		if (shelfDTOs == null)
			return null;
		List<Shelf> shelves = new ArrayList<>();
		for (ShelfDTO shelfDTO : shelfDTOs) {
			shelves.add(convert(shelfDTO));
		}
		return shelves;
	}

	public List<Rack> convertToRack(Collection<RackDTO> rackDTOs) {
		if (rackDTOs == null)
			return null;
		List<Rack> racks = new ArrayList<>();
		for (RackDTO rackDTO : rackDTOs) {
			racks.add(convert(rackDTO));
		}
		return racks;
	}

	public List<Crate> convertToCrate(Collection<CrateDTO> crateDTOs) {
		if (crateDTOs == null)
			return null;
		List<Crate> crates = new ArrayList<>();
		for (CrateDTO crateDTO : crateDTOs) {
			crates.add(convert(crateDTO));
		}
		return crates;
	}
	
	@Override
	public List<ItemCount> convertObjectToItemCount(List<Object[]> items) {
		List<ItemCount> listOfItems = new ArrayList<>();
		if (items != null && items.size() > 0) {
			items.stream().forEach((record) -> {
				Warehouse warehouse = record[0] == null ? null
						: warehouseService.findById(((BigInteger) record[0]).longValue());
				String itemCode = record[1] == null ? null : (String) record[1];
				String description = record[2] == null ? null : (String) record[2];
				String vendorAlias = record[3] == null ? null : (String) record[3];
				String barcode = record[4] == null ? null : (String) record[4];
				int status = record[5] == null ? null : (Integer) record[5];
				Double reorderPoint = record[6] == null ? null : (Double) record[6];
				Double unitPrice = record[7] == null ? null : (Double) record[7];
				Double wholesalePrice = record[8] == null ? null : (Double) record[8];
				Shelf shelf = record[9] == null ? null : shelfService.findById(((BigInteger) record[9]).longValue());
				Rack rack = record[10] == null ? null : rackService.findById(((BigInteger) record[10]).longValue());
				Crate crate = record[11] == null ? null : crateService.findById(((BigInteger) record[11]).longValue());
				listOfItems.add(new ItemCount(warehouse, itemCode, description, vendorAlias, barcode, status, reorderPoint, unitPrice,
						wholesalePrice, shelf, rack, crate));
			});
		}
		return listOfItems;
	}
	
	@Override
	public List<ItemCountQuantityDTO> convertObjectToItemCountQuantityDTO(List<Object[]> items) {
		try {
			List<ItemCountQuantityDTO> listOfItems = new ArrayList<>();
			if (items != null && items.size() > 0) {
				items.stream().forEach((record) -> {
					String itemCode;
					String description;
					Double quantity;
					Double rate;
					itemCode = record[0] == null ? null : (String) record[0];
					description = record[1] == null ? null : (String) record[1];
					if(record[2] instanceof BigInteger){
						quantity = record[2] == null ? null : ((BigInteger) record[2]).doubleValue();
					} else{
						quantity = record[2] == null ? null : ((BigDecimal) record[2]).doubleValue();
					}
					if(record[3] instanceof BigInteger){
						rate = record[3] == null ? null : ((BigInteger) record[3]).doubleValue();
					} else{
						rate = record[3] == null ? null : ((BigDecimal) record[3]).doubleValue();
					}
					listOfItems.add(new ItemCountQuantityDTO(itemCode, description, quantity, rate));
				});
			}
			return listOfItems;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public List<ItemCountDTO> convertObjectToItemCountDTO(List<Object[]> items) {
		List<ItemCountDTO> listOfItems = new ArrayList<>();
		Map<Long, WarehouseNetworkDTO> warehouseMap = new HashMap<>();
		Map<Long, ShelfDTO> shelfMap = new HashMap<>();
		Map<Long, RackDTO> rackMap = new HashMap<>();
		Map<Long, CrateDTO> crateMap = new HashMap<>();
		try {
			if (items != null && items.size() > 0) {
				List<WarehouseDTO> warehouses = warehouseService.getAllWarehousesDTO();
				Map<Long, WarehouseDTO> map = new HashMap<>();
				for (WarehouseDTO warehouse : warehouses) {
					map.put(warehouse.getId(), warehouse);
				}
				items.stream().forEach((record) -> {
					WarehouseNetworkDTO warehouseNetworkDTO = null;
					ShelfDTO shelfDTO = null;
					RackDTO rackDTO = null;
					CrateDTO crateDTO = null;
					if(record[0] != null){
						Long id = ((BigInteger) record[0]).longValue();
						if(warehouseMap.containsKey(id)){
							warehouseNetworkDTO = warehouseMap.get(id);
						}else{
							warehouseNetworkDTO = convertToWarehouseNetworkDTO(warehouseService.findById(id));
							warehouseMap.put(id, warehouseNetworkDTO);
						}
					}
					if(record[9] != null){
						Long id = ((BigInteger) record[9]).longValue();
						if(shelfMap.containsKey(id)){
							shelfDTO = shelfMap.get(id);
						}else{
							shelfDTO = convert(shelfService.findById(id));
							shelfMap.put(id, shelfDTO);
						}
					}
					if(record[10] != null){
						Long id = ((BigInteger) record[10]).longValue();
						if(rackMap.containsKey(id)){
							rackDTO = rackMap.get(id);
						}else{
							rackDTO = convert(rackService.findById(id));
							rackMap.put(id, rackDTO);
						}
					}
					if(record[11] != null){
						Long id = ((BigInteger) record[11]).longValue();
						if(crateMap.containsKey(id)){
							crateDTO = crateMap.get(id);
						}else{
							crateDTO = convert(crateService.findById(id));
							crateMap.put(id, crateDTO);
						}
					}
					String itemCode = record[1] == null ? null : (String) record[1];
					String description = record[2] == null ? null : (String) record[2];
					String vendorAlias = record[3] == null ? null : (String) record[3];
					String barcode = record[4] == null ? null : (String) record[4];
					int status = record[5] == null ? null : (Integer) record[5];
					Double reorderPoint = record[6] == null ? null : (Double) record[6];
					Double unitPrice = record[7] == null ? null : (Double) record[7];
					Double wholesalePrice = record[8] == null ? null : (Double) record[8];
					ItemCountDTO count = new ItemCountDTO(warehouseNetworkDTO, itemCode, description, vendorAlias, 
							barcode, status, reorderPoint, unitPrice, wholesalePrice, shelfDTO, rackDTO, crateDTO);
					listOfItems.add(count);
				});
			}
			return listOfItems;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<ItemSalesRequest> convertToItemSalesRequestList(List<ItemRequestPair> requests) {
		try {
			List<ItemSalesRequest> newRequests = new ArrayList<>();
			Map<String, ItemSalesRequest> map = new HashMap<>();
			for (ItemRequestPair request : requests) {
				ItemSalesRequest itemSalesRequest = null;
				if (map.containsKey(request.getItemCode())) {
					itemSalesRequest = map.get(request.getItemCode());
				} else {
					itemSalesRequest = new ItemSalesRequest();
					itemSalesRequest.setItemCode(request.getItemCode());
					map.put(request.getItemCode(), itemSalesRequest);
				}
				if (ItemStatus.RETURN_EXPECTED.isEquals(request.getStatus())
						|| ItemStatus.RETURN_DISPATCHED_BY_HUB.isEquals(request.getStatus())
						|| ItemStatus.RETURN_RECEIVED_BY_HUB.isEquals(request.getStatus())
						|| ItemStatus.RETURN_RECEIVED_BY_WAREHOUSE.isEquals(request.getStatus())) {
					itemSalesRequest.setQuantityReturnExpected(itemSalesRequest.getQuantityReturnExpected() + 1.0);
					itemSalesRequest.getQuantityReturnExpectedBarcodes().add(request.getBarcode());
				} else {
					itemSalesRequest.setQuantityRequested(itemSalesRequest.getQuantityRequested() + 1.0);
					itemSalesRequest.getQuantityRequestedBarcodes().add(request.getBarcode());
				}
			}
			return newRequests;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<ItemRequestPair> convertToItemRequestPairList(List<ItemSalesRequest> requests) {
		try {
			List<ItemRequestPair> newRequests = new ArrayList<>();
			for (ItemSalesRequest request : requests) {
				for (int i = 0; i < request.getQuantityRequested(); i++) {
					String warehouse = null;
					String itemCode = request.getItemCode();
					String barcode = null;// request.getQuantityRequestedBarcodes().get(i);
					Date receiveDate = null;
					Date bestBefore = null;
					Date sellingDate = null;
					int warrantyInMonths = 0;
					String vendorProductId = null;
					int newStatus = ItemStatus.AVAILABLE.value();
					String statusText = ItemStatus.AVAILABLE.text();
					String assignedSalesOrder = null;
					String purchaseOrder = null;
					newRequests.add(new ItemRequestPair(warehouse, itemCode, barcode, receiveDate, bestBefore,
							sellingDate, warrantyInMonths, vendorProductId, newStatus, statusText, assignedSalesOrder,
							purchaseOrder, 0, 0));
				}/*
				for (int i = 0; i < request.getQuantityReturnExpected(); i++) {
					String warehouse = null;
					String itemCode = request.getItemCode();
					String barcode = null;// request.getQuantityReturnExpectedBarcodes().get(i);
					Date receiveDate = null;
					Date bestBefore = null;
					Date sellingDate = null;
					int warrantyInMonths = 0;
					String vendorProductId = null;
					int newStatus = ItemStatus.RETURN_EXPECTED.value();
					String statusText = ItemStatus.RETURN_EXPECTED.text();
					String assignedSalesOrder = null;
					String purchaseOrder = null;
					newRequests.add(new ItemRequestPair(warehouse, itemCode, barcode, receiveDate, bestBefore,
							sellingDate, warrantyInMonths, vendorProductId, newStatus, statusText, assignedSalesOrder,
							purchaseOrder, 0, 0));
				}*/
			}
			return newRequests;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Map<String, SalesOrderItemLine> convertToMap(Collection<SalesOrderItemLine> items) {
		Map<String, SalesOrderItemLine> map = new HashMap<>();
		for (SalesOrderItemLine salesOrderItemLine : items) {
			map.put(salesOrderItemLine.getBarcode(), salesOrderItemLine);
		}
		return map;
	}

	@Override
	public ItemOrderLine convertObjectToItemOrderLine(Object[] records) {
		if (records == null || records.length == 0)
			return null;
		Object record[] = (Object[]) records[0];
		String barcode = record[1] == null ? null : (String) record[1];
		String description = record[2] == null ? null : (String) record[2];
		Boolean isItemSerialized = record[3] == null ? null : (Boolean) record[3];
		String itemCode = record[4] == null ? null : (String) record[4];
		Double unitPrice = record[6] == null ? null : (Double) record[6];
		String vendorAlias = record[7] == null ? null : (String) record[7];
		Double wholesalePrice = record[8] == null ? null : (Double) record[8];
		return new ItemOrderLine(itemCode, vendorAlias, null, unitPrice, wholesalePrice, description, isItemSerialized,
				barcode);
	}

	@Override
	public ItemCount convertObjectListToItemCount(List<Object[]> items) {
		List<ItemCount> itemCounts = convertObjectToItemCount(items);
		if (itemCounts != null && itemCounts.size() > 0) {
			return itemCounts.get(0);
		}
		return null;
	}

	@Override
	public List<SubkitDTO> convertSubkitToSubkitDTO(List<Subkit> subkits) {
		if (subkits == null)
			return null;
		List<SubkitDTO> subkitDTOs = new ArrayList<>();
		for (Subkit subkit : subkits) {
			subkitDTOs.add(convert(subkit));
		}
		return subkitDTOs;
	}

	private SubkitDTO convert(Subkit subkit) {
		if (subkit == null)
			return null;
		SubkitDTO subkitDTO = new SubkitDTO(subkit.getId(), subkit.getSubkitNumber(), subkit.getKitNumber(),
				convertToSalesOrderLineItemDTOCustomized(subkit.getItems()), subkit.getLastModDttm(),
				subkit.getCreatedDttm(), subkit.getDate(), subkit.getStatus(), convert(subkit.getLocation()));
		return subkitDTO;
	}

	@Override
	public List<SubkitDTO> convertObjectToSubkitDTO(List<Object[]> subkits) {
		List<SubkitDTO> _subkits = new ArrayList<>();
		if (subkits != null && subkits.size() > 0) {
			subkits.stream().forEach((record) -> {
				long id = record[0] == null ? null : ((BigInteger) record[0]).longValue();
				Warehouse location = record[1] == null ? null
						: warehouseService.findById(((BigInteger) record[1]).longValue());
				String subkitNumber = record[2] == null ? null : (String) record[2];
				String kitNumber = record[3] == null ? null : (String) record[3];
				int status = record[4] == null ? null : (Integer) record[4];
				_subkits.add(
						new SubkitDTO(id, subkitNumber, kitNumber, null, null, null, null, status, convert(location)));
			});
		}
		return _subkits;
	}

	@Override
	public List<ItemStatusDTO> convertItemStatusEnumToItemStatusDTO(List<ItemStatus> itemStatus) {
		if (itemStatus == null) {
			return null;
		}
		List<ItemStatusDTO> items = new ArrayList<>();
		for (ItemStatus item : itemStatus) {
			ItemStatusDTO itemStatusDTO = new ItemStatusDTO(item.value(), item.text());
			items.add(itemStatusDTO);
		}
		return items;
	}

	@Override
	public List<KitDTO> quickConvertKitToKitDTO(Collection<Kit> kits) {
		if (kits == null)
			return null;
		List<KitDTO> kitDTOs = new ArrayList<>();
		for (Kit kit : kits) {
			KitDTO kitDTO = new KitDTO();
			WarehouseDTO assignedWarehouse = new WarehouseDTO();
			assignedWarehouse.setName(kit.getAssignedWarehouse().getName());
			kitDTO.setAssignedWarehouse(assignedWarehouse);
			WarehouseDTO requestWarehouse = new WarehouseDTO();
			requestWarehouse.setName(kit.getRequestWarehouse().getName());
			kitDTO.setDate(kit.getDate());
			kitDTO.setKitNumber(kit.getKitNumber());
			kitDTO.setStatus(kit.getStatus());
			kitDTO.setRequestWarehouse(requestWarehouse);
			kitDTO.setItems(new ArrayList<>());
			kitDTO.setSubkits(new ArrayList<>());
			kitDTOs.add(kitDTO);
		}
		return kitDTOs;
	}

	@Override
	public List<SalesOrderLineItemDTO> quickConvertSalesOrderItemLineToSalesOrderItemLineDTO(
			Collection<SalesOrderItemLine> items) {
		if (items == null) {
			return null;
		}
		try {
			List<SalesOrderLineItemDTO> newItems = new ArrayList<>();
			for (SalesOrderItemLine salesOrderItemLine : items) {
				SalesOrderLineItemDTO salesOrderLineItemDTO = new SalesOrderLineItemDTO();
				ItemNetworkDTO item = new ItemNetworkDTO();
				item.setDescription(salesOrderItemLine.getDescription());
				item.setCode(salesOrderItemLine.getItemCode());
				item.setVendorAlias(salesOrderItemLine.getVendorAlias());
				salesOrderLineItemDTO.setItem(item);
				WarehouseNetworkDTO warehouse = new WarehouseNetworkDTO();
				warehouse.setId(salesOrderItemLine.getCurrentLocation().getId());
				warehouse.setName(salesOrderItemLine.getCurrentLocation().getName());
				warehouse.setCode(salesOrderItemLine.getCurrentLocation().getCode());
				salesOrderLineItemDTO.setCurrentLocation(warehouse);
				salesOrderLineItemDTO.setBarcode(salesOrderItemLine.getBarcode());
				salesOrderLineItemDTO.setUnitPrice(salesOrderItemLine.getUnitPrice());
				salesOrderLineItemDTO.setWholesalePrice(salesOrderItemLine.getWholesalePrice());
				salesOrderLineItemDTO.setSerialNumber(salesOrderItemLine.getSerialNumber());
				salesOrderLineItemDTO.setStatus(salesOrderItemLine.getStatus());
				salesOrderLineItemDTO.setScrapStatus(salesOrderItemLine.getScrapStatus());
				salesOrderLineItemDTO.setSuggestion(salesOrderItemLine.getSuggestion());
				salesOrderLineItemDTO.setKitNumber(salesOrderItemLine.getKitNumber());
				salesOrderLineItemDTO.setSubkitNumber(salesOrderItemLine.getSubkitNumber());
				salesOrderLineItemDTO.setOrderNo(salesOrderItemLine.getOrderNo());
				if(salesOrderItemLine.getSubkitNumber() != null){
					salesOrderItemLine.getSubkitNumber();
				}
				newItems.add(salesOrderLineItemDTO);
			}
			return newItems;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return null;
		}
	}

	@Override
	public List<PurchaseOrderDTO> quickConvertToPurchaseOrderDTO(List<PurchaseOrder> orders) {
		List<PurchaseOrderDTO> listOfOrders = new ArrayList<>();
		for (PurchaseOrder purchaseOrder : orders) {
			Vendor vendor = purchaseOrder.getVendor();
			VendorDTO vendorDTO = new VendorDTO();
			vendorDTO.setName(vendor.getName());
			PurchaseOrderDTO order = new PurchaseOrderDTO(purchaseOrder.getId(), purchaseOrder.getOrderNo(), vendorDTO,
					null, purchaseOrder.getStatus(), purchaseOrder.getDate(), null, 0, purchaseOrder.getTotal(),
					purchaseOrder.getAmtPaid(), purchaseOrder.getAmtBalance(), null, null, convertToWarehouseNetworkDTO(purchaseOrder.getShippingWarehouse()), null, null, 0,
					purchaseOrder.getInvoiceNo(), purchaseOrder.getInvoiceDate(), 0, null, null, null, null, 0.0, null);
			listOfOrders.add(order);
		}
		return listOfOrders;
	}

	@Override
	public DTResult<ItemCountDTO> convert(List<ItemCountDTO> itemCounts) {
		DTResult<ItemCountDTO> result = new DTResult<>();
		result.data = itemCounts;
		result.recordsFiltered = itemCounts.size();
		result.recordsTotal = itemCounts.size();
		return result;
	}
	
	@Override
	public List<WarehouseNetworkDTO> convertToWarehouseNetworkDTOList(List<Warehouse> warehouses){
		if(warehouses == null){
			return null;
		}
		List<WarehouseNetworkDTO> list = new ArrayList<>(); 
		if(warehouses.size() == 0)
			return list;
		for(Warehouse warehouse : warehouses){
			list.add(convertToWarehouseNetworkDTO(warehouse));
		}
		return list;
	}

	@Override
	public List<WarehouseNetworkDTO> convertObjectToWarehouseNetworkDTOList(List<Object[]> records) {
		List<WarehouseNetworkDTO> listOfItems = new ArrayList<>();
		if (records != null && records.size() > 0) {
			records.stream().forEach((record) -> {
				Long id = record[0] == null ? null : ((BigInteger) record[0]).longValue();
				String name = record[1] == null ? null : (String) record[1];
				String code = record[2] == null ? null : (String) record[2];
				boolean isMotherWarehouse = record[3] == null ? null : (Boolean) record[3];
				listOfItems.add(new WarehouseNetworkDTO(id, name, code, isMotherWarehouse));
			});
		}
		return listOfItems;
	}
}
