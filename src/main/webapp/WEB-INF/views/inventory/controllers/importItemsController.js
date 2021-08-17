inventoryApp.controller("importItemsController", function ($scope, $window, $location, fileUpload) {
	$scope.importFile=null;
    $scope.importItems = function(){
        var file = $('input[type=file]')[0].files[0];
        var uploadUrl = "/api/items/import";
        $('#btnUpload').button('loading');
        var result = fileUpload.uploadFileToUrl(file, uploadUrl, $scope.resultCallback);
        if(result == 'OK'){
        	
        }else{
        }
    };
    $scope.importItemsNew = function(){
        var file = $('input[type=file]')[0].files[0];
        var uploadUrl = "/api/items/importNew";
        $('#btnUpload').button('loading');
        var result = fileUpload.uploadFileToUrl(file, uploadUrl, $scope.resultCallback);
        if(result == 'OK'){
        	
        }else{
        }
    };
    $scope.resultCallback = function(result){
    	if(result == 200){
    		$('#btnUpload').button('reset');
    		alert("Please wait for some time while database is refreshed with new data!");
    		$window.location.reload();
    	}
    }
});