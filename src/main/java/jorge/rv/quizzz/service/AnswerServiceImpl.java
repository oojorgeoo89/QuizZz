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
import jorge.rv.quizzz.repository.AnswerRepository;

@Service("AnswerService")
public class AnswerServiceImpl implements AnswerService {

	private static final Logger logger = LoggerFactory.getLogger(AnswerServiceImpl.class);
	private AnswerRepository answerRepository;
	
	@Autowired
	public AnswerServiceImpl(AnswerRepository answerRepository) {
		this.answerRepository = answerRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public Answer find(Long id) throws ResourceUnavailableException {
		Answer answer = answerRepository.findOne(id);
		
		if (answer == null) {
			logger.error("Answer " + id + " not found");
			throw new ResourceUnavailableException("Answer " + id + " not found");
		}
		
		return answer;
	}
	
	@Override
	@Transactional
	public Answer save(Answer answer) throws UnauthorizedActionException {
		int count = answerRepository.countByQuestion(answer.getQuestion());
		answer.setOrder(count + 1);
		
		return answerRepository.save(answer);
	}

	@Override
	@Transactional
	public Answer update(Answer newAnswer) throws ResourceUnavailableException, UnauthorizedActionException {
		Answer currentAnswer = find(newAnswer.getId());
		
		mergeAnswers(currentAnswer, newAnswer); 
		return answerRepository.save(currentAnswer);
	}

	@Override
	@Transactional
	public void delete(Long id) throws ResourceUnavailableException, UnauthorizedActionException {
		Answer currentAnswer = find(id);
		
		answerRepository.delete(currentAnswer);
	}
	
	private void mergeAnswers(Answer currentAnswer, Answer newAnswer) {
		currentAnswer.setText(newAnswer.getText());
		currentAnswer.setIscorrect(newAnswer.getIscorrect());
		
		if (newAnswer.getOrder() != null) {
			currentAnswer.setOrder(newAnswer.getOrder());
		}
	}

	@Override
	public Boolean checkAnswer(Answer answer) {
		return answer.getIscorrect();
	}

	@Override
	public List<Answer> findQuestionsByQuiz(Question question) {
		return answerRepository.findByQuestionOrderByOrderAsc(question);
	}

}
