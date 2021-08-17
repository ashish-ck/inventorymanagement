inventoryApp.controller("stockAdjustmentsController", function ($scope, $location, CRUDOperations, DTOptionsBuilder, DTColumnBuilder, $window) {
	$scope.listOfAdjustments = [];
    $scope.viewDetails = function(orderNo){
    	$window.open($location.$$protocol + "://" + $location.$$host + ":" + $location.$$port + "/#/" + "inventory/adjustments/" + orderNo);
    }
	$scope.adjustmentTblOptions = DTOptionsBuilder.newOptions()
	.withOption('ajax', function(data, callback, settings){
		// make an ajax request using data.start and data.length
		$scope.isLoadingData = true;
        var promisePost = CRUDOperations.post(data, '/api/adjustments/getStockAdjustmentsPaginated');
        promisePost.then(function(data){        	
        	$scope.listOfAdjustments = data.data.data;
        	$scope.isLoadingData = false;
        	callback(data.data);
        });
	})
	.withOption('createdRow', function (row, request, index) {
		request.date = moment(request.orderDate).format("DD/MMM/YYYY");
		var total = 0.0;
		for(var i = 0; i < request.items.length; i++){
			total += request.items[i].quantityDelivered;
		};
		$('td', row).eq(0).html('<h4><span class="label bg-teal">' + request.orderNumber + '</label></h4>');
        $('td', row).eq(1).html('<p class="text-info"><i class="fa fa-calendar"></i> ' + request.date + '</p>');
        $('td', row).eq(2).html('<p class="text-warning"><i class="fa fa-map-marker"></i> ' + request.warehouse.name + '</p>');
        $('td', row).eq(3).html('<p class="text-danger"><b> ' + total + '</b></p>');
        $('td', row).eq(4).html('<button type="button" class="btn btn-link" onclick="viewDetails(\'' + request.orderNumber +'\')"> View Details </button>');

	})
	.withDataProp('data')
	.withOption('processing', true)
	.withOption('serverSide', true)
	.withPaginationType('full_numbers')
	.withDisplayLength(25)
		$scope.adjustmentColumns = [
	                       DTColumnBuilder.newColumn("orderNumber", "Order #"),
	                       DTColumnBuilder.newColumn("orderDate", "Date"),
	                       DTColumnBuilder.newColumn("warehouse.name", "Warehouse"),
	                       DTColumnBuilder.newColumn("items", "Quantity Adjusted"),
	                       DTColumnBuilder.newColumn("status", "Details")
	                      ]
});