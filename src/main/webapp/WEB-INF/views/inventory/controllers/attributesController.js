inventoryApp.controller("attributesController", function ($scope, $location, CRUDOperations) {

	$scope.listOfAttributes = [];
	$scope.possibleValues = [];
	
	$scope.listOfUnits = [];
	$scope.isUpdateForm = false;
	$scope.currentAttribute = {
			id: 0,
			name: null,
			unit: null,
			possibleValues: null
	};
	
	$scope.fetchInitData = function(){
		if(!$scope.fetchUnits()){
			alert('Error');
		}
	}
	
	$scope.createNewAttribute = function(isValid){
		if(isValid){
			$(".updating").button('loading');
	        var promisePost = CRUDOperations.post($scope.currentAttribute, "/api/attributes/create");
	        var result = promisePost.then(function (data) {
	            if (data.data.valid) {
	            	if(!$scope.isUpdateForm){
	            		$scope.listOfAttributes.push(data.data.payload);
	            		$(".updating").button('reset');
	            	}
	            	alert(data.data.message);
	            	$scope.resetForm();
	            	$('#createAttributeModal').modal('hide');
	            }
	        },
			function (errorpl) {
	        	$scope.isUpdatingAttribute = false;
				alert("Error!");
			});			
		}
	}
	
	$scope.resetForm = function(){
		$(".updating").button('reset');
		$scope.currentAttribute = {
				id: 0,
				name: null,
				unit: null,
				possibleValues: null
		};
	}
	
	$scope.fetchUnits = function(){
		var promiseget = CRUDOperations.get("/api/units/get");
		return promiseget.then(function (data) {
			if(data.data.valid){
				$scope.listOfUnits = data.data.payload;
			}else{
				alert(data.data.message);
			}
		},
		function (errorpl) {
			alert("Something went wrong");
		});
	}
	
    $scope.fetchAllAttributes = function () {
        var promiseget = CRUDOperations.get("/api/attributes/get");
        promiseget.then(function (data) {
        	if(data.data.valid){
        		$scope.listOfAttributes = data.data.payload;
        	}else{
        		alert(data.data.message);
        	}
        },
      function (errorpl) {
        	alert("Error!");
      });
    }
    
    $scope.openCreateAttributeModal = function (){
    	$scope.isUpdateForm = false;
    	$scope.resetForm();
		$('#createAttributeModal').modal('show');
    }
    
    $scope.editAttributeModal = function(attribute){
    	$scope.currentAttribute = attribute;
    	$scope.isUpdateForm = true;
    	$('#createAttributeModal').modal('show');
    }
	
});