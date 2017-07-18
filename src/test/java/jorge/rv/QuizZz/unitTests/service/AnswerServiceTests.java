package jorge.rv.QuizZz.unitTests.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import jorge.rv.quizzz.exceptions.ActionRefusedException;
import jorge.rv.quizzz.exceptions.QuizZzException;
import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Answer;
import jorge.rv.quizzz.model.AuthenticatedUser;
import jorge.rv.quizzz.model.Question;
import jorge.rv.quizzz.model.Quiz;
import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.repository.AnswerRepository;
import jorge.rv.quizzz.service.AnswerServiceImpl;
import jorge.rv.quizzz.service.QuestionService;

public class AnswerServiceTests {

	AnswerServiceImpl service;

	// Mocks
	AnswerRepository answerRepository;
	QuestionService questionService;

	User internalUser = new User();
	AuthenticatedUser user = new AuthenticatedUser(internalUser);
	Quiz quiz = new Quiz();
	Question question = new Question();
	Answer answer = new Answer();

	@Before
	public void before() {
		answerRepository = mock(AnswerRepository.class);
		questionService = mock(QuestionService.class);

		service = new AnswerServiceImpl(answerRepository);
		service.setQuestionService(questionService);

		internalUser.setId(1l);
		quiz.setCreatedBy(user.getUser());
		quiz.setId(1l);
		question.setQuiz(quiz);
		answer.setQuestion(question);
		answer.setId(1l);

	}

	// Save

	@Test
	public void testSaveAnswerShouldSave() throws UnauthorizedActionException {
		service.save(answer);
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
		answer.setOrder(1);
		when(answerRepository.findOne(answer.getId())).thenReturn(answer);
		when(answerRepository.save(answer)).thenReturn(answer);

		Answer returned = service.update(answer);

		verify(answerRepository, times(1)).save(answer);
		assertEquals(returned.getText(), answer.getText());
		assertEquals(returned.getOrder(), (Integer) 1);
	}

	@Test(expected = ResourceUnavailableException.class)
	public void testUpdateUnexistentAnswer() throws QuizZzException {
		answer.setText("test");

		when(answerRepository.findOne(answer.getId())).thenReturn(null);

		service.update(answer);
	}

	@Test(expected = UnauthorizedActionException.class)
	public void testUpdateFromWrongUser() throws QuizZzException {
		answer.setText("test");

		when(answerRepository.findOne(answer.getId())).thenReturn(answer);
		doThrow(new UnauthorizedActionException()).when(answerRepository).save(answer);

		service.update(answer);
	}

	// Delete

	@Test
	public void testDelete_isNotCorrect_ShouldDelete() throws QuizZzException {
		when(questionService.checkIsCorrectAnswer(question, answer.getId())).thenReturn(false);

		service.delete(answer);

		verify(answerRepository, times(1)).delete(answer);
	}

	@Test(expected = ActionRefusedException.class)
	public void testDelete_isCorrect_ShouldNotDelete() throws QuizZzException {
		when(questionService.checkIsCorrectAnswer(question, answer.getId())).thenReturn(true);

		service.delete(answer);

		verify(answerRepository, never()).delete(answer);
	}

	@Test(expected = UnauthorizedActionException.class)
	public void testDeleteFromWrongUser() throws QuizZzException {
		answer.setText("test");

		doThrow(new UnauthorizedActionException()).when(answerRepository).delete(answer);

		service.delete(answer);
	}

}
