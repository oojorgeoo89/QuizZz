(function() {

	var app = angular.module("playQuizApp", []);

	var playQuizCtrl = function($scope, $http) {	
		
		$scope.Math = window.Math;
		
		var questions = [];
		var answers = [];
		
		$scope.totalQuestions = 0;
		$scope.questionCount = 0;
		
		$scope.isPlaying = true;
		
		$scope.initialize = function() {
			if ($scope.quizId == 0)
				return;
			
			$scope.playing = true;
		
			$http.get("/api/quizzes/" + $scope.quizId + "/questions?onlyValid=true")
				.then(
					function(response) {
						questions = questions.concat(response.data);
						$scope.totalQuestions = questions.length;
						$scope.setQuestion($scope.questionCount);
					}, 
					function(reason) {
						$scope.error = "Could not fetch the data.";
					}
				);
		}
		
		$scope.setQuestion = function(questionNumber) {

			$http.get("/api/questions/" + questions[questionNumber].id + "/answers")
				.then(
					function(response) {
						$scope.currentQuestion = questions[questionNumber];
						$scope.currentAnswers = response.data;
					}, 
					function(reason) {
						$scope.error = "Could not fetch the data.";
					}
				);
		}
		
		$scope.answerQuestion = function(selection) {
			if (selection === undefined) {
				alert("Please, choose an answer");
				return;
			}
						
			answers.push({
				question: $scope.currentQuestion.id,
				selectedAnswer: selection
			});
			
			$scope.questionCount++;
			if ($scope.questionCount == $scope.totalQuestions) {
				$scope.submitAnswers();
			} else {
				$scope.setQuestion($scope.questionCount);
			}	
		}
		
		$scope.submitAnswers = function() {
			$http.post("/api/quizzes/" + $scope.quizId + "/submitAnswers",
					JSON.stringify(answers))
			.then(
				function(response) {
					$scope.results = response.data;
					$scope.playing = false;
				}, 
				function(reason) {
					$scope.error = "Could not fetch the data.";
				}
			);
		}
	
		$scope.initialize();	
	};

	app.controller("PlayQuizCtrl", ["$scope", "$http", playQuizCtrl]);

}());