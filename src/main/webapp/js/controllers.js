var pvpsControllers = angular.module('pvpsControllers', []);

pvpsControllers.controller('ApplicationController', function ($scope, $location, $log, $route, $routeParams) {

    $scope.getImageEndpoint = function (imageId) {
        return 'r/file/' + imageId + '/view';
    };

    $scope.getDownloadEndpoint = function (imageId) {
        return 'r/file/' + imageId + '/download';
    };

    $scope.getResultEndpoint = function (imageId) {
        return '/' + imageId + '/result';
    };

    $scope.resizedImage = $routeParams.imageId ? $scope.getImageEndpoint($routeParams.imageId) : null;
    $scope.downloadLocation = $routeParams.imageId ? $scope.getDownloadEndpoint($routeParams.imageId) : null;
    $scope.working = false;

    $scope.files = [];

    $scope.change = function (event) {
        $scope.files = [];
        for (var i = 0; i < event.files.length; i++) {
            $scope.files.push(event.files[i]);
        }

        $scope.$apply();
    };

    $scope.canUpload = function () {
        return $scope.files.length > 0 && !$scope.working;
    };

    $scope.displayResult = function () {
        return $scope.resizedImage != null;
    };

    $scope.submit = function () {
        $scope.working = true;
        var formData = new FormData();
        var file = $scope.files[0];

        formData.append("file", file);

        $.ajax({
            url: "r/file/upload",
            type: "POST",
            data: formData,
            processData: false,
            contentType: false,
            success: function (result) {
                var imageId = result.properties.images[0];
                $scope.resizedImage = $scope.getImageEndpoint(imageId);
                $scope.downloadLocation = $scope.getDownloadEndpoint(imageId);
                $scope.working = false;
                $location.path($scope.getResultEndpoint(imageId));
                $scope.$apply();
            },
            error: function (result) {
                $scope.working = false;
                $('#uploadResult').html("upload failed: <code>HTTP/" + result.status + ", " + result.statusText + "</code>");
            }
        });
    };

    $scope.reset = function () {
        $scope.files = [];
        $scope.resizedImage = null;
        $scope.downloadLocation = null;
        $scope.working = false;
        $location.path('/upload');
    }
});
