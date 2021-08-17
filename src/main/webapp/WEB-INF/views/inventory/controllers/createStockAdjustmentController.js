inventoryApp.controller("createStockAdjustmentController", function ($scope, $location, CRUDOperations, $routeParams, $filter) {
	$scope.listOfWarehouses = [];
	$scope.listOfSerializedItems = [];
	$scope.selectedItem = null;
	$scope.existingQuantity = 0;
	$scope.adjustment = 0;
	$scope.updatedQuantity = 0;
	$scope.getSerialNumbers = false;
	$scope.order = {
			orderDate: $filter('date')(new Date(), "yyyy-MM-dd"),
			items: []
	};
	$scope.isUpdate = false;
	$scope.getItemDetails = function(){
		$scope.resetModalForm();
		$('#getAdjustmentModal').modal('show');
	}
	$scope.fetchInitData = function(){
		$scope.fetchWarehouses();
		if($routeParams.orderNumber){
			$scope.isUpdate = true;
			var promiseget = CRUDOperations.get("/api/adjustments/get/"+$routeParams.orderNumber);
			promiseget.then(function (data) {
				if(data.data.valid){
					$scope.order = data.data.payload;
					$scope.status = "Fulfilled";
					$scope.order.orderDate = $filter('date')($scope.order.orderDate, "yyyy-MM-dd");
				}else{
					alert(data.data.message);
				}
			},
			function (errorpl) {
				alert("Error!");
			});
		}
	}
	$scope.warehouseSelectEvent = function(){
		if($scope.order.warehouse == null)
			return;
		var id = $scope.order.warehouse.id;
		var promiseGet = CRUDOperations.get("/api/warehouses/get/" + id);
		promiseGet.then(function (data) {
			if(data.data.valid){
				$scope.order.warehouse = data.data.payload;
			}
		},
		function (errorpl) {
			alert("Error!");
		});
	}
	$scope.addItemToOrder = function(){
		if($scope.getSerialNumbers){
			angular.forEach($scope.listOfSerializedItems, function(barcode, key){
				var orderItem = {
						item: $scope.selectedItem,
						currentStatus: 1,
						currentLocatioin: $scope.order.warehouse,
						barcode: barcode,
						serialNumber: barcode,
						removed: $scope.quantity < 0,
						quantity: $scope.updatedQuantity,
						quantityDelivered: $scope.quantity < 0 ? -1 : 1
				}
				$scope.order.items.push(orderItem);
			});
		}else{
			var orderItem = {
					item: $scope.selectedItem,
					currentStatus: 1,
					currentLocatioin: $scope.order.warehouse,
					barcode: $scope.selectedItem.barcode,
					serialNumber: null,
					removed: $scope.quantity < 0,
					quantity: $scope.updatedQuantity,
					quantityDelivered: $scope.adjustment
			}
			$scope.order.items.push(orderItem);
		}
		$scope.resetModalForm();
		$('#getAdjustmentModal').modal('hide');
	}
	$scope.resetModalForm = function(){
		$scope.selectedItem = null;
		$scope.existingQuantity = 0;
		$scope.adjustment = 0;
		$scope.updatedQuantity = 0;
		$scope.getSerialNumbers = false;
		$scope.listOfSerializedItems = null;
		$scope.listOfItems = [];
	}
	$scope.itemSelectEvent = function(){
		//$scope.selectedItem = item;
		$scope.existingQuantity = 0;
		$scope.adjustment = 0;
		$scope.updatedQuantity = 0;
		angular.forEach($scope.selectedItem.itemCount, function(count, key){
			if(count.warehouse.id == $scope.order.warehouse.id){
				$scope.existingQuantity = count.quantityAvailable;
				$scope.updatedQuantity = count.quantityAvailable;
			}
		});
		$scope.getSerialNumbers = false;
		if($scope.selectedItem.serialized){
			$scope.getSerialNumbers = true;
		}
	}
	$scope.itemQuantityEvent = function(){
		$scope.adjustment = $scope.updatedQuantity - $scope.existingQuantity;
		$scope.quantityChangeEvent();
	}
	$scope.fetchRemoteItems = function(code){
		if(code.length > 2){
			var values = [code];
			var searchCriterias = [
			                       {
			                    	   key: 'code',
			                    	   value: values
			                       },
			                       {
			                    	   key: 'tags-like',
			                    	   value: values
			                       },
			                       {
			                    	   key: 'barcode',
			                    	   value: values
			                       }
			                       ];

			var promisePost = CRUDOperations.post(searchCriterias, "/api/items/searchItemsByCriteria");
			var result = promisePost.then(function (data) {
				if (data.data.valid) {
					$scope.listOfItems = data.data.payload;
					$scope.criteriaText = "";
				}
			},
			function (errorpl) {
				alert("Error!");
			});

		}
	}
	
	$scope.quantityChangeEvent = function(){
		if($scope.getSerialNumbers){
			$scope.listOfSerializedItems = null;
			var quantity = $scope.adjustment;
			if($scope.adjustment < 0){
				quantity = -1 * quantity;
			}
			$scope.listOfSerializedItems = new Array(quantity);
		}
	}

	$scope.fetchWarehouses = function(){
		var promiseget = CRUDOperations.get("/api/warehouses/getWarehouaseNames");
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

	$scope.saveOrder = function(isValid){		
		if(isValid){
			var promisePost = CRUDOperations.post($scope.order, "/api/adjustments/save");
			$('.update').button('loading');
			var result = promisePost.then(function(data){
				if(data.data.valid){
					$('.update').button('reset');
					alert(data.data.message);
					$location.path('/inventory/adjustments/'+data.data.payload.orderNumber);
				}else{
					alert(data.data.message);
				}
			}, function(errorpl){
				$('.update').button('reset');
				alert("Error!");
			});
		}
	}
	
});