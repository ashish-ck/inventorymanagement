inventoryApp.controller("itemTreeController", function ($scope, $http, $location, CRUDOperations, DTOptionsBuilder, DTColumnBuilder, $window, localStorageService) {
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
			"2200": "Purchase Open"
			};
	$scope.displayTable = false;
	$scope.fetchInitData = function(){
		$scope.fetchAllKits();
	}
	$scope.loc = {warehouse : 0, shelf: 0, rack: 0, crate: 0};
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
	$scope.listOfItems = [];
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
	$scope.openKitOptions = DTOptionsBuilder.newOptions()
	.withOption('ajax', function(data, callback, settings){
		var warehouseId = $scope.loc.warehouse, shelfId, rackId, crateId;
		shelfId = $scope.loc.shelf;
		rackId = $scope.loc.rack;
		crateId = $scope.loc.crate;
		$scope.data = data;
		var promisePost = CRUDOperations.post($scope.data, '/api/warehouses/warehouseItems/' + warehouseId + "/" + shelfId + "/" + rackId + "/" + crateId);
		$scope.isLoadingData = true;
		promisePost.then(function(data){
			$scope.listOfItems = data.data.data;
			$scope.exportItems($scope.listOfItems);
			for (var i = 0;i < $scope.listOfItems.length;i++){
				$scope.listOfItems[i].statusText = $scope.kitStatus($scope.listOfItems[i].status);
				if(!$scope.listOfItems[i].shelf){
					$scope.listOfItems[i].shelf = {name:"", id: 0};
				}
				if(!$scope.listOfItems[i].rack){
					$scope.listOfItems[i].rack = {name:"", id: 0};
				}
				if(!$scope.listOfItems[i].crate){
					$scope.listOfItems[i].crate = {name:"", id: 0};
				}
			}
			$scope.isLoadingData = false;
			callback(data.data);
		});
	})
	.withOption('createdRow', function (row, request, index) {
		request.statusText = $scope.kitStatus(request.status);
		$('td', row).eq(0).html(index + 1);
		$('td', row).eq(1).html('<a href="javascript:void(0);">' + request.barcode + '</a>');
		$('td', row).eq(1).on("click", function(o){
			$scope.item = request;
			item = request;
			if(item.shelf){
				$scope.item.shelf = {name: item.shelf.name, id: item.shelf.id};
				$scope.getShelves(item.warehouse.id);
			}else
				$scope.item.shelf = {name: "s", id: 0};
			if(item.rack){
				$scope.item.rack = {name: item.rack.name, id: item.shelf.id};
				$scope.getRacks(item.rack.id);
			}
			else
				$scope.item.rack = {name: "r", id: 0};
			if(item.crate){
				$scope.item.crate = {name: item.crate.name, id: item.crate.id};
				$scope.getCrates(item.crate.id);
			}
			else
				$scope.item.crate = {name: "c", id: 0};
	        $("#createWarehouseItemModal").modal('show');
	        $('#itemModal').modal('show');
		});
		$('td', row).eq(2).html(request.itemCode);
		var noStorageLocation = false;
		if(!request.shelf || !request.rack || !request.crate){
			noStorageLocation = true;
		}else{
			if(request.shelf.name == 'default' || request.rack.name == 'default' || request.crate.name == 'default'){
				noStorageLocation = true;
			}else if(request.shelf.name == '' || request.rack.name == '' || request.crate.name == ''){
				noStorageLocation = true;
			}
		}
		if(request.shelf && request.shelf.name){
			$('td', row).eq(3).html(request.shelf.name);
		}
		else
			$('td', row).eq(3).html('');
		if(request.rack && request.rack.name){
			$('td', row).eq(4).html(request.rack.name);
		}
		else
			$('td', row).eq(4).html('');
		if(request.crate && request.crate.name){
			$('td', row).eq(5).html(request.crate.name);
		}
		else
			$('td', row).eq(5).html('');
		$('td', row).eq(6).html(request.statusText);
	})
	.withDataProp('data')
	.withOption('processing', true)
	.withOption('serverSide', true)
	.withPaginationType('full_numbers')
	.withDisplayLength(10)
	$scope.itemsColumns = [DTColumnBuilder.newColumn("status", "Index"),
		DTColumnBuilder.newColumn("barcode", "Barcode"),
		DTColumnBuilder.newColumn("itemCode", "Item Code"),
		DTColumnBuilder.newColumn("shelf.name", "Shelf"),
		DTColumnBuilder.newColumn("rack.name", "Rack"),
		DTColumnBuilder.newColumn("crate.name", "Crate"),
		DTColumnBuilder.newColumn("status", "Status")]
	$scope.finalCategories = [];
	$scope.listOfShelves = [];
	$scope.listOfRacks = [];
	$scope.listOfCrates = [];
	$scope.rootCategory = [];
	$scope.listOfItems = [];
	$scope.isAllWarehousesSelected = false;
	$scope.isWarehouseSelected = false;
	$scope.isShelfSelected = false;
	$scope.isRackSelected = false;
	$scope.isCrateSelected = false;
	$scope.selectedNode = null;
	$scope.item = {};
	$scope.selectedShelf = null;
	$scope.selectedRack = null;
	$scope.selectedCrate = null;
	$scope.selectUnlocated = false;
	$scope.exportableItems = [];
	//$scope.selectUnlocated.value = false;
	$scope.warehouse = {
			id: 0,
			name: "",
			addressLine1: "",
			addressLine2: "",
			city: "",
			location: "",
			state: "",
			country: "",
			pincode: "",
			code: "",
			motherWarehouse: false,
			motherWarehouseCode: ""
	}
	$scope.shelf = {
			description: "",
			name: "",			
	}
	$scope.rack = {
			description: "",
			name: "",			
	}
	$scope.crate = {
			description: "",
			name: "",			
	}
	$scope.listOfItems = [];
	$scope.showRow = function(bool){
		alert();
		return $scope.selectUnlocated == true?!(item.shelf && item.rack && item.crate) || 
				(item.shelf.name == 'default' && item.rack.name == 'default' && item.crate.name == 'default'):true
	}
	$scope.updateItem = function(item){
		if(item.status.toString() == "600"){
			alert("Item is issued.");
			return;
		}
		var promisePost = CRUDOperations.post(item, "/api/items/updateItemStorageLocationByBarcode");
		if(item != undefined){
			if(item.shelf != undefined && item.rack != undefined  && item.crate != undefined){
				var result = promisePost.then(function (data) {
		            if (data.data.valid) {
		            	$('#createWarehouseItemModal').modal('hide');
		            }
		        },
				function (errorpl) {
					alert("Error!");
				});
			}
		}
	}
	$scope.isLoading = function () {
		return $http.pendingRequests.length !== 0;
	};
	$scope.exportItems = function(items){
		$scope.exportableItems = [];
		$scope.exportableItems.push(["Barcode","Item Code","Unit Price", "Wholesale Price","Warehouse","Shelf","Rack","Crate", "Status"]);
		for (var i = 0;i < items.length;i++){
			if(items[i].status != 100){
				var _item1 = [];
				_item1.push(items[i].barcode);
				_item1.push(items[i].itemCode);
				_item1.push(items[i].unitPrice);
				_item1.push(items[i].wholesalePrice);
				_item1.push(items[i].warehouse.name);
				
				if(items[i].shelf)
					_item1.push(items[i].shelf.name);
				else
					_item1.push("");
				if(items[i].rack)
					_item1.push(items[i].rack.name);
				else
					_item1.push("");
				if(items[i].crate)
					_item1.push(items[i].crate.name);
				else
					_item1.push("");
				_item1.push($scope.itemStatuses[items[i].status.toString()]);
				$scope.exportableItems.push(_item1);
			}
		}
	}
	$scope.JSONToCSVConvertor = function(JSONData, ReportTitle, ShowLabel) {
	    var arrData = typeof JSONData != 'object' ? JSON.parse(JSONData) : JSONData;
	    var CSV = '';    
	    //CSV += ReportTitle + '\r\n\n';
	    if (ShowLabel) {
	        var row = "";
	        for (var index in arrData[0]) {
	            row += index + ',';
	        }
	        row = row.slice(0, -1);
	        //CSV += row + '\r\n';
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
		$scope.JSONToCSVConvertor($scope.exportableItems, "Barcode Items", true);
    };
	$scope.isLoadingWarehouses = false;
	$scope.listOfWarehouses = [];
    $scope.isUpdateForm = false;
    $scope.editWarehouse = function(warehouse){
	    	angular.forEach($scope.listOfWarehouses, function(value, key) {
	    		if(value.id == warehouse.details.id){
	    			$scope.warehouse  = value;
	    		 }
	    	  });
    		$scope.isUpdateForm = true;
        	$('#createWarehouseModal').modal('show');
    }
	$scope.createNewWarehouse = function(isValid){
		if(isValid){
			$('#btnUpdate').button('loading');
	        var promisePost = CRUDOperations.post($scope.warehouse, "/api/warehouses/create");
	        $scope.listOfWarehouses = [];
	        var result = promisePost.then(function (data) {
	            if (data.data.valid) {
	            	if($scope.isUpdateForm){
	            		$('#btnUpdate').button('reset');
	            		$scope.isUpdateForm = false;
	            	}else{
	            		$scope.listOfWarehouses.push(data.data.payload);
	            		$('#btnUpdate').button('reset');
	            	}
	            	$scope.fetchAllWarehouse();
	            	alert(data.data.message);
	            	$scope.resetWarehouse();
	            	$('#createWarehouseModal').modal('hide');
	            }
	        },
			function (errorpl) {
				alert("Error!");
			});			
		}
	}
	$scope.resetWarehouse = function(){
		$scope.warehouse = {
				id: 0,
				name: "",
				addressLine1: "",
				addressLine2: "",
				city: "",
				location: "",
				state: "",
				country: "",
				pincode: "",
				code: "",
				motherWarehouse: false,
				motherWarehouseCode: ""
		}
	}
	
	
	$scope.openAddWarehouseModal = function() {
		    	$scope.resetWarehouse();
		    	$('#createWarehouseModal').modal('show');
		    
     }
$scope.createNewShelf = function(isValid){
	var warehouseId = $scope.selectedNode.details.id;
	if(isValid){
		if($scope.shelf){
			if(!$scope.shelf.name.startsWith("S")){
				alert("Shelf name should start with S.");
				return;
			}
		}
		$('#btnUpdate').button('loading');
        var promisePost = CRUDOperations.post($scope.shelf, "/api/shelves/warehouse/"+ warehouseId +"/create");
        var result = promisePost.then(function (data) {
            if (data.data.valid) {
            	$scope.fetchAllWarehouse();
            	$('#btnUpdate').button('reset');
            	alert("Shelf Created");
            	$scope.resetShelf();
            	$('#createShelfModal').modal('hide');
            }
        },
		function (errorpl) {
			alert("Error!");
		});			
	}
}
	$scope.resetShelf = function(){
		$scope.shelf = {
				description: "",
				name: "",			
		}
	}
	$scope.openAddShelfModal = function() {
		$scope.resetShelf();
    	$('#createShelfModal').modal('show');
    }
	$scope.createNewRack = function(isValid){
		var shelfId = $scope.selectedNode.details.id;
		if($scope.rack){
			if(!$scope.rack.name.startsWith("R")){
				alert("Rack name should start with R.");
			}
		}
		if(isValid){
			$('#btnUpdate').button('loading');
	        var promisePost = CRUDOperations.post($scope.rack, "/api/shelves/"+ shelfId +"/addRack");
	        var result = promisePost.then(function (data) {
	            if (data.data.valid) {
	            	$scope.fetchAllWarehouse();
	            	$('#btnUpdate').button('reset');
	            	alert("Rack Created");
	            	$scope.resetShelf();
	            	$('#createRackModal').modal('hide');
	            }
	        },
			function (errorpl) {
				alert("Error!");
			});			
		}
	}
	$scope.resetRack = function(){
		$scope.rack = {
				description: "",
				name: "",			
		}
	}
	$scope.openAddRackModal = function() {
		$scope.resetRack();
    	$('#createRackModal').modal('show');
    }
	$scope.createNewCrate = function(isValid){
		var rackId = $scope.selectedNode.details.id;
		if(isValid){
			if($scope.crate){
				if(!$scope.crate.name.startsWith("C")){
					alert("Crate name should start with C.");
					return;
				}
			}
			$('#btnUpdate').button('loading');
	        var promisePost = CRUDOperations.post($scope.crate, "/api/racks/"+ rackId +"/addCrate");
	        var result = promisePost.then(function (data) {
	            if (data.data.valid) {
	            	$scope.fetchAllWarehouse();
	            	$('#btnUpdate').button('reset');
	            	alert("Crate Created");
	            	$scope.resetShelf();
	            	$('#createCrateModal').modal('hide');
	            }
	        },
			function (errorpl) {
				alert("Error!");
			});			
		}
	}
	$scope.resetCrate = function(){
		$scope.crate = {
				description: "",
				name: "",			
		}
	}
	$scope.openAddCrateModal = function() {
		$scope.resetCrate();
    	$('#createCrateModal').modal('show');
    }
	$scope.openAddItemModal = function() {
        alert("add item");
    }
	$scope.getShelves = function(id){
		var promiseget = CRUDOperations.get("/api/warehouses/"+ id +"/getShelves");
		promiseget.then(function (data) {
			if(data.data.valid){
				$scope.listOfShelves = data.data.payload;
				for (var i = 0;i < data.data.payload.length;i++){
					data.data.payload[i].type = "shelf";
				}
			}else{
				alert(data.data.message);
			}
		},
		function (errorpl) {
			alert("Error!");
		});
	}
	$scope.getRacks = function(id){
		var promiseget = CRUDOperations.get("/api/shelves/"+ id +"/getRacks");
		promiseget.then(function (data) {
			if(data.data.valid){
				$scope.listOfRacks = data.data.payload;
				for (var i = 0;i < data.data.payload.length;i++){
					data.data.payload[i].type = "rack";
				}
			}else{
				alert(data.data.message);
			}
		},
		function (errorpl) {
			alert("Error!");
		});
	}
	$scope.getCrates = function(id){
		var promiseget = CRUDOperations.get("/api/racks/"+ id +"/getCrates");
		promiseget.then(function (data) {
			if(data.data.valid){
				$scope.listOfCrates = data.data.payload;
				for (var i = 0;i < data.data.payload.length;i++){
					data.data.payload[i].type = "crate";
				}
			}else{
				alert(data.data.message);
			}
		},
		function (errorpl) {
			alert("Error!");
		});
	}
	$scope.getItemsForTreeNode = function (node){
		$scope.selectedNode = node;
		if(node != null){
			if(node.details.type == "shelf"){
				$scope.loc.shelf = node.details.id;
				$scope.isAllWarehousesSelected = false;
				$scope.isWarehouseSelected = false;
				$scope.isShelfSelected = true;
				$scope.isRackSelected = false;
				$scope.isCrateSelected = false;
				$scope.loc = {warehouse: 0, shelf: node.details.id, rack: 0, crate : 0};
				var warehouseId = $scope.loc.warehouse;
				var shelfId = $scope.loc.shelf;
				var rackId = $scope.loc.rack;
				var crateId = $scope.loc.crate;
				$scope.openKitTblInstance.reloadData(null, false);
			}
		    else if(node.details.type == "crate"){
		    	$scope.loc.crate = node.details.id;
		    	$scope.isAllWarehousesSelected = false;
		    	$scope.isWarehouseSelected = false;
		    	$scope.isShelfSelected = false;
		    	$scope.isRackSelected = false;
		    	$scope.isCrateSelected = true;
		    	$scope.loc = {warehouse: 0, shelf: 0, rack: 0, crate : node.details.id};
		    	var warehouseId = $scope.loc.warehouse;
				var shelfId = $scope.loc.shelf;
				var rackId = $scope.loc.rack;
				var crateId = $scope.loc.crate;
				$scope.openKitTblInstance.reloadData(null, false);
			}
		    else if(node.details.type == "rack"){
		    	$scope.loc.rack = node.details.id;
		    	$scope.isAllWarehousesSelected = false;
		    	$scope.isWarehouseSelected = false;
		    	$scope.isShelfSelected = false;
		    	$scope.isRackSelected = true;
		    	$scope.isCrateSelected = false;
		    	$scope.loc = {warehouse: 0, shelf: 0, rack: node.details.id, crate : 0};
		    	var warehouseId = $scope.loc.warehouse;
				var shelfId = $scope.loc.shelf;
				var rackId = $scope.loc.rack;
				var crateId = $scope.loc.crate;
				$scope.openKitTblInstance.reloadData(null, false);
		    }
		    else if(node.details.type == "warehouses"){
		    	$scope.loc = {warehouse: 0, shelf: 0, rack: 0, crate : 0};
		    	$scope.loc.warehouse = 0;
		    	$scope.loc.shelf = 0;
		    	$scope.loc.rack = 0;
		    	$scope.loc.crate = 0;
		    	$scope.isAllWarehousesSelected = true;
		    	$scope.isWarehouseSelected = false;
		    	$scope.isShelfSelected = false;
		    	$scope.isRackSelected = false;
		    	$scope.isCrateSelected = false;
		    	$scope.openKitTblInstance.reloadData(null, false);
		    	var promiseget = CRUDOperations.get("/api/items/getAllBarcodeItems");
				promiseget.then(function (data) {
					if(data.data.valid){
						$scope.listOfItems = data.data.payload;
						for (var i = 0;i < $scope.listOfItems.length;i++){
							$scope.listOfItems[i].statusText = $scope.itemStatuses[$scope.listOfItems[i].status.toString()];
						}
						$scope.exportItems($scope.listOfItems);
					}else{
						alert(data.data.message);
					}
				},
				function (errorpl) {
					alert("Error!");
				});
		    }
		    else {
		    	$scope.loc.warehouse = node.details.id;
		    	$scope.loc.shelf = 0;
		    	$scope.loc.rack = 0;
		    	$scope.loc.crate = 0;
		    	$scope.isAllWarehousesSelected = false;
		    	$scope.isWarehouseSelected = true;
		    	$scope.isShelfSelected = false;
		    	$scope.isRackSelected = false;
		    	$scope.isCrateSelected = false;
		    	var warehouseId = $scope.loc.warehouse;
				var shelfId = $scope.loc.shelf;
				var rackId = $scope.loc.rack;
				var crateId = $scope.loc.crate;
				$scope.openKitTblInstance.reloadData(null, false);
			}
		}
	}
	$scope.shelfSelectEvent = function(shelf){
		$scope.item.shelf = shelf;
		$scope.listOfRacks = shelf.racks;
		$scope.loc.shelf = shelf.id;
		$scope.loc.rack = 0;
		$scope.loc.crate = 0;
	}
	$scope.rackSelectEvent = function(rack){
		$scope.item.rack = rack;
		$scope.listOfCrates = rack.crates;
		$scope.loc.rack = rack.id;
		$scope.loc.crate = 0;
	}
	$scope.crateSelectEvent = function(crate){
		$scope.item.crate  = crate;
	}
	 $scope.delete = function(data) {
	        data.nodes = [];
	 };
	 $scope.showHideUnlocated = function(item){
		 //selectUnlocated.value?(!item.shelf||!item.rack||!item.crate):true
		 if(!$scope.selectUnlocated.value){
			 return true;
		 }
		 if(!item.shelf || !item.rack || !item.crate){
			 return true;
		 }
		 return false;
	 }
	 $scope.add = function(data, newvalue) {
	    	var isCollapsed = newvalue.collapsed;
	        var newName = newvalue.details.name ;
	        var value = {
	        		"id" : newvalue.details.id,
					"name" : newvalue.details.name,
					"description" : newvalue.details.description,
					"type" : newvalue.details.type,
	        }
	        data.children.push({details: value,collapsed:isCollapsed,children: []});
	    };
	    $scope.tree = [{details: {
			"id" : 1,
			"name" : "Warehouses",
			"description" : "Warehouses",
	 }, children: []}];
	$scope.fetchAllWarehouse = function () {
		var promiseget = CRUDOperations.get("/api/inventory/getUserDetails");
		promiseget.then(function (data) {
			if(data.data.warehouse){
				$scope.userDetails = data.data;
				$scope.isLoadingWarehouses = true;
				var promiseget1 = CRUDOperations.get("/api/warehouses/get");
				$scope.listOfWarehouses = [];
				promiseget1.then(function (data) {
					if(data.data.valid){
						var warehouses = data.data.payload;
						$scope.listOfWarehouses  = [];
						for (var i = 0;i < warehouses.length;i++){
							warehouses[i].type = "warehouse";
							if(warehouses[i].code == $scope.userDetails.warehouse || $scope.userDetails.admin){
								$scope.listOfWarehouses.push(warehouses[i]);
							}
							$scope.loc.warehouse = warehouses[i].id; 
						}
						$scope.isLoadingWarehouses = false;
						$scope.initTree();	
					}else{
						alert(data.data.message);
					}
				},
				function (errorpl) {
					$scope.isLoadingWarehouses = false;
					alert("Error!");
				});
			}else{
			}
		},
		function (errorpl) {
			alert("Error!");
		});
	}
	$scope.fetchUserData = function(){
		var promiseget = CRUDOperations.get("/api/inventory/getUserDetails");
		promiseget.then(function (data) {
			if(data.data.valid){
				$scope.userDetails = data.data.payload;
			}else{
			}
		},
		function (errorpl) {
			alert("Error!");
		});
	}
	$scope.initTree = function () {
		$scope.tree = [{details: {
			"id" : 1,
			"name" : "Warehouses",
			"description" : "Warehouses",
			"type" : "warehouses"
	 }, children: []}];
		//adding warehouse in tree
		angular.forEach($scope.listOfWarehouses, function(value, key) {
			var value = {
					"id" : value.id,
					"name" : value.name,
					"description" : value.name,
					"type" : "warehouse"
			}
			var warehouse = {
					details: value,
					collapsed : true,
					children: []
			}
			angular.forEach($scope.tree, function(value, key) {	
				$scope.add(value,warehouse);
			});
		});
		// adding shelves in warehouses in tree
		angular.forEach($scope.listOfWarehouses, function(value, key) {
			$scope.warehouseShelves = value.shelves;
			$scope.warehousename = value.name;
			angular.forEach($scope.warehouseShelves, function(value, key) {
			var value = {
					"id" : value.id,
					"name" : value.name,
					"description" : value.name,
					"type" : "shelf"
			}
			var shelf = {
					details: value,
					collapsed : true,
					children: []
			}
			angular.forEach($scope.tree, function(value, key) {
				angular.forEach(value.children, function(value, key) {
					if(value.details.name == $scope.warehousename){
						$scope.add(value,shelf);
					}
				});
				});
			});
		});
		// adding racks in shelves in warehouse in tree
		angular.forEach($scope.listOfWarehouses, function(value, key) {
			$scope.warehouseShelves = value.shelves;
			$scope.warehousename = value.name;
			angular.forEach($scope.warehouseShelves, function(value, key) {
				$scope.warehouseRacks = value.racks;
				$scope.shelfname = value.name;
					angular.forEach($scope.warehouseRacks, function(value, key) {
					var value = {
							"id" : value.id,
							"name" : value.name,
							"description" : value.name,
							"type" : "rack"
					}
					var rack = {
							details: value,
							collapsed : true,
							children: []
					}
					angular.forEach($scope.tree, function(value, key) {
						angular.forEach(value.children, function(value, key) {
							if(value.details.name == $scope.warehousename){
								angular.forEach(value.children, function(value, key) {
									 if(value.details.name == $scope.shelfname){
										 $scope.add(value,rack);
									 }
								});
							}
						});
						});
			        });
			});
		});
		// adding crates in shelves in warehouse in tree
		angular.forEach($scope.listOfWarehouses, function(value, key) {
			$scope.warehouseShelves = value.shelves;
			$scope.warehousename = value.name;
			angular.forEach($scope.warehouseShelves, function(value, key) {
				$scope.warehouseRacks = value.racks;
				$scope.shelfname = value.name;
					angular.forEach($scope.warehouseRacks, function(value, key) {
						$scope.warehouseCrates = value.crates;
						$scope.rackname = value.name;
						angular.forEach($scope.warehouseCrates, function(value, key) {
							var value = {
									"id" : value.id,
									"name" : value.name,
									"description" : value.name,
									"type" : "crate"
									
							}
							var crate = {
									details: value,
									collapsed : true,
									children: []
							}
							angular.forEach($scope.tree, function(value, key) {
								angular.forEach(value.children, function(value, key) {
									if(value.details.name == $scope.warehousename){
									angular.forEach(value.children, function(value, key) {
										if(value.details.name == $scope.shelfname){
											angular.forEach(value.children, function(value, key) {
												 if(value.details.name == $scope.rackname){
													 $scope.add(value,crate);
												 }
											});
										}	
									});
								}
								});
								});
						});
			        });
			});
		});
	}
});