inventoryApp.controller("unitsController", function ($scope, $location, CRUDOperations) {

	$scope.listOfUnits = [];

	$scope.unit = {
			id: 0,
			name: "",
			notation: "",
			description: ""
	}

	$scope.isUpdateForm = false;

	$scope.createNewUnit = function(isValid){
		if(isValid){
			var promisePost = CRUDOperations.post($scope.unit, "/api/units/create");
			var result = promisePost.then(function (data) {
				if (data.data.valid) {
					if($scope.isUpdateForm){
						$scope.isUpdateForm = false;
					}else{
						$scope.listOfUnits.push(data.data.payload);
					}
					alert(data.data.message);
					$scope.resetForm();
					$('#createUnitModal').modal('hide');
				}
			},
			function (errorpl) {
				alert("Error!");
			});
		}
	}
	$scope.resetForm = function(){
		$scope.unit = {
				id: 0,
				name: "",
				notation: "",
				description: ""
		}
	}

	$scope.fetchAllUnits = function () {
		var promiseget = CRUDOperations.get("/api/units/get");
		promiseget.then(function (data) {
			if(data.data.valid){
				$scope.listOfUnits = data.data.payload;
			}else{
				alert(data.data.message);
			}
		},
		function (errorpl) {
			alert("Error!");
		});
	}

	$scope.openCreateUnitModal = function (){
		$scope.resetForm();
		$('#createUnitModal').modal('show');
	}

	$scope.editUnit = function(unit){
		$scope.isUpdateForm = true;
		$scope.unit  = unit;
		$('#createUnitModal').modal('show');
	}

});