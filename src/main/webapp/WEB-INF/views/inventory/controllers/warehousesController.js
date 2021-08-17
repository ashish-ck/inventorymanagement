inventoryApp.controller("warehousesController", function ($scope, $location, CRUDOperations) {

	$scope.listOfWarehouses = [];
	
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
	$scope.isUpdateForm = false;
	
	$scope.createNewWarehouse = function(isValid){
	
		if(isValid){
			$('#btnUpdate').button('loading');
	        var promisePost = CRUDOperations.post($scope.warehouse, "/api/warehouses/create");
	        var result = promisePost.then(function (data) {
	            if (data.data.valid) {
	            	if($scope.isUpdateForm){
	            		$('#btnUpdate').button('reset');
	            		$scope.isUpdateForm = false;
	            	}else{
	            		$scope.listOfWarehouses.push(data.data.payload);
	            		$('#btnUpdate').button('reset');
	            	}
	            	alert(data.data.message);
	            	$scope.resetForm();
	            	$('#createWarehouseModal').modal('hide');
	            }
	        },
			function (errorpl) {
				alert("Error!");
			});			
		}
	}
	
	$scope.resetForm = function(){
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
	
    $scope.fetchAllWarehouses = function () {
        var promiseget = CRUDOperations.get("/api/warehouses/get");
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
    
    $scope.openCreateWarehouseModal = function(){
    	$scope.resetForm();
    	$('#createWarehouseModal').modal('show');
    }
   
    $scope.editWarehouse = function(warehouse){
    	$scope.isUpdateForm = true;
    	$scope.warehouse  = warehouse;
    	$('#createWarehouseModal').modal('show');
    }
});