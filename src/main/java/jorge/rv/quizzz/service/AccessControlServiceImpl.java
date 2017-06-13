package jorge.rv.quizzz.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Answer;
import jorge.rv.quizzz.model.AuthenticatedUser;
import jorge.rv.quizzz.model.Question;
import jorge.rv.quizzz.model.Quiz;
import jorge.rv.quizzz.model.User;

@Service("AccessControlService")
public class AccessControlServiceImpl implements AccessControlService {

	@Override
	public void checkUserPriviledges(AuthenticatedUser user, Quiz quiz) throws UnauthorizedActionException {
		if (!canUserModifyQuiz(user, quiz)) {
			throw new UnauthorizedActionException();
		}
	}

	@Override
	public void checkUserPriviledges(AuthenticatedUser user, Question question) throws UnauthorizedActionException {
		checkUserPriviledges(user, question.getQuiz());
	}

	@Override
	public void checkUserPriviledges(AuthenticatedUser user, Answer answer) throws UnauthorizedActionException {
		checkUserPriviledges(user, answer.getQuestion());
	}
	
	@Override
	public void checkUserPriviledges(AuthenticatedUser user, User userToDelete) throws UnauthorizedActionException {
		if (!user.getUser().equals(userToDelete)) {
			throw new UnauthorizedActionException();
		}
	}

	@Override
	public void checkCurrentUserPriviledges(Quiz quiz) throws UnauthorizedActionException {
		checkUserPriviledges(getCurrentUser(), quiz);
	}

	@Override
	public void checkCurrentUserPriviledges(Question question) throws UnauthorizedActionException {
		checkUserPriviledges(getCurrentUser(), question);
	}

	@Override
	public void checkCurrentUserPriviledges(Answer answer) throws UnauthorizedActionException {
		checkUserPriviledges(getCurrentUser(), answer);
	}

	@Override
	public void checkCurrentUserPriviledges(User userToDelete) throws UnauthorizedActionException {
		checkUserPriviledges(getCurrentUser(), userToDelete);
	}
	
	private boolean canUserModifyQuiz(AuthenticatedUser user, Quiz quiz) {
		return quiz.getCreatedBy().equals(user.getUser());
	}
	
	private AuthenticatedUser getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication.getPrincipal() == null) {
			return null;
		}
		
		return (AuthenticatedUser) authentication.getPrincipal();
	}
}
