inventoryApp.controller("createCategoryController", function ($scope, $location, CRUDOperations) {
	
	$scope.listOfParentCategories = [];
	
	$scope.fetchInitData = function () {
		var promiseget = CRUDOperations.get("/api/categories/get");
		promiseget.then(function (data) {
			if(data.statusText == "OK"){
				$scope.listOfParentCategories = data.data;
			}else{
				alert("Something went wrong!");
			}
		},
		function (errorpl) {
			alert("Error!");
		});
	}
	
	$scope.resetForm = function () {
		$scope.name = "";
		$scope.description = "";
		$scope.parentCategory = "";
		$(".select2").val(null);
		$(".select2").select2();
	}
	
	$scope.createNewCategory = function(){
		var newCategory = {
				name: $scope.name,
				description: $scope.description,
				parentCategory: $scope.parentCategory.id
		}
		
        var promisePost = CRUDOperations.post(newCategory, "/api/categories/create");
        var result = promisePost.then(function (data) {
            if (data.statusText == "Created") {
            	alert('Category Created!');
            	$scope.resetForm();
            	$('#createCategoryModal').modal('hide');
            }
        },
		function (errorpl) {
			alert("Error!");
		});
	}

	
});