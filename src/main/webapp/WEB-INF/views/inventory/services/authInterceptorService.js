'use strict';
inventoryApp.factory('authInterceptorService', ['$q', '$location', '$window', 'localStorageService', function ($q, $location, $window, localStorageService) {

    var authInterceptorServiceFactory = {};

    var _request = function (config) {

        config.headers = config.headers || {};

        var authData = localStorageService.get('authorizationData');
        if (authData) {
            config.headers.Authorization = authData.token;
        }

        return config;
    }

    var _responseError = function (rejection) {
        if (rejection.status === 403) {
        	$location.path('/403');
        }else if(rejection.status === 401){
        	localStorageService.remove('authorizationData');
        	$window.location.href = $location.$$protocol + "://" + $location.$$host + ":" + $location.$$port + "/login";
        }else if(rejection.status === 500){
        	$location.path('/500');
        }
    }

    authInterceptorServiceFactory.request = _request;
    authInterceptorServiceFactory.responseError = _responseError;

    return authInterceptorServiceFactory;
}]);