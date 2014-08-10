var app = angular.module('app', ['ngRoute', 'pvpsControllers']);

app.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/upload',{
        templateUrl: 'views/upload.html',
        controller: 'ApplicationController'
    }).when('/:imageId/result', {
        templateUrl: 'views/result.html',
        controller: 'ApplicationController'
    }).when('/',{
        templateUrl: 'views/intro.html',
        controller: 'ApplicationController'
    });
}]);
