package jorge.rv.QuizZz.unitTests.service;

import org.junit.Before;
import org.junit.Test;

import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Answer;
import jorge.rv.quizzz.model.AuthenticatedUser;
import jorge.rv.quizzz.model.Question;
import jorge.rv.quizzz.model.Quiz;
import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.service.AccessControlService;
import jorge.rv.quizzz.service.AccessControlServiceImpl;

public class AccessControlServiceTests {

	AccessControlService service = new AccessControlServiceImpl();
	
	User internalUser1 = new User();
	AuthenticatedUser user1 = new AuthenticatedUser(internalUser1);
	User internalUser2 = new User();
	AuthenticatedUser user2 = new AuthenticatedUser(internalUser2);
	
	Quiz quiz = new Quiz();
	Question question = new Question();
	Answer answer = new Answer();

	@Before
	public void before() {
		internalUser1.setId(1l);
		internalUser2.setId(2l);
		
		quiz.setCreatedBy(user1.getUser());
		question.setQuiz(quiz);
		answer.setQuestion(question);
	}
	
	@Test
	public void userCanModifyQuiz() throws UnauthorizedActionException {
		service.checkUserPriviledges(user1, quiz);
	}
	
	@Test(expected = UnauthorizedActionException.class)
	public void userCanNotModifyQuiz() throws UnauthorizedActionException {
		service.checkUserPriviledges(user2, quiz);
	}
	
	@Test
	public void userCanModifyQuestion() throws UnauthorizedActionException {
		service.checkUserPriviledges(user1, question);
	}
	
	@Test(expected = UnauthorizedActionException.class)
	public void userCanNotModifyQuestion() throws UnauthorizedActionException {
		service.checkUserPriviledges(user2, question);
	}
	
	@Test
	public void userCanModifyAnswer() throws UnauthorizedActionException {
		service.checkUserPriviledges(user1, answer);
	}
	
	@Test(expected = UnauthorizedActionException.class)
	public void userCanNotModifyAnswer() throws UnauthorizedActionException {
		service.checkUserPriviledges(user2, answer);
	}
}
