package com.drivojoy.inventory.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.drivojoy.inventory.dto.AttributeDTO;
import com.drivojoy.inventory.dto.CrateDTO;
import com.drivojoy.inventory.dto.ItemCategoryDTO;
import com.drivojoy.inventory.dto.ItemCountDTO;
import com.drivojoy.inventory.dto.ItemCountQuantityDTO;
import com.drivojoy.inventory.dto.ItemDTO;
import com.drivojoy.inventory.dto.ItemOrderLineDTO;
import com.drivojoy.inventory.dto.ItemRequestPair;
import com.drivojoy.inventory.dto.ItemSalesRequest;
import com.drivojoy.inventory.dto.ItemStatusDTO;
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
import com.drivojoy.inventory.models.Attribute;
import com.drivojoy.inventory.models.Crate;
import com.drivojoy.inventory.models.Item;
import com.drivojoy.inventory.models.ItemCategory;
import com.drivojoy.inventory.models.ItemCount;
import com.drivojoy.inventory.models.ItemDetails;
import com.drivojoy.inventory.models.ItemOrderLine;
import com.drivojoy.inventory.models.Kit;
import com.drivojoy.inventory.models.PurchaseOrder;
import com.drivojoy.inventory.models.Rack;
import com.drivojoy.inventory.models.SalesOrderItemLine;
import com.drivojoy.inventory.models.Shelf;
import com.drivojoy.inventory.models.Subkit;
import com.drivojoy.inventory.models.TransferOrder;
import com.drivojoy.inventory.models.Vendor;
import com.drivojoy.inventory.models.Warehouse;
import com.drivojoy.inventory.models.datatables.DTResult;
import com.drivojoy.inventory.utils.Constants.ItemStatus;

/**
 * Service that convert a database model to a view model
 * @author ashishsingh
 *
 */
@Service
@Transactional
public interface IDTOWrapper {

	ItemCategory convert(ItemCategoryDTO itemCategoryDTO);
	ItemCategoryDTO convert(ItemCategory itemCategory);
	
	Attribute convert(AttributeDTO attributeDTO);
	AttributeDTO convert(Attribute attribute);
	
	WarehouseDTO convert(Warehouse warehouse);
	Warehouse convert(WarehouseDTO warehouseDTO);
	
	VendorDTO convert(Vendor vendor);
	Vendor convert(VendorDTO vendorDTO);
	
	ItemDTO convert(Item item);
	Item convert(ItemDTO itemDTO);
	
	PurchaseOrder convert(PurchaseOrderDTO purchaseOrderDTO);
	PurchaseOrderDTO convert(PurchaseOrder purchaseOrder);
	
	Kit convert(KitDTO kit);
	KitDTO convert(Kit kit);
	KitDTO quickConvert(Kit kit);
	KitDTO quickConvertNew(Kit kit);
	TransferOrder convert(TransferOrderDTO orderDTO);
	TransferOrderDTO convert(TransferOrder order);
	
	ItemOrderLineDTO convert(ItemOrderLine orderLineItem);
	ItemOrderLine convert(ItemOrderLineDTO orderLineItemDTO);
	PurchaseOrderDTO quickConvert(PurchaseOrder purchaseOrder);
	ItemDetails convert(ItemRequestPair details);
	ItemRequestPair convert(ItemDetails details);
	ShelfDTO convert(Shelf shelf);
	RackDTO convert(Rack rack);
	CrateDTO convert(Crate crate);
	Shelf convert(ShelfDTO shelf);
	Rack convert(RackDTO rack);
	Crate convert(CrateDTO crate);
	WarehouseNetworkDTO convertToWarehouseNetworkDTO(Warehouse warehouse);
	WarehouseNetworkDTO convertWarehouseDTOToWarehouseNetworkDTO(WarehouseDTO warehouse);
	List<VendorDTO> convertToVendorDTO(Collection<Vendor> vendors);
	List<ItemCount> convertToItemCount(Collection<ItemCountDTO> itemCountDTO);
	List<ItemCountDTO> convertToItemCountDTO(Collection<ItemCount> itemCount);
	List<ItemDTO> convertToItemDTOList(Collection<Item> items);
	List<ItemDTO> quickConvertToItemDTOList(Collection<Item> items);
	List<Item> convertToItemList(Collection<ItemDTO> items);
	List<PurchaseOrderDTO> convertToPurchaseOrderDTO(Collection<PurchaseOrder> orders);
	List<WarehouseDTO> convertToWarehouseDTO(List<Warehouse> warehouses);
	List<AttributeDTO> convertToAttributeDTO(List<Attribute> attributes);
	List<TransferOrderDTO> convertToTransferOrderDTO(Collection<TransferOrder> orders);
	List<TransferOrder> convertToTransferOrder(Collection<TransferOrderDTO> orders);
	List<KitDTO> convertToKitDTO(Collection<Kit> kits);
	List<Kit> convertToKits(Collection<KitDTO> kitDTO);
	List<ShelfDTO> convertToShelfDTO(Collection<Shelf> shelfs);
	List<RackDTO> convertToRackDTO(Collection<Rack> racks);
	List<CrateDTO> convertToCrateDTO(Collection<Crate> crates);
	List<Shelf> convertToShelf(Collection<ShelfDTO> shelfDTOs);
	List<Rack> convertToRack(Collection<RackDTO> rackDTOs);
	List<Crate> convertToCrate(Collection<CrateDTO> crateDTOs);
	List<ItemCount> convertObjectToItemCount(List<Object[]> items);
	List<SubkitDTO> convertObjectToSubkitDTO(List<Object[]> subkits);
	ItemCount convertObjectListToItemCount(List<Object[]> items);
	List<ItemSalesRequest> convertToItemSalesRequestList(List<ItemRequestPair> requests);
	List<ItemRequestPair> convertToItemRequestPairList(List<ItemSalesRequest> requests);
	Map<String, SalesOrderItemLine> convertToMap(Collection<SalesOrderItemLine> items);
	ItemOrderLine convertObjectToItemOrderLine(Object object[]);
	List<SubkitDTO> convertSubkitToSubkitDTO(List<Subkit> subkits);
	List<ItemStatusDTO> convertItemStatusEnumToItemStatusDTO(List<ItemStatus> itemStatus);
	List<KitDTO> quickConvertKitToKitDTO(Collection<Kit> kits);
	List<SalesOrderLineItemDTO> quickConvertSalesOrderItemLineToSalesOrderItemLineDTO(Collection<SalesOrderItemLine> items);
	List<PurchaseOrderDTO> convertToPurchaseOrderDTONew(List<PurchaseOrder> purchaseOrders);
	List<ItemCountDTO> convertObjectToItemCountDTO(List<Object[]> items);
	List<PurchaseOrderDTO> quickConvertToPurchaseOrderDTO(List<PurchaseOrder> resultSetFull);
	DTResult<ItemCountDTO> convert(List<ItemCountDTO> itemCounts);
	List<WarehouseNetworkDTO> convertToWarehouseNetworkDTOList(List<Warehouse> warehouses);
	List<WarehouseNetworkDTO> convertObjectToWarehouseNetworkDTOList(List<Object[]> records);
	List<ItemCountQuantityDTO> convertObjectToItemCountQuantityDTO(List<Object[]> items);
}
