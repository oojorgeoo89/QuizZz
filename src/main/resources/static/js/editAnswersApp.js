(function() {

	var app = angular.module("editAnswersApp", ['dndLists']);

	var editAnswersCtrl = function($scope, $http) {
		
		$scope.refreshAnswers = function() {
			if ($scope.questionId == 0)
				return;

			$http.get("/api/questions/" + $scope.questionId + "/answers")
			.then(
					function(response) {
						$scope.answers = response.data;
						$scope.newAnswer = "";
						$scope.refreshCorrectAnswer();
					}, 
					function(reason) {
						console.log(reason.data);
					}
			);
		}
		
		$scope.refreshCorrectAnswer = function() {
			var url = "/api/questions/" + $scope.questionId + "/correctAnswer";
			
			$http.get(url)
				.then(
						function(response) {
							if (response.data != "") {
								$scope.correctAnswer = response.data.id;
							} else if ($scope.answers.length > 0) {
								$scope.correctAnswer = $scope.answers[0].id;
							}
						}, 
						function(reason) {
							console.log(reason.data);
						}
				);
		}
				
		$scope.saveAnswer = function(answerId, answerText) {
			var url = "/api/answers";
			
			if (answerId != 0) {
				url = url + "/" + answerId;
			}

			$http.post(url + "?text=" + answerText + "&question_id=" + $scope.questionId)
			.then(
					function(response) {
						console.log(response.data);
						if (answerId == 0) {
							$scope.answers.push(response.data);
							$scope.newAnswer = "";
							
							if ($scope.answers.length == 1) {
								$scope.correctAnswer = $scope.answers[0].id;
							}
						}
					}, 
					function(reason) {
						console.log(reason.data);
					}
			);
		}
		
		$scope.saveAll = function(correctAnswer) {
			
			if (correctAnswer === undefined) {
				alert("Please, select a correct answer.");
				return;
			}
			
			$scope.saveAllAnswers();
			$scope.setCorrectAnswer(correctAnswer);
		}
		
		$scope.saveAllAnswers = function() {
			var url = "/api/answers/updateAll";
			
			$http.post(url + "?question_id=" + $scope.questionId,
					JSON.stringify($scope.answers))
				.then(
						function(response) {
							console.log(response.data);
						}, 
						function(reason) {
							console.log(reason.data);
						}
				);
		}
		
		$scope.setCorrectAnswer = function(correctAnswer) {
			var url = "/api/questions/" + $scope.questionId + "/correctAnswer";
			
			$http.post(url + "?answer_id=" + correctAnswer)
				.then(
						function(response) {
							console.log(response.data);
						}, 
						function(reason) {
							console.log(reason.data);
						}
				);
		}
		
		$scope.deleteAnswer = function(answerId) {
			if (answerId == 0)
				return;

			$http.delete("/api/answers/" + answerId)
			.then(
					function(response) {
						for (var i = 0; i < $scope.answers.length; i++) {
						    if ($scope.answers[i].id == answerId) {
						    	$scope.answers.splice(i, 1);
						    	break;
						    }
						}
					}, 
					function(reason) {
						console.log(reason.data);
					}
			);
		}
		
		$scope.refreshAnswers();
	};

	app.controller("EditAnswersCtrl", ["$scope", "$http", editAnswersCtrl]);

}());