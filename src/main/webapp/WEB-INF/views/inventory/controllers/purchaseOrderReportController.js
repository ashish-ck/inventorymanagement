inventoryApp.controller("purchaseOrderReportController", function ($scope, $location, CRUDOperations) {
	$scope.selectedDateRange = null;
	$scope.purchaseOrderReportData = null;
	$scope.purchaseOrderItemDetails = [];
	$scope.isLoadingData = false;
	$scope.selectedWarehouse = null;
	$scope.fetchInitData = function(){
		$scope.fetchWarehouses();
	}
	$scope.itemStatuses = {
			"100": "Out of Stock", 
			"200": "Available",
			"300": "Reserved", 
			"400": "Dispatched",
			"500": "Received",
			"600": "Issued",
			"700": "Invoiced",
			"800": "Return Expected", 
			"900": "Return Recieved By Hub", 
			"1000": "Return Dispatched By Hub",
			"1100": "Return Received By Warehouse",
			"1200": "Stock Loss",
			"1300": "Requested",
			"1400": "Delivered",
			"1500": "To be Removed",
			"1600": "To be Removed",
			"1700": "Restocked",
			"1800": "On the Fly",
			"1900": "Out of Stock Requested",
			"2000": "In Transit",
			"2100": "In Hand",
			"2200": "Purchase Open",
			"10000": "Received", 
			"20000": "Customer Kept",
			"30000": "Lost", 
			"40000": "Not Received",
			"50000": "Not Available",
			"55000": "Scrap Not Expected",
			"60000":"Scrap Expected",
			};
	$scope.fetchReportData = function(){
		if ($scope.selectedDateRange) {
			$scope.purchaseOrderReportData = null;
			$scope.purchaseOrderItemDetails = [];
			$scope.fromDate = moment($scope.selectedDateRange.split(" - ")[0]).format("YYYY/MM/DD");
			$scope.toDate = moment($scope.selectedDateRange.split(" - ")[1]).format("YYYY/MM/DD");
			$('.update').button("loading");
			$scope.isLoadingData = true;
			var promiseget = CRUDOperations.get("api/purchaseOrders/getByDateRange?fromDate="+$scope.fromDate+"&toDate="+$scope.toDate + (($scope.selectedWarehouse)?"&warehouseId="+$scope.selectedWarehouse:"&warehouseId="));
			promiseget.then(function (data) {
				if(data.data.valid){
					$scope.purchaseOrderReportData = data.data.payload;
					$scope.purchaseOrderItemDetails = $scope.purchaseOrderReportData.orders;
					var labels = [];
					var qtyDelivered = [];
					var qtyRestocked = [];
					var qtyReturnExpected = [];
					var qtyOnTheFly = [];
					var purchaseOrdersOutgoing = [];
					var itemsInvoiced = [];
					for(var i = 0; i < $scope.purchaseOrderReportData.orders.length; i++){
						var count = $scope.purchaseOrderReportData.orders[i];
						labels.push(count.date);
						qtyDelivered.push(count.quantityDelivered);
						qtyRestocked.push(count.quantityDelivered);
						qtyReturnExpected.push(count.quantityDelivered);
						qtyOnTheFly.push(count.quantityDelivered);
						//purchaseOrdersOutgoing.push(1);
					}
					var areaChartData = {
							labels: labels,
							datasets: [
								{
						        	   label: "PurchaseOrders Outgoing",
						        	   fillColor: "rgb(153, 255, 102)",
						        	   strokeColor: "rgb(153, 255, 102)",
						        	   pointColor: "rgb(153, 255, 102)",
						        	   pointStrokeColor: "rgb(153, 255, 102)",
						        	   pointHighlightFill: "#fff",
						        	   pointHighlightStroke: "rgb(153, 255, 102)",
						        	   data: purchaseOrdersOutgoing
						           },
						           {
						        	   label: "Items used",
						        	   fillColor: "rgb(153, 255, 102)",
						        	   strokeColor: "rgb(153, 255, 102)",
						        	   pointColor: "rgb(153, 255, 102)",
						        	   pointStrokeColor: "rgb(153, 255, 102)",
						        	   pointHighlightFill: "#fff",
						        	   pointHighlightStroke: "rgb(153, 255, 102)",
						        	   data: itemsInvoiced
						           },
							           {
							        	   label: "Qty. Delivered",
							        	   fillColor: "rgb(153, 255, 102)",
							        	   strokeColor: "rgb(153, 255, 102)",
							        	   pointColor: "rgb(153, 255, 102)",
							        	   pointStrokeColor: "rgb(153, 255, 102)",
							        	   pointHighlightFill: "#fff",
							        	   pointHighlightStroke: "rgb(153, 255, 102)",
							        	   data: qtyDelivered
							           },
							           {
							        	   label: "Qty. Returned",
							        	   fillColor: "rgb(204, 0, 0)",
							        	   strokeColor: "rgb(204, 0, 0)",
							        	   pointColor: "rgb(204, 0, 0)",
							        	   pointStrokeColor: "rgb(204, 0, 0)",
							        	   pointHighlightFill: "#fff",
							        	   pointHighlightStroke: "rgb(204, 0, 0)",
							        	   data: qtyRestocked
							           },
							           {
							        	   label: "Ad-hoc items",
							        	   fillColor: "rgb(128, 128, 128)",
							        	   strokeColor: "rgb(128, 128, 128)",
							        	   pointColor: "rgb(128, 128, 128)",
							        	   pointStrokeColor: "rgb(128, 128, 128)",
							        	   pointHighlightFill: "#fff",
							        	   pointHighlightStroke: "rgb(128, 128, 128)",
							        	   data: qtyOnTheFly
							           }
							           ]
					};
					var lineChartOptions = {
							//Boolean - If we should show the scale at all
							showScale: true,
							//Boolean - Whether grid lines are shown across the chart
							scaleShowGridLines: true,
							//String - Colour of the grid lines
							scaleGridLineColor: "rgba(0,0,0,.05)",
							//Number - Width of the grid lines
							scaleGridLineWidth: 1,
							//Boolean - Whether to show horizontal lines (except X axis)
							scaleShowHorizontalLines: true,
							//Boolean - Whether to show vertical lines (except Y axis)
							scaleShowVerticalLines: true,
							//Boolean - Whether the line is curved between points
							bezierCurve: true,
							//Number - Tension of the bezier curve between points
							bezierCurveTension: 0.3,
							//Boolean - Whether to show a dot for each point
							pointDot: false,
							//Number - Radius of each point dot in pixels
							pointDotRadius: 4,
							//Number - Pixel width of point dot stroke
							pointDotStrokeWidth: 1,
							//Number - amount extra to add to the radius to cater for hit detection outside the drawn point
							pointHitDetectionRadius: 20,
							//Boolean - Whether to show a stroke for datasets
							datasetStroke: true,
							//Number - Pixel width of dataset stroke
							datasetStrokeWidth: 2,
							//Boolean - Whether to fill the dataset with a color
							datasetFill: true,
							//String - A legend template
							//legendTemplate: "<ul class=''"><% for (var i=0; i<datasets.length; i++){%><li><span style=\"background-color:<%=datasets[i].lineColor%>\"></span><%if(datasets[i].label){%><%=datasets[i].label%><%}%></li><%}%></ul>",
							//Boolean - whether to maintain the starting aspect ratio or not when responsive, if set to false, will take up entire container
							maintainAspectRatio: true,
							//Boolean - whether to make the chart responsive to window resizing
							responsive: true
					};
					var lineChartCanvas = $("#lineChart").get(0).getContext("2d");
					lineChartOptions.datasetFill = false;
					var lineChart = new Chart(lineChartCanvas).Line(areaChartData, lineChartOptions);
					//then you just need to generate the legend
					var legend = '<ul class="list-inline">'+
										'<li><h4><span class="label" style="background-color: rgb(128, 128, 128); color: #000">PurchaseOrders Outgoing : '+$scope.purchaseOrderReportData.orders.length+'</span></h4></li>'+
										'<li><h4><span class="label" style="background-color: rgb(128, 128, 128); color: #000">Items Invoiced : '+$scope.purchaseOrderReportData.totalQuantityInvoiced+'</span></h4></li>'+
										'<li><h4><span class="label" style="background-color: rgb(128, 128, 128); color: #000">Amount Invoiced : '+ parseInt($scope.purchaseOrderReportData.amountInvoiced)+'</span></h4></li>'+
										'<li><h4><span class="label" style="background-color: rgb(153, 255, 102); color: #000">Qty. Delivered : '+$scope.purchaseOrderReportData.totalQuantityDelivered+'</span></h4></li>'+
										'<li><h4><span class="label" style="background-color: rgb(204, 0, 0); color: #000">Qty. Returned : '+$scope.purchaseOrderReportData.totalQuantityRestocked+'</span></h4></li>'+
										'<li><h4><span class="label" style="background-color: rgb(128, 128, 128); color: #000">Ad-hoc Items : '+$scope.purchaseOrderReportData.totalQuantityOnTheFly+'</span></h4></li>'+
								 '</ul>'
					//and append it to your page somewhere
					document.getElementById('js-legend').innerHTML = legend;
					$('.update').button("reset");
					$scope.isLoadingData = false;
				}else{
					alert(data.data.message);
					$('.update').button("reset");
					$scope.isLoadingData = false;
				}
			},
			function (errorpl) {
				alert("Error!");
				$('.update').button("reset");
				$scope.isLoadingData = false;
			});
		}
	}
	$scope.openPurchaseOrderRequest = function(purchaseOrderNumber){
        var promiseget = CRUDOperations.get("/api/purchaseOrders/get/"+purchaseOrderNumber);
        promiseget.then(function (data) {
        	if(data.data.valid){
        		$scope.selectedRequest = data.data.payload;
        		$scope.showRequestItems($scope.selectedRequest);
        	}else{
        		alret(data.data.message);
        	}
        },
      function (errorpl) {
        	alert("Error!");
      });
	}
    $scope.showRequestItems = function(request){
    	if(request){
        	$scope.selectedRequest = request;
        	angular.forEach($scope.selectedRequest.items, function(item, key){
        		item.toUpdateStatus = false;
        	});
        	$('#requestItemsModal').modal('show');
    	}
    }
    $scope.fetchWarehouses = function(){
        var promiseget = CRUDOperations.get("/api/warehouses/getWarehouseNames");
        promiseget.then(function (data) {
        	if(data.data.valid){
        		$scope.listOfWarehouses = data.data.payload;
        	}else{
        		alert(data.data.message);
        	}
        },
      function (errorpl) {
        	alert("Error!");
      });
	}
    $scope.warehouseSelectEvent = function(){
    	$scope.selectedWarehouse = $scope.purchaseOrderItemDetails.selectedWarehouse.id;
    }
    $scope.exportItems = function(){
		var items = $scope.purchaseOrderItemDetails;
		$scope.exportableItems = [];
		$scope.exportableItems.push(["PO #","Kit Number","Shipping Warehouse", "Vendor","Qty. Delivered", "Total Wholesale Price","Total Unit Price","Vat tax","Status","Amount Invoiced"]);
		var barcodes = [];
		barcodes.push(["PO #","Kit Number", "Item Code","Barcode", "Wholesale Price","Unit Price"]);
		for (var i = 0;i < items.length;i++){
				var _item1 = [];
				_item1.push(items[i].orderNo);
				_item1.push(items[i].kitNumber);
				_item1.push(items[i].shippingWarehouse);
				_item1.push(items[i].vendor);
				_item1.push(items[i].quantityDelivered);
				_item1.push(items[i].totalWholesalePrice);
				_item1.push(items[i].totalUnitPrice);
				_item1.push(items[i].vatTax);
				_item1.push(items[i].statusText);
				_item1.push(items[i].amountInvoiced);
				$scope.exportableItems.push(_item1);
				for (var j = 0;j < items[i].items.length;j++){
					_item1 = [];
					_item1.push(items[i].orderNo);
					_item1.push(items[i].kitNumber);
					_item1.push(items[i].items[j].itemCode);
					_item1.push(items[i].items[j].barcode);
					_item1.push(items[i].items[j].wholesalePrice);
					_item1.push(items[i].items[j].unitPrice);
					barcodes.push(_item1);
				}
		}
		for (var i = 0;i < barcodes.length;i++){
			$scope.exportableItems.push(barcodes[i]);
		}
	}
	$scope.JSONToCSVConvertor = function(JSONData, ReportTitle, ShowLabel) {
	    var arrData = typeof JSONData != 'object' ? JSON.parse(JSONData) : JSONData;
	    var CSV = '';
	    if (ShowLabel) {
	        var row = "";
	        for (var index in arrData[0]) {
	            row += index + ',';
	        }
	        row = row.slice(0, -1);
	    }
	    for (var i = 0; i < arrData.length; i++) {
	        var row = "";
	        for (var index in arrData[i]) {
	            row += '"' + arrData[i][index] + '",';
	        }
	        row.slice(0, row.length - 1);
	        CSV += row + '\r\n';
	    }
	    if (CSV == '') {        
	        alert("Invalid data");
	        return;
	    }   
	    var fileName = "";
	    fileName += ReportTitle.replace(/ /g,"_");   
	    var uri = 'data:text/csv;charset=utf-8,' + escape(CSV);
	    var link = document.createElement("a");    
	    link.href = uri;
	    link.style = "visibility:hidden";
	    link.download = fileName + "_" + new Date() +  ".csv";
	    document.body.appendChild(link);
	    link.click();
	    document.body.removeChild(link);
	}
	$scope.exportData = function(){
		$scope.exportItems();
		$scope.JSONToCSVConvertor($scope.exportableItems, "Consolidated report: From " + $scope.fromDate +" to "+ $scope.toDate, true);
    };
});