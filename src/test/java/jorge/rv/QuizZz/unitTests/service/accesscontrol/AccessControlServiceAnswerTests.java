package jorge.rv.QuizZz.unitTests.service.accesscontrol;

import org.junit.Before;
import org.junit.Test;

import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Answer;
import jorge.rv.quizzz.model.AuthenticatedUser;
import jorge.rv.quizzz.model.Question;
import jorge.rv.quizzz.model.Quiz;
import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.service.accesscontrol.AccessControlService;
import jorge.rv.quizzz.service.accesscontrol.AccessControlServiceAnswer;

public class AccessControlServiceAnswerTests {

	// Service under test
	AccessControlService<Answer> service;

	User internalUser1 = new User();
	AuthenticatedUser user1 = new AuthenticatedUser(internalUser1);
	User internalUser2 = new User();
	AuthenticatedUser user2 = new AuthenticatedUser(internalUser2);

	Quiz quiz = new Quiz();
	Question question = new Question();
	Answer answer = new Answer();

	@Before
	public void before() {
		service = new AccessControlServiceAnswer();

		internalUser1.setId(1l);
		internalUser2.setId(2l);

		quiz.setCreatedBy(user1.getUser());
		question.setQuiz(quiz);
		answer.setQuestion(question);
	}

	@Test
	public void canUserCreateObject_userOwnsQuiz_shouldAllow() {
		service.canUserCreateObject(user1, answer);
	}

	@Test(expected = UnauthorizedActionException.class)
	public void canUserCreateObject_userDoesntOwnQuiz_shouldThrowException() {
		service.canUserCreateObject(user2, answer);
	}

	@Test
	public void canUserReadObject_userOwnsAnswer_shouldAllowRead() {
		service.canUserReadObject(user1, user1.getId());
	}

	@Test
	public void canUserReadObject_userDoentOwnAnswer_shouldAllowRead() {
		service.canUserReadObject(user2, user1.getId());
	}

	@Test
	public void canUserReadAllObjects_shouldNeverThrowException() {
		service.canUserReadAllObjects(user1);
	}

	@Test
	public void canUserUpdateObject_userOwnsAnswer_shouldAllowModification() {
		service.canUserUpdateObject(user1, answer);
	}

	@Test(expected = UnauthorizedActionException.class)
	public void canUserUpdateObject_userDoesntOwnAnswer_shouldThrowException() {
		service.canUserUpdateObject(user2, answer);
	}

	@Test
	public void canUserDeleteObject_userOwnsAnswer_shouldAllowModification() {
		service.canUserDeleteObject(user1, answer);
	}

	@Test(expected = UnauthorizedActionException.class)
	public void canUserDeleteObject_userDoesntOwnAnswer_shouldThrowException() {
		service.canUserDeleteObject(user2, answer);
	}

}
