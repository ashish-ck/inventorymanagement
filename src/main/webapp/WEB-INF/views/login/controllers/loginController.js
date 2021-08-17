'use strict';
loginApp.controller('loginController', ['$scope', '$location', 'authService', '$window', 'CRUDOperations', function ($scope, $location, authService, $window, CRUDOperations) {
 
    $scope.loginData = {
        userName: "",
        password: ""
    };
    
    $scope.fetchInitData = function(){
    	
    	authService.logOut();
    	
//    	if(authService.authentication.isAuth){
//    		var url = $location.$$protocol + "://" + $location.$$host + ":" + $location.$$port + "/";
//    		var promisePost = CRUDOperations.post(authService.getAuthToken(), "/login/do");
//			var result = promisePost.then(function (data) {
//				if(data.status == 200)
//					$window.location.href = url;
//				else
//					alert('cookie not set');
//			});
//    	}
    }
 
    $scope.message = "";
 
    $scope.login = function () {
 
        authService.login($scope.loginData).then(function (response) {
        	var url = $location.$$protocol + "://" + $location.$$host + ":" + $location.$$port + "/";
        	$window.location.href = url;
//    		var promisePost = CRUDOperations.post(authService.getAuthToken(), "/login/do");
//			var result = promisePost.then(function (data) {
//				if(data.status == 200){
//					//alert('cookie set');
//					$window.location.href = url;
//			}else
//					alert('cookie not set');
//			});
 
        },
         function (err) {
        	 alert("Login Failed!");
             $scope.message = err.error_description;
         });
    };
 
}]);