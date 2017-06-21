package jorge.rv.quizzz.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private static final Logger logger = LoggerFactory.getLogger(AccessControlServiceImpl.class);

	@Override
	public void checkUserPriviledges(AuthenticatedUser user, Quiz quiz) throws UnauthorizedActionException {
		if (!canUserModifyQuiz(user, quiz)) {
			logger.error("The user " + user.getId() + " can't modify quiz " + quiz.getId());
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
			logger.error("The user " + user.getId() + " can't delete user " + userToDelete.getId());
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
		if (user == null) {
			return false;
		}
		
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
