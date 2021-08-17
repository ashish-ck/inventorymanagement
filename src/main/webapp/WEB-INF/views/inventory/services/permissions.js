angular.module('inventoryApp')
.factory('permissions', function ($rootScope) {
    var permissionList;
    return {
        setPermissions: function (permissions) {
            permissionList = permissions;
            $rootScope.$broadcast('permissionsChanged');
        },
        hasPermission: function (permission) {        	
            permission = permission.trim();            
            return permissionList.indexOf(permission) > -1 || permissionList.indexOf('all') > -1;
            //return _.some(permissionList, function (item) {
            //    if (_.isString(item.Name)) {
            //        return item.Name.trim() === permission
            //    }
            //});
        }
    };
})