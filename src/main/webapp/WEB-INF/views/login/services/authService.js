'use strict';
loginApp.factory('authService', ['$http', '$q', 'localStorageService', function ($http, $q, localStorageService) {

    //var serviceBase = 'http://drivojoydev.cloudapp.net:9001/oauth/';
    var serviceBase = 'http://drivojoy.com:9001/oauth/';
    var authServiceFactory = {};

    var _authentication = {
        isAuth: false,
        userName : ""
    };

    var _login = function (loginData) {

        var data = "grant_type=password&username=" + loginData.userName + "&password=" + loginData.password;

        var deferred = $q.defer();

        $http.post(serviceBase + 'token', data, { headers: { 'Content-Type': 'application/x-www-form-urlencoded' } }).success(function (response) {

            localStorageService.set('authorizationData', { token: response.access_token, userName: loginData.userName });

            _authentication.isAuth = true;
            _authentication.userName = loginData.userName;

            deferred.resolve(response);

        }).error(function (err, status) {
            _logOut();
            deferred.reject(err);
        });

        return deferred.promise;

    };

    var _logOut = function () {

        localStorageService.remove('authorizationData');

        _authentication.isAuth = false;
        _authentication.userName = "";

    };

    var _fillAuthData = function () {

        var authData = localStorageService.get('authorizationData');
        if (authData)
        {
            _authentication.isAuth = true;
            _authentication.userName = authData.userName;
        }

    }
    
    var _getAuthToken = function (){
    	var authData = localStorageService.get('authorizationData');
        if (authData)
        {
        	return authData.token;
        }
        return null;
    }

    //authServiceFactory.saveRegistration = _saveRegistration;
    authServiceFactory.login = _login;
    authServiceFactory.logOut = _logOut;
    authServiceFactory.fillAuthData = _fillAuthData;
    authServiceFactory.authentication = _authentication;
    authServiceFactory.getAuthToken = _getAuthToken;

    return authServiceFactory;
}]);