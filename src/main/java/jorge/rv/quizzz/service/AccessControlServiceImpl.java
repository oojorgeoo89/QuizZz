package jorge.rv.quizzz.service;

import org.springframework.stereotype.Service;

import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Answer;
import jorge.rv.quizzz.model.Question;
import jorge.rv.quizzz.model.Quiz;
import jorge.rv.quizzz.model.UserInfo;

@Service("AccessControlService")
public class AccessControlServiceImpl implements AccessControlService {

	@Override
	public void checkUserPriviledges(UserInfo user, Quiz quiz) throws UnauthorizedActionException {
		if (!canUserModifyQuiz(user, quiz)) {
			throw new UnauthorizedActionException();
		}
	}

	@Override
	public void checkUserPriviledges(UserInfo user, Question question) throws UnauthorizedActionException {
		checkUserPriviledges(user, question.getQuiz());
	}

	@Override
	public void checkUserPriviledges(UserInfo user, Answer answer) throws UnauthorizedActionException {
		checkUserPriviledges(user, answer.getQuestion());
	}
	
	@Override
	public void checkUserPriviledges(UserInfo user, UserInfo userToDelete) throws UnauthorizedActionException {
		if (!user.equals(userToDelete)) {
			throw new UnauthorizedActionException();
		}
	}

	
	private boolean canUserModifyQuiz(UserInfo user, Quiz quiz) {
		return quiz.getCreatedBy().equals(user);
	}


}
