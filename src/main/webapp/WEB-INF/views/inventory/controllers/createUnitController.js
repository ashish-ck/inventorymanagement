inventoryApp.controller("createUnitController", function ($scope, $location, CRUDOperations) {
	$scope.createNewUnit = function(){
		var newUnit = {
				id: 0,
				name: $scope.name,
				notation: $scope.notation,
				description: $scope.description
		}
        var promisePost = CRUDOperations.post(newUnit, "/api/units/create");
        var result = promisePost.then(function (data) {
            if (data.data.valid) {
            	$scope.resetForm();
            	$('#createUnitModal').modal('hide');
            }
            alert(data.data.message);
        },
		function (errorpl) {
			alert("Error!");
		});
	}
	$scope.resetForm = function(){
		$scope.id = 0;
		$scope.name = "";
		$scope.notation = "";
		$scope.description = "";
	}
});