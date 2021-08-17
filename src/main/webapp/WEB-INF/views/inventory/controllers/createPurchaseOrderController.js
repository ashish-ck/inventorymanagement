inventoryApp.directive('showtab',
    function () {
        return {
            link: function (scope, element, attrs) {
                element.click(function(e) {
                    e.preventDefault();
                    $(element).tab('show');
                });
            }
        };
    });
inventoryApp.controller("createPurchaseOrderController", function ($http, $scope, $filter, $routeParams, $location, CRUDOperations, $window, purchaseOrderService) {
	$scope.purchaseOrder = {
			id: 0,
			orderNo: "",
			date: $filter('date')(new Date(), "yyyy-MM-dd"),
			vendor: null,
			items: [],
			status: [],
			itemDetails: [],
			subTotal: 0,
			taxes: 0,
			total: 0,
			amtPaid: 0,
			amtBalance: 0,
			paymentDetails: [],
			taxDetails: [],
			shippingWarehouse: null,
			contactPerson: "",
			contactNumber: "",
			invoiceDate: $filter('date')(new Date(), "yyyy-MM-dd"),
			shippingDate: $filter('date')(new Date(), "yyyy-MM-dd"),
			version: 1,
			invoiceNo: "",
			vatTax: 0,
			isOnTheFly: true
	};
	$scope.paymentDate = $filter('date')(new Date(), "yyyy-MM-dd");
	$scope.listOfVendors = [];
	$scope.listOfWarehouses = [];
	$scope.listOfPaymentModes = ["Cash", "Cheque", "Card", "Demand Draft", "Net-Banking"];
	$scope.listOfItems = [];
	$scope.listOfSerializedItems = [];
	$scope.listOfDeliveryItems = [];
	$scope.deliveryItem = null;
	$scope.isUpdateForm = false;
	$scope.quantityReceived = 0.0;
	$scope.quantityExpected = 0.0;
	$scope.balanceQuantity = 0.0;
	$scope.itemsReceived = false;
	$scope.isOrderInvoiced = false;
	$scope.isEditingItem = false;
	$scope.isPaymentReceived = false;
	$scope.unitPrice = 0;
	$scope.wholesalePrice = 0;
	$scope.key = null;
	$scope.quantity = 0;
	$scope.quantityExpected = 0;
	$scope.quantityReceived = 0;
	$scope.balanceQuantity = 0;
	$scope.tagTransform = function (newTag) {
	        return newTag;
	 };
	 $scope.isLoading = function () {
			return $http.pendingRequests.length !== 0;
		};
	$scope.getOrderItemLine = function(){
		$('#addOrderItemModal').modal('show');
	}
	
	$scope.getPaymentDetails = function(){
		$('#getPaymentDetailsModal').modal('show');
	}
	
	$scope.getTaxDetails = function(){
		$('#getTaxDetailsModal').modal('show');
	}
	
	$scope.getDeliveryOrderItem = function(){
		if($scope.purchaseOrder.shippingDate){
			$('#getDeliveryOrderItemModal').modal('show');
		}else{
			alert('Please provide shipping date');
		}
	}
	$scope.displayRights = function(){
		if($scope.userDetails){
			if($scope.userDetails.authorities[0].authority == "ROLE_ADMIN"){
				return true;
			}else if($scope.userDetails.authorities[0].authority == "ROLE_INVENTORYMANAGER"){
				if($scope.userDetails.warehouse == $scope.selectedRequest.requestWarehouse.code ||
						$scope.userDetails.warehouse == $scope.selectedRequest.assignedWarehouse.code){
					return true;
				}
			} else if($scope.userDetails.authorities[0].authority == "ROLE_HUBMANAGER"){
				if($scope.userDetails.warehouse == $scope.selectedSubkit.location.code ||
						$scope.userDetails.warehouse == $scope.selectedRequest.requestWarehouse.code){
					return true;
				}
			}
		}
		return false;
	}
	$scope.fetchUserData = function(){
		var promiseget = CRUDOperations.get("/api/inventory/getUserDetails");
		promiseget.then(function (data) {
			if(data.data.warehouse){
				$scope.userDetails = data.data;
				var promiseGet = CRUDOperations.get("/api/warehouses/getByCode/" + $scope.userDetails.warehouse);
		        var result = promiseGet.then(function (data) {
		            if (data.data.valid) {
		            	//$scope.purchaseOrder.shippingWarehouse = data.data.payload;
		            	$scope.warehouseSelectEvent();
		            }else{
		            	alert(data.data.message);
		            }
		        },
				function (errorpl) {
					alert("Error!");
					$('.update').button('reset');
				});
			}else{
			}
		},
		function (errorpl) {
			alert("Error!");
		});
	}
	$scope.fetchInitData = function(){
		$scope.fetchVendors();
		$scope.fetchUserData();
		$scope.fetchWarehouses();
		if($routeParams.orderNo){
			//this is an edit form.
			//fetch details for the item here
			$scope.isUpdateForm = true;
			var promiseget = CRUDOperations.get("/api/purchaseOrders/get/"+$routeParams.orderNo);
			promiseget.then(function (data) {
				if(data.data.valid){
					$scope.purchaseOrder = data.data.payload;
					var vendorAddress = $scope.purchaseOrder.vendor.addressLine1 + ', ';
					vendorAddress += ($scope.purchaseOrder.vendor.addressLine2 + ', ');
					vendorAddress += ($scope.purchaseOrder.vendor.location + ', ');
					vendorAddress += ($scope.purchaseOrder.vendor.city + ' - ');
					vendorAddress += ($scope.purchaseOrder.vendor.pincode + ' . ');
					vendorAddress += ($scope.purchaseOrder.vendor.state + ', ');
					vendorAddress += ($scope.purchaseOrder.vendor.country);
					$scope.vendorAddress = vendorAddress;
					var shippingAddress = $scope.purchaseOrder.shippingWarehouse.addressLine1 + ', ';
					shippingAddress += ($scope.purchaseOrder.shippingWarehouse.addressLine2 + ', ');
					shippingAddress += ($scope.purchaseOrder.shippingWarehouse.location + ', ');
					shippingAddress += ($scope.purchaseOrder.shippingWarehouse.city + ' - ');
					shippingAddress += ($scope.purchaseOrder.shippingWarehouse.pincode + ' . ');
					shippingAddress += ($scope.purchaseOrder.shippingWarehouse.state + ', ');
					shippingAddress += ($scope.purchaseOrder.shippingWarehouse.country);
					$scope.shippingAddress = shippingAddress;
					$scope.quantityExpected = 0;
					$scope.quantityReceived = 0;
					angular.forEach($scope.purchaseOrder.itemDetails, function(item, key){
						$scope.quantityReceived += 1;
					});
					angular.forEach($scope.purchaseOrder.items, function(item, key){
						$scope.quantityExpected += 1;
					});
					$scope.purchaseOrder.items = $scope.barcodeToQuantity($scope.purchaseOrder.items);
					if($scope.purchaseOrder.status.length >= 2){
						//$scope.purchaseOrder.items = $scope.quantityToBarcode($scope.purchaseOrder.items);
					}
					//$scope.itemsReceived = $scope.quantityReceived == $scope.quantityExpected;  
					$scope.balanceQuantity = $scope.quantityExpected - $scope.quantityReceived ;
					
					if($scope.balanceQuantity == 0){
						$scope.itemsReceived = true;
					}
					if(!$scope.purchaseOrder.shippingDate){
						$scope.purchaseOrder.shippingDate = $filter('date')(new Date(), "yyyy-MM-dd");
					}
										
					if($scope.purchaseOrder.invoiceNo){
						$scope.isOrderInvoiced = true;
					}else{
						$scope.isOrderInvoiced = false;
						$scope.purchaseOrder.invoiceNo = null;
						$scope.purchaseOrder.invoiceDate = $filter('date')(new Date(), "yyyy-MM-dd");
					}
				}else{
					alert(data.data.message);
				}
			},
			function (errorpl) {
				alert("Error!");
			});
		}else{
			var existingData = purchaseOrderService.getData();
			if(existingData && existingData.length > 0){
				for( var i = 0; i < existingData.length ; i++){
					//get item details here by api call
					var inventoryItem = {};
					var item = existingData[i];
					var promiseget = CRUDOperations.get("/api/items/getByCode?itemCode="+encodeURI(item.itemCode));
					promiseget.then(function(data){
						if(data.data.valid){
							inventoryItem = data.data.payload;
							var orderItem = {
									itemCode: inventoryItem.code,
									vendorAlias: inventoryItem.vendorAlias,
									quantity: item.quantity,
									unit: inventoryItem.unitOfMeasurement,
									unitPrice: inventoryItem.unitPrice ? inventoryItem.unitPrice : 0,
									wholesalePrice: inventoryItem.wholesalePrice ? inventoryItem.wholesalePrice : 0,
									totalPrice: ((inventoryItem.unitPrice ? inventoryItem.unitPrice : 0) * item.quantity),
									description: inventoryItem.description,
									itemSerialized: inventoryItem.serialized,
									barcode: inventoryItem.barcode
							}
							$scope.purchaseOrder.items.push(orderItem);
							$scope.purchaseOrder.subTotal += orderItem.totalPrice;
							$scope.quantityExpected += orderItem.quantity;
						}else{
							alert(data.data.message);
						}
					}, function(err){
						alert("Something went wrong!");
					});
				};
				purchaseOrderService.setData(null);				
			}
		}
	}
	
	$scope.itemSelectEvent = function(item){
		$scope.quantity = $scope.selectedItem.reorderQuantity;
		$scope.unitPrice = $scope.selectedItem.unitPrice;
		$scope.wholesalePrice = $scope.selectedItem.wholesalePrice;
		$scope.description = $scope.selectedItem.description;
		$scope.unit = $scope.selectedItem.unitOfMeasurement;
		$scope.vendorAlias = $scope.selectedItem.vendorAlias;
	}
	
	$scope.vendorSelectEvent = function (){
		var address = $scope.purchaseOrder.vendor.addressLine1 + ', ';
		address += ($scope.purchaseOrder.vendor.addressLine2 + ', ');
		address += ($scope.purchaseOrder.vendor.location + ', ');
		address += ($scope.purchaseOrder.vendor.city + ' - ');
		address += ($scope.purchaseOrder.vendor.pincode + ' . ');
		address += ($scope.purchaseOrder.vendor.state + ', ');
		address += ($scope.purchaseOrder.vendor.country);
		$scope.vendorAddress = address;
		
		$scope.purchaseOrder.contactPerson = $scope.purchaseOrder.vendor.contactPerson;
		$scope.purchaseOrder.contactNumber = $scope.purchaseOrder.vendor.contactNo;
	}
	
	$scope.warehouseSelectEvent = function (){
		if($scope.purchaseOrder.shippingWarehouse == null)
			return;
		var id = $scope.purchaseOrder.shippingWarehouse.id;
		var promiseGet = CRUDOperations.get("/api/warehouses/get/" + id);
		promiseGet.then(function (data) {
			if(data.data.valid){
				$scope.purchaseOrder.shippingWarehouse = data.data.payload;
				if($scope.userDetails){
					if($scope.userDetails.authorities[0].authority == "ROLE_ADMIN"){
						if($scope.purchaseOrder.shippingWarehouse){
							$scope.purchaseOrder.shippingWarehouse = data.data.payload;
							var address = $scope.purchaseOrder.shippingWarehouse.addressLine1 + ', ';
							address += ($scope.purchaseOrder.shippingWarehouse.addressLine2 + ', ');
							address += ($scope.purchaseOrder.shippingWarehouse.location + ', ');
							address += ($scope.purchaseOrder.shippingWarehouse.city + ' - ');
							address += ($scope.purchaseOrder.shippingWarehouse.pincode + ' . ');
							address += ($scope.purchaseOrder.shippingWarehouse.state + ', ');
							address += ($scope.purchaseOrder.shippingWarehouse.country);
							$scope.shippingAddress = address;
							return;
						}
					}
				}
				$scope.purchaseOrder.shippingWarehouse = data.data.payload;
				var address = $scope.purchaseOrder.shippingWarehouse.addressLine1 + ', ';
				address += ($scope.purchaseOrder.shippingWarehouse.addressLine2 + ', ');
				address += ($scope.purchaseOrder.shippingWarehouse.location + ', ');
				address += ($scope.purchaseOrder.shippingWarehouse.city + ' - ');
				address += ($scope.purchaseOrder.shippingWarehouse.pincode + ' . ');
				address += ($scope.purchaseOrder.shippingWarehouse.state + ', ');
				address += ($scope.purchaseOrder.shippingWarehouse.country);
				$scope.shippingAddress = address;
			}
		},
		function (errorpl) {
			alert("Error!");
		});
		
		
		
		if($scope.purchaseOrder.shippingWarehouse == null)
			return;
		var id = $scope.purchaseOrder.shippingWarehouse.id;
		var promiseGet = CRUDOperations.get("/api/warehouses/get/" + id);
		promiseGet.then(function (data) {
			if(data.data.valid){
				$scope.purchaseOrder.shippingWarehouse = data.data.payload;
				var address = $scope.purchaseOrder.shippingWarehouse.addressLine1 + ', ';
				address += ($scope.purchaseOrder.shippingWarehouse.addressLine2 + ', ');
				address += ($scope.purchaseOrder.shippingWarehouse.location + ', ');
				address += ($scope.purchaseOrder.shippingWarehouse.city + ' - ');
				address += ($scope.purchaseOrder.shippingWarehouse.pincode + ' . ');
				address += ($scope.purchaseOrder.shippingWarehouse.state + ', ');
				address += ($scope.purchaseOrder.shippingWarehouse.country);
				$scope.shippingAddress = address;
			}
		},
		function (errorpl) {
			alert("Error!");
		});
	}
	
	$scope.deliveryItemSelectEvent = function(){
		if($scope.deliveryItem){
			var itemAlreadyAdded = false;
			angular.forEach($scope.purchaseOrder.itemDetails, function(item, key){
				if(item.itemCode == $scope.deliveryItem.itemCode && item.quantityRequested == item.quantityOrdered){
					itemAlreadyAdded = true;
				}
			});
			if(itemAlreadyAdded){
				alert('item is already added, select another item!');
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
	
	$scope.addItemToOrder = function(i){
		var key = $scope.key;
		for (var i = 0;i < $scope.purchaseOrder.items.length;i++){
			if(!$scope.purchaseOrder.items[i].unitPrice ||
					 !$scope.purchaseOrder.items[i].wholesalePrice){
				alert("Cannot create purchase order without wholesale price and unit price!");
				return;
			}else if(parseFloat($scope.purchaseOrder.items[i].unitPrice) == 0 ||
					parseFloat($scope.purchaseOrder.items[i].wholesalePrice) == 0){
				alert("Cannot create purchase order without wholesale price and unit price!");
				return;
			}else if(parseFloat($scope.purchaseOrder.items[i].unitPrice) < parseFloat($scope.purchaseOrder.items[i].wholesalePrice)){
				alert("Wholesale Price cannot be more than Cost Price!");
				return;
			}
		}
		if($scope.isEditingItem){
			//$scope.purchaseOrder.items[key].quantity = $scope.quantity;
			$scope.purchaseOrder.items[key].unitPrice = $scope.unitPrice;
			$scope.purchaseOrder.items[key].wholesalePrice = $scope.wholesalePrice;
			$scope.purchaseOrder.items[key].quantity = $scope.quantity;
			$scope.purchaseOrder.items[key].totalPrice = $scope.unitPrice * $scope.quantity;
		}else{
			var orderItem = {
					itemCode: $scope.selectedItem.code,
					vendorAlias: $scope.vendorAlias,
					quantity: $scope.quantity,
					unit: $scope.selectedItem.unitOfMeasurement,
					unitPrice: $scope.unitPrice,
					wholesalePrice: $scope.wholesalePrice,
					totalPrice: ($scope.unitPrice * $scope.quantity),
					description: $scope.description,
					itemSerialized: $scope.selectedItem.serialized,
					barcode: $scope.barcode
			}
			$scope.purchaseOrder.items.push(orderItem);
			//$scope.purchaseOrder.subTotal += orderItem.totalPrice;
			//$scope.quantityExpected += orderItem.quantity;
		
		}
		//$scope.calculateOrderTotal();
		$scope.resetModalForm();
		$scope.listOfItems = [];
		$('#addOrderItemModal').modal('hide');	

	}
	
	$scope.removeItem = function(index){
		$scope.purchaseOrder.items.splice(index, 1);
	}
	
	$scope.editItem = function(index){
		$scope.key = index;
		$scope.isEditingItem = true;
		var orderItem = $scope.purchaseOrder.items[index];
		$scope.selectedItem = {};
		$scope.selectedItem.code = orderItem.itemCode;
		$scope.vendorAlias = orderItem.vendorAlias;
		$scope.quantity = orderItem.quantity;
		$scope.unit = orderItem.unit;
		$scope.unitPrice = orderItem.unitPrice;
		$scope.wholesalePrice = orderItem.wholesalePrice;
		$scope.description = orderItem.description;
		$scope.selectedItem.serialized = orderItem.itemSerialized;
		$scope.selectedItem.barcode  = orderItem.barcode;
		$('#addOrderItemModal').modal('show');
	}
		
	$scope.resetModalForm = function(){
		$scope.selectedItem.code = null;
		$scope.vendorAlias = "";
		$scope.quantity = "";
		$scope.selectedItem.unitOfMeasurement = "";
		$scope.unitPrice = "";
		$scope.wholesalePrice = "";
		$scope.description = "";
	}
	
	$scope.calculateOrderTotal = function(){
		$scope.purchaseOrder.subTotal = 0;
		$scope.quantityExpected = 0;
		angular.forEach($scope.purchaseOrder.items, function(item, key){
			for (var i = 0; i < parseFloat(item.quantity);i++){
				$scope.purchaseOrder.subTotal += parseFloat(item.wholesalePrice);
			}
			$scope.quantityExpected += parseFloat(item.quantity);
		});
		$scope.purchaseOrder.total = parseFloat($scope.purchaseOrder.subTotal) + parseFloat($scope.purchaseOrder.vatTax);
		$scope.purchaseOrder.amtBalance = parseFloat($scope.purchaseOrder.total) - parseFloat($scope.purchaseOrder.amtPaid);
	}
	
	
	
	$scope.addPaymentDetails = function(){
		var paymentDetails = {
				paymentMode: $scope.paymentMode,
				paymentDate: $scope.paymentDate,
				transactionId: $scope.transactionId,
				amtPaid: $scope.amtPaid
		}
		$scope.purchaseOrder.paymentDetails.push(paymentDetails);
		$scope.purchaseOrder.amtPaid += parseFloat(paymentDetails.amtPaid);
		$scope.calculateOrderTotal();
		$scope.resetPaymentForm();
		$('#getPaymentDetailsModal').modal('hide');
	}
	
	$scope.resetPaymentForm = function(){
		$scope.paymentMode = null;
		$scope.paymentDate = "";
		$scope.transactionId = "";
		$scope.amtPaid = 0;
	}

	$scope.addTaxDetails = function(){
		var taxDetails = {
				tax: $scope.taxName,
				rate: $scope.taxRate,
				taxAmount: $scope.taxAmt
		}
		$scope.purchaseOrder.taxDetails.push(taxDetails);
		$scope.purchaseOrder.taxes += parseFloat(taxDetails.taxAmount);
		$scope.calculateOrderTotal();
		$scope.resetTaxForm();
		$('#getTaxDetailsModal').modal('hide');
	}
	
	$scope.resetTaxForm = function(){
		$scope.taxName = "";
		$scope.taxRate = "";
		$scope.taxAmt = 0;
	}
	
	$scope.calculateTaxAmount = function(){
		var taxAmt = ($scope.taxRate * $scope.purchaseOrder.subTotal)/100;
		$scope.taxAmt = $filter('number')(taxAmt, 0);
	}
	
	$scope.addItemToDeliveryList = function(){
		if($scope.getSerialNumbers){
			angular.forEach($scope.listOfDeliveryItems, function(item, key){
				var deliveryOrderItem = {
						warehouse: $scope.purchaseOrder.shippingWarehouse.name,
						itemCode: $scope.deliveryItem.itemCode,
						barcode: item.vendorId,
						quantityRequested: 1,
						quantityPerUnit: 1,
						receiveDate: $scope.purchaseOrder.shippingDate,
						vendorProductId: item.vendorId,
						quantityOrdered: item.quantity,
						status: 200
				}
				$scope.purchaseOrder.itemDetails.push(deliveryOrderItem);
				$scope.quantityReceived += parseFloat(1);
			});			
		}else{
			var deliveryOrderItem = {
					warehouse: $scope.purchaseOrder.shippingWarehouse.name,
					itemCode: $scope.deliveryItem.itemCode,
					barcode: $scope.deliveryItem.barcode,
					quantityRequested: $scope.deliveryItemQuantity,
					quantityPerUnit: $scope.deliveryItemQuantity,
					receiveDate: $scope.purchaseOrder.shippingDate,
					vendorProductId: "",
					quantityOrdered: $scope.deliveryItem.quantity,
					status: 200
			}
			$scope.purchaseOrder.itemDetails.push(deliveryOrderItem);
			$scope.quantityReceived += parseFloat($scope.deliveryItemQuantity);
		}
		$scope.balanceQuantity = $scope.quantityExpected - $scope.quantityReceived ;
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
		if($scope.purchaseOrder.shippingDate){
			if($scope.purchaseOrder.items.length > 0){
				var proceed = confirm('Autofilling will result in loosing existing data. Do you wish to continue?');
				if(proceed == true){
					$scope.purchaseOrder.itemDetails = [];
					var items = $scope.purchaseOrder.items;
					$scope.quantityExpected = 0;
					var map = {};
					angular.forEach(items, function(item, key) {
						  if(!item.itemSerialized){
								var deliveryOrderItem = {
										warehouse: $scope.purchaseOrder.shippingWarehouse.name,
										itemCode: item.itemCode,
										barcode: item.barcode,
										unitPrice: item.unitPrice,
										wholesalePrice: item.wholesalePrice,
										quantityPerUnit: 1,
										receiveDate: $scope.purchaseOrder.shippingDate,
										vendorProductId: "",
										quantityOrdered: 1,
										quantityRequested: 1,
										quantity: item.quantity,
										status: 200
								};
								$scope.purchaseOrder.itemDetails.push(deliveryOrderItem);
								$scope.quantityExpected += item.quantity;
								$scope.quantityReceived += item.quantity;
								$scope.balanceQuantity = $scope.quantityExpected - $scope.quantityReceived;
						  }else{
							  var deliveryOrderItem = {
										warehouse: $scope.purchaseOrder.shippingWarehouse.name,
										itemCode: item.itemCode,
										barcode: item.barcode,
										unitPrice: item.unitPrice,
										wholesalePrice: item.wholesalePrice,
										quantityPerUnit: 1,
										receiveDate: $scope.purchaseOrder.shippingDate,
										vendorProductId: "",
										quantityOrdered: 1,
										quantityRequested: 1,
										quantity: item.quantity,
										status: 200
								};
								$scope.purchaseOrder.itemDetails.push(deliveryOrderItem);
								$scope.quantityExpected += item.quantity;
								$scope.quantityReceived += item.quantity;
								$scope.balanceQuantity = $scope.quantityExpected - $scope.quantityReceived;
							  $scope.listOfSerializedItems.push(item);
						  }
					});
					if($scope.listOfSerializedItems.length > 0){
						//alert('Some items in the order are serialized. Please fill their details manually!');
					}				
				}
			}			
		}else{
			alert('Please provide shipping date');
		}
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
									},{
										key: 'alias',
										value: values
									}
			                       ];
			
	        var promisePost = CRUDOperations.post(searchCriterias, "/api/items/searchByCriteria");
	        var result = promisePost.then(function (data) {
	            if (data.status == 200) {
	            	$scope.listOfItems = data.data;
	            	$scope.criteriaText = "";
	            }
	        },
			function (errorpl) {
				alert("Error!");
			});
			
		}
	}

	$scope.fetchVendors = function(){
        var promiseget = CRUDOperations.get("/api/vendors/get");
        promiseget.then(function (data) {
        	if(data.data.valid){
        		$scope.listOfVendors = data.data.payload;
        	}else{
        		alert(data.data.message);
        	}
        },
      function (errorpl) {
        	alert("Error!");
      });
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
	$scope.saveOrder = function(isValid){
		if(isValid){
			if($scope.purchaseOrder.id == 0){
				var newitems = [];
				for (var i = 0;i < $scope.purchaseOrder.items.length;i++){
					for (var j = 0;j < parseInt($scope.purchaseOrder.items[i].quantity);j++){
						newitems.push($scope.purchaseOrder.items[i]);
					}
				}
				$scope.purchaseOrder.items = newitems;
				$scope.calculateOrderTotal();
		        var promisePost = CRUDOperations.post($scope.purchaseOrder, "/api/purchaseOrders/create");
		        $('.update').button('loading');
		        var result = promisePost.then(function (data) {
		            if (data.data.valid) {
		            	alert(data.data.message);
		            	$('.update').button('reset');
						$location.path('/purchaseOrders/edit/' + data.data.payload.orderNo);
		            }else{
		            	alert(data.data.message);
		            }
		        },
				function (errorpl) {
					alert("Error!");
					$('.update').button('reset');
				});				
			}else{
				$scope.purchaseOrder.items = $scope.quantityToBarcode($scope.purchaseOrder.items);
		        var promisePost = CRUDOperations.post($scope.purchaseOrder, "/api/purchaseOrders/update");
		        $('.update').button('loading');
		        var result = promisePost.then(function (data) {
		            if (data.data.valid) {
		            	alert(data.data.message);
		            	$scope.fetchInitData();
		            	$('.update').button('reset');
		            	$window.location.reload();
		            }else{
		            	alert(data.data.message);
		            }
		        },
				function (errorpl) {
					alert("Error!");
					$('.update').button('reset');
				});				
			}
		}
	}
	$scope.receiveOrderItems = function(){
		$scope.purchaseOrder.itemDetails = $scope.quantityToBarcode($scope.purchaseOrder.itemDetails);
		$scope.purchaseOrder.items = $scope.quantityToBarcode($scope.purchaseOrder.items);
		var promisePost = CRUDOperations.post($scope.purchaseOrder, "/api/purchaseOrders/receive/"+$scope.purchaseOrder.orderNo);
		$('.update').button('loading');
		var result = promisePost.then(function(data){
			if(data.data.valid){
				$('.update').button('reset');
				$scope.fetchInitData();
				$window.location.reload();
			}
			alert(data.data.message);
		}, function(errorpl){
			$('.update').button('reset');
			alert("Error!");
		});
	}
	$scope.updateOrderInvoice = function(){
		//$scope.purchaseOrder.itemDetails = $scope.quantityToBarcode($scope.purchaseOrder.itemDetails);
		$scope.purchaseOrder.items = $scope.quantityToBarcode($scope.purchaseOrder.items);
		var promisePost = CRUDOperations.post($scope.purchaseOrder, "/api/purchaseOrders/invoice/"+$scope.purchaseOrder.orderNo);
		$('.update').button('loading');
		var result = promisePost.then(function(data){
			if(data.data.valid){
				$('.update').button('reset');
				$scope.fetchInitData();
				$window.location.reload();
			}
			alert(data.data.message);
		}, function(errorpl){
			$('.update').button('reset');
			alert("Error!");
		});
	}
	$scope.disableReceive = function(){
		if($scope.purchaseOrder){
			if($scope.purchaseOrder.status){
				if($scope.purchaseOrder.status.length > 0){
					if($scope.purchaseOrder.status.indexOf("Received") != -1 || $scope.purchaseOrder.itemDetails.length == 0)
						return true;
				}
			}
		}
		return false;
	}
	
	$scope.barcodeToQuantity = function(items){
		var map = {};
		for (var k = 0;k < items.length;k++){
			if(items[k].itemCode in map){
				var item = map[items[k].itemCode];
				item.quantity += 1;
				if(item.barcode != undefined || item.barcode != null){
					item.barcodes.push(items[k].barcode);
				}
				map[items[k].itemCode] = item;
			}else {
				var item = items[k];
				item.quantity = 1;
				item.barcodes = [];
				if(items[k].barcode != undefined || items[k].barcode != null){
					item.barcodes.push(items[k].barcode);
				}
				map[items[k].itemCode] = item;
			}
		}
		var newItems = [];
		for (var itemCode in map){
			newItems.push(map[itemCode]);
		}
		return newItems;
	}
	
	$scope.quantityToBarcode = function(items){
		var newItems = [];
		for (var k = 0;k < items.length;k++){
			for (var j = 0;j < items[k].quantity;j++){
				if(items[k].barcodes)
					items[k].barcode = items[k].barcodes[j];
				newItems.push(items[k]);
			}
		}
		return newItems;
	}
	$scope.exportItems = function(){
		var items = $scope.purchaseOrder.itemDetails;
		$scope.exportableItems = [];
		$scope.exportableItems.push(["Barcode","Item Code","Unit Price", "Wholesale Price","Warehouse","Order Number", "Date", "Status"]);
		for (var i = 0;i < items.length;i++){
			if(items[i].status == 200){
				var _item1 = [];
				_item1.push(items[i].barcode);
				_item1.push(items[i].itemCode);
				_item1.push(items[i].unitPrice);
				_item1.push(items[i].wholesalePrice);
				_item1.push($scope.purchaseOrder.shippingWarehouse.name);
				_item1.push($scope.purchaseOrder.orderNo);
				_item1.push($scope.purchaseOrder.date);
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
		$scope.JSONToCSVConvertor($scope.exportableItems, "Bulk Purchase Order Items: " + $scope.purchaseOrder.orderNo, true);
    };
})