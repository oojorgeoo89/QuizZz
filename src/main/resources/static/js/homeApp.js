(function() {

  var app = angular.module("homeApp", []);

  var homeCtrl = function($scope, $http) {

    $http.get("/api/quizzes")
    	.then(
        	function(response) {
        		$scope.quizzes = response.data.content;
        	}, 
        	function(reason) {
        		$scope.error = "Could not fetch the data.";
        	}
    	);

  };
  
  app.controller("HomeCtrl", ["$scope", "$http", homeCtrl]);

}());