package com.drivojoy.inventory.models.datatables;

import java.util.List;

/// <summary>
/// A full result, as understood by jQuery DataTables.
/// </summary>
/// <typeparam name="T">The data type of each row.</typeparam>
public class DTResult<T>
{
    /// <summary>
    /// The draw counter that this object is a response to - from the draw parameter sent as part of the data request.
    /// Note that it is strongly recommended for security reasons that you cast this parameter to an integer, rather than simply echoing back to the client what it sent in the draw parameter, in order to prevent Cross Site Scripting (XSS) attacks.
    /// </summary>
    public int draw;

    /// <summary>
    /// Total records, before filtering (i.e. the total number of records in the database)
    /// </summary>
    public long recordsTotal;

    /// <summary>
    /// Total records, after filtering (i.e. the total number of records after filtering has been applied - not just the number of records being returned for this page of data).
    /// </summary>
    public long recordsFiltered;

    /// <summary>
    /// The data to be displayed in the table.
    /// This is an array of data source objects, one for each row, which will be used by DataTables.
    /// Note that this parameter's name can be changed using the ajaxDT option's dataSrc property.
    /// </summary>
    public List<T> data;
}