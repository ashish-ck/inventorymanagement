'use strict';
inventoryApp.factory('authService', ['$http', '$q', 'localStorageService', '$cookies', function ($http, $q, localStorageService, $cookies) {

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
        $cookies.remove("IDrivoJoyAuthCookie");
        _authentication.isAuth = false;
        _authentication.userName = "";

    };

    var _fillAuthData = function () {

        var authData = localStorageService.get('authorizationData');
        if (authData)
        {
            _authentication.isAuth = true;
            _authentication.userName = authData.userName;
        }else{
        	//try to get from cookie
        	authData = $cookies.get("IDrivoJoyAuthCookie");
        	_authentication.userName = $cookies.get("UserName");
        	if (authData)
            {
        		authData = authData.replace(/"/g, "");
        		_authentication.userName = _authentication.userName.replace(/"/g, "");
                _authentication.isAuth = true;
                
                localStorageService.set('authorizationData', { token: authData, userName: _authentication.userName });
                //_authentication.userName = authData.userName;
            }
        }

    }

    //authServiceFactory.saveRegistration = _saveRegistration;
    authServiceFactory.login = _login;
    authServiceFactory.logOut = _logOut;
    authServiceFactory.fillAuthData = _fillAuthData;
    authServiceFactory.authentication = _authentication;

    return authServiceFactory;
}]);