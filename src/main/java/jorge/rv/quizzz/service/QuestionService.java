package jorge.rv.quizzz.service;

import java.util.List;

import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Answer;
import jorge.rv.quizzz.model.Question;
import jorge.rv.quizzz.model.UserInfo;

public interface QuestionService {
	Question save(Question question, UserInfo user) throws UnauthorizedActionException;
	Question find(Long id) throws ResourceUnavailableException;
	Question update(Long id, Question question, UserInfo user) throws ResourceUnavailableException, UnauthorizedActionException;
	void delete(Long id, UserInfo user) throws  ResourceUnavailableException, UnauthorizedActionException;
	
	List<Answer> findAnswersByQuestion(Long id) throws ResourceUnavailableException;
}
