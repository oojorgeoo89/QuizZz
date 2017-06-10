package jorge.rv.quizzz.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Question;
import jorge.rv.quizzz.model.Quiz;
import jorge.rv.quizzz.model.UserInfo;

public interface QuizService {
	Quiz save(Quiz quiz, UserInfo user);
	Page<Quiz> findAll(Pageable pageable);
	Quiz find(Long id) throws ResourceUnavailableException;
	Quiz update(Long id, Quiz quiz, UserInfo user) throws ResourceUnavailableException, UnauthorizedActionException;
	void delete(Long id, UserInfo user) throws ResourceUnavailableException, UnauthorizedActionException;
	
	List<Question> findQuestionsByQuiz(Long id) throws ResourceUnavailableException;
}
