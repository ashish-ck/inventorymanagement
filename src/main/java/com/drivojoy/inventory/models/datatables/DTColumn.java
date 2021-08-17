package com.drivojoy.inventory.models.datatables;

/// <summary>
/// A jQuery DataTables column.
/// </summary>
public class DTColumn
{
    /// <summary>
    /// Column's data source, as defined by columns.data.
    /// </summary>
    public String Data;

    /// <summary>
    /// Column's name, as defined by columns.name.
    /// </summary>
    public String Name;

    /// <summary>
    /// Flag to indicate if this column is searchable (true) or not (false). This is controlled by columns.searchable.
    /// </summary>
    public boolean Searchable;

    /// <summary>
    /// Flag to indicate if this column is orderable (true) or not (false). This is controlled by columns.orderable.
    /// </summary>
    public boolean Orderable;
    /// <summary>
    /// Specific search value.
    /// </summary>
    public DTSearch Search;
	public String getData() {
		return Data;
	}
	public void setData(String data) {
		Data = data;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public boolean isSearchable() {
		return Searchable;
	}
	public void setSearchable(boolean searchable) {
		Searchable = searchable;
	}
	public boolean isOrderable() {
		return Orderable;
	}
	public void setOrderable(boolean orderable) {
		Orderable = orderable;
	}
	public DTSearch getSearch() {
		return Search;
	}
	public void setSearch(DTSearch search) {
		Search = search;
	}
    
    
}