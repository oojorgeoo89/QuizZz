package jorge.rv.quizzz.service;

import java.util.List;

import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Answer;
import jorge.rv.quizzz.model.Question;
import jorge.rv.quizzz.model.Quiz;

public interface QuestionService {
	Question save(Question question) throws UnauthorizedActionException;
	Question find(Long id) throws ResourceUnavailableException;
	Question update(Question question) throws ResourceUnavailableException, UnauthorizedActionException;
	void delete(Long id) throws  ResourceUnavailableException, UnauthorizedActionException;
	
	List<Answer> findAnswersByQuestion(Long id) throws ResourceUnavailableException;
	Boolean checkAnswer(Question question, Long selectedAnswer);
	List<Question> findQuestionsByQuiz(Quiz quiz);
}
