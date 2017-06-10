package jorge.rv.quizzz.service;

import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Answer;
import jorge.rv.quizzz.model.UserInfo;

public interface AnswerService {
	Answer save(Answer answer, UserInfo user) throws UnauthorizedActionException;
	Answer find(Long id) throws ResourceUnavailableException;
	Answer update(Long id, Answer newAnswer, UserInfo user) throws UnauthorizedActionException, ResourceUnavailableException;
	void delete(Long id, UserInfo user) throws UnauthorizedActionException, ResourceUnavailableException;
}
