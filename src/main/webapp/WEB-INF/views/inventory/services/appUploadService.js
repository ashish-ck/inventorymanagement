inventoryApp.service('fileUpload', ['$http', function ($http) {
    this.uploadFileToUrl = function(file, uploadUrl, callback){
        var fd = new FormData();
        fd.append("file", file);
        console.log(fd)
        return $http.post(uploadUrl, fd, {
            transformRequest: function(data, headersGetterFunction) {
                return data;
            },
            headers: { 'Content-Type': undefined }
            }).success(function(data, status) {
            	//alert('Uploaded successfully');
            	callback(status);
                return data.statusText;
            }).error(function(data, status) {
                return data;
            });
        
//        $http({
//            method: 'POST',
//            url: uploadUrl,
//            headers: {'Content-Type': undefined},
//            data: fd,
//            transformRequest: function(data, headersGetterFunction) {
//                            return data;
//             }
//         })
//        .success(function(data, status) {   
//                        alert("success");
//         })
//        $http.post(uploadUrl, {
//        	
//			transformRequest : angular.identity,
//			headers: {'Content-Type': 'multipart/form-data;boundary=uploadFile'},
//			data: fd
//		}).success(function() {
//			alert('success!');
//		}).error(function() {
//		});
    }
}]);