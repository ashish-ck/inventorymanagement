inventoryApp.controller("itemReportController", function ($scope, $location, CRUDOperations) {
	$scope.selectedDateRange = null;
	$scope.itemReportData = null;
	$scope.itemItemDetails = [];
	$scope.isLoadingData = false;
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
	    	$scope.selectedWarehouse = $scope.itemItemDetails.selectedWarehouse.id;
	    }
	$scope.fetchReportData = function(){
		if ($scope.selectedDateRange) {
			$scope.itemReportData = null;
			$scope.itemItemDetails = [];
			$scope.fromDate = moment($scope.selectedDateRange.split(" - ")[0]).format("YYYY/MM/DD");
			$scope.toDate = moment($scope.selectedDateRange.split(" - ")[1]).format("YYYY/MM/DD");
			$('.update').button("loading");
			$scope.isLoadingData = true;
			var promiseget = CRUDOperations.get("/api/items/getByDateRange?fromDate="+$scope.fromDate+"&toDate="+$scope.toDate + (($scope.selectedWarehouse)?"&warehouseId="+$scope.selectedWarehouse:"&warehouseId="));
			promiseget.then(function (data) {
				if(data.data.valid){
					$scope.itemReportData = data.data.payload;
					$scope.itemItemDetails = $scope.itemReportData.items;
					var labels = [];
					var qtyOutgoing = [];
					var qtyIncoming = [];
					var qtyCurrentStockCount = [];
					for(var i = 0; i < $scope.itemReportData.items.length; i++){
						var count = $scope.itemReportData.items[i];
						//labels.push(count.date);
						qtyOutgoing.push(count.quantityOutgoing);
						qtyIncoming.push(count.quantityIncoming);
						qtyCurrentStockCount.push(count.quantityCurrentStockCount);
					}
					var areaChartData = {
							labels: labels,
							datasets: [
								{
						        	   label: "Items Outgoing",
						        	   fillColor: "rgb(153, 255, 102)",
						        	   strokeColor: "rgb(153, 255, 102)",
						        	   pointColor: "rgb(153, 255, 102)",
						        	   pointStrokeColor: "rgb(153, 255, 102)",
						        	   pointHighlightFill: "#fff",
						        	   pointHighlightStroke: "rgb(153, 255, 102)",
						        	   data: qtyOutgoing
						           },
						           {
						        	   label: "Items used",
						        	   fillColor: "rgb(153, 255, 102)",
						        	   strokeColor: "rgb(153, 255, 102)",
						        	   pointColor: "rgb(153, 255, 102)",
						        	   pointStrokeColor: "rgb(153, 255, 102)",
						        	   pointHighlightFill: "#fff",
						        	   pointHighlightStroke: "rgb(153, 255, 102)",
						        	   data: qtyIncoming
						           },
							           {
							        	   label: "Qty. Delivered",
							        	   fillColor: "rgb(153, 255, 102)",
							        	   strokeColor: "rgb(153, 255, 102)",
							        	   pointColor: "rgb(153, 255, 102)",
							        	   pointStrokeColor: "rgb(153, 255, 102)",
							        	   pointHighlightFill: "#fff",
							        	   pointHighlightStroke: "rgb(153, 255, 102)",
							        	   data: qtyCurrentStockCount
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
										'<li><h4><span class="label" style="background-color: rgb(153, 255, 102); color: #000">Qty. Outgoing : '+$scope.itemReportData.totalQuantityOutgoing+'</span></h4></li>'+
										'<li><h4><span class="label" style="background-color: rgb(204, 0, 0); color: #000">Qty. Incoming : '+$scope.itemReportData.totalQuantityIncoming+'</span></h4></li>'+
										'<li><h4><span class="label" style="background-color: rgb(128, 128, 128); color: #000">Qty. Current Stock Count : '+$scope.itemReportData.totalQuantityCurrentStockCount+'</span></h4></li>'+
								 '</ul>'
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
	$scope.openItemRequest = function(itemNumber){
        var promiseget = CRUDOperations.get("/api/items/getByCode?itemCode="+itemNumber);
        promiseget.then(function (data) {
        	if(data.data.valid){
        		$scope.selectedRequest = {};
        		$scope.selectedRequest.items = [];
        		$scope.selectedRequest.items.push(data.data.payload);
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
        		//item.statusText = $scope.itemStatuses[item.status.toString()];
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
    	$scope.selectedWarehouse = $scope.itemItemDetails.selectedWarehouse.id;
    }
    $scope.exportItems = function(){
		var items = $scope.itemItemDetails;
		$scope.exportableItems = [];
		$scope.exportableItems.push(["Item Code","Qty. Outgoing", "Qty. Incoming", "Qty. Current Stock Count"]);
		for (var i = 0;i < items.length;i++){
				var _item1 = [];
				_item1.push(items[i].itemCode);
				_item1.push(items[i].quantityOutgoing);
				_item1.push(items[i].quantityIncoming);
				_item1.push(items[i].quantityCurrentStockCount);
				$scope.exportableItems.push(_item1);
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
		$scope.JSONToCSVConvertor($scope.exportableItems, "Reconcile report: From " + $scope.fromDate +" to "+ $scope.toDate, true);
    };
});