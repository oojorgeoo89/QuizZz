package jorge.rv.QuizZz.unitTests.service;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import jorge.rv.quizzz.exceptions.InvalidParametersException;
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
	
	//Mocks
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
		quiz.setId(1l);
		
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
		
		when(questionRepository.findOne(question.getId())).thenReturn(question);
		when(questionRepository.save(question)).thenReturn(question);
		Question returned = service.update(question.getId(), question);
		
		verify(questionRepository, times(1)).save(question);
		assertEquals(returned.getText(), question.getText());
	}
	
	@Test(expected = ResourceUnavailableException.class)
	public void testUpdateUnexistentQuestion() throws QuizZzException {
		question.setText("test");
		
		when(questionRepository.findOne(question.getId())).thenReturn(null);
		
		service.update(question.getId(), question);
	}
	
	@Test(expected = UnauthorizedActionException.class)
	public void testUpdateFromWrongUser() throws QuizZzException {
		question.setText("test");
		
		when(questionRepository.findOne(question.getId())).thenReturn(question);
		doThrow(new UnauthorizedActionException())
			.when(questionRepository).save(question);
		
		service.update(question.getId(), question);
	}
	
	// Delete

	@Test
	public void testDeleteShouldDelete() throws QuizZzException {
		when(questionRepository.findOne(question.getId())).thenReturn(question);
		service.delete(question.getId());
		
		verify(questionRepository, times(1)).delete(question);
	}
	
	@Test(expected = ResourceUnavailableException.class)
	public void testDeleteUnexistentQuestion() throws QuizZzException {
		question.setText("test");
		
		when(questionRepository.findOne(question.getId())).thenReturn(null);
		
		service.delete(question.getId());
	}
	
	@Test(expected = UnauthorizedActionException.class)
	public void testDeleteFromWrongUser() throws QuizZzException {
		question.setText("test");
		
		when(questionRepository.findOne(question.getId())).thenReturn(question);
		doThrow(new UnauthorizedActionException())
			.when(questionRepository).delete(question);
		
		service.delete(question.getId());
	}
	
	// FindAnswersById
	
	@Test
	public void testFindAnswersByQuestionWithAvailableQuestionAndEmptyQuestions() throws ResourceUnavailableException {
		question.setAnswers(new ArrayList<Answer>());
		when(questionRepository.findOne(question.getId())).thenReturn(question);
		
		List<Answer> answers = service.findAnswersByQuestion(question.getId());
		
		assertEquals(0, answers.size());
	}

	@Test
	public void testFindAnswersByQuestionWithAvailableQuestionAndAnswersAvailable() throws ResourceUnavailableException {
		ArrayList<Answer> originalAnswers = new ArrayList<>();
		originalAnswers.add(new Answer());
		originalAnswers.add(new Answer());
		originalAnswers.add(new Answer());
		
		question.setAnswers(originalAnswers);
		when(questionRepository.findOne(question.getId())).thenReturn(question);
		
		List<Answer> answers = service.findAnswersByQuestion(question.getId());
		
		assertEquals(3, answers.size());
	}
	
	@Test(expected = ResourceUnavailableException.class)
	public void testFindQuestionsByQuizWithInvalidQuizID() throws ResourceUnavailableException {
		when(questionRepository.findOne(question.getId())).thenReturn(null);
		
		service.findAnswersByQuestion(question.getId());
	}
	
	@Test(expected = InvalidParametersException.class)
	public void testCheckAnswer_answerNotFound_shouldThrowException() {
		List<Answer> listAnswers = generateAnswers(DEFAULT_NUMBER_OF_ANSWERS);
		question.setAnswers(listAnswers);
		
		service.checkAnswer(question, (long) (DEFAULT_NUMBER_OF_ANSWERS + 5));
	}
	
	@Test()
	public void testCheckAnswer_answerFound_shouldReturnCorrect() {
		List<Answer> listAnswers = generateAnswers(DEFAULT_NUMBER_OF_ANSWERS);
		question.setAnswers(listAnswers);
		
		when(answerService.checkAnswer(any(Answer.class))).thenReturn(true);
		
		boolean isCorrect = service.checkAnswer(question, (long) (DEFAULT_NUMBER_OF_ANSWERS-1));
		
		verify(answerService, times(1)).checkAnswer(any(Answer.class));
		assertTrue(isCorrect);
	}
	
	@Test()
	public void testCheckAnswer_answerFound_shouldReturnIncorrect() {
		List<Answer> listAnswers = generateAnswers(DEFAULT_NUMBER_OF_ANSWERS);
		question.setAnswers(listAnswers);
		
		when(answerService.checkAnswer(any(Answer.class))).thenReturn(false);
		
		boolean isCorrect = service.checkAnswer(question, (long) (DEFAULT_NUMBER_OF_ANSWERS-1));
		
		verify(answerService, times(1)).checkAnswer(any(Answer.class));
		assertFalse(isCorrect);
	}
	
	private List<Answer> generateAnswers(int numberOfAnswers) {
		List<Answer> list = new ArrayList<>();
		
		for (int i=0; i<numberOfAnswers; i++) {
			Answer answer = new Answer();
			answer.setId((long) i);
			list.add(answer);
		}
		
		return list;
	}

}
