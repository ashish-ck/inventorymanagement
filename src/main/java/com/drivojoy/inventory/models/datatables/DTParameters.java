package com.drivojoy.inventory.models.datatables;

import java.util.List;

/// <summary>
/// The parameters sent by jQuery DataTables in AJAX queries.
/// </summary>
public class DTParameters
{
    /// <summary>
    /// Draw counter.
    /// This is used by DataTables to ensure that the Ajax returns from server-side processing requests are drawn in sequence by DataTables (Ajax requests are asynchronous and thus can return out of sequence).
    /// This is used as part of the draw return parameter (see below).
    /// </summary>
    public int draw;

    /// <summary>
    /// An array defining all columns in the table.
    /// </summary>
    public DTColumn[] columns ;

    /// <summary>
    /// An array defining how many columns are being ordering upon - i.e. if the array length is 1, then a single column sort is being performed, otherwise a multi-column sort is being performed.
    /// </summary>
    public List<DTOrder> order ;

    /// <summary>
    /// Paging first record indicator.
    /// This is the start point in the current data set (0 index based - i.e. 0 is the first record).
    /// </summary>
    public int start ;

    public int getDraw() {
		return draw;
	}
	/// <summary>
    /// Number of records that the table can display in the current draw.
    /// It is expected that the number of records returned will be equal to this number, unless the server has fewer records to return.
    /// Note that this can be -1 to indicate that all records should be returned (although that negates any benefits of server-side processing!)
    /// </summary>
    public int length ;

    /// <summary>
    /// Global search value. To be applied to all columns which have searchable as true.
    /// </summary>
    public DTSearch search ;

    /// <summary>
    /// Custom column that is used to further sort on the first Order column.
    /// </summary>
	public String sortOrder;
	
	public void setDraw(int draw) {
		this.draw = draw;
	}
	public DTColumn[] getColumns() {
		return columns;
	}
	public void setColumns(DTColumn[] columns) {
		this.columns = columns;
	}
	public List<DTOrder> getOrder() {
		return order;
	}
	public void setOrder(List<DTOrder> order) {
		this.order = order;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public DTSearch getSearch() {
		return search;
	}
	public void setSearch(DTSearch search) {
		this.search = search;
	}
	
    public String getSortOrder() {
		return columns != null && order != null && order.size() > 0
                ? (columns[order.get(0).Column].Data + (order.get(0).Dir == DTOrderDir.desc ? " " + order.get(0).Dir : ""))
                : null;
	}
	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}
    
    

}
