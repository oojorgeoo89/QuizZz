package jorge.rv.QuizZz.unitTests.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import jorge.rv.quizzz.exceptions.QuizZzException;
import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Answer;
import jorge.rv.quizzz.model.Question;
import jorge.rv.quizzz.model.Quiz;
import jorge.rv.quizzz.model.UserInfo;
import jorge.rv.quizzz.repository.AnswerRepository;
import jorge.rv.quizzz.service.AccessControlService;
import jorge.rv.quizzz.service.AnswerService;
import jorge.rv.quizzz.service.AnswerServiceImpl;

public class AnswerServiceTests {

	AnswerService service;
	
	//Mocks
	AccessControlService accessControlService;
	AnswerRepository answerRepository;
	
	UserInfo user = new UserInfo();
	Quiz quiz = new Quiz();
	Question question = new Question();
	Answer answer = new Answer();
	
	@Before
	public void before() {
		accessControlService = mock(AccessControlService.class);
		answerRepository = mock(AnswerRepository.class);
		service = new AnswerServiceImpl(answerRepository, accessControlService);
		
		user.setId(1l);
		quiz.setCreatedBy(user);
		quiz.setId(1l);
		question.setQuiz(quiz);
		answer.setQuestion(question);
		answer.setId(1l);
		
	}
	
	// Save
	
	@Test
	public void testSaveAnswerShouldSave() throws UnauthorizedActionException {
		service.save(answer, user);
		verify(answerRepository, times(1)).save(answer);
	}
	
	// Find
	
	@Test
	public void findExistingAnswer() throws ResourceUnavailableException {
		when(answerRepository.findOne(answer.getId())).thenReturn(answer);
		
		Answer returned = service.find(answer.getId());
		
		verify(answerRepository, times(1)).findOne(answer.getId());
		assertNotNull(returned);
		assertEquals(answer.getId(), returned.getId());
	}
	
	@Test(expected = ResourceUnavailableException.class)
	public void findNonExistingQuestion() throws ResourceUnavailableException {
		when(answerRepository.findOne(quiz.getId())).thenReturn(null);
		
		service.find(quiz.getId());
	}
	
	// Update
	
	@Test
	public void testUpdateShouldUpdate() throws QuizZzException {
		answer.setText("test");
		
		when(answerRepository.findOne(answer.getId())).thenReturn(answer);
		when(answerRepository.save(answer)).thenReturn(answer);
		Answer returned = service.update(answer.getId(), answer, answer.getQuestion().getQuiz().getCreatedBy());
		
		verify(answerRepository, times(1)).save(answer);
		assertEquals(returned.getText(), answer.getText());
	}
	
	@Test(expected = ResourceUnavailableException.class)
	public void testUpdateUnexistentAnswer() throws QuizZzException {
		answer.setText("test");
		
		when(answerRepository.findOne(answer.getId())).thenReturn(null);
		
		service.update(answer.getId(), answer, answer.getQuestion().getQuiz().getCreatedBy());
	}
	
	@Test(expected = UnauthorizedActionException.class)
	public void testUpdateFromWrongUser() throws QuizZzException {
		answer.setText("test");
		
		when(answerRepository.findOne(answer.getId())).thenReturn(answer);
		doThrow(new UnauthorizedActionException())
			.when(accessControlService).checkUserPriviledges(user, answer);
		
		service.update(answer.getId(), answer, user);
	}
	
	// Delete

	@Test
	public void testDeleteShouldDelete() throws QuizZzException {
		when(answerRepository.findOne(answer.getId())).thenReturn(answer);
		service.delete(answer.getId(), answer.getQuestion().getQuiz().getCreatedBy());
		
		verify(answerRepository, times(1)).delete(answer);
	}
	
	@Test(expected = ResourceUnavailableException.class)
	public void testDeleteUnexistentAnswer() throws QuizZzException {
		answer.setText("test");
		
		when(answerRepository.findOne(answer.getId())).thenReturn(null);
		
		service.delete(answer.getId(), user);
	}
	
	@Test(expected = UnauthorizedActionException.class)
	public void testDeleteFromWrongUser() throws QuizZzException {
		answer.setText("test");
		
		when(answerRepository.findOne(answer.getId())).thenReturn(answer);
		doThrow(new UnauthorizedActionException())
			.when(accessControlService).checkUserPriviledges(user, answer);
		
		service.delete(answer.getId(), user);
	}


}
