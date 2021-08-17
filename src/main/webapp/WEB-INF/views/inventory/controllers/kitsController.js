inventoryApp.controller("kitsController", function ($scope, $rootScope, $location, CRUDOperations, $window, DTOptionsBuilder, DTColumnBuilder, localStorageService) {
	$scope.kitStatus = function(status){
		var s = {
			"100": "New",
			"200": "In Progress",
			"300": "Invoiced",
			"400": "Return Expected",
			"500": "Return Received By Hub",
			"600": "Return Dispatched By Hub",
			"700": "Return Received By Warehouse",
			"800": "Disputed",
			"900": "Closed",
			"1000": "Cancelled",
			"1100": "Re-opened"
		};
		return s[status.toString()];
	};
	$scope.status = {
			"100": "New",
			"200": "In Progress",
			"300": "Invoiced",
			"400": "Return Expected",
			"500": "Return Received By Hub",
			"600": "Return Dispatched By Hub",
			"700": "Return Received By Warehouse",
			"800": "Disputed",
			"900": "Closed",
			"1000": "Cancelled",
			"1100": "Re-opened"
	};
	$scope.colorCode = function(prefix, status){
		var s = {
				"100": "warning",
				"200": "info",
				"300": "default",
				"400": "primary",
				"500": "primary",
				"600": "primary",
				"700": "primary",
				"800": "danger",
				"900": "success",
				"1000": "danger",
				"1100": "maroon"
			};
		return prefix + "-" +s[status.toString()];
	}
	$scope.listOfOpenKits = [];
	$scope.listOfClosedKits = [];
	$scope.loadingKits = false;
	$scope.selectedRequest = null;
	$scope.listOfStatuses = [];
	$scope.selectAll = false;
	$scope.selectedStatus = null;
	$scope.selectedItemsCount = 0;
	$scope.currentItemsStatus = null;
	$scope.currentWarehouse = null;
	$scope.toWarehouse = null;
	$scope.requestWarehouse = null;
	$scope.assignedWarehouse = null;
	$scope.listOfWarehouses = [];
	$scope.listOfInventories = [];
	$scope.isLoadingData = false;
	$scope.openKitTblInstance = {};
	$scope.closedKitTblInstance = {};
	$scope.selectedDateRange = null;
	$scope.warehouse = {};
	$scope.selectedStatus1 = {status: undefined};
	$scope.fetchInitData = function(){
		var s = {"100": "New",
				"200": "In Progress",
				"300": "Invoiced",
				"400": "Return Expected",
				"500": "Return Received By Hub",
				"600": "Return Dispatched By Hub",
				"700": "Return Received By Warehouse",
				"800": "Disputed",
				"900": "Closed",
				"1000": "Cancelled",
				"1100": "Re-opened"};
				arr = [];
				for (key in s){
					r = {};
					r.status = key;
					r.text = s[key];
					arr.push(r);
				}
				$scope.fstatus = arr; 
		$scope.fetchAllKits();
		$scope.fetchWarehouses();
	}
	$scope.refreshData = function(){
		$scope.selectedStatus = null;
		$scope.listOfStatuses = [];
		$scope.currentWarehouse = null;	
		if($scope.selectedRequest){
			$('#requestItemsModal').modal('hide');
			var promiseget = CRUDOperations.get("/api/kits/get/"+$scope.selectedRequest.kitNumber);
			promiseget.then(function (data) {
				if(data.data.valid){
					$scope.selectedRequest = data.data.payload;
					for( var i = 0; i < $scope.selectedRequest.items.length; i++){
						var item = $scope.selectedRequest.items[i];
						item.statusText = $scope.kitStatus(item.currentStatus);
						item.toUpdateStatus = false;
					}
					$scope.selectedStatus = null;
					$scope.listOfStatuses = [];
					$rootScope.selectedStatus = null;
					$rootScope.listOfStatuses = [];
					$scope.currentWarehouse = null;
					$scope.currentItemsStatus = null;
				}else{
					alert(data.data.message);
				}
			},
			function (errorpl) {
				alert("Error!");
			});
		}
		$scope.openKitTblInstance.reloadData(null, false);
		$scope.closedKitTblInstance.reloadData(null, false);
	}
	$scope.fromDate = ($scope.selectedDateRange)?moment($scope.selectedDateRange.split(" - ")[0], ["MM-DD-YYYY", "YYYY-MM-DD"]):null;//.format("ddd, DD MMM YYYY HH:mm:ss [GMT]"): null;
	$scope.toDate = ($scope.selectedDateRange)?moment($scope.selectedDateRange.split(" - ")[1],["MM-DD-YYYY", "YYYY-MM-DD"]):null;//.format("ddd, DD MMM YYYY HH:mm:ss [GMT]"): null;
	$scope.requestWarehouseId = ($scope.requestWarehouseId)?$scope.requestWarehouseId: null;
	$scope.assignedWarehouseId = ($scope.assignedWarehouseId)?$scope.assignedWarehouseId: null;
	$scope.closedKitOptions = DTOptionsBuilder.newOptions()
	.withOption('ajax', function(data, callback, settings){
		$scope.isLoadingData = true;
		var requestParams = {params: data, fromDate: $scope.fromDate, toDate:$scope.toDate, requestWarehouseId: $scope.requestWarehouseId, assignedWarehouseId: $scope.assignedWarehouseId, status : $scope.selectedStatus2};
		var promisePost = CRUDOperations.post(requestParams, '/api/kits/getClosedKits');
		promisePost.then(function(data){        	
			$scope.listOfClosedKits = data.data.data;
			for (var i = 0;i < $scope.listOfClosedKits.length;i++){
				$scope.listOfClosedKits[i].statusText = $scope.kitStatus($scope.listOfClosedKits[i].status); 
			}
			$scope.isLoadingData = false;
			callback(data.data);
		});
	})
	.withOption('aaSorting', [1, 'desc'])
	.withOption('createdRow', function (row, request, index) {
		request.statusText = $scope.kitStatus(request.status);
		request.date = moment(request.date).format("DD/MMM/YYYY hh:mm a");
		$('td', row).eq(0).html('<h4><span class="label bg-teal" onclick="viewDetails(\'' + request.kitNumber + '\', '+request.status+')">' + request.kitNumber + '</span></h4>');
		$('td', row).eq(1).html('<p class="text-info"><i class="fa fa-calendar"></i> ' + request.date + '</p>');
		$('td', row).eq(2).html('<p class="text-warning"><i class="fa fa-map-marker"></i> ' + request.assignedWarehouse.name + '</p>');
		if(request.requestWarehouse){
			$('td', row).eq(3).html('<p class="text-success"><i class="fa fa-map-marker"></i> ' + request.requestWarehouse.name + '</p>');
		}else{
			$('td', row).eq(3).html('<p class="text-success"><i class="fa fa-map-marker"></i> ' + request.assignedWarehouse.name + '</p>');
		}
		$('td', row).eq(4).html('<h4><span class="label ' + $scope.colorCode('label', request.status) + '">'+request.statusText+'</span></h4>');
		$('td', row).eq(5).html('<button type="button" class="btn btn-link" onclick="viewDetails(\'' + request.kitNumber + '\', '+request.status+')"> View Details </button>');
	})
	.withDataProp('data')
	.withOption('processing', true)
	.withOption('serverSide', true)
	.withPaginationType('full_numbers')
	.withDisplayLength(25)
	$scope.fromDate = ($scope.selectedDateRange)?moment($scope.selectedDateRange.split(" - ")[0],["MM-DD-YYYY", "YYYY-MM-DD"]):null;//.format("ddd, DD MMM YYYY HH:mm:ss [GMT]"): null;
		$scope.toDate = ($scope.selectedDateRange)?moment($scope.selectedDateRange.split(" - ")[1,["MM-DD-YYYY", "YYYY-MM-DD"]]):null;//.format("ddd, DD MMM YYYY HH:mm:ss [GMT]"): null;
	$scope.requestWarehouseId = ($scope.requestWarehouseId)?$scope.requestWarehouseId:null;
	$scope.assignedWarehouseId = ($scope.assignedWarehouseId)?$scope.assignedWarehouseId:null;
	var warehouse = "";
	$scope.openKitOptions = DTOptionsBuilder.newOptions()
	.withOption('ajax', function(data, callback, settings){
		var requestParams = {params: data, fromDate: $scope.fromDate, toDate:$scope.toDate, requestWarehouseId: $scope.requestWarehouseId, assignedWarehouseId: $scope.assignedWarehouseId, status : $scope.selectedStatus2};
		var promisePost = CRUDOperations.post(requestParams, "/api/kits/getOpenKits");
		$scope.isLoadingData = true;
		promisePost.then(function(data){
			$scope.listOfOpenKits = data.data.data;
			for (var i = 0;i < $scope.listOfOpenKits.length;i++){
				$scope.listOfOpenKits[i].statusText = $scope.kitStatus($scope.listOfOpenKits[i].status); 
			}
			$scope.isLoadingData = false;
			callback(data.data);
		});
	})
	.withOption('aaSorting', [1, 'desc'])
	.withOption('createdRow', function (row, request, index) {
		request.statusText = $scope.kitStatus(request.status);
		request.date = moment(request.date).format("DD/MMM/YYYY hh:mm a");
		$('td', row).eq(0).html('<h4><span class="label bg-teal" onclick="viewDetails(\'' + request.kitNumber + '\', '+request.status+')">' + request.kitNumber + '</span></h4>');
		$('td', row).eq(1).html('<p class="text-info"><i class="fa fa-calendar"></i> ' + request.date + '</p>');
		$('td', row).eq(2).html('<p class="text-warning"><i class="fa fa-map-marker"></i> ' + request.assignedWarehouse.name + '</p>');
		if(request.requestWarehouse){
			$('td', row).eq(3).html('<p class="text-success"><i class="fa fa-map-marker"></i> ' + request.requestWarehouse.name + '</p>');
		}else{
			$('td', row).eq(3).html('<p class="text-success"><i class="fa fa-map-marker"></i> ' + request.assignedWarehouse.name + '</p>');
		}
		$('td', row).eq(4).html('<h4><span class="label ' + $scope.colorCode('label', request.status) + '">'+request.statusText+'</span></h4>');
		$('td', row).eq(5).html('<button type="button" class="btn btn-link" onclick="viewDetails(\'' + request.kitNumber + '\', '+request.status+')"> View Details </button>');
	})
	.withDataProp('data')
	.withOption('processing', true)
	.withOption('serverSide', true)
	.withPaginationType('full_numbers')
	.withDisplayLength(25)
	$scope.itemsColumns = [
	                       DTColumnBuilder.newColumn("kitNumber", "Req #"),
	                       DTColumnBuilder.newColumn("date", "Date"),
	                       DTColumnBuilder.newColumn("assignedWarehouse.name", "Assigned Warehouse"),
	                       DTColumnBuilder.newColumn("requestWarehouse", "Request Warehouse"),
	                       //DTColumnBuilder.newColumn("statusText", "Status"),
	                       DTColumnBuilder.newColumn("status", "Status"),
	                       DTColumnBuilder.newColumn("status", "Action")
	                       ]
	$scope.fetchWarehouses = function(){
		var promiseget = CRUDOperations.get("/api/warehouses/getWarehouseNames");
		promiseget.then(function (data) {
			if(data.data.valid){
				for(var i = 0; i < data.data.payload.length; i++){
					var warehouse = data.data.payload[i];
					if(warehouse.name != $scope.currentWarehouse){
						$scope.listOfWarehouses.push(warehouse);
					}
				}
			}else{
				alert(data.data.message);
			}
		},
		function (errorpl) {
			alert("Error!");
		});
	}
	$scope.showRequestItems = function(requestId, status){
		if(requestId){
			var promiseget = CRUDOperations.get("/api/kits/get/"+requestId);
			promiseget.then(function (data) {
				if(data.data.valid){
					$scope.selectedRequest = data.data.payload;
					for( var i = 0; i < $scope.selectedRequest.items.length; i++){
						var item = $scope.selectedRequest.items[i];
						item.statusText = $scope.kitStatus(item.currentStatus);
						item.toUpdateStatus = false;
					}
					$scope.selectedStatus = null;
					$scope.listOfStatuses = [];
					$rootScope.selectedStatus = null;
					$rootScope.listOfStatuses = [];
					$scope.currentWarehouse = null;
					$scope.currentItemsStatus = null;
					$('#requestItemsModal').modal({
						backdrop: 'static',
						keyboard: false
					});
					$('#requestItemsModal').modal('show');
				}else{
					alert(data.data.message);
				}
			},
			function (errorpl) {
				alert("Error!");
			});
		}
	}
	$scope.selectAllEvent = function(value){
		$scope.selectAll = value;
		if($scope.isSelectAllPossible()){
			for(var i = 0; i < $scope.selectedRequest.items.length; i++){
				var item = $scope.selectedRequest.items[i];
				if(item.toUpdateStatus != $scope.selectAll){
					item.toUpdateStatus = $scope.selectAll;
					$scope.itemSelectEvent(item);
				}
			};
		}else{
			alert("List has items with different statuses and cannot be selected all at once. Please select items with same status and perform the operation!");
			$scope.selectAll = false;
		}
	}
	$scope.statusSelectEvent = function(status){
		$scope.selectedStatus2 = status.status;
		$('#openRequestsTbl').DataTable().clear();
		$('#closedRequestsTbl').DataTable().clear();
		$scope.openKitTblInstance.reloadData(null, false);
		$scope.closedKitTblInstance.reloadData(null, false);
	}
	$scope.isSelectAllPossible = function(){
		var result = true;
		var selectedStatus = 0;
		for(var i = 0; i < $scope.selectedRequest.items.length; i++){
			var item = $scope.selectedRequest.items[i];
			if(selectedStatus == 0)
				selectedStatus == item.currentStatus;
			else{
				if(item.currentStatus != selectedStatus)
					result = false;
			}
		};
		return result;
	}
	$scope.itemSelectEvent = function(item){
		if(item.toUpdateStatus){
			if($scope.currentItemsStatus){ 
				if($scope.currentItemsStatus != item.currentStatus || $scope.currentWarehouse != item.currentLocation.name){
					alert("Cannot select items with multiple statuses/warehouses!");
					item.toUpdateStatus = false;
				}else{
					if(item.currentStatus == 1){
						if(item.barcode){
							$scope.selectedItemsCount++;
						}else{
							alert("Please enter the barcode for item "+item.item.code+" before marking it as reserved!");
							item.toUpdateStatus = false;
						}
					}else{
						$scope.selectedItemsCount++;
					}
				}
			}else{
				$scope.currentWarehouse = item.currentLocation.name;
				if(item.currentStatus == 1 || item.currentStatus == 3){
					if(item.barcode){
						$scope.listOfStatuses = [
						                         {key: "Reserved", value: 2}
						                         ]
						$scope.currentItemsStatus = item.currentStatus;
						$scope.selectedItemsCount++;
					}else{
						alert("Please enter the barcode for item "+item.item.code+" before marking it as reserved!");
						item.toUpdateStatus = false;
					}

				}else if(item.currentStatus == 2){
					$scope.currentItemsStatus = item.currentStatus;
					$scope.selectedItemsCount++;
					if($scope.selectedRequest.requestWarehouse.code == item.currentLocation.code){
						$scope.listOfStatuses = [
						                         {key: "Issued", value: 4}
						                         ]						
					}else{
						$scope.listOfStatuses = [
						                         {key: "Create Transfer Order", value: -99}
						                         ]
					}
				}else if(item.currentStatus == 4){

				}else if(item.currentStatus == 5){
					$scope.currentItemsStatus = item.currentStatus;
					$scope.selectedItemsCount++;
					if($scope.selectedRequest.assignedWarehouse.code == $scope.selectedRequest.requestWarehouse.code){
						$scope.listOfStatuses = [
						                         {key: "Issued", value: 4}
						                         ]						
					}
				}else if(item.currentStatus == 8){
					$scope.currentItemsStatus = item.currentStatus;
					$scope.selectedItemsCount++;
					$scope.listOfStatuses = [
					                         {key: "Available", value: 1}
					                         ]
				}
			}
		}else{
			$scope.selectedItemsCount--;
			if($scope.selectedItemsCount == 0){
				$scope.currentItemsStatus = null;
				$scope.currentWarehouse = null;
			}
		}
	}
	$scope.itemQuantityDeliveredEvent = function(item){
		if(item.quantityDelivered > item.quantity){
			alert("Quantity Delivered cannot be greater than quantity!");
			item.quantityDelivered = 0;
		}
	}
	$scope.warehouseSelectEventRequest = function(warehouse){
		$scope.requestWarehouse = {};
		$scope.requestWarehouseId = warehouse.id;
		$('#openRequestsTbl').DataTable().clear();
		$('#closedRequestsTbl').DataTable().clear();
		$scope.openKitTblInstance.reloadData(null, false);
		$scope.closedKitTblInstance.reloadData(null, false); 
	}
	$scope.warehouseSelectEventAssigned = function(warehouse){
		$scope.assignedWarehouse = {};
		$scope.assignedWarehouseId = warehouse.id;
		$('#openRequestsTbl').DataTable().clear();
		$('#closedRequestsTbl').DataTable().clear();
		$scope.openKitTblInstance.reloadData(null, false);
		$scope.closedKitTblInstance.reloadData(null, false);
	}
	$scope.dateChangeEvent = function(){
		$scope.fromDate = ($scope.selectedDateRange)?moment($scope.selectedDateRange.split(" - ")[0], ["MM-DD-YYYY", "YYYY-MM-DD"]):null;//.format("YYYY-MM-DD"): null;
		$scope.toDate = ($scope.selectedDateRange)?moment($scope.selectedDateRange.split(" - ")[1], ["MM-DD-YYYY", "YYYY-MM-DD"]):null;//.format(["MM-DD-YYYY", "YYYY-MM-DD"]): null;
		$('#openRequestsTbl').DataTable().clear();
		$('#closedRequestsTbl').DataTable().clear();
		$scope.openKitTblInstance.reloadData(null, false);
		$scope.closedKitTblInstance.reloadData(null, false);
	}
	$scope.clearClickEvent = function(){
		$scope.selectedDateRange = undefined;
		$scope.fromDate = null;
		$scope.toDate = null;
		$scope.requestWarehouseId = null;
		$scope.assignedWarehouseId = null;
		$scope.warehouse.requestWarehouse = undefined;
		$scope.warehouse.assignedWarehouse = undefined;
		$scope.selectedStatus1 = {status: undefined};
		$scope.selectedStatus2 = null;
		$('#openRequestsTbl').DataTable().clear();
		$('#closedRequestsTbl').DataTable().clear();
		$scope.openKitTblInstance.reloadData(null, false);
		$scope.closedKitTblInstance.reloadData(null, false);
	}
	$scope.updateItemDetails = function(directCall){
		var itemsToBeUpdated = [];
		if($scope.selectedStatus != 0){
			if($scope.selectedStatus == -99){
				if($scope.toWarehouse){
					$('btnUpdate').button('loading');
					for( var i = 0; i < $scope.selectedRequest.items.length; i++){
						var item = $scope.selectedRequest.items[i];
						if(item.toUpdateStatus){
							var obj = {
									warehouse: item.currentLocation.name,
									itemCode: item.itemCode,
									barcode: item.barcode,
									vendorProductId: item.serialNumber,
									status: item.statusText,
									removed: item.removed,
									quantityRequested: item.quantity,
									quantityDelivered: item.quantityDelivered,
									quantityReturned: item.returnQuantity,
									quantityReturnExpected: item.quantityReturnExpected
							}
							itemsToBeUpdated.push(obj);
						}
					}
					var itemDetails = {
							voucherRef: $scope.selectedRequest.kitNumber,
							listOfItems: itemsToBeUpdated,
							status: $scope.selectedStatus,
							version: $scope.selectedRequest.version,
							workshopRef: $scope.toWarehouse,
							date: moment($scope.selectedRequest.date,["MM-DD-YYYY", "YYYY-MM-DD"])//.format("ddd, DD MMM YYYY HH:mm:ss [GMT]")
					}
					var promisePost = CRUDOperations.post(itemDetails, "/api/transferOrders/autoCreate");
					var result = promisePost.then(function (data) {
						if (data.data.valid) {
							$scope.selectAll = false;
							$scope.selectedStatus = null;
							$scope.currentItemsStatus = null;
							$scope.selectedItemsCount = 0;
							$scope.currentWarehouse = null;
							//$scope.fetchInitData();
							$scope.refreshData();
							$scope.listOfStatuses = [];
							alert(data.data.message);
							$window.open($location.$$protocol + "://" + $location.$$host + ":" + $location.$$port + "/#/inventory/transfers/edit/"+data.data.payload.orderNumber);
							$('.btnUpdate').button('reset');
						}else{
							alert(data.data.message);
							$('.btnUpdate').button('reset');
						}
					},
					function (errorpl) {
						alert("Error!");
						$('.btnUpdate').button('reset');
					});
				}else{
					alert("Select a warehouse to transfer the items to!");
				}
			}else{
				$('.btnUpdate').button('loading');
				//filter out items that are checked
				for( var i = 0; i < $scope.selectedRequest.items.length; i++){
					var item = $scope.selectedRequest.items[i];
					if(item.toUpdateStatus){
						var obj = {
								warehouse: item.currentLocation.name,
								itemCode: item.itemCode,
								barcode: item.barcode,
								vendorProductId: item.serialNumber,
								status: item.statusText,
								removed: item.removed,
								quantityRequested: item.quantity,
								quantityDelivered: item.quantityDelivered,
								quantityReturned: item.returnQuantity,
								quantityReturnExpected: item.quantityReturnExpected
						}
						itemsToBeUpdated.push(obj);	
					}
				}
				var itemDetails = {
						voucherRef: $scope.selectedRequest.kitNumber,
						listOfItems: itemsToBeUpdated,
						status: $scope.selectedStatus,
						version: $scope.selectedRequest.version,
						workshopRef: $scope.currentWarehouse,
						date: moment($scope.selectedRequest.date, ["MM-DD-YYYY", "YYYY-MM-DD"])//.format("ddd, DD MMM YYYY HH:mm:ss [GMT]")
				}
				var promisePost = CRUDOperations.post(itemDetails, "/api/kits/updateKitItemDetails");
				var result = promisePost.then(function (data) {
					if (data.data.valid) {
						$scope.selectedRequest = data.data.payload;
						if(!directCall){
							//$scope.showRequestItems($scope.selectedRequest.kitNumber);
						}else{
							$scope.selectedStatus = null;
							$scope.listOfStatuses = [];
							$rootScope.selectedStatus = null;
							$rootScope.listOfStatuses = [];
							$scope.currentWarehouse = null;
							$scope.currentItemsStatus = null;							
						}
						$scope.selectAll = false;
						$scope.selectedStatus = null;
						$scope.currentItemsStatus = null;
						$scope.selectedItemsCount = 0;
						$scope.currentWarehouse = null;
						$scope.refreshData();
						//$scope.fetchAllKits();
						$scope.listOfStatuses = [];
						alert(data.data.message);
						$('.btnUpdate').button('reset');
					}else{
						alert(data.data.message);
						$('.btnUpdate').button('reset');
					}
				},
				function (errorpl) {
					alert("Error!");
					$('.btnUpdate').button('reset');
				});    			
			}
		}
	}
	$scope.markStatus = function(kitNumber, status){
		var promiseget = CRUDOperations.get("/api/kits/get/"+kitNumber);
		promiseget.then(function (data) {
			if(data.data.valid){
				$scope.selectedRequest = data.data.payload;
				for(var i = 0; i < $scope.selectedRequest.items.length ; i++){
					var item = $scope.selectedRequest.items[i];
					item.statusText = $scope.kitStatus(item.currentStatus);
					item.toUpdateStatus = true;
					$scope.itemSelectEvent(item);
				}
				$scope.selectedStatus = status;
				if(status == -99){
					$scope.toWarehouse = $scope.selectedRequest.requestWarehouse.name;
				}
				$scope.updateItemDetails(true);	
			}else{
				alert(data.data.message);
			}
		},
		function (errorpl) {
			alert("Error!");
		});
	}
	$scope.checkIfItemBarcodeValid = function(itemCode, barcode){
		if(barcode && barcode.length > 3){
			var promise = CRUDOperations.get('/api/items/checkIfItemBarcodeValid?itemCode='+itemCode+'&barcode='+barcode);
			promise.then(function(data){
				if(data.data.valid){
					if(!data.data.payload){
						alert("Invalid Barcode!");
					}
				}
			});
		}
	}
	$scope.fetchAllKits = function(){
		
	}
	$scope.viewDetails = function (kitNo) {
		$window.open($location.$$protocol + "://" + $location.$$host + ":" + $location.$$port + "/#/" + "kits/view/" + kitNo);
	}
	$scope.fetchWarehouses = function(){
        var promiseget = CRUDOperations.get("/api/warehouses/getWarehouseNames");
        promiseget.then(function (data) {
        	if(data.data.valid){
        		$scope.listOfWarehouses = data.data.payload;
        		var promiseget1 = CRUDOperations.get("/api/inventory/getUserDetails");
        		promiseget1.then(function (data) {
        			if(data.data.warehouse){
        				$scope.userDetails = data.data;
        				var warehouses = [];
        				for (var i = 0; i < $scope.listOfWarehouses.length;i++){
        					if($scope.listOfWarehouses[i].parentWarehouse){
    							$scope.listOfInventories.push($scope.listOfWarehouses[i]);
    						}
        				}
                		for (var i = 0; i < $scope.listOfWarehouses.length;i++){
                			if($scope.userDetails){
                				if($scope.userDetails.authorities[0].authority == "ROLE_ADMIN"){
                					//warehouses.push($scope.listOfWarehouses[i]);
                					return;
                				}
                				if ($scope.userDetails.warehouse == $scope.listOfWarehouses[i].code){
                					warehouses.push($scope.listOfWarehouses[i]);
                				}
                			}
                		}
                		$scope.listOfWarehouses = warehouses;
        			}
        		},
        		function (errorpl) {
        			alert("Error!");
        		});
        	}else{
        		alert(data.data.message);
        	}
        },
      function (errorpl) {
        	alert("Error!");
      });
	}
});