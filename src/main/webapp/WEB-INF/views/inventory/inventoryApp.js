var inventoryApp = angular.module('inventoryApp', ['ngRoute','angularTreeview','datatables', 'ngSanitize', 'ui.select', 'LocalStorageModule', 'ngCookies']), permissionList;

inventoryApp.config(function($routeProvider) {
    $routeProvider
        .when('/dashboard', {
            templateUrl: '/resources/app/inventory/views/dashboard.html',
            controller: 'dashboardController',
            permission: '/dashboard'
        })
        .when('/items', {
            templateUrl: '/resources/app/inventory/views/viewItems.html',
            controller: 'itemsController',
            permission: '/items'
        })
        .when('/storageLocations', {
            templateUrl: '/resources/app/inventory/views/viewItemTree.html',
            controller: 'itemTreeController',
            permission: '/storageLocations'
        })
        .when('/items/create', {
            templateUrl: '/resources/app/inventory/views/addUpdateItem.html',
            controller: 'createItemController',
            permission: '/items/create'
        })
        .when('/items/import', {
            templateUrl: '/resources/app/inventory/views/importItems.html',
            controller: 'importItemsController',
            permission: '/items/import'
        })
        .when('/items/importNew', {
            templateUrl: '/resources/app/inventory/views/importItemsNew.html',
            controller: 'importItemsController',
            permission: '/items/importNew'
        })
        .when('/items/search', {
            templateUrl: '/resources/app/inventory/views/searchItems.html',
            controller: 'searchItemsController',
            permission: '/items/search'
        })
        .when('/items/edit/:itemId', {
            templateUrl: '/resources/app/inventory/views/addUpdateItem.html',
            controller: 'createItemController',
            permission: '/items/edit'
        })
        .when('/warehouses', {
            templateUrl: '/resources/app/inventory/views/viewWarehouses.html',
            controller: 'warehousesController',
            permission: '/warehouses'
        })
        .when('/warehouses/add', {
            templateUrl: '/resources/app/inventory/views/addUpdateWarehouse.html',
            controller: 'createWarehouseController',
            permission: '/warehouses/add'
        })
        .when('/units', {
            templateUrl: '/resources/app/inventory/views/viewUnits.html',
            controller: 'unitsController',
            permission: '/units'
        })
        .when('/units/add', {
            templateUrl: '/resources/app/inventory/views/addUpdateUnit.html',
            controller: 'createUnitController',
            permission: '/units/add'
        })
        .when('/categories', {
            templateUrl: '/resources/app/inventory/views/viewCategories.html',
            controller: 'categoriesController',
            permission: '/categories'
        })
        .when('/categories/add', {
            templateUrl: '/resources/app/inventory/views/addUpdateCategory.html',
            controller: 'createCategoryController',
            permission: '/categories/add'
        })
        .when('/purchaseOrders/create', {
            templateUrl: '/resources/app/inventory/views/addUpdatePurchaseOrder.html',
            controller: 'createPurchaseOrderController',
            permission: '/purchaseOrders/create'
        })
        .when('/purchaseOrders/view', {
            templateUrl: '/resources/app/inventory/views/viewPurchaseOrders.html',
            controller: 'purchaseOrdersController',
            permission: '/purchaseOrders/view'
        })
        .when('/purchaseOrders/edit/:orderNo', {
            templateUrl: '/resources/app/inventory/views/addUpdatePurchaseOrder.html',
            controller: 'createPurchaseOrderController',
            permission: '/purchaseOrders/edit'
        })
        .when('/purchaseOrders/templates', {
            templateUrl: '/resources/app/inventory/views/purchaseOrderTemplates.html',
        })
        .when('/attributes', {
            templateUrl: '/resources/app/inventory/views/viewAttributes.html',
            controller: 'attributesController',
            permission: '/attributes'
        })
        .when('/attributes/create', {
            templateUrl: '/resources/app/inventory/views/addUpdateAttribute.html',
            controller: 'createAttributeController',
            permission: '/attributes/create'
        })
        .when('/vendors', {
            templateUrl: '/resources/app/inventory/views/viewVendors.html',
            controller: 'vendorsController',
            permission: '/vendors'
        })
        .when('/vendors/search', {
            templateUrl: '/resources/app/inventory/views/searchVendors.html',
            controller: 'searchVendorsController',
            permission: '/vendors/search'
        })
        .when('/kits', {
            templateUrl: '/resources/app/inventory/views/viewKitRequests.html',
            controller: 'kitsController',
            permission: '/kits'
        })
        .when('/warehouseItems', {
            templateUrl: '/resources/app/inventory/views/viewStorageLocations.html',
            controller: 'storageLocationsController',
            permission: '/warehouseItems'
        })
        .when('/kits/view/:kitId', {
            templateUrl: '/resources/app/inventory/views/viewKit.html',
            controller: 'kitController',
            permission: '/kits/view'
        })
        .when('/kits/create/:requestId', {
            templateUrl: '/resources/app/inventory/views/addUpdateKit.html',
            controller: 'createKitController',
            permission: '/kits/create'
        })
        .when('/inventory/transfers', {
            templateUrl: '/resources/app/inventory/views/viewStockTransfers.html',
            controller: 'stockTransfersController',
            permission: '/inventory/transfers'
        })
        .when('/inventory/transfers/create', {
            templateUrl: '/resources/app/inventory/views/addUpdateStockTransfer.html',
            controller: 'createStockTransfersController',
            permission: '/inventory/transfers/create'
        })
        .when('/inventory/transfers/edit/:transferId', {
            templateUrl: '/resources/app/inventory/views/addUpdateStockTransfer.html',
            controller: 'createStockTransfersController',
            permission: '/inventory/transfers/edit'
        })/*
        .when('/inventory/adjustments', {
            templateUrl: '/resources/app/inventory/views/viewStockAdjustments.html',
            controller: 'stockAdjustmentsController',
            permission: '/inventory/adjustments'
        })
        .when('/inventory/adjustments/create', {
            templateUrl: '/resources/app/inventory/views/addUpdateStockAdjustment.html',
            controller: 'createStockAdjustmentController',
            permission: '/inventory/adjustments/create'
        })
        .when('/inventory/adjustments/:orderNumber', {
            templateUrl: '/resources/app/inventory/views/addUpdateStockAdjustment.html',
            controller: 'createStockAdjustmentController',
            permission: '/inventory/adjustments/edit'
        })*/
        .when('/inventory/reports/kits', {
            templateUrl: '/resources/app/inventory/views/kitReports.html',
            controller: 'kitReportController',
            permission: '/inventory/reports/kits'
        })
        .when('/inventory/reports/items', {
            templateUrl: '/resources/app/inventory/views/itemReports.html',
            controller: 'itemReportController',
            permission: '/inventory/reports/items'
        })
        .when('/inventory/recommendations', {
            templateUrl: '/resources/app/inventory/views/itemRecommendations.html',
            controller: 'recommendationsController',
            permission: '/inventory/recommendations'
        })
        .when('/inventory/reports/purchaseOrders', {
            templateUrl: '/resources/app/inventory/views/purchaseOrderReports.html',
            controller: 'purchaseOrderReportController',
            permission: '/inventory/reports/purchaseOrders'
        })
        .when('/inventory/reports/sales', {
            templateUrl: '/resources/app/inventory/views/salesReport.html',
            controller: 'salesReportController',
            permission: '/inventory/reports/sales'
        })
        .when('/inventory/reports/saved', {
            templateUrl: '/resources/app/inventory/views/savedReports.html',
            controller: 'savedReportController',
            permission: '/inventory/reports/saved'
        })
        .when('/inventory/taxes', {
            templateUrl: '/resources/app/inventory/views/taxes.html',
            controller: 'taxController',
            permission: '/inventory/taxes'
        })
        .when('/404', {
            templateUrl: '/resources/app/inventory/views/404.html'
        })
        .when('/403', {
            templateUrl: '/resources/app/inventory/views/403.html'
        })
        .when('/500', {
            templateUrl: '/resources/app/inventory/views/500.html'
        })
        .otherwise({ redirectTo: '/dashboard' });
});

inventoryApp.run(['authService', '$window', '$location', '$rootScope', 'CRUDOperations', 'permissions' , function (authService, $window, $location, $rootScope, CRUDOperations, permissions) {
    authService.fillAuthData();
    if(!authService.authentication.isAuth){
    	$window.location.href = $location.$$protocol + "://" + $location.$$host + ":" + $location.$$port + "/login";
    }
    
    permissionList = document.getElementById('permissionsList').value;
    permissions.setPermissions(permissionList);
}]);

inventoryApp.config(function ($httpProvider) {
    $httpProvider.interceptors.push('authInterceptorService');
});

inventoryApp.directive('fileModel', ['$parse', function ($parse) {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            var model = $parse(attrs.fileModel);
            var modelSetter = model.assign;
            
            element.bind('change', function(){
                scope.$apply(function(){
                    modelSetter(scope, element[0].files[0]);
                });
            });
        }
    };
}]);