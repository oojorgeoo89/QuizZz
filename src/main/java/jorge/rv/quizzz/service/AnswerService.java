package jorge.rv.quizzz.service;

import java.util.List;

import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Answer;
import jorge.rv.quizzz.model.Question;

public interface AnswerService {
	Answer save(Answer answer) throws UnauthorizedActionException;
	Answer find(Long id) throws ResourceUnavailableException;
	Answer update(Answer newAnswer) throws UnauthorizedActionException, ResourceUnavailableException;
	void delete(Long id) throws UnauthorizedActionException, ResourceUnavailableException;
	Boolean checkAnswer(Answer answer);
	List<Answer> findQuestionsByQuiz(Question question);
}
