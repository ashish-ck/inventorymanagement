inventoryApp.controller("kitController", function ($http, $scope, $rootScope,$filter, $location, CRUDOperations, $window, DTOptionsBuilder, DTColumnBuilder, localStorageService) {
	$scope.reservedItemsList = [];
	$scope.sendItemsList = [];
	$scope.receiveItemsList = [];
	$scope.issueToMechanicItemsList = [];
	$scope.returnItemsList = [];
	$scope.listOfWarehouses = [];
	$scope.vendorAddress = {};
	$scope.shippingAddress = {};
	$scope.returnItemsList = [];
	$scope.returnReturnExpectedItemsList = [];
	$scope.returnReceivedByHubItemsList = [];
	$scope.returnDispatchedByHubItemsList = [];
	$scope.returnReceivedByWarehouseItemsList = [];
	$scope.stockLossItemsList = [];
	$scope.returnExpectedItemsList = [];
	$scope.returnItemsList = []
	$scope.returnReceivedByHubItemsList = [];
	$scope.returnDispatchedByHubItemsList = [];
	$scope.returnReceivedByWarehouseItemsList = [];
	$scope.returnStockLossItemsList = [];
	$scope.returnScrapItemsList = [];
	$scope.listOfSerializedItems = [];
	$scope.exportableItems = [];
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
	$scope.listOfItems = [];
	$scope.listOfVendors = [];
	$scope.selectedSubkit = {};
	$scope.kitStatuses = {
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
	$scope.subkitStatuses = {
			"100": "New",
			"200": "Partially Reserved", 
			"300": "Reserved",
			"400": "Partially Dispatched", 
			"500": "Dispatched",
			"600": "Partially Received", 
			"700": "Received",
			"800": "Issued",
			"900": "Invoiced",
			"1000": "Return Expected",
			"1100": "Return Received By Hub", 
			"1200": "Return Dispatched By Hub",
			"1300": "Return Received By Warehouse",
			"1400": "Disputed",
			"1500": "Closed",
			"1600": "Cancelled",
			"1700": "Re-opened"
	};
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
			"60000": "Scrap Expected",
			};
	$scope.applicableStatus = {
			"100": "reserveTab",
			"200": "reserveTab", 
			"300": "sendTab",
			"400": "receiveTab", 
			"500": "receiveTab",
			"600": "issueToMechanicTab",
			"700": "issueToMechanicTab"
	};
	$scope.buttonText = {
			"reserveTab": ["Reserve", 300],
			"sendTab": ["Send", 400], 
			"receiveTab": ["Receive", 500],
			"issueToMechanicTab": ["Issue to Mechanic", 600]
	};
	$scope.kitStatus = function(status){
		return $scope.kitStatuses[status.toString()];
	};
	$scope.subkitStatus = function(status){
		return $scope.subkitStatuses[status.toString()];
	};
	$scope.itemStatus = function(status){
		return $scope.itemStatuses[status.toString()];
	};
	$scope.selectedRequest = null;
	$scope.listOfStatuses = [];
	$scope.selectAll = {
		       value : false
		     };
	$scope.selectedStatus = null;
	$scope.selectedItemsCount = 0;
	$scope.currentItemsStatus = null;
	$scope.currentWarehouse = null;
	$scope.toWarehouse = null;
	$scope.listOfWarehouses = [];
	$scope.isLoadingData = false;
	$scope.IsSubkitSelected = false;
	
	$scope.selectedListOfItems = [];
	$scope.selectedStatus = {};
	  
	$scope.openSubkitDetails = function(subkit){
		$scope.IsSubkitSelected = true;
	}
	$scope.fetchInitData = function(){
		$scope.fetchVendors();
		$scope.fetchWarehouses();
		$scope.selectedListOfItems = [];
		$scope.selectedStatus = {};
		$("[href=#subkitDetailsTab]").show();
		$("[href=#reserveTab]").show();
		$("[href=#sendTab]").show();
		$("[href=#receiveTab]").show();
		$("[href=#issueToMechanicTab]").show();
		var uri =  window.location.href;
		var lastslashindex = uri.lastIndexOf('/');	
		var requestId= uri.substring(lastslashindex  + 1)
		var promiseget = CRUDOperations.get("/api/kits/get/"+requestId);
		promiseget.then(function (data) {
			if(data.data.valid){
				$scope.selectedRequest = data.data.payload;
				$scope.selectedRequest.statusText = $scope.kitStatus($scope.selectedRequest.status);
				for( var i = 0; i < $scope.selectedRequest.items.length; i++){
					var item = $scope.selectedRequest.items[i];
					item.statusText = $scope.itemStatus(item.status);
					item.itemCode = item.item.code;
					item.toUpdateStatus = false;
					if($scope.selectedRequest.items[i].status != 100 || $scope.selectedRequest.items[i].status != 200){
						for( var j = 0; j < $scope.selectedRequest.subkits.length; j++)
						for( var k = 0; k < $scope.selectedRequest.subkits[j].items.length; k++)
							if($scope.selectedRequest.subkits[j].items[k].barcode == $scope.selectedRequest.items[i].barcode){
								$scope.selectedRequest.items[i].subkitNumber = $scope.selectedRequest.subkits[j].subkitNumber;
							}
					}else{
						for( var j = 0; j < $scope.selectedRequest.subkits.length; j++)
						for( var k = 0; k < $scope.selectedRequest.subkits[j].items.length; k++){
							if($scope.selectedRequest.subkits[j].items[k].currentLocation.id == $scope.selectedRequest.items[i].currentLocation.id 
									&& $scope.selectedRequest.subkits[j].items[k].itemCode == $scope.selectedRequest.items[i].itemCode
									&& !$scope.selectedRequest.subkits[j].items[k].barcode){
								$scope.selectedRequest.items[i].subkitNumber = $scope.selectedRequest.subkits[j].subkitNumber;
							}
					}
					}
				}
				for( var i = 0; i < $scope.selectedRequest.subkits.length; i++){
					var subkit = $scope.selectedRequest.subkits[i];
					subkit.statusText = $scope.subkitStatus(subkit.status);
				}
				$scope.selectedStatus = null;
				$scope.listOfStatuses = [];
				$rootScope.selectedStatus = null;
				$rootScope.listOfStatuses = [];
				$scope.currentWarehouse = null;
				$scope.currentItemsStatus = null;
				$scope.fetchOrders($scope.selectedRequest.kitNumber);
			}else{
				alert(data.data.message);
			}
		},
		function (errorpl) {
			alert("Error!");
		});
	}
	$scope.showReturnScrapButtons = function(status){
		return true;
	}
	$scope.isLoading = function () {
		return $http.pendingRequests.length !== 0;
	};
	$scope.viewDetails = function (subkit) {
		$scope.selectedSubkit = subkit;
		$scope.selectedListOfItems = [];
		$scope.selectedStatus = {};
		$scope.reserveItemsList = [];
		$scope.sendItemsList = [];
		$scope.receiveItemsList = [];
		$scope.issueToMechanicItemsList = [];
		$scope.returnExpectedItemsList = [];
		$scope.returnItemsList = []
		$scope.returnReceivedByHubItemsList = [];
		$scope.returnDispatchedByHubItemsList = [];
		$scope.returnReceivedByWarehouseItemsList = [];
		$scope.returnStockLossItemsList = [];
		$scope.returnScrapItemsList = [];
		$scope.isReturnScrapExpected = false;
		for (var i = 0;i < subkit.items.length;i++){
			$scope.selectedSubkit.items[i].statusText = $scope.itemStatus($scope.selectedSubkit.items[i].status);
			$scope.selectedSubkit.items[i].itemCode = $scope.selectedSubkit.items[i].item.code;
			var item = $scope.selectedSubkit.items[i].item;
			var scrapItem = $scope.selectedSubkit.items[i];
			if(scrapItem.status == 700){
				if(scrapItem.scrapStatus >= 10000 && scrapItem.scrapStatus <= 60000){
					if(scrapItem.scrapStatus != 50000){
						scrapItem.returnScrapStatusText = $scope.itemStatuses[scrapItem.scrapStatus.toString()];
						$scope.returnScrapItemsList.push(scrapItem);
					}else{
						scrapItem.returnScrapStatusText = $scope.itemStatuses["50000"];
						$scope.returnScrapItemsList.push(scrapItem);
						$scope.isReturnScrapExpected = true;
					}
				}else{
					scrapItem.scrapStatus = 50000;
					scrapItem.returnScrapStatusText = $scope.itemStatuses[scrapItem.scrapStatus.toString()];
					$scope.returnScrapItemsList.push(scrapItem);
					$scope.isReturnScrapExpected = true;
				}
			}
			switch($scope.selectedSubkit.items[i].status){
			case 100:
				$scope.reserveItemsList.push($scope.selectedSubkit.items[i]);
				break;
			case 200:
				$scope.reserveItemsList.push($scope.selectedSubkit.items[i]);
				break;
			case 300:
				if($scope.selectedSubkit.items[i].currentLocation.id == $scope.selectedRequest.requestWarehouse.id)
					$scope.receiveItemsList.push($scope.selectedSubkit.items[i]);
				else
					$scope.sendItemsList.push($scope.selectedSubkit.items[i]);
				break;
			case 400:
				$scope.receiveItemsList.push($scope.selectedSubkit.items[i]);
				break;
			case 500:
				$scope.issueToMechanicItemsList.push($scope.selectedSubkit.items[i]);
				break;
			case 600:
				break;
			case 700:
				break;
			case 800:
				$scope.returnItemsList.push($scope.selectedSubkit.items[i]);
				$scope.returnExpectedItemsList.push($scope.selectedSubkit.items[i]);
				break;
			case 900:
				$scope.returnItemsList.push($scope.selectedSubkit.items[i]);
				$scope.returnReceivedByHubItemsList.push($scope.selectedSubkit.items[i]);
				break;
			case 1000:
				$scope.returnItemsList.push($scope.selectedSubkit.items[i]);
				$scope.returnDispatchedByHubItemsList.push($scope.selectedSubkit.items[i]);
				break;
			case 1100:
				$scope.returnReceivedByWarehouseItemsList.push($scope.selectedSubkit.items[i]);
				break;
			case 1200:
				$scope.returnItemsList.push($scope.selectedSubkit.items[i]);
				$scope.stockLossItemsList.push($scope.selectedSubkit.items[i]);
				break;
			default:
				break;
			}
		}
		$('li.active a[data-toggle="tab"]').parent().removeClass('active');
	    $('.nav.nav-tabs a[href="#subkitDetailsTab"]').tab('show');
		$window.scrollTo(0, 0);
	}
	$scope.selectAllEvent = function(list){
		$scope.selectedListOfItems = [];
		if($scope.selectAll.value){
			for(var i = 0;i < list.length;i++){
				$scope.selectedListOfItems.push(list[i]);
				list[i].toUpdateStatus = true;
			}
		}else{
			for(var i = 0;i < list.length;i++){
				list[i].toUpdateStatus = false;
			}
		}
    }
	$scope.itemSelectEvent = function(list){
		$scope.selectedListOfItems = [];
		for(var i = 0;i < list.length;i++){
			if(list[i].toUpdateStatus){
				$scope.selectedListOfItems.push(list[i]);
				
			}
		}
    }
	$scope.updateItemDetails = function(status){
		var inventoryRequest = {
				  "date": $scope.selectedRequest.date,
				  "listOfItems": $scope.selectedListOfItems,
				  "status": status,
				  "voucherRef": $scope.selectedSubkit.subkitNumber,
				  "workshopRef": $scope.selectedSubkit.location.code
				};
		var errorItems = [];
		var barcodes = [];
		var duplicateBarcodes = [];
		if($scope.selectedListOfItems.length == 0){
			alert("Not items selected.");
			return;
		}
		if(status >= 10000 && status <= 60000){
			var found = false;
			var scrapErrorList = [];
			for(var i = 0;i < $scope.selectedListOfItems.length;i++){
				if(!($scope.selectedListOfItems[i].scrapStatus == 50000 || 
						$scope.selectedListOfItems[i].scrapStatus == 60000)){
					scrapErrorList.push($scope.selectedListOfItems[i].barcode);
				}
			}
			if(scrapErrorList.length > 0){
				alert("Cannot change scrap status for barcodes\n\n" + scrapErrorList.join('\n'));
				return;
			}
		}
		var map = {};
		for(var i = 0;i < $scope.selectedListOfItems.length;i++){
			if(!$scope.selectedListOfItems[i].barcode){
				errorItems.push($scope.selectedListOfItems[i].item.code); 
			}else{
				if($scope.selectedListOfItems[i].barcode in map){
					duplicateBarcodes.push($scope.selectedListOfItems[i].item.code + " : " + $scope.selectedListOfItems[i].barcode);
				}else{
					barcodes.push($scope.selectedListOfItems[i].barcode);
					map[$scope.selectedListOfItems[i].barcode] = $scope.selectedListOfItems[i].barcode;
				}
			}
		}
		if(errorItems.length > 0){
			alert("Barcode not defined for item codes:\n" + errorItems.join('\n'));
			return;
		}
		if(duplicateBarcodes.length > 0){
			alert("Duplicate barcodes for item codes " + duplicateBarcodes.join('\n'));
			return;
		}if(status == 300){
			var invalidBarcodes1 = [];
			for (var i = 0;i < barcodes.length;i++) {
				if(!barcodes[i].match(/^([a-zA-Z0-9]){6}$/)){
					invalidBarcodes1.push(barcodes[i] + " : Barcode Invalid.");
				}
			}
			if(invalidBarcodes1.length > 0){
				alert("Invalid barcodes\n\n" + invalidBarcodes1.join('\n') + "\n\n\n Enter valid barcodes.");
				return;
			}
			var promisepost = CRUDOperations.post(barcodes, "/api/warehouses/" + $scope.selectedSubkit.location.code + "/getAvailableBarcodesByBarcodes");
			promisepost.then(function (data) {
		        	if(data.data.valid){
		        		var items = data.data.payload;
		        		var invalidBarcodes = [];
		        		var validBarcodes = [];
		        		for (var i = 0;i < items.length;i++) {
		        	        if (items[i].indexOf("Barcode is valid.") != -1) {
		        	        	validBarcodes.push(items[i]);
		        	        }else {
		        	        	invalidBarcodes.push(items[i]);
		        	        }
		        	    }
		        		if(validBarcodes.length == barcodes.length){
		        			var promiseget = CRUDOperations.post(inventoryRequest, "/api/kits/updateKitItemDetails");
		        	        promiseget.then(function (data) {
		        	        	if(data.data.valid){
		        	        		var items = data.data.payload;
		        	        		alert(data.data.message);
		        	        		$window.location.reload();
		        	        	}else{
		        	        		alert(data.data.message);
		        	        	}
		        	        	$scope.loadingReorderItems = false;
		        	        },
		        	      function (errorpl) {
		        	        	$scope.loadingReorderItems = false;
		        	        	alert("Error!");
		        	      });
		        		}else{
		        			alert("Invalid barcodes\n\n" + invalidBarcodes.join('\n') + "\n\n\n Enter valid barcodes.");
		        		}
		        	}else{
		        		alert(data.data.message);
		        	}
		        	$scope.loadingReorderItems = false;
		        },
		      function (errorpl) {
		        	$scope.loadingReorderItems = false;
		        	alert("Error!");
		      });
		}else{
			var promiseget = CRUDOperations.post(inventoryRequest, "/api/kits/updateKitItemDetails");
	        promiseget.then(function (data) {
	        	if(data.data.valid){
	        		var items = data.data.payload;
	        		alert(data.data.message);
	        		$window.location.reload();
	        	}else{
	        		alert(data.data.message);
	        	}
	        	$scope.loadingReorderItems = false;
	        },
	      function (errorpl) {
	        	$scope.loadingReorderItems = false;
	        	alert("Error!");
	      });
		}
	}
	$scope.createPurchaseOrder = function(){
			$scope.purchaseOrder.items = [];
			for(var i = 0;i < $scope.selectedListOfItems.length;i++){
			if($scope.selectedListOfItems[i].status == 100 || $scope.selectedListOfItems[i].status == 200){
				var orderItem = {
						itemCode: $scope.selectedListOfItems[i].item.code,
						vendorAlias: $scope.selectedListOfItems[i].item.vendorAlias,
						description: $scope.selectedListOfItems[i].item.description,
						barcode: $scope.selectedListOfItems[i].barcode
				}
				$scope.purchaseOrder.items.push(orderItem);
				$scope.purchaseOrder.kitNumber = $scope.selectedRequest.kitNumber;
				$scope.purchaseOrder.subTotal += orderItem.totalPrice;
				$scope.quantityExpected += 1;
				$scope.calculateOrderTotal();
			}
		}
		$('#addOrderItemModal').modal('show');
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
	
	$scope.saveOrder = function(isValid){
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
		var promisePost = CRUDOperations.post($scope.purchaseOrder, "/api/purchaseOrders/create");
        $('.update').button('loading');
        var result = promisePost.then(function (data) {
            if (data.data.valid) {
            	alert(data.data.message);
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
	$scope.vendorSelectEvent = function(){
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
	$scope.warehouseSelectEvent = function(){
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
							var t = $scope.purchaseOrder.shippingWarehouse;
							var address = t.addressLine1 + ', ';
							address += (t.addressLine2 + ', ');
							address += (t.location + ', ');
							address += (t.city + ' - ');
							address += (t.pincode + ' . ');
							address += (t.state + ', ');
							address += (t.country);
							$scope.shippingAddress = address;
							$scope.purchaseOrder.shippingWarehouse = t;
							return;
						}
					}
				}
				var address = $scope.selectedSubkit.location.addressLine1 + ', ';
				address += ($scope.selectedSubkit.location.addressLine2 + ', ');
				address += ($scope.selectedSubkit.location.location + ', ');
				address += ($scope.selectedSubkit.location.city + ' - ');
				address += ($scope.selectedSubkit.location.pincode + ' . ');
				address += ($scope.selectedSubkit.location.state + ', ');
				address += ($scope.selectedSubkit.location.country);
				$scope.shippingAddress = address;
				if($scope.selectedSubkit.location.id == $scope.selectedRequest.requestWarehouse.id){
					$scope.purchaseOrder.shippingWarehouse = $scope.selectedRequest.requestedWarehouse;
				}else{
					$scope.purchaseOrder.shippingWarehouse = $scope.selectedRequest.assignedWarehouse;
				}
			}
		},
		function (errorpl) {
			alert("Error!");
		});
	}
	$scope.getOrderItemLine = function(){
	}
	$scope.removeItem = function(index){
		$scope.purchaseOrder.items.splice(index, 1);
	}
	$scope.returnItems = function(form){
	}
	$scope.addItemToOrder = function(){
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
	$scope.fetchOrders = function(kitNumber){
		var promisePost = CRUDOperations.get('/api/purchaseOrders/getOrdersByKitNumber/' + kitNumber);
        promisePost.then(function(data){
        	$scope.listOfOrders = data.data.payload;
        	$scope.exportItems($scope.selectedRequest);
        });
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
	$scope.checkIfItemBarcodeValid = function(barcode){
		if(barcode && barcode.length > 3){
			var promise = CRUDOperations.get('/api/items/checkIfItemBarcodeValid?barcode='+barcode);
			promise.then(function(data){
				if(data.data.valid){
					if(!data.data.payload){
						alert("Invalid Barcode!");
					}
				}
			});
		}
	}
	$scope.viewOrderDetails = function(order){
		if(!order.date){
			order.date = $filter('date')(new Date(), "yyyy-MM-dd");
		}
		if(!order.invoiceDate){
			order.invoiceDate = $filter('date')(new Date(), "yyyy-MM-dd");
		}
		if(!order.shippingDate){
			order.shippingDate = $filter('date')(new Date(), "yyyy-MM-dd");
		}
		$scope.purchaseOrder = order;
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
		$scope.selectedListOfItems = [];
		$scope.selectedStatus = {};
		$("[href=#subkitDetailsTab]").show();
		$("[href=#reserveTab]").show();
		$("[href=#sendTab]").show();
		$("[href=#receiveTab]").show();
		$("[href=#issueToMechanicTab]").show();
		angular.forEach($scope.purchaseOrder.itemDetails, function(item, key){
			$scope.quantityReceived += 1;
		});
		angular.forEach($scope.purchaseOrder.items, function(item, key){
			$scope.quantityExpected += 1;
		});
		$('li.active a[data-toggle="tab"]').parent().removeClass('active');
	    $('.nav.nav-tabs a[href="#purchaseOrderDetailsTab"]').tab('show');
		$window.scrollTo(0, 0);
	}
	$scope.autoFillDeliveryDetails = function(){
			if($scope.purchaseOrder.shippingDate){
				if($scope.purchaseOrder.items.length > 0){
					var proceed = confirm('Autofilling will result in loosing existing data. Do you wish to continue?');
					if(proceed == true){
						$scope.purchaseOrder.itemDetails = [];
						var items = $scope.purchaseOrder.items;
						$scope.quantityExpected = 0;
						$scope.quantityReceived = 0;
						angular.forEach(items, function(item, key) {
							  if(!item.itemSerialized){
									var deliveryOrderItem = {
											warehouse: $scope.purchaseOrder.shippingWarehouse.name,
											itemCode: item.itemCode,
											barcode: item.barcode,
											quantityPerUnit: 1,
											receiveDate: $scope.purchaseOrder.shippingDate,
											vendorProductId: "",
											quantityOrdered: 1,
											quantityRequested: 1,
											status: 200,
											unitPrice: item.unitPrice,
											wholesalePrice: item.wholesalePrice
									}
									$scope.purchaseOrder.itemDetails.push(deliveryOrderItem);
									$scope.quantityExpected += 1;
									$scope.quantityReceived += 1;
									$scope.balanceQuantity = 0;
							  }else{
								  $scope.listOfSerializedItems.push(item);
								  var deliveryOrderItem = {
											warehouse: $scope.purchaseOrder.shippingWarehouse.name,
											itemCode: item.itemCode,
											barcode: item.barcode,
											quantityPerUnit: 1,
											receiveDate: $scope.purchaseOrder.shippingDate,
											vendorProductId: "",
											quantityOrdered: 1,
											quantityRequested: 1,
											status: 200,
											unitPrice: item.unitPrice,
											wholesalePrice: item.wholesalePrice
									}
									$scope.purchaseOrder.itemDetails.push(deliveryOrderItem);
									$scope.quantityExpected += 1;
									$scope.quantityReceived += 1;
									$scope.balanceQuantity = 0;
							  }
						});
					}
				}
			}else{
				alert('Please provide shipping date');
			}
		}
		$scope.receiveOrderItems = function(){
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
		$scope.showReturnButtons = function(status){
			var valid = $scope.displayRights();
			switch(parseInt(status)){
			case 900:
				return $scope.returnExpectedItemsList.length > 0 && valid;
				break;
			case 1000:
				return $scope.returnReceivedByHubItemsList.length > 0 && valid;
				break;
			case 1100:
				return $scope.returnDispatchedByHubItemsList.length > 0 && valid;
				break;
			case 1200:
				return ($scope.returnDispatchedByHubItemsList.length > 0 ||
						$scope.returnReceivedByHubItemsList.length > 0 ||
						$scope.returnExpectedItemsList.length > 0) && valid;
				break;
			}
		}
		$scope.reserve = function(){
		}
		$scope.showPurchaseOrderButton = function(items){
			for (var i = 0;i < items.length;i++){
				if(items[i].status == 200)
					return true;
			}
			return true;
		}
		$scope.disableReceive = function(){
			if($scope.purchaseOrder){
				if($scope.purchaseOrder.status){
					if($scope.purchaseOrder.status.length > 0){
						for (var i = 0;i < $scope.purchaseOrder.status.length;i++){
							if($scope.purchaseOrder.status[i] == "Received")
								return true;
						}
					}
				}
			}
			return false;
		}
		$scope.showChangeSubkitLocationBtn = function(){
			if($scope.selectedRequest && $scope.selectedSubkit.location && $scope.selectedSubkit.status == 100){
				if($scope.selectedRequest.requestWarehouse.id != $scope.selectedSubkit.location.id)
					return true;
				return false;
			}
		}
		
		$scope.changeSubkitLocation = function(){
			var inventoryRequest = {
					  "date": $scope.selectedRequest.date,
					  "listOfItems": $scope.selectedListOfItems,
					  "status": status,
					  "voucherRef": $scope.selectedSubkit.subkitNumber,
					  "workshopRef": $scope.selectedSubkit.location.code
					};
			for (var i = 0;i < $scope.selectedListOfItems.length;i++){
				if($scope.selectedListOfItems[i].status >= 300){
					alert("Cannot change subkit location.");
					return;
				}
			}
				var promiseget = CRUDOperations.post(inventoryRequest, "/api/kits/changeSubkitLocation");
		        promiseget.then(function (data) {
		        	if(data.data.valid){
		        		var items = data.data.payload;
		        		alert(data.data.message);
		        		$window.location.reload();
		        	}else{
		        		alert(data.data.message);
		        	}
		        	$scope.loadingReorderItems = false;
		        },
		      function (errorpl) {
		        	$scope.loadingReorderItems = false;
		        	alert("Error!");
		      });
		}
		$scope.showBarcodeSuggestion = function(item){
			return true;
		}
		$scope.fetchUserData = function(){
			var promiseget = CRUDOperations.get("/api/inventory/getUserDetails");
			promiseget.then(function (data) {
				if(data.data.warehouse){
					$scope.userDetails = data.data;
				}else{
				}
			},
			function (errorpl) {
				alert("Error!");
			});
		}
		$scope.currentLocation = function(barcode, itemCode){
			if(barcode && $scope.selectedRequest && $scope.selectedRequest.subkits){
				for (var i = 0;i < $scope.selectedRequest.subkits.length;i++){
					for (var j = 0;j < $scope.selectedRequest.subkits[i].items.length;j++){
						if($scope.selectedRequest.subkits[i].items[j] == barcode){
							return $scope.selectedRequest.subkits[i].location.name;
						}
					}
				}
			}
			for (var i = 0;i < $scope.selectedRequest.subkits.length;i++){
				for (var j = 0;j < $scope.selectedRequest.subkits[i].items.length;j++){
					if($scope.selectedRequest.subkits[i].items[j] == itemCode){
						return $scope.selectedRequest.subkits[i].location.name;
					}
				}
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
		$scope.update = function(items){
			for (var i = 0;i < items.length;i++){
				var promiseget = CRUDOperations.post(items[i].itemCode, "/api/items/getByCode");
		        promiseget.then(function (data) {
		        	if(data.data.valid){
		        		var item = data.data.payload;
		        		if(item.attributes){
		        			item.attributes
		        			for (var k = 0;k < item.attributes.length;k++){
								if(item.attributes[k].attribute.name == 'Scrap Return'){
									if(item.attributes[k].value[0] == 'Scrap Return Expected'){
										scrapItem.scrapStatus = 60000;
										scrapItem.returnScrapStatusText = $scope.itemStatuses[scrapItem.scrapStatus.toString()];
										found = true;
									} else if(item.attributes[k].value[0] == 'Scrap Return Not Expected'){
										scrapItem.scrapStatus = 55000;
										scrapItem.returnScrapStatusText = $scope.itemStatuses[scrapItem.scrapStatus.toString()];
										found = true;
									}
								}
							}
		        		}
		        	}else{
		        		alert(data.data.message);
		        	}
		        	$scope.loadingReorderItems = false;
		        },
		      function (errorpl) {
		        	$scope.loadingReorderItems = false;
		        	alert("Error!");
		      });
			}
		}
		$scope.exportItems = function(kit){
			$scope.exportableItems = [];
			$scope.exportableItems.push(["Kit Number", "Date", "Assigned Warehouse","Request Warehouse", "Status"]);
			var _item1 = [];
			_item1.push(kit.kitNumber);
			kit.date = $filter('date')(kit.date, "yyyy-MM-dd");
			_item1.push(kit.date);
			_item1.push(kit.assignedWarehouse.name);
			_item1.push(kit.requestWarehouse.name);
			_item1.push($scope.kitStatuses[kit.status.toString()]);
			$scope.exportableItems.push(_item1);
			$scope.exportableItems.push([]);
			for (var j = 0;j < kit.subkits.length;j++){
				_item1 = [];
				$scope.exportableItems.push(["Subkit Number", "Date", "Location", "Status"]);
				_item1.push(kit.subkits[j].subkitNumber);
				kit.subkits[j].date = $filter('date')(kit.subkits[j].date, "yyyy-MM-dd");
				_item1.push(kit.subkits[j].date);
				_item1.push(kit.subkits[j].location.name);
				_item1.push($scope.subkitStatuses[kit.subkits[j].status.toString()]);
				$scope.exportableItems.push(_item1);
				$scope.exportableItems.push([]);
				$scope.exportableItems.push(["Subkit Number", "Barcode","Item Code","Unit Price", "Wholesale Price", "Current Location", "Status", "Scrap Status"]);
				for (var i = 0;i < kit.subkits[j].items.length;i++){
					if(kit.subkits[j].items[i].status > 200){
						_item1 = [];
						_item1.push(kit.subkits[j].subkitNumber);
						_item1.push(kit.subkits[j].items[i].barcode);
						_item1.push(kit.subkits[j].items[i].item.code);
						_item1.push(kit.subkits[j].items[i].unitPrice);
						_item1.push(kit.subkits[j].items[i].wholesalePrice);
						_item1.push(kit.subkits[j].items[i].currentLocation.name);
						_item1.push($scope.itemStatuses[kit.subkits[j].items[i].status.toString()]);
						_item1.push($scope.itemStatuses[kit.subkits[j].items[i].scrapStatus.toString()]);
						$scope.exportableItems.push(_item1);
					}
				}
				$scope.exportableItems.push([]);
			}
			for (var j = 0;j < $scope.listOfOrders.length;j++){
				_item1 = [];
				$scope.exportableItems.push(["PO Number", "Date", "Shipping Warehouse", "Status"]);
				_item1.push($scope.listOfOrders[j].orderNo);
				_item1.push($scope.listOfOrders[j].date);
				_item1.push($scope.listOfOrders[j].shippingWarehouse.name);
				_item1.push($scope.listOfOrders[j].status[$scope.listOfOrders[j].status.length - 1]);
				$scope.exportableItems.push(_item1);
				$scope.exportableItems.push([]);
				$scope.exportableItems.push(["PO Number", "Barcode","Item Code","Unit Price", "Wholesale Price", "Warehouse", "Status"]);
				for (var i = 0;i < $scope.listOfOrders[j].itemDetails.length;i++){
					_item1 = [];
					_item1.push($scope.listOfOrders[j].orderNo);
					_item1.push($scope.listOfOrders[j].itemDetails[i].barcode);
					_item1.push($scope.listOfOrders[j].itemDetails[i].itemCode);
					_item1.push($scope.listOfOrders[j].itemDetails[i].unitPrice);
					_item1.push($scope.listOfOrders[j].itemDetails[i].wholesalePrice);
					_item1.push($scope.listOfOrders[j].shippingWarehouse.name);
					_item1.push($scope.itemStatuses[$scope.listOfOrders[j].itemDetails[i].status.toString()]);
					$scope.exportableItems.push(_item1);
				}
				$scope.exportableItems.push([]);
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
		$scope.exportData = function () {
			$scope.JSONToCSVConvertor($scope.exportableItems, "Kit Details " + $scope.selectedRequest.kitNumber, true);
	    };
});