package jorge.rv.quizzz.service;

import java.util.List;

import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Answer;
import jorge.rv.quizzz.model.Question;

public interface QuestionService {
	Question save(Question question) throws UnauthorizedActionException;
	Question find(Long id) throws ResourceUnavailableException;
	Question update(Long id, Question question) throws ResourceUnavailableException, UnauthorizedActionException;
	void delete(Long id) throws  ResourceUnavailableException, UnauthorizedActionException;
	
	List<Answer> findAnswersByQuestion(Long id) throws ResourceUnavailableException;
}
