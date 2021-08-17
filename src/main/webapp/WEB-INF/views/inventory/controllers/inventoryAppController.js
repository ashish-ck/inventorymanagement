inventoryApp.controller('inventoryAppController', function ($scope, $rootScope, $location, $filter, DTOptionsBuilder, authService, $window, CRUDOperations) {
    $scope.appRouting = function (page) {
			$location.path(page);
    }
    $scope.logout = function(){
    	authService.logOut();
    	$window.location.href = $location.$$protocol + "://" + $location.$$host + ":" + $location.$$port + "/login";
    }
	dtOptions = DTOptionsBuilder.newOptions().withPaginationType('full_numbers').withDisplayLength(25);
});

inventoryApp.filter('propsFilter', function() {
	  return function(items, props) {
	    var out = [];

	    if (angular.isArray(items)) {
	      items.forEach(function(item) {
	        var itemMatches = false;

	        var keys = Object.keys(props);
	        for (var i = 0; i < keys.length; i++) {
	          var prop = keys[i];
	          var text = props[prop].toLowerCase();
	          if (item[prop].toString().toLowerCase().indexOf(text) !== -1) {
	            itemMatches = true;
	            break;
	          }
	        }

	        if (itemMatches) {
	          out.push(item);
	        }
	      });
	    } else {
	      // Let the output be the input untouched
	      out = items;
	    }

	    return out;
	  };
	});
