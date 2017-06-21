package jorge.rv.quizzz.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Question;
import jorge.rv.quizzz.model.Quiz;
import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.repository.QuizRepository;

@Service("QuizService")
public class QuizServiceImpl implements QuizService {

	private static final Logger logger = LoggerFactory.getLogger(QuizServiceImpl.class);
	private QuizRepository quizRepository;
	private AccessControlService accessControlService;
	
	@Autowired
	public QuizServiceImpl(QuizRepository quizRepository, AccessControlService accessControlService) {
		this.quizRepository = quizRepository;
		this.accessControlService = accessControlService;
	}
	
	@Override
	@Transactional
	public Quiz save(Quiz quiz, User user) {
		quiz.setCreatedBy(user);
		return quizRepository.save(quiz);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Quiz> findAll(Pageable pageable) {
		return quizRepository.findAll(pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Quiz find(Long id) throws ResourceUnavailableException {
		Quiz quiz = quizRepository.findOne(id);
		
		if (quiz == null) {
			logger.error("Quiz " + id + " not found");
			throw new ResourceUnavailableException();
		}
		
		return quiz;
	}

	@Override
	@Transactional
	public Quiz update(Long id, Quiz newQuiz) throws UnauthorizedActionException, ResourceUnavailableException {
		Quiz currentQuiz = find(id);
		accessControlService.checkCurrentUserPriviledges(currentQuiz);
		
		mergeQuizzes(currentQuiz, newQuiz);
		return quizRepository.save(currentQuiz);
	}

	@Override
	@Transactional
	public void delete(Long id) throws ResourceUnavailableException, UnauthorizedActionException {
		Quiz currentQuiz = find(id);
		accessControlService.checkCurrentUserPriviledges(currentQuiz);
		
		quizRepository.delete(currentQuiz);
	}

	@Override
	@Transactional
	public List<Question> findQuestionsByQuiz(Long id) throws ResourceUnavailableException {
		Quiz q = find(id);
		return q.getQuestions();
	}	

	private void mergeQuizzes(Quiz currentQuiz, Quiz newQuiz) {
		currentQuiz.setName(newQuiz.getName());
		currentQuiz.setDescription(newQuiz.getDescription());
	}

	@Override
	public Page<Quiz> search(String query, Pageable pageable) {
		return quizRepository.searchByName(query, pageable);
	}

	@Override
	public Page<Quiz> findQuizzesByUser(User user, Pageable pageable) {
		return quizRepository.findByCreatedBy(user, pageable);
	}	
	
}
