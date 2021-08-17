inventoryApp.controller("itemsController", function ($scope, $location, CRUDOperations, DTOptionsBuilder, DTColumnBuilder, $window, localStorageService) {
	
	$scope.listOfItems = [];
	
    $scope.itemsOptions = DTOptionsBuilder.newOptions()
    .withOption('ajax', {
        // Either you specify the AjaxDataProp here
        // dataSrc: 'data',
        url: '/api/items/getItems',
        contentType: "application/json; charset=utf-8",
        //headers: { 'Authorization': 'Bearer '+localStorageService.get('authorizationData').token },
        type: 'POST',
        data: function (returnData) { return returnData = JSON.stringify(returnData); }
    })
    .withOption('createdRow', function (row, data, index) {
    	var id = data.id;
        var code = $('td', row).eq(0).html();
        var desc = $('td', row).eq(1).html();
        var category = $('td', row).eq(2).html();
        var qty = $('td', row).eq(3).html();
        var mrp = $('td', row).eq(4).html();
        
        $('td', row).eq(0).html('<button type="button" onclick="viewDetails(' + id + ')" class="btn btn-primary">' + code + '</button>');
        $('td', row).eq(1).html('<p class="text-info"> ' + desc + '</p>');
        $('td', row).eq(2).html('<p class="text-info"> ' + category + '</p>');
        $('td', row).eq(3).html('<p class="text-info"> ' + qty + '</p>');
        $('td', row).eq(4).html('<p class="text-info"> ' + mrp + '</p>');

    })
    .withDataProp('data')
    .withOption('processing', true)
    .withOption('serverSide', true)
    .withPaginationType('full_numbers')

$scope.itemsColumns = [
    DTColumnBuilder.newColumn("code", "Req #"),
    DTColumnBuilder.newColumn("description", "Description"),
    DTColumnBuilder.newColumn("category.name", "Category"),
    //DTColumnBuilder.newColumn("quantityInHand", "Quantity In Hand"),
    DTColumnBuilder.newColumn("normalPrice", "MRP")
]
	
	
//    $scope.fetchActiveItems = function () {
//        var promiseget = CRUDOperations.get("/api/items/get");
//        promiseget.then(function (data) {
//        	if(data.statusText == "OK"){
//        		$scope.listOfItems = data.data;
//        	}else{
//        		alert("Something went wrong!");
//        	}
//        },
//      function (errorpl) {
//        	alert("Error!");
//      });
//    }

    $scope.openItemDetails = function(id){
    	$window.open("/#/items/edit/"+id);
    }
    
    $scope.getPath = function(itemId){
    	return 'items/edit/'+itemId;
    }
});