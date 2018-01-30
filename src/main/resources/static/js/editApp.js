(function() {

	var app = angular.module("editApp", ['dndLists']);

	var editCtrl = function($scope, $http) {
		
		$scope.isQuizSaving = false;
		$scope.isPublishing = false;
		
		$scope.refreshQuizData = function() {
			if ($scope.quizId == 0)
				return;

			$http.get("/api/quizzes/" + $scope.quizId)
			.then(
					function(response) {
						$scope.quiz = response.data;
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

			$http.post(url + "?name=" + $scope.quiz.name + "&description=" + $scope.quiz.description)
			.then(
					function(response) {
						console.log(response.data);
					}, 
					function(reason) {
						alert(reason.data);
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
			
			$scope.isQuizSaving = true;
			$scope.saveQuiz();

			$http.post(url + "?quiz_id=" + $scope.quizId,
				JSON.stringify($scope.questions))
			.then(
					function(response) {
						console.log(response.data);
						$scope.isQuizSaving = false;
					}, 
					function(reason) {
						alert(reason.data);
						console.log(reason.data);
						$scope.isQuizSaving = false;
					}
			);
		}
		
		$scope.publish = function() {
			var url = "/api/quizzes/" + $scope.quizId + "/publish";
			
			$scope.isPublishing = true;
			$scope.saveAll();

			$http.post(url)
			.then(
					function(response) {
						console.log(response.data);
						$scope.quiz.isPublished=false;
						$scope.isPublishing = false;
					}, 
					function(reason) {
						console.log(reason.data);
						alert("Please, set up at least one question with answers");
						$scope.isPublishing = false;
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