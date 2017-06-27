package jorge.rv.quizzz.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.AuthenticatedUser;
import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.model.UserOwned;

@Service("AccessControlService")
public class AccessControlServiceImpl implements AccessControlService {
	
	private static final Logger logger = LoggerFactory.getLogger(AccessControlServiceImpl.class);

	@Override
	public void checkUserPriviledges(AuthenticatedUser user, UserOwned obj) throws UnauthorizedActionException {
		if (!canUserModifyQuiz(user, obj)) {
			logger.error("The user " + user.getId() + " can't modify object owned by " + obj.getUser().getId());
			throw new UnauthorizedActionException("User " + user.getUsername() + " is not allowed to perform this action");
		}
	}
	
	@Override
	public void checkCurrentUserPriviledges(UserOwned obj) throws UnauthorizedActionException {
		checkUserPriviledges(getCurrentUser(), obj);
	}
	
	private boolean canUserModifyQuiz(AuthenticatedUser user, UserOwned obj) {
		if (user == null) {
			return false;
		}
		
		return obj.getUser().equals(user.getUser());
	}

	@Override
	public void checkUserPriviledges(AuthenticatedUser user, User userToDelete) throws UnauthorizedActionException {
		if (!user.getUser().equals(userToDelete)) {
			logger.error("The user " + user.getId() + " can't delete user " + userToDelete.getId());
			throw new UnauthorizedActionException("User " + user.getUsername() + " is not allowed to perform this action");
		}
	}


	@Override
	public void checkCurrentUserPriviledges(User userToDelete) throws UnauthorizedActionException {
		checkUserPriviledges(getCurrentUser(), userToDelete);
	}
	
	private AuthenticatedUser getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication.getPrincipal() == null) {
			return null;
		}
		
		return (AuthenticatedUser) authentication.getPrincipal();
	}
}
