package jorge.rv.quizzz.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Answer;
import jorge.rv.quizzz.model.Question;
import jorge.rv.quizzz.repository.QuestionRepository;

@Service("QuestionService")
public class QuestionServiceImpl implements QuestionService {
	
	private static final Logger logger = LoggerFactory.getLogger(QuestionServiceImpl.class);
	private QuestionRepository questionRepository;
	private AccessControlService accessControlService;
	
	@Autowired
	public QuestionServiceImpl(QuestionRepository questionRepository, AccessControlService accessControlService) {
		this.questionRepository = questionRepository;
		this.accessControlService = accessControlService;
	}
	
	@Override
	@Transactional
	public Question save(Question question) throws UnauthorizedActionException {
		accessControlService.checkCurrentUserPriviledges(question);
		
		return questionRepository.save(question);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Question find(Long id) throws ResourceUnavailableException {
		Question question = questionRepository.findOne(id);
		
		if (question == null) {
			logger.error("Question " + id + " not found");
			throw new ResourceUnavailableException("Question " + id + " not found");
		}
		
		return question;
	}

	@Override
	@Transactional
	public Question update(Long id, Question newQuestion) throws ResourceUnavailableException, UnauthorizedActionException {
		Question currentQuestion = find(id);
		accessControlService.checkCurrentUserPriviledges(currentQuestion);
		
		mergeQuestions(currentQuestion, newQuestion);
		return questionRepository.save(currentQuestion);
	}
	
	@Override
	@Transactional
	public void delete(Long id) throws ResourceUnavailableException, UnauthorizedActionException {
		Question currentQuestion = find(id);
		accessControlService.checkCurrentUserPriviledges(currentQuestion);
		
		questionRepository.delete(currentQuestion);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Answer> findAnswersByQuestion(Long id) throws ResourceUnavailableException {
		Question q = find(id);
		return q.getAnswers();
	}

	private void mergeQuestions(Question currentQuestion, Question newQuestion) {
		currentQuestion.setText(newQuestion.getText());
	}

}
