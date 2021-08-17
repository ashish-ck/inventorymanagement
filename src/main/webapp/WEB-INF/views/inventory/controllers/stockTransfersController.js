inventoryApp.controller("stockTransfersController", function ($scope, $location, CRUDOperations, DTOptionsBuilder, DTColumnBuilder, $window) {
	$scope.listOfOrders = [];
    $scope.viewDetails = function(orderNo){
    	$window.open($location.$$protocol + "://" + $location.$$host + ":" + $location.$$port + "/#/" + "inventory/transfers/edit/" + orderNo);
    }
	$scope.orderTblOptions = DTOptionsBuilder.newOptions()
	.withOption('ajax', function(data, callback, settings){
		// make an ajax request using data.start and data.length
		$scope.isLoadingData = true;
        var promisePost = CRUDOperations.post(data, '/api/transferOrders/getTransferOrdersPaginated');
        promisePost.then(function(data){
        	$scope.listOfOrders = data.data.data;
        	$scope.isLoadingData = false;
        	callback(data.data);
        });
	})
	.withOption('aaSorting', [1, 'desc'])	
	.withOption('createdRow', function (row, request, index) {
		var status = request.status;
		if(status == 1){
			$('td', row).eq(2).html('<h4><span class="label label-primary">' + request.statusText + '</label></h4>');
		}else if(status == 2 || status == 4){
			$('td', row).eq(2).html('<h4><span class="label label-warning">' + request.statusText + '</label></h4>');
		}else if(status == 3){
			$('td', row).eq(2).html('<h4><span class="label label-danger">' + request.statusText + '</label></h4>');
		}else if(status == 11){
			$('td', row).eq(2).html('<h4><span class="label label-success">' + request.statusText + '</label></h4>');
		}

		request.orderDate = moment(request.orderDate).format("DD/MMM/YYYY");


		$('td', row).eq(0).html('<h4><span class="label bg-teal">' + request.orderNumber + '</label></h4>');
        $('td', row).eq(1).html('<p class="text-info"><i class="fa fa-calendar"></i> ' + request.orderDate + '</p>');
        $('td', row).eq(3).html('<p class="text-danger"><i class="fa fa-map-marker"></i> ' + request.fromWarehouse.name + '</p>');
        $('td', row).eq(4).html('<p class="text-success"><i class="fa fa-map-marker"></i> ' + request.toWarehouse.name + '</p>');
        $('td', row).eq(5).html('<button type="button" class="btn btn-link" onclick="viewDetails(\'' + request.orderNumber +'\')"> View Details </button>');

	})
	.withDataProp('data')
	.withOption('processing', true)
	.withOption('serverSide', true)
	.withPaginationType('full_numbers')
	.withDisplayLength(25)
	
		$scope.orderColumns = [
	                       DTColumnBuilder.newColumn("orderNumber", "Order #"),
	                       DTColumnBuilder.newColumn("orderDate", "Date"),
	                       DTColumnBuilder.newColumn("statusText", "Status"),
	                       DTColumnBuilder.newColumn("fromWarehouse.name", "From"),
	                       DTColumnBuilder.newColumn("toWarehouse.name", "To"),
	                       DTColumnBuilder.newColumn("status", "Details"),
	                      ]
	
});