inventoryApp.controller("createItemController", function ($scope, $location, $routeParams, CRUDOperations) {
	$scope.ItemStatuses = {
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
	$scope.listOfItems = [];
	$scope.listOfCategories = [];
	$scope.listOfUnits = [];
	$scope.listOfWarehouses = [];
	$scope.listOfAttributes = [];
	$scope.listOfAttributeValues = [];
	$scope.itemCount = [];
	$scope.listOfTags = [];
	$scope.isUpdateForm = false;
	$scope.currentItem = {
			id: 0,
			code: "",
			description: "",
			//vendorAlias: $scope.vendorAlias,
			unitOfMeasurement: "",
			category: {},
			tags: [],
			//active: $scope.active,
			itemAttributes: [],
			barcode: "",
			warningPoint: 0,
			reorderPoint: 0,
			reorderQuantity: 0,
			itemCount: [],
			serialized: false,
			normalPrice: 0,
			wholesalePrice: 0
	};
	$scope.listOfOrders = [];
	$scope.listOfVendors = [];
	$scope.listOfItemDetails = null;
	$scope.isEditingAttribute = false;
	$scope.attribute = null;
	$scope.attributeValue = [];
	$scope.defaultWarehouse = null;
	$scope.location = {
			warehouse: null
	};
	$scope.isEditingCount = -1;
	$scope.count = {};

	$scope.tagTransform = function (newTag) {
		var item = {
				id: 0,
				name: newTag.toLowerCase()
		};
		return item;
	};

	$scope.fetchInitData = function(){
		if(!($scope.fetchTags() && $scope.fetchCategories() && $scope.fetchUnits() && $scope.fetchWarehouses() 
				&& $scope.fetchAttributes())){
			alert("something went wrong!");
		}

		if($routeParams.itemId){
//			$scope.fetchItemDetails($routeParams.itemId);
			//this is an edit form.
			//fetch details for the item here
			$scope.isUpdateForm = true;
			var promiseget = CRUDOperations.get("/api/items/get/"+$routeParams.itemId);
			promiseget.then(function (data) {
				if(data.data.valid){
					$scope.currentItem = data.data.payload;
					for (var i = 0;i < $scope.currentItem.itemCount.length;i++){
						$scope.currentItem.itemCount[i].statusText = $scope.ItemStatuses[$scope.currentItem.itemCount[i].status.toString()];
					}
					$scope.fetchVendorsForItem($scope.currentItem.id);
					//$scope.fetchItemDetails($routeParams.itemId);
					//$scope.fetchItemDetails($scope.currentItem.id);
				}else{
					alert(data.data.message);
				}
			},
			function (errorpl) {
				alert("Error!");
			});
		}else{

		}
	};
	
	$scope.fetchItemDetails = function(id){
		var promiseget = CRUDOperations.get("/api/items/getItemDetails/"+id);
		return promiseget.then(function (data) {
			if(data.data.valid){
				$scope.currentItem = data.data.payload;
				//$scope.fetchVendorsForItem($scope.currentItem.id);
				return true;
			}else{
				alert(data.data.message);
				return false;
			}
		},
		function (errorpl) {
			alert("Something went wrong!");
			return false;
		});
	}
	
	$scope.fetchVendorsForItem = function(id){
		var promiseget = CRUDOperations.get("/api/vendors/getVendorsByItem/"+id);
		return promiseget.then(function (data) {
			if(data.data.valid){
				$scope.listOfVendors = data.data.payload;
				return true;
			}else{
				alert(data.data.message);
				return false;
			}
		},
		function (errorpl) {
			alert("Something went wrong!");
			return false;
		});
	}

	$scope.fetchTags = function(){
		var promiseget = CRUDOperations.get("/api/tags/get");
		return promiseget.then(function (data) {
			if(data.statusText == "OK"){
				$scope.listOfTags = data.data;
				return true;
			}else{
				return false;
			}
		},
		function (errorpl) {
			alert("Something went wrong!");
			return false;
		});
	}

	$scope.fetchCategories = function(){
		var promiseget = CRUDOperations.get("/api/categories/get");
		return promiseget.then(function (data) {
			if(data.data.valid){
				$scope.listOfCategories = data.data.payload;
				return true;
			}else{
				alert(data.data.message);
				return false;
			}
		},
		function (errorpl) {
			alert("Something went wrong!");
			return false;
		});
	}

	$scope.fetchUnits = function(){
		var promiseget = CRUDOperations.get("/api/units/get");
		return promiseget.then(function (data) {
			if(data.data.valid){
				$scope.listOfUnits = data.data.payload;
				return true;
			}else{
				alert(data.data.message);
				return false;
			}
		},
		function (errorpl) {
			alert("Something went wrong!");
			return false;
		});
	}

	$scope.fetchWarehouses = function(){
		var promiseget = CRUDOperations.get("/api/warehouses/getWarehouseNames");
		return promiseget.then(function (data) {
			if(data.data.valid){
				$scope.listOfWarehouses = data.data.payload;
				return true;
			}else{
				alert(data.data.message);
				return false;
			}
		},
		function (errorpl) {
			alert("Something went wrong!");
			return false;
		});
	}
	$scope.fetchAttributes = function(){
		var promiseget = CRUDOperations.get("/api/attributes/get");
		return promiseget.then(function (data) {
			if(data.data.valid){
				$scope.listOfAttributes = data.data.payload;
				return true;
			}else{
				alert(data.data.message);
				return false;
			}
		},
		function (errorpl) {
			alert("Something went wrong!");
			return false;
		});
	}
	$scope.fetchAttributeValues = function(){
		$scope.listOfAttributeValues = [];
		$scope.attributeValue = [];
		if($scope.attribute){
			$scope.listOfAttributeValues = $scope.attribute.possibleValues;
		}
	}

	$scope.resetAttributeForm = function(){
		$scope.attribute = null;
		$scope.listOfAttributeValues = [];
		$scope.attributeValue = [];
	}

	$scope.addAttributeToList = function(){
		
		//assuming this will called only when a attribute value is selected
		isValid = true;
		
		if(isValid){
			var attribute = {
					attribute: $scope.attribute,
					value: $scope.attributeValue
			}
			if($scope.isEditingAttribute){
				for(var i = 0; i < $scope.currentItem.itemAttributes.length; i++){
					if($scope.currentItem.itemAttributes[i].attribute.name == $scope.attribute.name){
						$scope.currentItem.itemAttributes[i].value = $scope.attributeValue;
						break;
					}
				}
				$scope.isEditingAttribute = false;		
			}else{
				$scope.currentItem.itemAttributes.push(attribute);
			}
			
			$scope.resetAttributeForm();			
		}
	}
	
	$scope.editAttribute = function(attribute){
		$scope.attribute = attribute.attribute;
		$scope.fetchAttributeValues();
		$scope.attributeValue = attribute.value;
		$scope.isEditingAttribute = true;
	}


	$scope.saveItem = function(isValid){
		if(isValid){
			if(!$scope.isUpdateForm){
				if($scope.defaultWarehouse){
					var initialQuantity = {
							warehouseName: $scope.defaultWarehouse.name,
							quantityInHand: $scope.initialQuantity,
							quantityAvailable: $scope.initialQuantity
					}
					
					$scope.currentItem.itemCount.push(initialQuantity);
				}
			}

			var api, successMsg;
			if($scope.isUpdateForm){
				api = "/api/items/edit/"+$scope.currentItem.id;
				successMsg = "Item Updated!"
			}else{
				api = "/api/items/create";
				successMsg = "Item Created!"
			}
			var promisePost = CRUDOperations.post($scope.currentItem, api);
			var result = promisePost.then(function (data) {
				if (data.data.valid) {
					$scope.currentItem = data.data.payload;
					$scope.isUpdateForm = true;
					
					$location.path('/items/edit/' + $scope.currentItem.id);
				}
				alert(data.data.message);
			},
			function (errorpl) {
				alert("Error!");
			});
		}
	}
	
	$scope.addStorageLocation = function(){
		if(!$scope.currentItem.itemStorageLocations){
			$scope.currentItem.itemStorageLocations = [];
		}
		$scope.currentItem.itemStorageLocations.push($scope.location);
		$scope.location = {};
	}
	
	$scope.resetStorageLocationForm = function(){
		$scope.location = {};
	}
	
	$scope.resetInventoryLevelForm = function(){
		$scope.count = {};
	}
	
	$scope.warehouseSelectEvent = function(){
		for(var i = 0; i < $scope.currentItem.itemCount.length; i++){
			if($scope.count.warehouseName == $scope.currentItem.itemCount[i].warehouseName){
				alert("Warehouse already exists, please edit the quantity!");
				$scope.count = {};
				break;
			}
		}
	}
	
	$scope.setInventoryLevel = function(){
		if($scope.isEditingCount != -1){
			$scope.currentItem.itemCount[$scope.isEditingCount] = $scope.count;
		}else{
			if(!$scope.currentItem.itemCount)
				$scope.currentItem.itemCount = [];
						
			$scope.count.quantityInHand = 0;
			$scope.count.quantityAvailable = 0;
			$scope.count.quantityReserved = 0;
			$scope.count.quantityIssued = 0;
			$scope.count.quantityRestock = 0;
			$scope.currentItem.itemCount.push($scope.count);
		}
		$scope.count = {};
	}

	$scope.updateItemInventoryLevels = function(){
		var promisePost = CRUDOperations.post($scope.currentItem, "/api/items/updateInventoryLevels");
		var result = promisePost.then(function (data) {
			if (data.data.valid) {
				$scope.currentItem = data.data.payload;
				$location.path('/items/edit/' + $scope.currentItem.id);
			}
			alert(data.data.message);
		},
		function (errorpl) {
			alert("Error!");
		});	
	}
	
	$scope.editInventoryLevel = function(index){
		$scope.isEditingCount = index;
		$scope.count = jQuery.extend(true, {}, $scope.currentItem.itemCount[index]);
	}
});