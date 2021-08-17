inventoryApp.controller("searchItemsController", function ($scope, $location, CRUDOperations) {
	
	$scope.showAttributeSelectInput = false;
	$scope.showSearchValueInput = false;
	$scope.showTextInput = false;
	$scope.showValueSelectInput = false;
	$scope.showAttributeValueSelectInput = false;
	
	$scope.listOfWarehouses = [];
	$scope.listOfCategories = [];
	$scope.listOfAttributeValues = [];
	$scope.attributeValues = [];
	$scope.searchTextParam = [];
	$scope.listOfValueItems = [];
	$scope.listOfAttributes = [];
	$scope.searchCriteria = {};
	$scope.listOfCriterias = [];
	$scope.listOfItems = [];
	$scope.selectedAttribute = null;
	$scope.listOfTags = [];
	$scope.criteriaText = "";
	$scope.searchCriterias = [];
	$scope.searchCriteria = null;
	$scope.showNoItemsFoundError = false;
	$scope.attributeCriteria = null;
	
	
	$scope.fetchInitData = function(){
		$scope.listOfCriterias.push("Barcode");
		$scope.listOfCriterias.push("Category");
		$scope.listOfCriterias.push("Item Attribute");
		$scope.listOfCriterias.push("Item Code");
		$scope.listOfCriterias.push("Warehouse");
		$scope.listOfCriterias.push("Tags");
		
		$scope.fetchWarehouses();
		$scope.fetchTags();
		$scope.fetchItemAttributes();
		$scope.fetchCategories();
	}
	
    $scope.getPath = function(itemId){
    	return 'items/edit/'+itemId;
    }
	
	$scope.firstLevelCriteriaChange = function(){
		$scope.showSearchValueInput = true;
		if($scope.searchCriteria === "Barcode" || $scope.searchCriteria === "Item Code"){
			
			$scope.showTextInput = true;
			$scope.showValueSelectInput = false;
			$scope.currentSearchCriteriaText = "Enter "+$scope.searchCriteria;
			$scope.showAttributeValueSelectInput = false;
			$scope.showAttributeSelectInput = false;
			$scope.searchTextParam = null;
			
		}else if($scope.searchCriteria === "Warehouse" || $scope.searchCriteria === "Tags" || $scope.searchCriteria === "Category" ){
			$scope.listOfValueItems = [];
			if($scope.searchCriteria === "Warehouse"){
				$scope.listOfValueItems = $scope.listOfWarehouses;
			}else if($scope.searchCriteria === "Category"){
				$scope.listOfValueItems = $scope.listOfCategories;
			}else{
				$scope.listOfValueItems = $scope.listOfTags;
			}
			$scope.searchValues = [];
		
			$scope.showTextInput = false;
			$scope.showValueSelectInput = true;
			$scope.currentSearchCriteriaText = "Select "+$scope.searchCriteria;
			$scope.showAttributeValueSelectInput = false;
			$scope.showAttributeSelectInput = false;

			
		}else if($scope.searchCriteria === "Item Attribute" ){
			$scope.selectedAttribute = null;
			$scope.listOfAttributeValues = [];
			$scope.attributeValues = [];
			$scope.showAttributeSelectInput = true;
			$scope.showAttributeValueSelectInput = true;
			$scope.showTextInput = false;
			$scope.showValueSelectInput = false;
			$scope.currentSearchCriteriaText = "Select Attribute Value";
		}
	} 
	
	$scope.fetchCategories = function(){
		var promiseget = CRUDOperations.get("/api/categories/get");
		return promiseget.then(function (data) {
			if(data.data.valid){
				$scope.listOfCategories = data.data.payload;
			}else{
				alert(data.data.message);
			}
		},
		function (errorpl) {
			return false;
		});
	}
	
    $scope.fetchWarehouses = function(){
        var promiseget = CRUDOperations.get("/api/warehouses/getWarehouseNames");
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
    
    $scope.fetchTags = function(){
        var promiseget = CRUDOperations.get("/api/tags/get");
        promiseget.then(function (data) {
        	if(data.data.valid){
        		$scope.listOfTags = data.data.valid;
        	}else{
        		alert(data.data.message);
        	}
        },
      function (errorpl) {
        	alert("Error!");
      });
    }
    
    $scope.fetchItemAttributes = function(){
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
    
	$scope.fetchAttributeValues = function(){
		$scope.listOfAttributeValues = [];
		$scope.attributeValues = [];
		if($scope.selectedAttribute){
			$scope.listOfAttributeValues = $scope.selectedAttribute.possibleValues;
		}
	}
	
	$scope.addCriteria = function(){
		$scope.showSearchValueInput = false;
		$scope.showAttributeSelectInput = false;
		$scope.showNoItemsFoundError = false;
		if($scope.criteriaText != ""){
			$scope.criteriaText += " and ";
		}
		
		if($scope.searchCriteria === "Barcode"){
			var criteria = {
					key: 'barcode',
					value: [$scope.searchTextParam]
			}
			$scope.searchCriterias.push(criteria);
			$scope.criteriaText += " with barcode : "+$scope.searchTextParam;
		}else if ($scope.searchCriteria === "Item Code"){
			var criteria = {
					key: 'code',
					value: [$scope.searchTextParam]
			}
			$scope.searchCriterias.push(criteria);
			$scope.criteriaText += " with item code : "+$scope.searchTextParam;
		}else if($scope.searchCriteria === "Warehouse"){
			var criteria = {
					key: 'warehouse',
					value: $scope.searchValues
			}
			$scope.searchCriterias.push(criteria);
			$scope.criteriaText += " in warehouse : "+$scope.searchValues;
		}else if($scope.searchCriteria === "Category"){
			var criteria = {
					key: 'category',
					value: $scope.searchValues
			}
			$scope.searchCriterias.push(criteria);
			$scope.criteriaText += " in categories : "+$scope.searchValues;
		}else if($scope.searchCriteria === "Tags"){
			var criteria = {
					key: 'tags',
					value: $scope.searchValues
			}
			$scope.searchCriterias.push(criteria);
			$scope.criteriaText += " having tags : "+$scope.searchValues;
		}else if($scope.searchCriteria === "Item Attribute" ){
			if(!$scope.attributeCriteria){
				$scope.attributeCriteria = {
						key: 'attribute-'+$scope.selectedAttribute.name,
						value: []
				}
				angular.forEach($scope.attributeValues, function(value, key) {
					$scope.attributeCriteria.value.push(value);
				});
			}else{
				
				$scope.attributeCriteria.key += (","+$scope.selectedAttribute.name);
				angular.forEach($scope.attributeValues, function(value, key) {
					$scope.attributeCriteria.value.push(value);
				});
			}
			$scope.criteriaText += " with attribute "+$scope.selectedAttribute.name+" having values : "+$scope.attributeValues;
		}
		$scope.searchCriteria = null;
	}
	
	$scope.searchByCriteria = function(){
		$scope.showNoItemsFoundError = false;
		if($scope.attributeCriteria){
			$scope.searchCriterias.push($scope.attributeCriteria);
		}
        var promisePost = CRUDOperations.post($scope.searchCriterias, "/api/items/getByCriteria");
        var result = promisePost.then(function (data) {
            if (data.data.valid) {
            	$scope.listOfItems = data.data.payload;
            	if(!$scope.listOfItems || $scope.listOfItems.length == 0){
            		$scope.showNoItemsFoundError = true;
            	}
            	$scope.criteriaText = "";
            	$scope.searchCriterias = [];
            }
        },
		function (errorpl) {
			alert("Error!");
		});
	}
	
});