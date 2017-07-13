package jorge.rv.quizzz.service;

import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Answer;

public interface AnswerService {
	Answer save(Answer answer) throws UnauthorizedActionException;
	Answer find(Long id) throws ResourceUnavailableException;
	Answer update(Long id, Answer newAnswer) throws UnauthorizedActionException, ResourceUnavailableException;
	void delete(Long id) throws UnauthorizedActionException, ResourceUnavailableException;
	Boolean checkAnswer(Answer answer);
}
