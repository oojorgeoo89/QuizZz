package jorge.rv.quizzz.service.accesscontrol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.AuthenticatedUser;
import jorge.rv.quizzz.model.BaseModel;
import jorge.rv.quizzz.model.UserOwned;

public abstract class AccessControlServiceUserOwned<T extends BaseModel & UserOwned>
		implements AccessControlService<T> {

	private static final Logger logger = LoggerFactory.getLogger(AccessControlServiceUserOwned.class);

	@Override
	public void canUserCreateObject(AuthenticatedUser user, T object) throws UnauthorizedActionException {
		if (!canUserModifyObject(user, object)) {
			logger.error("The user " + user.getId() + " can't create this object");
			throw new UnauthorizedActionException(
					"User " + user.getUsername() + " is not allowed to perform this action");
		}
	}

	@Override
	public void canCurrentUserCreateObject(T object) throws UnauthorizedActionException {
		canUserCreateObject(getCurrentUser(), object);
	}

	@Override
	public void canUserReadObject(AuthenticatedUser user, Long id) throws UnauthorizedActionException {
		// By default, anyone can read objects
	}

	@Override
	public void canCurrentUserReadObject(Long id) throws UnauthorizedActionException {
		canUserReadObject(getCurrentUser(), id);
	}

	@Override
	public void canUserReadAllObjects(AuthenticatedUser user) throws UnauthorizedActionException {
		// By default, anyone can read objects
	}

	@Override
	public void canCurrentUserReadAllObjects() throws UnauthorizedActionException {
		canUserReadAllObjects(getCurrentUser());
	}

	@Override
	public void canUserUpdateObject(AuthenticatedUser user, T object) throws UnauthorizedActionException {
		if (!canUserModifyObject(user, object)) {
			logger.error("The user " + ((user != null) ? user.getId() : "null") + " can't update this object");
			throw new UnauthorizedActionException("User " + ((user != null) ? user.getUsername() : "null")
					+ " is not allowed to perform this action");
		}
	}

	@Override
	public void canCurrentUserUpdateObject(T object) throws UnauthorizedActionException {
		canUserUpdateObject(getCurrentUser(), object);
	}

	@Override
	public void canUserDeleteObject(AuthenticatedUser user, T object) throws UnauthorizedActionException {
		if (!canUserModifyObject(user, object)) {
			logger.error("The user " + ((user != null) ? user.getId() : "null") + " can't delete this object");
			throw new UnauthorizedActionException("User " + ((user != null) ? user.getUsername() : "null")
					+ " is not allowed to perform this action");
		}
	}

	@Override
	public void canCurrentUserDeleteObject(T object) throws UnauthorizedActionException {
		canUserDeleteObject(getCurrentUser(), object);
	}

	private boolean canUserModifyObject(AuthenticatedUser user, UserOwned obj) {
		if (user == null) {
			return false;
		}

		return obj.getUser().equals(user.getUser());
	}

	private AuthenticatedUser getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication.getPrincipal() == null || authentication.getPrincipal() instanceof String) {
			return null;
		}

		return (AuthenticatedUser) authentication.getPrincipal();
	}
}
