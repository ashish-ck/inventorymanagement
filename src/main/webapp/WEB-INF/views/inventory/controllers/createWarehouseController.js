inventoryApp.controller("createWarehouseController", function ($scope, $location, CRUDOperations) {

	$scope.warehouse = {
			id: 0,
			name: "",
			addressLine1: "",
			addressLine2: "",
			city: "",
			location: "",
			state: "",
			country: "",
			pincode: ""
	}
	$scope.isUpdateForm = false;
	
	$scope.createNewWarehouse = function(){
	
        var promisePost = CRUDOperations.post($scope.warehouse, "/api/warehouses/create");
        var result = promisePost.then(function (data) {
            if (data.data.valid) {
            	$scope.resetForm();
            	$('#createWarehouseModal').modal('hide');
            }
            alert(data.data.message);
        },
		function (errorpl) {
			alert("Error!");
		});
	}
	
	$scope.resetForm = function(){
		$scope.warehouse = {};
	}
	
});
	