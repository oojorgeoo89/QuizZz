(function() {

	var app = angular.module("editApp", []);

	var editCtrl = function($scope, $http) {
		
		$scope.refreshQuizData = function() {
			if ($scope.listId == 0)
				return;

			$http.get("/v1/quiz/" + $scope.listId)
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
			if ($scope.listId == 0)
				return;

			$http.get("/v1/quiz/" + $scope.listId + "/questions/")
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
			var url = "/v1/quiz/";
			if ($scope.listId != 0)
				url = url + $scope.listId + "/";

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
			var url = "/v1/quiz/" + $scope.listId + "/questions/";
			
			if (questionId != 0) {
				url = url + questionId;
			}

			$http.post(url + "?text=" + questionText)
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

			$http.delete("/v1/quiz/" + $scope.listId + "/questions/" + questionId)
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