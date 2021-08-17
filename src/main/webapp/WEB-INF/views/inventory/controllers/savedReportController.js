inventoryApp.controller("savedReportController", function ($scope, $location, CRUDOperations, $window, purchaseOrderService) {

	$scope.listOfReports = [];
	$scope.isLoadingReports = false;
	
	$scope.fetchInitData = function(){
	}
	
	$scope.fetchAllReports = function(){
		$scope.isLoadingReports = true;
		var promiseget = CRUDOperations.get("/api/reports/get");
		promiseget.then(function (data) {
			if(data.data.valid){
				$scope.listOfReports = data.data.payload;
			}else{
				alert(data.data.message);
			}
			$scope.isLoadingReports = false;
		},
		function (errorpl) {
			alert("Something went wrong!");
			$scope.isLoadingReports = false;
		});
	}
	
	$scope.generateReport = function(){
		$('.update').button('loading');
		var promiseget = CRUDOperations.get("/api/items/getInventoryCountSheet?userRef="+$scope.userReference);
		promiseget.then(function (data) {
			if(data.data.valid){
				alert(data.data.payload);
			}else{
				alert(data.data.message);
			}
			$('.update').button('reset');
		},
		function (errorpl) {
			alert("Something went wrong!");
			$('.update').button('reset');
		});
	}
	
	$scope.downloadReport = function(userReference, id){
		$window.open($location.$$protocol + "://" + $location.$$host + ":" + $location.$$port + "/api/reports/getById/"+userReference+"?reportId=" + id);
	}

});