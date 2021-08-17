inventoryApp.service('CRUDOperations', function ($http, $location, localStorageService) {
    //Create new record
    this.post = function (value, uri) {
        var request = $http({
            method: "post",
            url: uri,
            data: value
        });
        return request;
    }
    
    //Edit existing record
    this.put = function (value, uri) {
        var request = $http({
            method: "put",
            url: uri,
            data: value
        });
        return request;
    }

    //Get All default values
    this.get = function (uri) {
        var request = $http({
            method: "get",
            url: uri,
            data : "",
            headers: {
            	'X-Force-Content-Type': 'application/json',
            	'Content-Type' : 'application/json;charset=UTF-8'
            }
        });
        return request;
    }


});