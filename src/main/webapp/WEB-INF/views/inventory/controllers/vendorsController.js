inventoryApp.controller("vendorsController", function ($scope, $location, CRUDOperations) {

	$scope.listOfVendors = [];
	
	$scope.vendor = {
			id: 0,
			name: "",
			addressLine1: "",
			addressLine2: "",
			city: "",
			location: "",
			state: "",
			country: "",
			pincode: "",
			contactNo: "",
			contactPerson: ""
	}
	$scope.isUpdateForm = false;
	
	$scope.updateVendor = function(){
		
		if($scope.isUpdateForm){
	        var promisePost = CRUDOperations.put($scope.vendor, "/api/vendors/edit/"+$scope.vendor.id);
	        var result = promisePost.then(function (data) {
	            if (data.data.valid) {
	            	alert(data.data.message);
	            	$scope.isUpdateForm = false;
	            	$scope.resetForm();
	            	$('#createVendorModal').modal('hide');
	            }
	        },
			function (errorpl) {
				alert("Error!");
			});			
		}else{
	        var promisePost = CRUDOperations.post($scope.vendor, "/api/vendors/create");
	        var result = promisePost.then(function (data) {
	            if (data.data.valid) {
	        		$scope.listOfVendors.push(data.data.payload);
	        		alert('Vendor Created!');
	            	$('#createVendorModal').modal('hide');
	            }
	        },
			function (errorpl) {
				alert("Error!");
			});
		}
	

	}
	
	$scope.resetForm = function(){
		$scope.vendor = {
				id: 0,
				name: "",
				addressLine1: "",
				addressLine2: "",
				city: "",
				location: "",
				state: "",
				country: "",
				pincode: "",
				contactNo: "",
				contactPerson: ""
		}
	}
	
    $scope.fetchAllVendors = function () {
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
    
    $scope.openCreateVendorModal = function(){
    	$scope.resetForm();
    	$scope.isUpdateForm = false;
    	$('#createVendorModal').modal('show');
    }
   
    $scope.editVendor = function(vendor){
    	$scope.isUpdateForm = true;
    	$scope.vendor  = vendor;
    	$('#createVendorModal').modal('show');
    }
});