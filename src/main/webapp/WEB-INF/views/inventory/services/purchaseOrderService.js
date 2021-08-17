'use strict';
inventoryApp.factory('purchaseOrderService', ['$http', '$q', 'localStorageService', function ($http, $q, localStorageService) {

	var purchaseOrderFactory = {};

    var _items = [];

    var _getData = function () {
    	return _items;
    };

    var _setData = function (listOfItems) {
    	_items = listOfItems;
    };

    purchaseOrderFactory.getData = _getData;
    purchaseOrderFactory.setData = _setData;

    return purchaseOrderFactory;
}]);