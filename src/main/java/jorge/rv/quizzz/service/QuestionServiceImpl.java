package jorge.rv.quizzz.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Answer;
import jorge.rv.quizzz.model.Question;
import jorge.rv.quizzz.model.UserInfo;
import jorge.rv.quizzz.repository.QuestionRepository;

@Service("QuestionService")
public class QuestionServiceImpl implements QuestionService {
	
	private QuestionRepository questionRepository;
	private AccessControlService accessControlService;
	
	@Autowired
	public QuestionServiceImpl(QuestionRepository questionRepository, AccessControlService accessControlService) {
		this.questionRepository = questionRepository;
		this.accessControlService = accessControlService;
	}
	
	@Transactional
	public Question save(Question question, UserInfo user) throws UnauthorizedActionException {
		accessControlService.checkUserPriviledges(user, question);
		
		return questionRepository.save(question);
	}

	@Transactional(readOnly = true)
	public Question find(Long id) throws ResourceUnavailableException {
		Question question = questionRepository.findOne(id);
		
		if (question == null) {
			throw new ResourceUnavailableException();
		}
		
		return question;
	}

	@Override
	@Transactional
	public Question update(Long id, Question newQuestion, UserInfo user) throws ResourceUnavailableException, UnauthorizedActionException {
		Question currentQuestion = find(id);
		accessControlService.checkUserPriviledges(user, currentQuestion);
		
		mergeQuestions(currentQuestion, newQuestion);
		return questionRepository.save(currentQuestion);
	}
	
	@Transactional
	public void delete(Long id, UserInfo user) throws ResourceUnavailableException, UnauthorizedActionException {
		Question currentQuestion = find(id);
		accessControlService.checkUserPriviledges(user, currentQuestion);
		
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
