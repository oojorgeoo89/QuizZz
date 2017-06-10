package jorge.rv.quizzz.service;

import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Answer;
import jorge.rv.quizzz.model.Question;
import jorge.rv.quizzz.model.Quiz;
import jorge.rv.quizzz.model.UserInfo;

public interface AccessControlService {
	void checkUserPriviledges(UserInfo user, Quiz quiz) throws UnauthorizedActionException;
	void checkUserPriviledges(UserInfo user, Question question) throws UnauthorizedActionException;
	void checkUserPriviledges(UserInfo user, Answer answer) throws UnauthorizedActionException;
	void checkUserPriviledges(UserInfo user, UserInfo userToDelete) throws UnauthorizedActionException;
}
