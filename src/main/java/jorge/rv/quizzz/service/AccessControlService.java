package jorge.rv.quizzz.service;

import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Answer;
import jorge.rv.quizzz.model.AuthenticatedUser;
import jorge.rv.quizzz.model.Question;
import jorge.rv.quizzz.model.Quiz;
import jorge.rv.quizzz.model.User;

public interface AccessControlService {
	void checkUserPriviledges(AuthenticatedUser user, Quiz quiz) throws UnauthorizedActionException;
	void checkUserPriviledges(AuthenticatedUser user, Question question) throws UnauthorizedActionException;
	void checkUserPriviledges(AuthenticatedUser user, Answer answer) throws UnauthorizedActionException;
	void checkUserPriviledges(AuthenticatedUser user, User userToDelete) throws UnauthorizedActionException;
	
	void checkCurrentUserPriviledges(Quiz quiz) throws UnauthorizedActionException;
	void checkCurrentUserPriviledges(Question question) throws UnauthorizedActionException;
	void checkCurrentUserPriviledges(Answer answer) throws UnauthorizedActionException;
	void checkCurrentUserPriviledges(User userToDelete) throws UnauthorizedActionException;
}
