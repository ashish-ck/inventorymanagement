inventoryApp.controller("createStockTransfersController", function ($scope, $filter, $location, CRUDOperations, $routeParams) {
	$scope.listOfWarehouses = [];
	$scope.listOfToWarehouses = [];
	$scope.listOfItems = [];
	$scope.transferOrder = {
			items: [],
			itemDetails: [],
			recvdItemDetails: []
	};
	$scope.isUpdateForm = false;
	$scope.itemsSent = false;
	$scope.quantityTotal = 0;
	$scope.quantityDispatched = 0;
	$scope.quantityDelivered = 0;
	$scope.criteriaText=null;
	$scope.deliveryItem = null;
	$scope.getSerialNumbers = false;
	$scope.deliveryItemQuantity = 0;
	$scope.deliveryItemQuantityOrdered = 0;

	$scope.getDeliveryOrderItem = function(){
		$('#getDeliveryOrderItemModal').modal('show');
	}


	$scope.fetchInitData = function(){
		$scope.fetchWarehouses();
		if($routeParams.transferId){
			var promiseget = CRUDOperations.get("/api/transferOrders/get/"+$routeParams.transferId);
			promiseget.then(function(data){
				if(data.data.valid){
					$scope.transferOrder = data.data.payload;
					if($scope.transferOrder.sendDate == null || $scope.transferOrder.sendDate == undefined)
						$scope.transferOrder.sendDate = $filter('date')(new Date(), "yyyy-MM-dd");
					if($scope.transferOrder.receiveDate == null || $scope.transferOrder.receiveDate == undefined)
						$scope.transferOrder.receiveDate = $filter('date')(new Date(), "yyyy-MM-dd");
					$scope.calculateTotalQuantity();
					$scope.isUpdateForm = true;
					if($scope.transferOrder.status >= 3){
						$scope.quantityDispatched = $scope.quantityTotal;
						$scope.itemsSent = true;
					}
					if($scope.transferOrder.status >= 4){
						$scope.quantityDelivered = $scope.quantityTotal;
					}
					$scope.warehouseSelectEvent();
					$scope.toWarehouseSelectEvent();
				}else{
					alert(data.data.message);
				}
			}, function(err){
				if(err.status == 404){
					alert("No transfer order found!");
				}else{
					alert("Something went wrong!");
				}
			});
		}else{
			if($scope.transferOrder.orderDate == null || $scope.transferOrder.orderDate == undefined)
				$scope.transferOrder.orderDate = $filter('date')(new Date(), "yyyy-MM-dd");
		}
	}


	$scope.warehouseSelectEvent = function (){
		if($scope.transferOrder.fromWarehouse == null){
			return;
		}
		var promiseGet = CRUDOperations.get("/api/warehouses/get/" + $scope.transferOrder.fromWarehouse.id);
		promiseGet.then(function (data) {
			if(data.data.valid){
				$scope.transferOrder.fromWarehouse = data.data.payload;
				var address = $scope.transferOrder.fromWarehouse.addressLine1 + ', ';
				address += ($scope.transferOrder.fromWarehouse.addressLine2 + ', ');
				address += ($scope.transferOrder.fromWarehouse.location + ', ');
				address += ($scope.transferOrder.fromWarehouse.city + ' - ');
				address += ($scope.transferOrder.fromWarehouse.pincode + ' . ');
				address += ($scope.transferOrder.fromWarehouse.state + ', ');
				address += ($scope.transferOrder.fromWarehouse.country);
				$scope.pickUpAddress = address;

				//$scope.transferOrder.toWarehouse = null;

				angular.forEach($scope.listOfWarehouses, function(warehouse, key){
					if(warehouse.name != $scope.transferOrder.fromWarehouse.name){
						$scope.listOfToWarehouses.push(warehouse);
					}
				});
			}
		},
		function (errorpl) {
			alert("Error!");
		});
	}

	$scope.toWarehouseSelectEvent = function (){
		if($scope.transferOrder.toWarehouse == null)
			return;
		var promiseGet = CRUDOperations.get("/api/warehouses/get/" + $scope.transferOrder.toWarehouse.id);
		promiseGet.then(function (data) {
			if(data.data.valid){
				$scope.transferOrder.toWarehouse = data.data.payload;
				var address = $scope.transferOrder.toWarehouse.addressLine1 + ', ';
				address += ($scope.transferOrder.toWarehouse.addressLine2 + ', ');
				address += ($scope.transferOrder.toWarehouse.location + ', ');
				address += ($scope.transferOrder.toWarehouse.city + ' - ');
				address += ($scope.transferOrder.toWarehouse.pincode + ' . ');
				address += ($scope.transferOrder.toWarehouse.state + ', ');
				address += ($scope.transferOrder.toWarehouse.country);
				$scope.shippingAddress = address;
			}
		},
		function (errorpl) {
			alert("Error!");
		});
	}

	$scope.addItemToOrder = function(){
		var orderItem = {
				itemCode: $scope.selectedItem.itemCode,
				vendorAlias: $scope.vendorAlias,
				quantity: 1,
				unit: $scope.selectedItem.unitOfMeasurement,
				unitPrice: $scope.selectedItem.unitPrice,
				wholesalePrice: $scope.selectedItem.wholesalePrice,
				totalPrice: ($scope.selectedItem.unitPrice),
				description: $scope.description,
				itemSerialized: $scope.selectedItem.serialized,
				barcode: $scope.selectedItem.barcode
		}
		$scope.transferOrder.items.push(orderItem);
		$scope.quantityExpected += orderItem.quantity;
		$scope.resetModalForm();
		$scope.listOfItems = [];
		$scope.calculateTotalQuantity();
		$('#addOrderItemModal').modal('hide');
	}

	$scope.calculateTotalQuantity = function(){
		$scope.quantityTotal = 0.0;
		//$scope.quantityDispatched = 0;
		//$scope.quantityDelivered = 0;
		angular.forEach($scope.transferOrder.items, function(item, key){
			$scope.quantityTotal += parseInt(item.quantity);
		});
	}

	$scope.resetModalForm = function(){
		$scope.selectedItem.code = null;
		$scope.vendorAlias = "";
		$scope.quantity = "";
		$scope.selectedItem.unitOfMeasurement = "";
		$scope.unitPrice = "";
		$scope.description = "";
	}

	$scope.getOrderItemLine = function(){
		$('#addOrderItemModal').modal('show');
	}

	$scope.itemSelectEvent = function(){
		//$scope.quantity = $scope.selectedItem.reorderQuantity;
		$scope.unitPrice = $scope.selectedItem.normalPrice;
		//$scope.description = $scope.selectedItem.description;
		//$scope.unit = $scope.selectedItem.unitOfMeasurement;
		//$scope.vendorAlias = $scope.selectedItem.vendorAlias;
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

			var promisePost = CRUDOperations.post(searchCriterias, "/api/items/searchWarehouseItemsByCriteria/" + $scope.transferOrder.fromWarehouse.id);
			var result = promisePost.then(function (data) {
				if (data.statusText == "OK") {
					$scope.listOfItems = data.data;
					$scope.criteriaText = "";
				}
			},
			function (errorpl) {
				alert("Error!");
			});

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

	$scope.saveOrder = function(isValid){
		if(isValid){
			if($scope.transferOrder.id == 0){
				var promisePost = CRUDOperations.post($scope.transferOrder, "/api/transferOrders/save");
				var result = promisePost.then(function (data) {
					if (data.data.valid) {
						$scope.transferOrder = data.data.payload;
						alert(data.data.message);
						$location.path('/inventory/transfers/edit/'+$scope.transferOrder.orderNumber);
					}else{
						alert(data.data.message);
					}
				},
				function (errorpl) {
					alert("Error!");
				});				
			}else{
				var promisePost = CRUDOperations.post($scope.transferOrder, "/api/transferOrders/save");
				var result = promisePost.then(function (data) {
					if (data.data.valid) {
						$scope.transferOrder = data.data.payload;
						alert(data.data.message);
						$location.path('/inventory/transfers/edit/'+$scope.transferOrder.orderNumber);
					}else{
						alert(data.data.message);
					}
				},
				function (errorpl) {
					alert("Error!");
				});				
			}
		}
	}

	$scope.deliveryItemSelectEvent = function(){
		if($scope.deliveryItem){
			var itemAlreadyAdded = false;
			angular.forEach($scope.transferOrder.itemDetails, function(item, key){
				if(item.itemCode == $scope.deliveryItem.itemCode && item.quantityRequested == item.quantityOrdered){
					itemAlreadyAdded = true;
				}
			});
			if(itemAlreadyAdded){
				alert('item is already added, select another item!');
				$scope.deliveryItem = null;
			}else{
				$scope.deliveryItemQuantity = $scope.deliveryItem.quantity;
				$scope.deliveryItemQuantityOrdered = $scope.deliveryItem.quantity;
				if($scope.deliveryItem.itemSerialized){
					$scope.getSerialNumbers = true;
					$scope.deliveryItemQuantityPerUnit = 1;
					$scope.deliveryItemQuantityChangeEvent();
				}

			}
		}
	}

	$scope.deliveryItemQuantityChangeEvent = function(){
		$scope.listOfDeliveryItems = null;
		$scope.listOfDeliveryItems = new Array($scope.deliveryItemQuantity);
	}


	$scope.addItemToDeliveryList = function(){

		if($scope.getSerialNumbers){
			angular.forEach($scope.listOfDeliveryItems, function(item, key){
				var deliveryOrderItem = {
						warehouse: $scope.transferOrder.toWarehouse.name,
						itemCode: $scope.deliveryItem.itemCode,
						barcode: item.vendorId,
						quantityRequested: 1,
						quantityPerUnit: 1,
						receiveDate: $scope.transferOrder.sendDate,
						vendorProductId: item.vendorId,
						quantityOrdered: item.quantity
				}
				$scope.transferOrder.itemDetails.push(deliveryOrderItem);
				$scope.quantityDispatched += parseFloat(deliveryOrderItem.quantityRequested);
			});
		}else{
			var deliveryOrderItem = {
					warehouse: $scope.transferOrder.toWarehouse.name,
					itemCode: $scope.deliveryItem.itemCode,
					barcode: $scope.deliveryItem.barcode,
					quantityRequested: $scope.deliveryItemQuantity,
					quantityPerUnit: $scope.deliveryItemQuantity,
					receiveDate: $scope.transferOrder.sendDate,
					vendorProductId: "",
					quantityOrdered: $scope.deliveryItem.quantity
			}
			$scope.transferOrder.itemDetails.push(deliveryOrderItem);
			$scope.quantityDispatched += parseFloat(deliveryOrderItem.quantityRequested);
		}
		$scope.resetDeliveryItemForm();
		$('#getDeliveryOrderItemModal').modal('hide');
	}

	$scope.resetDeliveryItemForm = function(){
		$scope.deliveryItem = null;
		$scope.deliveryItemQuantity = 0;
		$scope.listOfDeliveryItems = [];
		$scope.deliveryItemQuantityOrdered = 0;
	}


	$scope.autoFillDeliveryDetails = function(){

		if($scope.transferOrder.sendDate){
			if($scope.transferOrder.items.length > 0){
				var proceed = confirm('Autofilling will result in loosing existing data. Do you wish to continue?');

				if(proceed == true){
					$scope.transferOrder.itemDetails = [];
					$scope.listOfSerializedItems = [];
					$scope.quantityDispatched = 0;
					var items = $scope.transferOrder.items;
					angular.forEach(items, function(item, key) {
						if(!item.itemSerialized){
							var deliveryOrderItem = {
									warehouse: $scope.transferOrder.toWarehouse.name,
									itemCode: item.itemCode,
									barcode: item.barcode,
									quantityRequested: item.quantity,
									quantityPerUnit: item.quantity,
									quantityDelivered: item.quantity,
									receiveDate: $scope.transferOrder.sendDate,
									vendorProductId: "",
									quantityOrdered: item.quantity
							}
							$scope.transferOrder.itemDetails.push(deliveryOrderItem);
							$scope.quantityDispatched += parseFloat(deliveryOrderItem.quantityRequested);
						}else{
							$scope.listOfSerializedItems.push(item);
						}
					});
					if($scope.listOfSerializedItems.length > 0){
						alert('Some items in the order are serialized. Please fill their details manually!');
					}				
				}
			}			
		}else{
			alert('Please provide shipping date');
		}
	}

	
	$scope.autoFillDeliveryRecvDetails = function(){
		if($scope.transferOrder.receiveDate){
			if($scope.transferOrder.items.length > 0){
				var proceed = confirm('Autofilling will result in loosing existing data. Do you wish to continue?');
				if(proceed == true){
					$scope.transferOrder.recvdItemDetails = [];
					$scope.quantityDelivered = 0;
					var items = $scope.transferOrder.itemDetails;
					angular.forEach(items, function(item, key) {
							var deliveryOrderItem = {
									warehouse: item.warehouse,
									itemCode: item.itemCode,
									barcode: item.barcode,
									quantityRequested: 1,
									quantityPerUnit: 1,
									quantityDelivered: 1,
									receiveDate: $scope.transferOrder.receiveDate,
									vendorProductId: "",
									quantityOrdered: 1
							}
							$scope.transferOrder.recvdItemDetails.push(deliveryOrderItem);
							$scope.quantityDelivered += parseFloat(deliveryOrderItem.quantityRequested);
					});
				}
			}			
		}else{
			alert('Please provide receiving date');
		}
	}
	
	$scope.sendItems = function(isValid){		
		if(isValid){
			var promisePost = CRUDOperations.post($scope.transferOrder, "/api/transferOrders/send");
			$(".update").button('loading');
			var result = promisePost.then(function (data) {
				if (data.data.valid) {
					$scope.fetchInitData();
				}
				alert(data.data.message);
				$(".update").button('reset');
			},
			function (errorpl) {
				alert("Error!");
				$(".update").button('reset');
			});				
		}
	}
	$scope.receiveItems = function(isValid){
		if(isValid){
			var promisePost = CRUDOperations.post($scope.transferOrder, "/api/transferOrders/receive");
			$(".update").button('loading');
			var result = promisePost.then(function (data) {
				if (data.data.valid) {
					$scope.fetchInitData();
				}
				alert(data.data.message);
				$(".update").button('reset');
			},
			function (errorpl) {
				alert("Error!");
				$(".update").button('reset');
			});				
		}
	}
	$scope.exportItems = function(){
		var items = $scope.transferOrder.itemDetails;
		$scope.exportableItems = [];
		$scope.exportableItems.push(["Barcode","Item Code","Unit Price", "Wholesale Price","From Warehouse","To Warehouse","Order Number","Order Date","Send Date","Receive Date", "Status"]);
		for (var i = 0;i < items.length;i++){
			if(items[i].status == 200){
				var _item1 = [];
				_item1.push(items[i].barcode);
				_item1.push(items[i].itemCode);
				_item1.push(items[i].unitPrice);
				_item1.push(items[i].wholesalePrice);
				_item1.push($scope.transferOrder.fromWarehouse.name);
				_item1.push($scope.transferOrder.toWarehouse.name);
				_item1.push($scope.transferOrder.orderNumber);
				_item1.push($scope.transferOrder.orderDate);
				_item1.push($scope.transferOrder.sendDate);
				_item1.push($scope.transferOrder.receiveDate);
				_item1.push("Available");
				$scope.exportableItems.push(_item1);
			}
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
		$scope.JSONToCSVConvertor($scope.exportableItems, "Stock Transfer Order Items: " + $scope.transferOrder.orderNo, true);
    };
});