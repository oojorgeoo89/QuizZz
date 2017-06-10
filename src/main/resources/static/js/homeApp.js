(function() {

  var app = angular.module("homeApp", []);

  var homeCtrl = function($scope, $http) {

    $http.get("/v1/quiz/")
    	.then(
        	function(response) {
        		$scope.quizzes = response.data;
        	}, 
        	function(reason) {
        		$scope.error = "Could not fetch the data.";
        	}
    	);

  };
  
  app.controller("HomeCtrl", ["$scope", "$http", homeCtrl]);

}());