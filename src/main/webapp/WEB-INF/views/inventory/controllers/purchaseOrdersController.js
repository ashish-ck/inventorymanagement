inventoryApp.controller("purchaseOrdersController", function ($scope, $location, CRUDOperations, DTOptionsBuilder, DTColumnBuilder, $window) {
	$scope.listOfOrders = [];
	$scope.warehouse = {};
	$scope.listOfWarehouses = [];
	$scope.selectedDateRange = null;
	$scope.ordersTblInstance = {};
	$scope.selectedStatus1 = {status: undefined};
	$scope.status = {
			"1": "Open",
			"2": "Received",
			"5": "Invoiced",
			"6": "Partially Paid",
			"7": "Paid",
			"11": "Fulfilled"
	};
    $scope.viewDetails = function(orderNo){
    	$window.open($location.$$protocol + "://" + $location.$$host + ":" + $location.$$port + "/#/" + "purchaseOrders/edit/" + orderNo);
    }
    $scope.fetchInitData = function(){
    	var s = {"1": "Open",
    			"2": "Received",
    			"5": "Invoiced",
    			"6": "Partially Paid",
    			"7": "Paid",
    			"11": "Fulfilled"};
				arr = [];
				for (key in s){
					r = {};
					r.status = key;
					r.text = s[key];
					arr.push(r);
				}
				$scope.fstatus = arr; 
    	$scope.fetchWarehouses();
    }
	$scope.orderTblOptions = DTOptionsBuilder.newOptions()
	.withOption('ajax', function(data, callback, settings){
		$scope.isLoadingData = true;
		var requestParams = {params: data, fromDate: $scope.fromDate, toDate:$scope.toDate, requestWarehouseId: $scope.warehouseId, assignedWarehouseId: null, status: $scope.selectedStatus2};
        var promisePost = CRUDOperations.post(requestParams, '/api/purchaseOrders/getPurchaseOrdersPaginated');
        promisePost.then(function(data){
        	$scope.listOfOrders = data.data.data;
        	$scope.isLoadingData = false;
        	callback(data.data);
        });
	})
	.withOption('aaSorting', [1, 'desc'])
	.withOption('createdRow', function (row, request, index) {
		var status = request.status[request.status.length-1];
		if(status == "Open"){
			$('td', row).eq(2).html('<h4><span class="label label-primary">' + status + '</label></h4>');
		}else if(status == "Received"){
			$('td', row).eq(2).html('<h4><span class="label label-warning">' + status + '</label></h4>');
		}else if(status == "Invoiced"){
			$('td', row).eq(2).html('<h4><span class="label label-danger">' + status + '</label></h4>');
		}else if(status == "Partially Paid"){
			$('td', row).eq(2).html('<h4><span class="label bg-lime">' + status + '</label></h4>');
		}else if(status == "Paid"){
			$('td', row).eq(2).html('<h4><span class="label bg-lime">' + status + '</label></h4>');
		}else if(status == "Fulfilled"){
			$('td', row).eq(2).html('<h4><span class="label label-success">' + status + '</label></h4>');
		}
		request.date = moment(request.date).format("DD/MMM/YYYY");
		$('td', row).eq(0).html('<h4><span class="label bg-teal">' + request.orderNo + '</label></h4>');
        $('td', row).eq(1).html('<p class="text-info"><i class="fa fa-calendar"></i> ' + request.date + '</p>');
        $('td', row).eq(3).html('<p class="text-warning"><i class="fa fa-map-marker"></i> ' + request.shippingWarehouse.name + '</b></p>');
        $('td', row).eq(4).html('<p class="text-warning"><i class="fa fa-user"></i> ' + request.vendor.name + '</p>');
        $('td', row).eq(5).html('<p class="text-danger"><b> ' + request.total + '</b></p>');
        $('td', row).eq(6).html('<p class="text-success"><b> ' + request.amtPaid + '</b></p>');
        $('td', row).eq(7).html('<button type="button" class="btn btn-link" onclick="viewDetails(\'' + request.orderNo +'\')"> View Details </button>');
	})
	.withDataProp('data')
	.withOption('processing', true)
	.withOption('serverSide', true)
	.withPaginationType('full_numbers')
	.withDisplayLength(25)
		$scope.orderColumns = [
	                       DTColumnBuilder.newColumn("orderNo", "Order #"),
	                       DTColumnBuilder.newColumn("date", "Date"),
	                       DTColumnBuilder.newColumn("status", "Status"),
	                       DTColumnBuilder.newColumn("shippingWarehouse", "Shipping Warehouse"),
	                       DTColumnBuilder.newColumn("vendor.name", "Vendor"),
	                       DTColumnBuilder.newColumn("total", "Total"),
	                       DTColumnBuilder.newColumn("amtPaid", "Paid"),
	                       DTColumnBuilder.newColumn("amtBalance", "Details"),
	                      ]
	$scope.warehouseSelectEvent = function(warehouse){
		if(warehouse){
			$scope.warehouseId = warehouse.id;
			$('#ordersTbl').DataTable().clear();
			$scope.ordersTblInstance.reloadData(null, false);
		}
	}
	$scope.dateChangeEvent = function(){
		$scope.fromDate = ($scope.selectedDateRange)?moment($scope.selectedDateRange.split(" - ")[0], ["MM-DD-YYYY", "YYYY-MM-DD"]):null;//.format("YYYY-MM-DD"): null;
		$scope.toDate = ($scope.selectedDateRange)?moment($scope.selectedDateRange.split(" - ")[1], ["MM-DD-YYYY", "YYYY-MM-DD"]):null;//.format(["MM-DD-YYYY", "YYYY-MM-DD"]): null;
		$('#ordersTbl').DataTable().clear();
		$scope.ordersTblInstance.reloadData(null, false);
	}
	$scope.clearClickEvent = function(){
		$scope.selectedDateRange = undefined;
		$scope.fromDate = null;
		$scope.toDate = null;
		$scope.warehouseId = null;
		$scope.warehouse.shippingWarehouse = undefined;
		$scope.selectedStatus1 = {status: undefined};
		$scope.selectedStatus2 = null;
		$('#ordersTbl').DataTable().clear();
		$scope.ordersTblInstance.reloadData(null, false);
	}
	$scope.statusSelectEvent = function(status){
		$scope.selectedStatus2 = status.status;
		$('#ordersTbl').DataTable().clear();
		$scope.ordersTblInstance.reloadData(null, false);
	}
	$scope.fetchWarehouses = function(){
		var promiseget = CRUDOperations.get("/api/warehouses/getWarehouseNames");
		promiseget.then(function (data) {
			if(data.data.valid){
				for(var i = 0; i < data.data.payload.length; i++){
					var warehouse = data.data.payload[i];
					if(warehouse.name != $scope.currentWarehouse){
						$scope.listOfWarehouses.push(warehouse);
					}
				}
			}else{
				alert(data.data.message);
			}
		},
		function (errorpl) {
			alert("Error!");
		});
	}
});