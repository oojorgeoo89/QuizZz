(function() {

	var app = angular.module("myQuizzesApp", []);

	var myQuizzesCtrl = function($scope, $http) {	  
	
		$scope.pagination = {
			pageNumber: 0,
			morePagesAvailable: true
		};
		
		$scope.loadNextPage = function(quizName, quizDescription) {
		
			if ($scope.pagination.morePagesAvailable) {
				$http.get("/api/users/" + $scope.userId + "/quizzes?page=" + $scope.pagination.pageNumber)
					.then(
						function(response) {
							if ($scope.quizzes == undefined) {
								$scope.quizzes = response.data.content;
							} else {
								$scope.quizzes = $scope.quizzes.concat(response.data.content);
							}
							
							$scope.pagination.morePagesAvailable = !response.data.last;
							$scope.pagination.pageNumber++;
						}, 
						function(reason) {
							$scope.error = "Could not fetch the data.";
						}
					);
			}
		}
		
		$scope.deleteQuiz = function(quizId) {
			if (quizId == 0)
				return;

			$http.delete("/api/quizzes/" + quizId)
			.then(
					function(response) {
						for (var i = 0; i < $scope.quizzes.length; i++) {
						    if ($scope.quizzes[i].id == quizId) {
						    	$scope.quizzes.splice(i, 1);
						    	break;
						    }
						}
					}, 
					function(reason) {
						console.log(reason.data);
					}
			);
		}
	
		$scope.loadNextPage();
		
	};

	app.controller("MyQuizzesCtrl", ["$scope", "$http", myQuizzesCtrl]);

}());