package com.drivojoy.inventory.utils;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.stereotype.Service;

@Service
public interface Constants {
	
	/* INVENTORY OPERATIONS */
	public static final String _SALES_ORDER_FULFILLMENT = "Sales Order Fulfillment";
	public static final String _SALES_ORDER_RESTOCK = "Sales Order Restock";
	public static final String _PURCHASE_ORDER_FULFILLMENT = "Purchase Order Fulfillment";
	public static final String _PURCHASE_ORDER_RETURN = "Purchase Order Return";
	public static final String _STOCK_ADJUSTMENT = "Stock Adjustment";
	public static final String _STOCK_TRANSFER_SEND = "Stock Transfer Send";
	public static final String _STOCK_TRANSFER_RECEIVE = "Stock Transfer Receive";
	public static final String _STOCK_TRANSFER = "Stock Transfer";
	public static final String _SUBKIT_UPDATE = "Subkit Update";
	
	/* DOCUMENT TYPES AND PREFIXES */
	public static final String _SALES_ORDER = "Sales Order";
	public static final String _SALES_ORDER_PREFIX = "SO";
	public static final String _PURCHASE_ORDER = "Purchase Order";
	public static final String _PURCHASE_ORDER_PREFIX = "PO";
	public static final String _STOCK_ADJUSTMENT_PREFIX = "SA";
	public static final String _STOCK_TRANSFER_PREFIX = "ST";
	
	/* ORDER STATUSES */
	public static final String _STATUS_NEW_TEXT = "New";
	public static final String _STATUS_PREPARED_TEXT = "Prepared";
	public static final String _STATUS_PARTIALLY_PREPARED_TEXT = "Partially Prepared";
	public static final String _STATUS_OPEN_TEXT = "Open";
	public static final String _STATUS_RECEIVED_TEXT = "Recevied";
	public static final String _STATUS_DISPATCHED_TEXT = "Dispatched";
	public static final String _STATUS_DELIVERED_TEXT = "Dispatched";
	public static final String _STATUS_PARTIALLY_DISPATCHED_TEXT = "Partially Delivered";
	public static final String _STATUS_INVOICED_TEXT = "Invoiced";
	public static final String _STATUS_PARTIALLY_PAID_TEXT = "Partially Paid";
	public static final String _STATUS_PAID_TEXT = "Paid";
	public static final String _STATUS_REOPEN_TEXT = "Reopend";
	public static final String _STATUS_RETURN_TEXT = "Returned";
	public static final String _STATUS_REFUNDED_TEXT = "Refunded";
	public static final String _STATUS_FULFILLED_TEXT = "Fulfilled";
	
	public static final int _STATUS_OPEN = 1;
	public static final int _STATUS_RECEIVED = 2;
	public static final int _STATUS_DISPATCHED = 3;
	public static final int _STATUS_DELIVERED = 4;
	public static final int _STATUS_INVOICED = 5;
	public static final int _STATUS_PARTIALLY_PAID = 6;
	public static final int _STATUS_PAID = 7;
	public static final int _STATUS_REOPEN = 8;
	public static final int _STATUS_RETURN = 9;
	public static final int _STATUS_REFUNDED = 10;
	public static final int _STATUS_FULFILLED = 11;
	
	/* ERROR CODES AND RETURN CODES */
	public static final String _GENERIC_ERROR_MESSAGE = "Something went wrong, we are working on it";
	public static final String _API_BUSY_MESSAGE = "Transaction is busy, please try again in 30 seconds!";
	/* Deprecated */
	/* COLS FOR EXCEL UPLOADS */
	public static final int _CODE_COL = 0;
	public static final int _BARCODE_COL = 1;
	public static final int _DESCRIPTION_COL = 2;
	public static final int _UNIT_COL = 3;
	public static final int _VENDORALIAS_COL = 4;
	public static final int _CATEGORY_COL = 5;
	public static final int _WARNINGPT_COL = 7;
	public static final int _TAGS_COL = 6;
	public static final int _REORDERPT_COL = 8;
	public static final int _REORDERQTY_COL = 9;
	public static final int _PRICE_COL = 10;
	public static final int _WHOLESALE_COL = 11;
	public static final int _SERIALIZED_COL = 12;
	public static final int _QTY_COL = 13;
	public static final int _WAREHOUSE_COL = 14;
	public static final int _TAX_COL = 15;
	public static final int _BIKEBRAND_COL = 16;
	public static final int _BIKEMODEL_COL = 17;
	
	
	/* Deprecated */
	/* STATUS FOR ITEM DETAILS */
	public static final int _AVAILABLE = 1;
	public static final String _AVAILABLE_TEXT = "Available";
	public static final int _RESERVED = 2;
	public static final String _RESERVED_TEXT = "Reserved";
	public static final int _PARTIALLY_RESERVED = 3;
	public static final String _PARTIALLY_RESERVED_TEXT = "Partially Reserved";
	public static final int _ISSUED = 4;
	public static final String _ISSUED_TEXT = "Issued";
	public static final int _PARTIALLY_ISSUED = 5;
	public static final String _PARTIALLY_ISSUED_TEXT = "Partially Issued";
	public static final int _INVOICED = 7;
	public static final String _INVOICED_TEXT = "Invoiced";
	public static final int _REREQUESTED = 6;
	public static final String _REREQUESTED_TEXT = "Re-requested";
	public static final int _RESTOCK = 8;
	public static final String _RESTOCK_TEXT = "Restock";
	public static final int _NOT_IN_STOCK = -1;
	public static final String _NOT_IN_STOCK_TEXT = "Out of Stock";
	public static final int _EXPIRED = 9;
	public static final String _EXPIRED_TEXT = "Exipred";
	public static final int _IN_TRANSIT = 10;
	public static final String _IN_TRANSIT_TEXT = "In Transit";

	
	/* QUERY MODES */
	public static final int _ADD_MODE = 1;
	public static final int _OR_MODE = 2;
	
	/* Deprecated */
	/*KIT STATUES*/
	public static final int _NEW = 1;
	public static final int _PARTIALLY_PREPARED = 2;
	public static final int _PREPARED = 3;
	public static final int _DISPATCHED = 4;
	public static final int _PARTIALLY_DISPATCHED = 5;
	public static final int _IN_PROGRESS = 6;
	public static final int _CLOSED = 10;
	//public static final int _CLOSED = 7;
	
	public enum ItemStatus {
		OUT_OF_STOCK(100, "Out of Stock"), 
		AVAILABLE(200, "Available"),
		RESERVED(300, "Reserved"), 
		DISPATCHED(400, "Dispatched"),
		RECEIVED(500, "Received"),
		ISSUED(600, "Issued"),
		INVOICED(700, "Invoiced"),
		RETURN_EXPECTED(800, "Return Expected"), 
		RETURN_RECEIVED_BY_HUB(900, "Return Recieved By Hub"), 
		RETURN_DISPATCHED_BY_HUB(1000, "Return Dispatched By Hub"),
		RETURN_RECEIVED_BY_WAREHOUSE(1100, "Return Received By Warehouse"),
		STOCK_LOSS(1200, "Stock Loss"),
		ON_THE_FLY(1800, "On the Fly"),
		OUT_OF_STOCK_REQUESTED(1900, "Out of Stock Requested"),
		IN_TRANSIT(2000, "In Transit"),
		IN_HAND(2100, "In Hand"),
		PURCHASE_OPEN(2200, "Purchase Open"),
		SCRAP_RECEIVED(10000, "Received"), 
		SCRAP_CUSTOMER_KEPT(20000, "Customer Kept"),
		SCRAP_LOST(30000, "Lost"), 
		SCRAP_NOT_RECEIVED(40000, "Not Received"),
		SCRAP_NOT_AVAILABLE(50000, "Not Available"),
		SCRAP_NOT_EXPECTED(55000, "Scrap Not Expected"),
		SCRAP_EXPECTED(60000, "Scrap Expected");
		private final int value;
	    private final String text;
		private ItemStatus(int value, String text) {
			this.value = value;
	        this.text = text;
	    }
		public int value(){
			return value;
		}
	    public String text() {
	        return text;
	    }
	    public static ItemStatus getStatus(int status){
	    	for (ItemStatus warehouseItemStatus: ItemStatus.values()){
	    		if(warehouseItemStatus.value() == status)
	    			return warehouseItemStatus;
	    	}
	    	return null;
	    }
	    public boolean isEquals(int status){
	    	return value == status;
	    }
	    public boolean equals(ItemStatus status){
	    	return value == status.value;
	    }
	    public Collection<ItemStatus> preConditions(){
	    	Collection<ItemStatus> previousStates = new ArrayList<>();
			switch (ItemStatus.getStatus(value)) {
			case AVAILABLE:
				previousStates.add(ItemStatus.OUT_OF_STOCK);
				previousStates.add(ItemStatus.RETURN_RECEIVED_BY_WAREHOUSE);
				break;
			case RESERVED:
				previousStates.add(ItemStatus.AVAILABLE);
				previousStates.add(ItemStatus.OUT_OF_STOCK);
				break;
			case DISPATCHED:
				previousStates.add(ItemStatus.RESERVED);
				break;
			case RECEIVED:
				previousStates.add(ItemStatus.RESERVED);
				previousStates.add(ItemStatus.DISPATCHED);
				break;
			case ISSUED:
				previousStates.add(ItemStatus.RECEIVED);
				previousStates.add(ItemStatus.AVAILABLE);
				break;
			case INVOICED:
				previousStates.add(ItemStatus.AVAILABLE);
				previousStates.add(ItemStatus.RESERVED);
				previousStates.add(ItemStatus.DISPATCHED);
				previousStates.add(ItemStatus.RECEIVED);
				previousStates.add(ItemStatus.ISSUED);
				previousStates.add(ItemStatus.RETURN_EXPECTED);
				break;
			case RETURN_EXPECTED:
				previousStates.add(ItemStatus.RESERVED);
				previousStates.add(ItemStatus.DISPATCHED);
				previousStates.add(ItemStatus.RECEIVED);
				previousStates.add(ItemStatus.ISSUED);
				break;
			case RETURN_RECEIVED_BY_HUB:
				previousStates.add(ItemStatus.RETURN_EXPECTED);
				break;
			case RETURN_DISPATCHED_BY_HUB:
				previousStates.add(ItemStatus.RETURN_RECEIVED_BY_HUB);
				break;
			case RETURN_RECEIVED_BY_WAREHOUSE:
				previousStates.add(ItemStatus.RETURN_EXPECTED);
				previousStates.add(ItemStatus.RETURN_DISPATCHED_BY_HUB);
				break;
			case STOCK_LOSS:
				previousStates.add(ItemStatus.RETURN_EXPECTED);
				previousStates.add(ItemStatus.RETURN_RECEIVED_BY_HUB);
				previousStates.add(ItemStatus.RETURN_DISPATCHED_BY_HUB);
				previousStates.add(ItemStatus.RETURN_RECEIVED_BY_WAREHOUSE);
				break;
			default:
				break;
			}
			return previousStates;
	    }
	};
	public enum SubkitStatus {
		NEW(100, "New"),
		PARTIALLY_RESERVED(200, "Partially Reserved"), 
		RESERVED(300, "Reserved"),
		PARTIALLY_DISPATCHED(400, "Partially Dispatched"), 
		DISPATCHED(500, "Dispatched"),
		PARTIALLY_RECEIVED(600, "Partially Received"), 
		RECEIVED(700, "Received"),
		ISSUED(800, "Issued"),
		INVOICED(900, "Invoiced"),
		RETURN_EXPECTED(1000, "Return Expected"),
		RETURN_RECEIVED_BY_HUB(1100, "Return Received By Hub"), 
		RETURN_DISPATCHED_BY_HUB(1200, "Return Dispatched By Hub"),
		RETURN_RECEIVED_BY_WAREHOUSE(1300, "Return Received By Warehouse"),
		DISPUTED(1400, "Disputed"),
		CLOSED(1500, "Closed"),
		CANCELLED(1600, "Cancelled"), 
		REOPENED(1700, "Re-opened");
		private final int value;
	    private final String text;
		private SubkitStatus(int value, String text) {
			this.value = value;
	        this.text = text;
	    }
		public int value(){
			return value;
		}
	    public String text() {
	        return text;
	    }
	    
	    public static SubkitStatus getStatus(int status){
	    	for (SubkitStatus subkitStatus: SubkitStatus.values()){
	    		if(subkitStatus.value() == status)
	    			return subkitStatus;
	    	}
	    	return null;
	    }
	    
	    public boolean isEquals(int status){
	    	return value == status;
	    }
	};

	public enum KitStatus {
		NEW(100, "New"),
		IN_PROGRESS(200, "In Progress"), 
		INVOICED(300, "Invoiced"),
		RETURN_EXPECTED(400, "Return Expected"),
		RETURN_RECEIVED_BY_HUB(500, "Return Received By Hub"),
		RETURN_DISPATCHED_BY_HUB(600, "Return Dispatched By Hub"),
		RETURN_RECEIVED_BY_WAREHOUSE(700, "Return Received By Warehouse"),
		DISPUTED(800, "Disputed"),
		CLOSED(900, "Closed"),
		CANCELLED(1000, "Cancelled"),
		REOPENED(1100, "Re-opened");
		private final int value;
	    private final String text;
		private KitStatus(int value, String text) {
			this.value = value;
	        this.text = text;
	    }
		public int value(){
			return value;
		}
	    public String text() {
	        return text;
	    }
	    public static KitStatus getStatus(int status){
	    	for (KitStatus kitStatus: KitStatus.values()){
	    		if(kitStatus.value() == status)
	    			return kitStatus;
	    	}
	    	return null;
	    }
	    public boolean isEquals(int status){
	    	return value == status;
	    }
	};
}
