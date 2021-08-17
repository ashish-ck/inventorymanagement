var loginApp = angular.module('loginApp', ['ngRoute', 'datatables', 'ngSanitize', 'ui.select', 'LocalStorageModule']);

loginApp.config(function($routeProvider) {
    $routeProvider
        .when('/login', {
            templateUrl: '/resources/app/login/views/login.html',
            controller: 'loginController'
        })
        .otherwise({ redirectTo: '/login' });
});

loginApp.run(['authService', function (authService) {
    authService.fillAuthData();
}]);

loginApp.config(function ($httpProvider) {
    $httpProvider.interceptors.push('authInterceptorService');
});
