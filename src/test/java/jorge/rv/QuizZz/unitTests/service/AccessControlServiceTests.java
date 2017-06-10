package jorge.rv.QuizZz.unitTests.service;

import org.junit.Before;
import org.junit.Test;

import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Answer;
import jorge.rv.quizzz.model.Question;
import jorge.rv.quizzz.model.Quiz;
import jorge.rv.quizzz.model.UserInfo;
import jorge.rv.quizzz.service.AccessControlService;
import jorge.rv.quizzz.service.AccessControlServiceImpl;

public class AccessControlServiceTests {

	AccessControlService service = new AccessControlServiceImpl();
	
	UserInfo user1 = new UserInfo();
	UserInfo user2 = new UserInfo();
	
	Quiz quiz = new Quiz();
	Question question = new Question();
	Answer answer = new Answer();

	@Before
	public void before() {
		user1.setId(1l);
		user2.setId(2l);
		
		quiz.setCreatedBy(user1);
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
