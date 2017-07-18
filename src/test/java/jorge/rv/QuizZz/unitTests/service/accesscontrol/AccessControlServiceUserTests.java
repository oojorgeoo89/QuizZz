package jorge.rv.QuizZz.unitTests.service.accesscontrol;

import org.junit.Before;
import org.junit.Test;

import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.AuthenticatedUser;
import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.service.accesscontrol.AccessControlServiceUser;

public class AccessControlServiceUserTests {

	// Service under test
	AccessControlServiceUser service;

	User internalUser1 = new User();
	AuthenticatedUser user1 = new AuthenticatedUser(internalUser1);
	User internalUser2 = new User();
	AuthenticatedUser user2 = new AuthenticatedUser(internalUser2);

	@Before
	public void before() {
		service = new AccessControlServiceUser();

		internalUser1.setId(1l);
		internalUser2.setId(2l);
	}

	@Test
	public void canUserCreateObject_shouldAlwaysAllow() {
		service.canUserCreateObject(null, user1.getUser());
	}

	@Test
	public void canUserReadObject_userOwnsUser_shouldAllowRead() {
		service.canUserReadObject(user1, user1.getId());
	}

	@Test
	public void canUserReadObject_userDoentOwnUser_shouldAllowRead() {
		service.canUserReadObject(user2, user1.getId());
	}

	@Test
	public void canUserReadAllObjects_shouldNeverThrowException() {
		service.canUserReadAllObjects(user1);
	}

	@Test
	public void canUserUpdateObject_userOwnsUser_shouldAllowModification() {
		internalUser1.setEnabled(true);

		service.canUserUpdateObject(user1, user1.getUser());
	}

	@Test(expected = UnauthorizedActionException.class)
	public void canUserUpdateObject_userDoesntOwnFullyRegisteredUser_shouldAllowModification() {
		internalUser1.setEnabled(true);

		service.canUserUpdateObject(user2, user1.getUser());
	}

	@Test
	public void canUserUpdateObject_UnauthenticatedModifiesUserNotFullyRegistered_shouldAllowModification() {
		internalUser1.setEnabled(false);

		service.canUserUpdateObject(null, user1.getUser());
	}

	@Test
	public void canUserDeleteObject_userOwnsUser_shouldAllowModification() {
		service.canUserDeleteObject(user1, user1.getUser());
	}

	@Test(expected = UnauthorizedActionException.class)
	public void canUserDeleteObject_userDoesntOwnUser_shouldThrowException() {
		service.canUserDeleteObject(user2, user1.getUser());
	}

}
