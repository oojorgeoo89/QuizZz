package jorge.rv.quizzz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Answer;
import jorge.rv.quizzz.model.UserInfo;
import jorge.rv.quizzz.repository.AnswerRepository;

@Service("AnswerService")
public class AnswerServiceImpl implements AnswerService {

	private AnswerRepository answerRepository;
	private AccessControlService accessControlService;
	
	@Autowired
	public AnswerServiceImpl(AnswerRepository answerRepository, AccessControlService accessControlService) {
		this.answerRepository = answerRepository;
		this.accessControlService = accessControlService;
	}

	@Override
	@Transactional(readOnly = true)
	public Answer find(Long id) throws ResourceUnavailableException {
		Answer answer = answerRepository.findOne(id);
		
		if (answer == null) {
			throw new ResourceUnavailableException();
		}
		
		return answer;
	}
	
	@Override
	@Transactional
	public Answer save(Answer answer, UserInfo user) throws UnauthorizedActionException {
		accessControlService.checkUserPriviledges(user, answer);
		
		return answerRepository.save(answer);
	}

	@Override
	@Transactional
	public Answer update(Long id, Answer newAnswer, UserInfo user) throws ResourceUnavailableException, UnauthorizedActionException {
		Answer currentAnswer = find(id);
		accessControlService.checkUserPriviledges(user, currentAnswer);
		
		mergeAnswers(currentAnswer, newAnswer); 
		return answerRepository.save(currentAnswer);
	}

	@Override
	@Transactional
	public void delete(Long id, UserInfo user) throws ResourceUnavailableException, UnauthorizedActionException {
		Answer currentAnswer = find(id);
		accessControlService.checkUserPriviledges(user, currentAnswer);
		
		answerRepository.delete(currentAnswer);
	}
	
	private void mergeAnswers(Answer currentAnswer, Answer newAnswer) {
		currentAnswer.setText(newAnswer.getText());
		currentAnswer.setIscorrect(newAnswer.getIscorrect());
	}

}
