var app = angular.module('app', []);
var appController = app.controller('ApplicationController', function ($scope, $log, $rootScope) {
    $scope.resizedImage = null;
    $scope.downloadLocation = null;
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
                $scope.resizedImage = 'r/file/' + result.properties.images[0] + '/view';
                $scope.downloadLocation = 'r/file/' + result.properties.images[0] + '/download';
                $scope.working = false;
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
    }
});
