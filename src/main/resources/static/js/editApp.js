(function() {

	var app = angular.module("editApp", ['dndLists']);

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
		
		$scope.saveQuiz = function() {
			var url = "/api/quizzes/";
			
			if ($scope.quizId != 0)
				url = url + $scope.quizId + "/";

			$http.post(url + "?name=" + $scope.quizName + "&description=" + $scope.quizDescription)
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
							$scope.questions.push(response.data);
							$scope.newQuestion = "";
						}
					}, 
					function(reason) {
						console.log(reason.data);
					}
			);
		}
		
		$scope.saveAll = function() {
			var url = "/api/questions/updateAll";
			
			$scope.saveQuiz();

			$http.post(url + "?quiz_id=" + $scope.quizId,
				JSON.stringify($scope.questions))
			.then(
					function(response) {
						console.log(response.data);
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
						for (var i = 0; i < $scope.questions.length; i++) {
						    if ($scope.questions[i].id == questionId) {
						    	$scope.questions.splice(i, 1);
						    	break;
						    }
						}
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