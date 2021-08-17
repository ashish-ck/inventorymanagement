package com.drivojoy.inventory.models.datatables;

/// <summary>
/// A search, as sent by jQuery DataTables when doing AJAX queries.
/// </summary>
public class DTSearch
{
    /// <summary>
    /// Global search value. To be applied to all columns which have searchable as true.
    /// </summary>
    public String value;

    /// <summary>
    /// true if the global filter should be treated as a regular expression for advanced searching, false otherwise.
    /// Note that normally server-side processing scripts will not perform regular expression searching for performance reasons on large data sets, but it is technically possible and at the discretion of your script.
    /// </summary>
    public boolean regex;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isRegex() {
		return regex;
	}

	public void setRegex(boolean regex) {
		this.regex = regex;
	}
    
    
}