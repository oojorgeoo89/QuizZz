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
						}
					}, 
					function(reason) {
						console.log(reason.data);
					}
			);
		}
		
		$scope.saveAll = function() {
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