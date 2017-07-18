package jorge.rv.QuizZz.unitTests.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;

import jorge.rv.quizzz.exceptions.ActionRefusedException;
import jorge.rv.quizzz.exceptions.QuizZzException;
import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Answer;
import jorge.rv.quizzz.model.Question;
import jorge.rv.quizzz.model.Quiz;
import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.repository.QuestionRepository;
import jorge.rv.quizzz.service.AnswerService;
import jorge.rv.quizzz.service.QuestionService;
import jorge.rv.quizzz.service.QuestionServiceImpl;

public class QuestionServiceTests {

	private static final int DEFAULT_NUMBER_OF_ANSWERS = 10;

	QuestionService service;

	// Mocks
	QuestionRepository questionRepository;
	AnswerService answerService;

	User user = new User();
	Quiz quiz = new Quiz();
	Question question = new Question();

	@Before
	public void before() {
		questionRepository = mock(QuestionRepository.class);
		answerService = mock(AnswerService.class);
		service = new QuestionServiceImpl(questionRepository, answerService);

		user.setId(1l);
		quiz.setCreatedBy(user);
		quiz.setId(1l);
		question.setQuiz(quiz);
		question.setId(1l);

	}

	// Save

	@Test
	public void testSaveQuestionShouldSave() throws UnauthorizedActionException {
		service.save(question);
		verify(questionRepository, times(1)).save(question);
	}

	// Find

	@Test
	public void findExistingQuestion() throws ResourceUnavailableException {
		when(questionRepository.findOne(question.getId())).thenReturn(question);

		Question returned = service.find(question.getId());

		verify(questionRepository, times(1)).findOne(question.getId());
		assertNotNull(returned);
		assertEquals(question.getId(), returned.getId());
	}

	@Test(expected = ResourceUnavailableException.class)
	public void findNonExistingQuestion() throws ResourceUnavailableException {
		when(questionRepository.findOne(quiz.getId())).thenReturn(null);

		service.find(quiz.getId());
	}

	// Update

	@Test
	public void testUpdateShouldUpdate() throws QuizZzException {
		question.setText("test");
		question.setOrder(1);

		when(questionRepository.findOne(question.getId())).thenReturn(question);
		when(questionRepository.save(question)).thenReturn(question);
		Question returned = service.update(question);

		verify(questionRepository, times(1)).save(question);
		assertEquals(returned.getText(), question.getText());
	}

	@Test
	public void testUpdateShouldUpdate_noOrder() throws QuizZzException {
		question.setText("test");

		when(questionRepository.findOne(question.getId())).thenReturn(question);
		when(questionRepository.save(question)).thenReturn(question);
		Question returned = service.update(question);

		verify(questionRepository, times(1)).save(question);
		assertEquals(returned.getText(), question.getText());
	}

	@Test(expected = ResourceUnavailableException.class)
	public void testUpdateUnexistentQuestion() throws QuizZzException {
		question.setText("test");

		when(questionRepository.findOne(question.getId())).thenReturn(null);

		service.update(question);
	}

	@Test(expected = UnauthorizedActionException.class)
	public void testUpdateFromWrongUser() throws QuizZzException {
		question.setText("test");

		when(questionRepository.findOne(question.getId())).thenReturn(question);
		doThrow(new UnauthorizedActionException()).when(questionRepository).save(question);

		service.update(question);
	}

	// Delete

	@Test
	public void testDelete_QuizIsNotPublished_ShouldDelete() throws QuizZzException {
		question.getQuiz().setIsPublished(false);

		service.delete(question);

		verify(questionRepository, times(1)).delete(question);
	}

	@Test
	public void testDelete_IsInvalid_ShouldDelete() throws QuizZzException {
		question.getQuiz().setIsPublished(true);
		question.setIsValid(false);

		service.delete(question);

		verify(questionRepository, times(1)).delete(question);
	}

	@Test
	public void testDelete_SeveralValidQuestions_ShouldDelete() throws QuizZzException {
		question.getQuiz().setIsPublished(true);
		question.setIsValid(true);
		when(questionRepository.countByQuizAndIsValidTrue(question.getQuiz())).thenReturn(2);

		service.delete(question);

		verify(questionRepository, times(1)).delete(question);
	}

	@Test(expected = ActionRefusedException.class)
	public void testDelete_SeveralValidQuestions_ShouldntDelete() throws QuizZzException {
		question.getQuiz().setIsPublished(true);
		question.setIsValid(true);
		when(questionRepository.countByQuizAndIsValidTrue(question.getQuiz())).thenReturn(1);

		service.delete(question);

		verify(questionRepository, times(1)).delete(question);
	}

	@Test(expected = UnauthorizedActionException.class)
	public void testDeleteFromWrongUser() throws QuizZzException {
		doThrow(new UnauthorizedActionException()).when(questionRepository).delete(question);

		service.delete(question);
	}

	@Test
	public void testCheckAnswer_answerFound_shouldReturnCorrect() {
		Answer correctAnswer = new Answer();
		correctAnswer.setId(1l);
		question.setCorrectAnswer(correctAnswer);
		question.setIsValid(true);

		boolean isCorrect = service.checkIsCorrectAnswer(question, correctAnswer.getId());

		assertTrue(isCorrect);
	}

	@Test
	public void testCheckAnswer_answerFound_shouldReturnIncorrect() {
		Answer correctAnswer = new Answer();
		correctAnswer.setId(1l);
		question.setCorrectAnswer(correctAnswer);
		question.setIsValid(true);

		boolean isCorrect = service.checkIsCorrectAnswer(question, correctAnswer.getId() + 1);

		assertFalse(isCorrect);
	}

	@Test
	public void testCheckAnswer_questionIsInvalid_shouldReturnIncorrect() {
		Answer correctAnswer = new Answer();
		correctAnswer.setId(1l);
		question.setCorrectAnswer(correctAnswer);
		question.setIsValid(false);

		boolean isCorrect = service.checkIsCorrectAnswer(question, correctAnswer.getId());

		assertFalse(isCorrect);
	}

	@Test
	public void testCheckAnswer_questionDoesntHaveCorrectAnswerSet_shouldReturnIncorrect() {
		question.setIsValid(true);

		boolean isCorrect = service.checkIsCorrectAnswer(question, 1l);

		assertFalse(isCorrect);
	}

	@Test
	public void testGetCorrectAnswer_noCorrectAnswerSet_shouldReturnNull() {
		List<Answer> answers = generateAnswers(DEFAULT_NUMBER_OF_ANSWERS);
		question.setAnswers(answers);

		Answer correctAnswer = service.getCorrectAnswer(question);

		assertNull(correctAnswer);
	}

	@Test
	public void testGetCorrectAnswer_correctAnswerSet_shouldReturnIt() {
		Answer answer = new Answer();
		answer.setId(1l);
		question.setCorrectAnswer(answer);
		question.setIsValid(true);

		Answer correctAnswer = service.getCorrectAnswer(question);

		assertEquals(answer, correctAnswer);
	}

	@Test
	public void testSetCorrectAnswer_shouldSetIt() {
		Answer answer = new Answer();
		answer.setId(1l);

		service.setCorrectAnswer(question, answer);

		assertEquals(answer, question.getCorrectAnswer());
	}

	@Test
	public void testAddAnswerToQuestion_firstAnswer_shouldEnableQuestionAndMarkItAsCorrect() {
		when(answerService.countAnswersInQuestion(question)).thenReturn(0);
		question.setIsValid(false);
		question.setCorrectAnswer(null);
		Answer answer = new Answer();
		answer.setId(1l);

		when(answerService.save(any(Answer.class))).thenAnswer(new org.mockito.stubbing.Answer<Answer>() {
			@Override
			public Answer answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				return (Answer) args[0];
			}
		});

		service.addAnswerToQuestion(answer, question);

		assertTrue(question.getIsValid());
		assertEquals(answer, question.getCorrectAnswer());
		verify(answerService, times(1)).save(answer);
		verify(questionRepository, times(2)).save(question);
	}

	@Test
	public void testAddAnswerToQuestion_firstAnswerButValid_shouldMarkItAsCorrect() {
		when(answerService.countAnswersInQuestion(question)).thenReturn(0);
		question.setIsValid(true);
		question.setCorrectAnswer(null);
		Answer answer = new Answer();
		answer.setId(1l);

		when(answerService.save(any(Answer.class))).thenAnswer(new org.mockito.stubbing.Answer<Answer>() {
			@Override
			public Answer answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				return (Answer) args[0];
			}
		});

		service.addAnswerToQuestion(answer, question);

		assertTrue(question.getIsValid());
		assertEquals(answer, question.getCorrectAnswer());
		verify(answerService, times(1)).save(answer);
		verify(questionRepository, times(1)).save(question);
	}

	@Test
	public void testAddAnswerToQuestion_notFirstAnswerInvalidQuestion_shouldNotMarkItAsCorrect_shouldMarkItAsValid() {
		when(answerService.countAnswersInQuestion(question)).thenReturn(1);
		question.setIsValid(false);
		question.setCorrectAnswer(null);
		Answer answer = new Answer();
		answer.setId(1l);

		when(answerService.save(any(Answer.class))).thenAnswer(new org.mockito.stubbing.Answer<Answer>() {
			@Override
			public Answer answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				return (Answer) args[0];
			}
		});

		service.addAnswerToQuestion(answer, question);

		assertTrue(question.getIsValid());
		verify(answerService, times(1)).save(answer);
		verify(questionRepository, times(1)).save(question);
	}

	@Test
	public void testAddAnswerToQuestion_notFirstAnswer_shouldNotMarkItAsCorrect() {
		when(answerService.countAnswersInQuestion(question)).thenReturn(1);
		question.setIsValid(true);
		question.setCorrectAnswer(null);
		Answer answer = new Answer();
		answer.setId(1l);

		when(answerService.save(any(Answer.class))).thenAnswer(new org.mockito.stubbing.Answer<Answer>() {
			@Override
			public Answer answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				return (Answer) args[0];
			}
		});

		service.addAnswerToQuestion(answer, question);

		assertTrue(question.getIsValid());
		verify(answerService, times(1)).save(answer);
		verify(questionRepository, never()).save(question);
	}

	private List<Answer> generateAnswers(int numberOfAnswers) {
		List<Answer> list = new ArrayList<>();

		for (int i = 0; i < numberOfAnswers; i++) {
			Answer answer = new Answer();
			answer.setId((long) i);
			list.add(answer);
		}

		return list;
	}

}
