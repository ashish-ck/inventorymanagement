inventoryApp.controller("dashboardController", function ($scope, $location, CRUDOperations, $window, purchaseOrderService) {
	$scope.listOfItems = [];
	$scope.selectAll = false;
	$scope.loadingOutOfStockItems = false;
	$scope.loadingWarningItems = false;
	$scope.loadingReorderItems = false;
	$scope.listOfReorderItems = [];
	$scope.listOfWarningItems = [];
	$scope.selectAllReorderItems = false;
	$scope.selectAllWarningItems = false;
	$scope.addToWarningPO = [];
	$scope.addToReorderPO = [];
	$scope.addToPO = [];
	$scope.returnExpectedKits = [];
	$scope.fetchInitData = function(){
		$scope.fetchOutOfStockItems();
		$scope.fetchReturnExpectedKits();
		$scope.fetchReorderItems();
	}
    $scope.fetchOutOfStockItems = function () {
    	var fromDate = moment().format("ddd, DD MMM YYYY HH:mm:ss [GMT]");
    	var toDate = moment(fromDate).add(3, 'days').format("ddd, DD MMM YYYY HH:mm:ss [GMT]");
    	$scope.loadingOutOfStockRItems = true;
        var promiseget = CRUDOperations.get("/api/kits/getOutOfStockItems?fromDate="+fromDate+"&toDate="+toDate);
        promiseget.then(function (data) {
        	if(data.data.valid){
        		$scope.listOfItems = data.data.payload;
        	}else{
        		alert(data.data.message);
        	}
    		$scope.loadingOutOfStockItems = false;
        },
      function (errorpl) {
        	alert("Error!");
        	$scope.loadingOutOfStockItems = false;
      });
    }
    
    $scope.fetchReturnExpectedKits = function () {
        var promiseget = CRUDOperations.get("/api/kits/getReturnExpectedKits");
        promiseget.then(function (data) {
        	if(data.data.valid){
        		$scope.returnExpectedKits = data.data.payload;
        	}else{
        		alert(data.data.message);
        	}
        },
      function (errorpl) {
        	alert("Error!");
        	$scope.loadingOutOfStockItems = false;
      });
    }
    
    $scope.fetchReorderItems = function () {
    	$scope.loadingReorderItems = true;
        var promiseget = CRUDOperations.get("/api/items/getReorderItems");
        promiseget.then(function (data) {
        	if(data.data.valid){
        		var reorderItems = data.data.payload;
        		for(var i = 0; i < reorderItems.length; i++){
        			var brand = "";
        			for(var k = 0; k < reorderItems[i].itemAttributes.length; k++){
    					if(reorderItems[i].itemAttributes[k].attribute.name == "Brand"){
    						brand = reorderItems[i].itemAttributes[k].value[0];
    						break;
    					}
    				}
        			for(var j = 0; j < reorderItems[i].itemCount.length; j++){
        				var itemView = {
        					itemCode: reorderItems[i].code,
        					description: reorderItems[i].description,
        					/*quantityInHand: reorderItems[i].quantityInHand,*/
        					category: reorderItems[i].category.name,
        					warningPoint: reorderItems[i].itemCount[j].warningPoint,
        					reorderPoint: reorderItems[i].itemCount[j].reorderPoint,
        					warehouse: reorderItems[i].itemCount[j].warehouseName,
        					brand: brand,
        					/*quantity: reorderItems[i].itemCount[j].reorderQuantity,*/
        					toUpdateStatus: false
        				}
        				if(itemView.reorderPoint > 0)
        					$scope.listOfReorderItems.push(itemView);
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
    
    $scope.selectAllEvent = function(value){
    	$scope.selectAll = value;
    	for(var i =0; i < $scope.listOfItems.length; i++){
    		$scope.listOfItems[i].toUpdateStatus = $scope.selectAll;
    	}
    	$scope.itemSelectEvent();
    }
    $scope.selectAllWarningItemsEvent = function(value){
    	$scope.selectAllWarningItems = value;
    	for(var i =0; i < $scope.listOfWarningItems.length; i++){
    		$scope.listOfWarningItems[i].toUpdateStatus = $scope.selectAllWarningItems;
    	}
    	$scope.warningItemSelectEvent();
    }
    $scope.selectAllReorderItemsEvent = function(value){
    	$scope.selectAllReorderItems = value;
    	for(var i =0; i < $scope.listOfReorderItems.length; i++){
    		$scope.listOfReorderItems[i].toUpdateStatus = $scope.selectAllReorderItems;
    	}
    	$scope.reorderItemSelectEvent();
    }
    
    $scope.itemSelectEvent = function(){
    	$scope.addToPO = [];
    	for( var i = 0; i < $scope.listOfItems.length; i++){
    		if($scope.listOfItems[i].toUpdateStatus){
    			$scope.addToPO.push($scope.listOfItems[i]);
    		}
    	}
    }
    $scope.reorderItemSelectEvent = function(){
    	$scope.addToReorderPO = [];
    	for( var i = 0; i < $scope.listOfReorderItems.length; i++){
    		if($scope.listOfReorderItems[i].toUpdateStatus){
    			$scope.addToReorderPO.push($scope.listOfReorderItems[i]);
    		}
    	}
    }
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
    $scope.warningItemSelectEvent = function(){
    	$scope.addToWarningPO = [];
    	for( var i = 0; i < $scope.listOfWarningItems.length; i++){
    		if($scope.listOfWarningItems[i].toUpdateStatus){
    			$scope.addToWarningPO.push($scope.listOfWarningItems[i]);
    		}
    	}
    }
    $scope.autoCreatePO = function(){
    	purchaseOrderService.setData($scope.addToPO);
    	$location.path("purchaseOrders/create/");
    }
    
    $scope.autoCreateWarningPO = function(){
    	purchaseOrderService.setData($scope.addToWarningPO);
    	$location.path("purchaseOrders/create/");
    }
    
    $scope.autoCreateReorderPO = function(){
    	purchaseOrderService.setData($scope.addToReorderPO);
    	$location.path("purchaseOrders/create/");
    }
    $scope.viewDetails = function(kitNo) {
		$window.open($location.$$protocol + "://" + $location.$$host + ":" + $location.$$port + "/#/" + "kits/view/" + kitNo);
	}
});