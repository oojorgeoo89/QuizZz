package jorge.rv.QuizZz.unitTests.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import jorge.rv.quizzz.exceptions.ActionRefusedException;
import jorge.rv.quizzz.exceptions.InvalidParametersException;
import jorge.rv.quizzz.exceptions.QuizZzException;
import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Question;
import jorge.rv.quizzz.model.Quiz;
import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.model.support.Response;
import jorge.rv.quizzz.model.support.Result;
import jorge.rv.quizzz.repository.QuizRepository;
import jorge.rv.quizzz.service.QuestionService;
import jorge.rv.quizzz.service.QuizService;
import jorge.rv.quizzz.service.QuizServiceImpl;

public class QuizServiceTests {

	private static final int DEFAULT_PAGE_SIZE = 5;
	private static final int DEFAULT_NUMBER_OF_QUESTIONS = 10;
	private static final Pageable pageable = createDefaultPage();

	QuizService service;

	// Mocks
	QuizRepository quizRepository;
	QuestionService questionService;

	User user = new User();
	Quiz quiz = new Quiz();

	@Before
	public void before() {
		quizRepository = mock(QuizRepository.class);
		questionService = mock(QuestionService.class);
		service = new QuizServiceImpl(quizRepository, questionService);

		user.setId(1l);

		quiz.setCreatedBy(user);
		quiz.setId(1l);
		quiz.setId(2l);

	}

	private static Pageable createDefaultPage() {
		return new PageRequest(0, DEFAULT_PAGE_SIZE);
	}

	// Save

	@Test
	public void testSaveQuiz() {
		service.save(quiz, user);
		verify(quizRepository, times(1)).save(quiz);
	}

	// FindAll

	@Test
	public void findAllQuizzesEmpty() {
		when(quizRepository.findAll(pageable)).thenReturn(new PageImpl<>(new ArrayList<Quiz>()));

		List<Quiz> result = service.findAll(pageable).getContent();

		verify(quizRepository, times(1)).findAll(pageable);
		assertEquals(0, result.size());
	}

	@Test
	public void findAllQuizzesWithContent() {
		ArrayList<Quiz> q = new ArrayList<>();
		q.add(quiz);
		q.add(new Quiz());
		when(quizRepository.findAll(pageable)).thenReturn(new PageImpl<>(q));

		List<Quiz> result = service.findAll(pageable).getContent();

		verify(quizRepository, times(1)).findAll(pageable);
		assertEquals(2, result.size());
	}

	// Search

	@Test
	public void testFindWithEmptyString() {
		ArrayList<Quiz> q = new ArrayList<>();
		q.add(quiz);
		q.add(new Quiz());
		when(quizRepository.searchByName("", pageable)).thenReturn(new PageImpl<>(q));

		List<Quiz> result = service.search("", pageable).getContent();

		verify(quizRepository, times(1)).searchByName("", pageable);
		assertEquals(2, result.size());
	}

	@Test
	public void testFindWithStringEmptyRepo() {
		when(quizRepository.searchByName("test", pageable)).thenReturn(new PageImpl<>(new ArrayList<Quiz>()));

		List<Quiz> result = service.search("test", pageable).getContent();

		verify(quizRepository, times(1)).searchByName("test", pageable);
		assertEquals(0, result.size());
	}

	@Test
	public void testFindWithString() {
		ArrayList<Quiz> q = new ArrayList<>();
		q.add(quiz);
		q.add(new Quiz());
		when(quizRepository.searchByName("test", pageable)).thenReturn(new PageImpl<>(q));

		List<Quiz> result = service.search("test", pageable).getContent();

		verify(quizRepository, times(1)).searchByName("test", pageable);
		assertEquals(2, result.size());
	}

	// Find by User

	@Test
	public void testFindByUser() {
		ArrayList<Quiz> q = new ArrayList<>();
		q.add(quiz);
		q.add(new Quiz());
		when(quizRepository.findByCreatedBy(user, pageable)).thenReturn(new PageImpl<>(q));

		List<Quiz> result = service.findQuizzesByUser(user, pageable).getContent();

		verify(quizRepository, times(1)).findByCreatedBy(user, pageable);
		assertEquals(2, result.size());
	}

	// Find

	@Test
	public void findExistingQuiz() throws ResourceUnavailableException {
		when(quizRepository.findOne(quiz.getId())).thenReturn(quiz);

		Quiz returned = service.find(quiz.getId());

		verify(quizRepository, times(1)).findOne(quiz.getId());
		assertNotNull(returned);
		assertEquals(quiz.getId(), returned.getId());
	}

	@Test(expected = ResourceUnavailableException.class)
	public void findNonExistingQuiz() throws ResourceUnavailableException {
		when(quizRepository.findOne(quiz.getId())).thenReturn(null);

		service.find(quiz.getId());
	}

	// Update

	@Test
	public void testUpdateShouldUpdate() throws QuizZzException {
		quiz.setName("test");

		when(quizRepository.findOne(quiz.getId())).thenReturn(quiz);
		when(quizRepository.save(quiz)).thenReturn(quiz);
		Quiz returned = service.update(quiz);

		verify(quizRepository, times(1)).save(quiz);
		assertTrue(quiz.getName().equals(returned.getName()));
	}

	@Test(expected = ResourceUnavailableException.class)
	public void testUpdateUnexistentQuiz() throws QuizZzException {
		quiz.setName("test");

		when(quizRepository.findOne(quiz.getId())).thenReturn(null);

		service.update(quiz);
	}

	@Test(expected = UnauthorizedActionException.class)
	public void testUpdateFromWrongUser() throws QuizZzException {
		quiz.setName("test");

		when(quizRepository.findOne(quiz.getId())).thenReturn(quiz);
		doThrow(new UnauthorizedActionException()).when(quizRepository).save(quiz);

		service.update(quiz);
	}

	// Delete

	@Test
	public void testDeleteShouldDelete() throws QuizZzException {
		service.delete(quiz);

		verify(quizRepository, times(1)).delete(quiz);
	}

	@Test(expected = UnauthorizedActionException.class)
	public void testDeleteFromWrongUser() throws QuizZzException {
		doThrow(new UnauthorizedActionException()).when(quizRepository).delete(quiz);

		service.delete(quiz);
	}

	@Test
	public void testCheckAnswers() {
		List<Response> listBundles = generateAnswersBundle(DEFAULT_NUMBER_OF_QUESTIONS);

		Quiz quiz = new Quiz();
		List<Question> listQuestions = generateQuestions(DEFAULT_NUMBER_OF_QUESTIONS);
		quiz.setQuestions(listQuestions);

		when(questionService.checkIsCorrectAnswer(any(Question.class), any(Long.class))).thenReturn(true)
				.thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false).thenReturn(false)
				.thenReturn(false).thenReturn(false).thenReturn(false);

		Result results = service.checkAnswers(quiz, listBundles);

		verify(questionService, times(DEFAULT_NUMBER_OF_QUESTIONS)).checkIsCorrectAnswer(any(Question.class),
				any(Long.class));
		assertEquals(5, results.getCorrectQuestions());
		assertEquals(DEFAULT_NUMBER_OF_QUESTIONS, results.getTotalQuestions());
	}

	@Test
	public void testCheckAnswers_withInvalidQuestions_shouldIgnoreThem() {
		List<Response> listBundles = generateAnswersBundle(DEFAULT_NUMBER_OF_QUESTIONS);

		Quiz quiz = new Quiz();
		List<Question> listQuestions = generateQuestions(DEFAULT_NUMBER_OF_QUESTIONS);
		listQuestions.get(0).setIsValid(false);
		quiz.setQuestions(listQuestions);

		when(questionService.checkIsCorrectAnswer(any(Question.class), any(Long.class))).thenReturn(true)
				.thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false).thenReturn(false)
				.thenReturn(false).thenReturn(false).thenReturn(false);

		Result results = service.checkAnswers(quiz, listBundles);

		verify(questionService, times(DEFAULT_NUMBER_OF_QUESTIONS - 1)).checkIsCorrectAnswer(any(Question.class),
				any(Long.class));
		assertEquals(5, results.getCorrectQuestions());
		assertEquals(DEFAULT_NUMBER_OF_QUESTIONS - 1, results.getTotalQuestions());
	}

	@Test(expected = InvalidParametersException.class)
	public void testCheckAnswers_questionIsNotAnswered_shouldThrowException() {
		List<Response> listBundles = generateAnswersBundle(DEFAULT_NUMBER_OF_QUESTIONS);

		Quiz quiz = new Quiz();
		List<Question> listQuestions = generateQuestions(DEFAULT_NUMBER_OF_QUESTIONS + 1);
		quiz.setQuestions(listQuestions);

		when(questionService.checkIsCorrectAnswer(any(Question.class), any(Long.class))).thenReturn(true)
				.thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false).thenReturn(false)
				.thenReturn(false).thenReturn(false).thenReturn(false);

		service.checkAnswers(quiz, listBundles);
	}

	@Test(expected = ActionRefusedException.class)
	public void testPublishQuiz_withNoValidQuestions_shouldThrowException() {
		when(questionService.countValidQuestionsInQuiz(quiz)).thenReturn(0);

		service.publishQuiz(quiz);
	}

	@Test
	public void testPublishQuiz_withOneValidQuestion_shouldPublish() {
		when(questionService.countValidQuestionsInQuiz(quiz)).thenReturn(1);

		service.publishQuiz(quiz);

		verify(quizRepository, times(1)).save(quiz);
		assertTrue(quiz.getIsPublished());
	}

	private List<Question> generateQuestions(int numberOfQuestions) {
		List<Question> list = new ArrayList<>();

		for (int i = 0; i < numberOfQuestions; i++) {
			Question question = new Question();
			question.setId((long) i);
			question.setIsValid(true);
			list.add(question);
		}

		return list;
	}

	private List<Response> generateAnswersBundle(int numberOfQuestions) {
		List<Response> list = new ArrayList<>();

		for (int i = 0; i < numberOfQuestions; i++) {
			Response answersBundle = new Response();
			answersBundle.setQuestion((long) i);
			answersBundle.setSelectedAnswer((long) i);
			list.add(answersBundle);
		}

		return list;
	}

}
