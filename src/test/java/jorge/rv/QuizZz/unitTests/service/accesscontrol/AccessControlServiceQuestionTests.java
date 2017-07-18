package jorge.rv.QuizZz.unitTests.service.accesscontrol;

import org.junit.Before;
import org.junit.Test;

import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.AuthenticatedUser;
import jorge.rv.quizzz.model.Question;
import jorge.rv.quizzz.model.Quiz;
import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.service.accesscontrol.AccessControlService;
import jorge.rv.quizzz.service.accesscontrol.AccessControlServiceQuestion;

public class AccessControlServiceQuestionTests {

	// Service under test
	AccessControlService<Question> service;

	User internalUser1 = new User();
	AuthenticatedUser user1 = new AuthenticatedUser(internalUser1);
	User internalUser2 = new User();
	AuthenticatedUser user2 = new AuthenticatedUser(internalUser2);

	Quiz quiz = new Quiz();
	Question question = new Question();

	@Before
	public void before() {
		service = new AccessControlServiceQuestion();

		internalUser1.setId(1l);
		internalUser2.setId(2l);

		quiz.setCreatedBy(user1.getUser());
		question.setQuiz(quiz);
	}

	@Test
	public void canUserCreateObject_userOwnsQuiz_shouldAllow() {
		service.canUserCreateObject(user1, question);
	}

	@Test(expected = UnauthorizedActionException.class)
	public void canUserCreateObject_userDoesntOwnQuiz_shouldThrowException() {
		service.canUserCreateObject(user2, question);
	}

	@Test
	public void canUserReadObject_userOwnsQuestion_shouldAllowRead() {
		service.canUserReadObject(user1, user1.getId());
	}

	@Test
	public void canUserReadObject_userDoentOwnQuestion_shouldAllowRead() {
		service.canUserReadObject(user2, user1.getId());
	}

	@Test
	public void canUserReadAllObjects_shouldNeverThrowException() {
		service.canUserReadAllObjects(user1);
	}

	@Test
	public void canUserUpdateObject_userOwnsQuestion_shouldAllowModification() {
		service.canUserUpdateObject(user1, question);
	}

	@Test(expected = UnauthorizedActionException.class)
	public void canUserUpdateObject_userDoesntOwnQuestion_shouldThrowException() {
		service.canUserUpdateObject(user2, question);
	}

	@Test
	public void canUserDeleteObject_userOwnsQuestion_shouldAllowModification() {
		service.canUserDeleteObject(user1, question);
	}

	@Test(expected = UnauthorizedActionException.class)
	public void canUserDeleteObject_userDoesntOwnQuestion_shouldThrowException() {
		service.canUserDeleteObject(user2, question);
	}

}
