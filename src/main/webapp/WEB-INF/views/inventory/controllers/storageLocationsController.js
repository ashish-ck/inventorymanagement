inventoryApp.controller("storageLocationsController", function ($scope, $rootScope, $location, CRUDOperations, $window, DTOptionsBuilder, DTColumnBuilder, localStorageService) {
	$scope.kitStatus = function(status){
		var s = {
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
				"2200": "Purchase Open"
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
	$scope.listOfWarehouses = [];
	$scope.isLoadingData = false;
	$scope.openKitTblInstance = {};
	$scope.closedKitTblInstance = {};
	$scope.fetchInitData = function(){
		$scope.fetchAllKits();
	}
	$scope.openKitOptions = DTOptionsBuilder.newOptions()
	.withOption('ajax', function(data, callback, settings){
		var warehouseId = 3, shelfId, rackId, crateId;
		shelfId = rackId = crateId = 1;
		var promisePost = CRUDOperations.post(data, '/api/warehouses/warehouseItems/' + warehouseId + "/" + shelfId + "/" + rackId + "/" + crateId);
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
	.withOption('createdRow', function (row, request, index) {
		request.statusText = $scope.kitStatus(request.status);
		$('td', row).eq(0).html(index + 1);
		$('td', row).eq(1).html(request.barcode);
		$('td', row).eq(2).html(request.itemCode);
		$('td', row).eq(3).html(request.warehouse.name);
		$('td', row).eq(4).html(request.shelf.name);
		$('td', row).eq(5).html(request.rack.name);
		$('td', row).eq(6).html(request.crate.name);
		$('td', row).eq(7).html(request.statusText);
	})
	.withDataProp('data')
	.withOption('processing', true)
	.withOption('serverSide', true)
	.withPaginationType('full_numbers')
	.withDisplayLength(25)
	$scope.itemsColumns = [DTColumnBuilder.newColumn("status", "Index"),
		DTColumnBuilder.newColumn("barcode", "Barcode"),
		DTColumnBuilder.newColumn("itemCode", "Item Code"),
		DTColumnBuilder.newColumn("warehouse.name", "Warehouse"),
		DTColumnBuilder.newColumn("shelf.name", "Shelf"),
		DTColumnBuilder.newColumn("rack.name", "Rack"),
		DTColumnBuilder.newColumn("crate.name", "Crate"),
		DTColumnBuilder.newColumn("status", "Status")]
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
	$scope.fetchAllKits = function(){
		
	}
	$scope.viewDetails = function (kitNo) {
		$window.open($location.$$protocol + "://" + $location.$$host + ":" + $location.$$port + "/#/" + "kits/view/" + kitNo);
	}
});