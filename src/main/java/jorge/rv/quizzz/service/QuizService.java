package jorge.rv.quizzz.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Question;
import jorge.rv.quizzz.model.Quiz;
import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.model.support.AnswersBundle;
import jorge.rv.quizzz.model.support.Results;

public interface QuizService {
	Quiz save(Quiz quiz, User user);
	Page<Quiz> findAll(Pageable pageable);
	Quiz find(Long id) throws ResourceUnavailableException;
	Quiz update(Quiz quiz) throws ResourceUnavailableException, UnauthorizedActionException;
	void delete(Long id) throws ResourceUnavailableException, UnauthorizedActionException;
	
	List<Question> findQuestionsByQuiz(Long id) throws ResourceUnavailableException;
	Page<Quiz> search(String query, Pageable pageable);
	Page<Quiz> findQuizzesByUser(User user, Pageable pageable);
	Results checkAnswers(Long quiz_id, List<AnswersBundle> answersBundle);
}
