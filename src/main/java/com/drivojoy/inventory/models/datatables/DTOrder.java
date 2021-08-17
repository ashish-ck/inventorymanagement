package com.drivojoy.inventory.models.datatables;

/// <summary>
/// An order, as sent by jQuery DataTables when doing AJAX queries.
/// </summary>
public class DTOrder
{
    /// <summary>
    /// Column to which ordering should be applied.
    /// This is an index reference to the columns array of information that is also submitted to the server.
    /// </summary>
    public int Column ;

    /// <summary>
    /// Ordering direction for this column.
    /// It will be dt-string asc or dt-string desc to indicate ascending ordering or descending ordering, respectively.
    /// </summary>
    public DTOrderDir Dir;

	public int getColumn() {
		return Column;
	}

	public void setColumn(int column) {
		Column = column;
	}

	public DTOrderDir getDir() {
		return Dir;
	}

	public void setDir(DTOrderDir dir) {
		Dir = dir;
	}
    
    
}