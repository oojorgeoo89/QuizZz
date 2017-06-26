(function() {

	var app = angular.module("editApp", []);

	var editCtrl = function($scope, $http) {
		
		$scope.refreshQuizData = function() {
			if ($scope.quizId == 0)
				return;

			$http.get("/api/quizzes/" + $scope.quizId)
			.then(
					function(response) {
						$scope.quizName = response.data.name;
						$scope.quizDescription = response.data.description;
					}, 
					function(reason) {
						console.log(reason.data);
					}
			);
		}

		$scope.refreshQuestions = function() {
			if ($scope.quizId == 0)
				return;

			$http.get("/api/quizzes/" + $scope.quizId + "/questions/")
			.then(
					function(response) {
						$scope.questions = response.data;
						$scope.newQuestion = "";
					}, 
					function(reason) {
						console.log(reason.data);
					}
			);
		}
		
		$scope.saveQuiz = function(quizName, quizDescription) {
			var url = "/api/quizzes/";
			if ($scope.quizId != 0)
				url = url + $scope.quizId + "/";

			$http.post(url + "?name=" + quizName + "&description=" + quizDescription)
			.then(
					function(response) {
						console.log(response.data);
					}, 
					function(reason) {
						console.log(reason.data);
					}
			);
		}
		
		$scope.saveQuestion = function(questionId, questionText) {
			var url = "/api/questions";
			
			if (questionId != 0) {
				url = url + "/" + questionId;
			}

			$http.post(url + "?text=" + questionText + "&quiz_id=" + $scope.quizId)
			.then(
					function(response) {
						console.log(response.data);
						if (questionId == 0) {
							$scope.refreshQuestions();
						}
					}, 
					function(reason) {
						console.log(reason.data);
					}
			);
		}
		
		$scope.deleteQuestion = function(questionId) {
			if (questionId == 0)
				return;

			$http.delete("/api/questions/" + questionId)
			.then(
					function(response) {
						$scope.refreshQuestions();
					}, 
					function(reason) {
						console.log(reason.data);
					}
			);
		}
		
		$scope.refreshQuizData();
		$scope.refreshQuestions();
	};

	app.controller("EditCtrl", ["$scope", "$http", editCtrl]);

}());