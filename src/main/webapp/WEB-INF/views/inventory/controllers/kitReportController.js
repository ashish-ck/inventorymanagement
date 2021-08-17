inventoryApp.controller("kitReportController", function ($scope, $location, CRUDOperations) {
	$scope.selectedDateRange = null;
	$scope.kitReportData = null;
	$scope.kitItemDetails = [];
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
	$scope.fetchReportData = function(){
		if ($scope.selectedDateRange) {
			$scope.kitReportData = null;
			$scope.kitItemDetails = [];
			$scope.fromDate = moment($scope.selectedDateRange.split(" - ")[0]).format("YYYY/MM/DD");
			$scope.toDate = moment($scope.selectedDateRange.split(" - ")[1]).format("YYYY/MM/DD");
			$('.update').button("loading");
			$scope.isLoadingData = true;
			var warehouse = "";
			var promiseget = CRUDOperations.get("api/kits/getByDateRange?fromDate="+$scope.fromDate+"&toDate="+$scope.toDate + (($scope.selectedWarehouse)?"&warehouseId="+$scope.selectedWarehouse:"&warehouseId="));
			promiseget.then(function (data) {
				if(data.data.valid){
					$scope.kitReportData = data.data.payload;
					$scope.kitItemDetails = $scope.kitReportData.kitItems;
					var labels = [];
					var qtyDelivered = [];
					var qtyRestocked = [];
					var qtyReturnExpected = [];
					var qtyOnTheFly = [];
					var kitsOutgoing = [];
					var itemsInvoiced = [];
					for(var i = 0; i < $scope.kitReportData.countByDate.length; i++){
						var count = $scope.kitReportData.countByDate[i];
						labels.push(count.date);
						qtyDelivered.push(count.qtyDelivered);
						qtyRestocked.push(count.qtyRestocked);
						qtyReturnExpected.push(count.qtyReturnExpected);
						qtyOnTheFly.push(count.qtyOnTheFly);
						//kitsOutgoing.push(1);
					}
					var areaChartData = {
							labels: labels,
							datasets: [
								{
						        	   label: "Kits Outgoing",
						        	   fillColor: "rgb(153, 255, 102)",
						        	   strokeColor: "rgb(153, 255, 102)",
						        	   pointColor: "rgb(153, 255, 102)",
						        	   pointStrokeColor: "rgb(153, 255, 102)",
						        	   pointHighlightFill: "#fff",
						        	   pointHighlightStroke: "rgb(153, 255, 102)",
						        	   data: kitsOutgoing
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
										'<li><h4><span class="label" style="background-color: rgb(128, 128, 128); color: #000">Kits Outgoing : '+$scope.kitReportData.kitItems.length+'</span></h4></li>'+
										'<li><h4><span class="label" style="background-color: rgb(128, 128, 128); color: #000">Items Invoiced : '+$scope.kitReportData.totalQuantityInvoiced+'</span></h4></li>'+
										'<li><h4><span class="label" style="background-color: rgb(128, 128, 128); color: #000">Amount Invoiced : '+ parseInt($scope.kitReportData.amountInvoiced)+'</span></h4></li>'+
										'<li><h4><span class="label" style="background-color: rgb(153, 255, 102); color: #000">Qty. Delivered : '+$scope.kitReportData.totalQuantityDelivered+'</span></h4></li>'+
										'<li><h4><span class="label" style="background-color: rgb(204, 0, 0); color: #000">Qty. Returned : '+$scope.kitReportData.totalQuantityRestocked+'</span></h4></li>'+
										'<li><h4><span class="label" style="background-color: rgb(128, 128, 128); color: #000">Ad-hoc Items : '+$scope.kitReportData.totalQuantityOnTheFly+'</span></h4></li>'+
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
	$scope.openKitRequest = function(kitNumber){
        var promiseget = CRUDOperations.get("/api/kits/get/"+kitNumber);
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
        		item.statusText = $scope.itemStatuses[item.status.toString()];
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
    	$scope.selectedWarehouse = $scope.kitItemDetails.selectedWarehouse.id;
    }
    $scope.exportItems = function(){
		var items = $scope.kitItemDetails;
		$scope.exportableItems = [];
		$scope.exportableItems.push(["Kit Number","Assigned Warehouse","Qty Delivered", "Qty Invoiced", "Qty Return Expected","Qty Restocked","Qty Stock Loss","Status","Amount Invoiced"]);
		var barcodes = [];
		for (var i = 0;i < items.length;i++){
				var _item1 = [];
				_item1.push(items[i].kitNumber);
				_item1.push(items[i].assignedWarehouse);
				_item1.push(items[i].quantityDelivered);
				_item1.push(items[i].quantityInvoiced);
				_item1.push(items[i].quantityReturnExpected);
				_item1.push(items[i].quantityRestocked);
				_item1.push(items[i].quantityStockLoss);
				_item1.push(items[i].statusText);
				_item1.push(items[i].amountInvoiced);
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
		$scope.JSONToCSVConvertor($scope.exportableItems, "Consolidated report: From " + $scope.fromDate +" to "+ $scope.toDate, true);
    };
});