inventoryApp.controller("recommendationsController", function ($scope, $rootScope, $location, CRUDOperations, $window, DTOptionsBuilder, DTColumnBuilder, localStorageService) {
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
	$scope.isLoadingData = false;
	$scope.openKitTblInstance = {};
	$scope.closedKitTblInstance = {};
	$scope.selectedDateRange = null;
	$scope.warehouse = {};
	$scope.fetchInitData = function(){
		$scope.fetchAllKits();
		$scope.fetchWarehouses();
	}
	$scope.fromDate = ($scope.selectedDateRange)?moment($scope.selectedDateRange.split(" - ")[0], ["MM-DD-YYYY", "YYYY-MM-DD"]):null;//.format("ddd, DD MMM YYYY HH:mm:ss [GMT]"): null;
	$scope.toDate = ($scope.selectedDateRange)?moment($scope.selectedDateRange.split(" - ")[1],["MM-DD-YYYY", "YYYY-MM-DD"]):null;//.format("ddd, DD MMM YYYY HH:mm:ss [GMT]"): null;
	$scope.requestWarehouseId = ($scope.requestWarehouseId)?$scope.requestWarehouseId: null;
	$scope.assignedWarehouseId = ($scope.assignedWarehouseId)?$scope.assignedWarehouseId: null;
	$scope.closedKitOptions = DTOptionsBuilder.newOptions()
	.withOption('ajax', function(data, callback, settings){
		$scope.isLoadingData = true;
		var requestParams = {params: data, fromDate: $scope.fromDate, toDate:$scope.toDate, requestWarehouseId: $scope.requestWarehouseId, assignedWarehouseId: $scope.assignedWarehouseId};
		var promisePost = CRUDOperations.post(requestParams, "/api/items/getSlowMovingItems");
		promisePost.then(function(data){        	
			$scope.listOfClosedKits = data.data.data;
			$scope.isLoadingData = false;
			callback(data.data);
		});
	})
	.withOption('createdRow', function (row, request, index) {
		$('td', row).eq(0).html('<p>' + request.itemCode + '</p>');
		$('td', row).eq(1).html('<p>' + request.description + '</p>');
		$('td', row).eq(2).html('<p>' + request.quantity + '</p>');
		$('td', row).eq(3).html('<p>' + request.rate + '</p>');
	})
	.withDataProp('data')
	.withOption('aaSorting', [3, 'asc'])
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
		var requestParams = {params: data, fromDate: $scope.fromDate, toDate:$scope.toDate, requestWarehouseId: $scope.requestWarehouseId, assignedWarehouseId: $scope.assignedWarehouseId};
		var promisePost = CRUDOperations.post(requestParams, "/api/items/getFastMovingItems");
		$scope.isLoadingData = true;
		promisePost.then(function(data){
			$scope.listOfOpenKits = data.data.data;
			$scope.isLoadingData = false;
			callback(data.data);
		});
	})
	.withOption('createdRow', function (row, request, index) {
		$('td', row).eq(0).html('<p>' + request.itemCode + '</p>');
		$('td', row).eq(1).html('<p>' + request.description + '</p>');
		$('td', row).eq(2).html('<p>' + request.quantity + '</p>');
		$('td', row).eq(3).html('<p>' + request.rate + '</p>');
	})
	.withDataProp('data')
	.withOption('processing', true)
	.withOption('serverSide', true)
	.withPaginationType('full_numbers')
	.withDisplayLength(25)
	.withOption('aaSorting', [3, 'desc'])
	$scope.itemsColumns = [
		DTColumnBuilder.newColumn("itemCode", "Item Code"),
		DTColumnBuilder.newColumn("description", "Description"),
		DTColumnBuilder.newColumn("quantity", "Quantity"),
		DTColumnBuilder.newColumn("rate", "Items / Day")]
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
	$scope.statusSelectEvent = function(value){
		$scope.selectedStatus = value;
		$scope.fetchWarehouses();
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