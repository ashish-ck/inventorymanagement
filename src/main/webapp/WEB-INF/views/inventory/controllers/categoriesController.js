inventoryApp.directive('tree', function() {
	return {
		restrict: 'E', // tells Angular to apply this to only html tag that is <tree>
		replace: true, // tells Angular to replace <tree> by the whole template
		scope: {
			t: '=src' // create an isolated scope variable 't' and pass 'src' to it.  
		},    
		template: '<ul><branch ng-repeat="c in t.children" src="c"></branch></ul>'    
	};
})

inventoryApp.directive('branch', function($compile) {
	return {
		restrict: 'E', // tells Angular to apply this to only html tag that is <branch>
		replace: true, // tells Angular to replace <branch> by the whole template
		scope: {
			b: '=src' // create an isolated scope variable 'b' and pass 'src' to it.
		},    
		template: '<li><span onclick="callMe(this)" ng-attr-id="{{ b.details.id }}"><i class="fa fa-folder-open"></i> {{ b.details.name }}</span></li>',
		link: function(scope, element, attrs) {
			//// Check if there are any children, otherwise we'll have infinite execution
			var has_children = scope.b.children.length;

			//// Manipulate HTML in DOM
			if (has_children > 0) {
				element.addClass('parent_li').find('span').attr('title', 'Collapse this branch');
				element.append('<tree src="b"></tree>');
				// recompile Angular because of manual appending
				$compile(element.contents())(scope); 
			}else{
				element.find('i').removeClass('fa fa-folder-open').addClass('fa fa-folder-open-o');
			}

			//// Bind events
			element.on('click', function(event) {
		        var children = element.find('ul');
		        if (children.is(":visible")) {
		            children.hide('fast');
		            element.attr('title', 'Expand this branch').find('i').addClass('fa-folder').removeClass('fa-folder-open');
		        } else {
		            children.show('fast');
		            element.attr('title', 'Collapse this branch').find('i').addClass('fa-folder-open').removeClass('fa-folder');
		        }
		        event.stopPropagation();
			});      
		}
	};
});

inventoryApp.controller("categoriesController", function ($scope, $location, CRUDOperations) {

	//$scope.listOfCategories = [];
	$scope.itemCategories = [];
	$scope.rootCategories = [];
	$scope.finalCategories = [];
	$scope.listOfItems = [];
	$scope.category = {
			id: 0,
			name: "",
			description: "",
			parentCategory: null
	}
	$scope.isUpdateForm = false;
	$scope.isLoadingCategories = false;
	
	$scope.selectedCategory = {
			id: 0,
			name: "",
			description: "",
			parentCategory: null
	}
	
	$scope.resetForm = function () {
		$('.updating').button('reset');
		$scope.category = {
				id: 0,
				name: "",
				description: "",
				parentCategory: null
		}
	}
	
    $scope.getPath = function(itemId){
    	return 'items/edit/'+itemId;
    }
	
	$scope.createNewCategory = function(isValid){
		if(isValid){
			$('.updating').button('loading');
	        var promisePost = CRUDOperations.post($scope.category, "/api/categories/create");
	        var result = promisePost.then(function (data) {
	            if (data.data.valid) {
	            	if(!$scope.isUpdateForm){
	            		$scope.itemCategories.push(data.data.payload);
	            		
	            	}
	            	$scope.resetForm();
	            	$scope.initCategories();
	            	$('#createCategoryModal').modal('hide');
	            }
	            $('.updating').button('reset');
	            alert(data.data.message);
	        },
			function (errorpl) {
	        	$('.updating').button('reset');
				alert("Error!");
			});			
		}
	}

	$scope.fetchAllCategories = function () {
		$scope.isLoadingCategories = true;
		var promiseget = CRUDOperations.get("/api/categories/get");
		promiseget.then(function (data) {
			if(data.data.valid){
				$scope.itemCategories = data.data.payload;
				//$scope.listOfCategories = data.data;
				$scope.isLoadingCategories = false;
				$scope.initCategories();
			}else{
				alert(data.data.message);
			}
		},
		function (errorpl) {
			$scope.isLoadingCategories = false;
			alert("Error!");
		});
	}
	
	$scope.initCategories = function(){
    	angular.forEach($scope.itemCategories, function(value, key) {
    		if(value.parentCategory == 0){
    			var parentCategory = {
    					details: value,
    					children: []
    			}
    			$scope.rootCategories.push(parentCategory);
    		}
    	});
    	$scope.createNewCategoryList();
	}
	
	$scope.createNewCategoryList = function(){
		angular.forEach($scope.rootCategories, function(value, key) {
			value.children = $scope.getChildren(value.details.id);
			$scope.finalCategories = {
					children: []
			}
			$scope.finalCategories.children.push(value);
		});
	}
	
	$scope.getChildren = function(categoryId){
		var childrens = [];
		angular.forEach($scope.itemCategories, function(value, key) {
			if(value.parentCategory == categoryId){
				//$scope.listOfCategories.splice(key, 1);
				var currentCategory = {
						details: value,
						children: $scope.getChildren(value.id)
				}
				childrens.push(currentCategory);
			}
		});
		return childrens;
	}
	
	$scope.treeClickEvent = function (id) {
    	angular.forEach($scope.itemCategories, function(value, key) {
    		if(value.id == id){
    			$scope.selectedCategory = value;
    			//return $scope.selectedCategory;
    		}
    	});
		$scope.isLoadingItems = true;
		var promiseget = CRUDOperations.get("/api/items/getByCategory/"+id);
		promiseget.then(function (data) {
			if(data.data.valid){
				$scope.listOfItems = data.data.payload;
			}else{
				alert(data.data.message);
			}
			$scope.isLoadingItems = false;
		},
		function (errorpl) {
			$scope.isLoadingItems = false;
			alert("Error!");
		});
	}
	
	$scope.editCategory = function () {
		$scope.category = $scope.selectedCategory; 
		$scope.isUpdateForm = true;
    	$scope.openCreateCategoryModal();
	}
	
	$scope.getSelectedCategory = function(id){
    	angular.forEach($scope.itemCategories, function(value, key) {
    		if(value.id == id){
    			$scope.selectedCategory = value;
    			return $scope.selectedCategory;
    		}
    	});
	}
	
	
	$scope.openCreateCategoryModal = function(){
		$('#createCategoryModal').modal('show');
	}
});