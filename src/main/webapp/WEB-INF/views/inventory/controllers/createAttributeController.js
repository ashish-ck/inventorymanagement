inventoryApp.controller("createAttributeController", function ($scope, $location, CRUDOperations) {
	
	
	$scope.listOfUnits = [];
	
	$scope.fetchInitData = function(){
		if(!$scope.fetchUnits()){
			alert('Error');
		}
	}
	
	$scope.createNewAttribute = function(isValid){
		if(isValid){
			var newAttribute = {
					id: 0,
					name: $scope.name,
					unit: $scope.unit,
					possibleValues: $scope.possibleValues
			}
	        var promisePost = CRUDOperations.post(newAttribute, "/api/attributes/create");
	        var result = promisePost.then(function (data) {
	            if (data.statusText == "Created") {
	            	alert('Attribute Created!');
	            	$scope.resetForm();
	            	$('#createAttributeModal').modal('hide');
	            }
	        },
			function (errorpl) {
				alert("Error!");
			});			
		}
	}
	
	$scope.resetForm = function(){
		$scope.id = 0;
		$scope.name = "";
		$scope.unit = "";
	}
	
	$scope.fetchUnits = function(){
		var promiseget = CRUDOperations.get("/api/units/get");
		return promiseget.then(function (data) {
			if(data.statusText == "OK"){
				$scope.listOfUnits = data.data;
				return true;
			}else{
				return false;
			}
		},
		function (errorpl) {
			return false;
		});
	}
	
});