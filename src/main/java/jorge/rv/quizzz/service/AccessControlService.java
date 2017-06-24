package jorge.rv.quizzz.service;

import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.AuthenticatedUser;
import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.model.UserOwned;

public interface AccessControlService {
	void checkUserPriviledges(AuthenticatedUser user, UserOwned obj) throws UnauthorizedActionException;
	void checkCurrentUserPriviledges(UserOwned obj) throws UnauthorizedActionException;
	
	void checkCurrentUserPriviledges(User userToDelete) throws UnauthorizedActionException;
	void checkUserPriviledges(AuthenticatedUser user, User userToDelete) throws UnauthorizedActionException;
}
