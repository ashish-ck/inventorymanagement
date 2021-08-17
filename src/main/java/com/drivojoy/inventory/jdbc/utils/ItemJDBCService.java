package com.drivojoy.inventory.jdbc.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedCaseInsensitiveMap;

import com.drivojoy.inventory.dto.ItemDTO;
import com.drivojoy.inventory.services.IUoMService;

/**
 * 
 * @author ashishsingh
 *
 * This is a JDBC Service that runs stored procedures from mysql
 */

@Component
public class ItemJDBCService {
	
	@Autowired
	private IUoMService uomService;
	
	//private JdbcTemplate jdbcTemplate;
	private SimpleJdbcCall getItemsByCategoryProc;
	private SimpleJdbcCall updateItemStatusAndCountProc;
	
	private final Logger logger = LoggerFactory.getLogger(ItemJDBCService.class);
	
    @Autowired
    public void setDataSource(DataSource dataSource) {
    	//Refer getItemsByCategory Stored Procedure from /src/main/resources/com/drivojoy/config/production/procedures.sql
        this.getItemsByCategoryProc =  new SimpleJdbcCall(dataSource).withProcedureName("getItemsByCategory");
      //Refer updateItemStatusAndCount Stored Procedure from /src/main/resources/com/drivojoy/config/production/procedures.sql
        this.updateItemStatusAndCountProc = new SimpleJdbcCall(dataSource).withProcedureName("updateItemStatusAndCount");
    }
	
    /**
     * 
     * @param id Id of the parent category
     * @return	Returns all the items that fall under parent category identified by id
     */
    @SuppressWarnings("rawtypes")
	public List<ItemDTO> getItemsByCategory(long id){
    	try{
        	List<ItemDTO> itemList = new ArrayList<>();
        	SqlParameterSource in = new MapSqlParameterSource().addValue("in_id", id);
        	Map<String, Object> out = getItemsByCategoryProc.execute(in);
        	List list = (List)out.get("#result-set-1");
        	Iterator ir = list.iterator();
        	while(ir.hasNext()){
        		LinkedCaseInsensitiveMap map = (LinkedCaseInsensitiveMap) ir.next();
				ItemDTO item = new ItemDTO((long)map.get("id"), (String)map.get("code"), 
        				(String)map.get("description"),
        				uomService.getById((long)map.get("unit_of_measurement")).getNotation(),
        				null, null, (boolean)map.get("is_active"),
        				(String)map.get("vendor_alias"), null,(String)map.get("barcode"), 
        				(double)map.get("warning_point"), (double)map.get("reorder_point"), 
        				(double)map.get("reorder_quantity"), 0, null,
        				(double)map.get("normal_price"), (double)map.get("wholesale_price"),
        				null, null, (boolean)map.get("is_serialized"), null, false, null);
        		logger.debug("Adding item to list : "+item.toString());
        		itemList.add(item);
        	}
        	return itemList;    		
    	}catch(Exception ex){
    		logger.error("Exception thrown while executing stored procedure call :"+ex.getCause());
    		throw new RuntimeException(ex.getMessage());
    	}
    }

    /**
     * @deprecated This service is deprecated and is no longer
     * @param barcodes Barcodes
     * @param status Status
     * @param voucherRef Voucher ref
     * @param quantity Quantity
     */
    
    public void updateItemStatusAndCount(String barcodes, int status, String voucherRef, double quantity){
    	try{
    		SqlParameterSource in = new MapSqlParameterSource().addValue("status_to_set", status).addValue("barcodes", barcodes).addValue("voucherRef", voucherRef).addValue("quantity", quantity);
        	updateItemStatusAndCountProc.execute(in);
    	}catch(Exception ex){
    		logger.error("Exception thrown while executing updateItemStatusAndCount procedure : "+ex.getCause());
    		throw new RuntimeException(ex.getMessage());
    	}
    	
    }
}
