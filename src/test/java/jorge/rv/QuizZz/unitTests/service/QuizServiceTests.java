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

import jorge.rv.quizzz.exceptions.InvalidParametersException;
import jorge.rv.quizzz.exceptions.QuizZzException;
import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Question;
import jorge.rv.quizzz.model.Quiz;
import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.model.support.AnswersBundle;
import jorge.rv.quizzz.model.support.Results;
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
		doThrow(new UnauthorizedActionException())
			.when(quizRepository).save(quiz);
		
		service.update(quiz);
	}
	
	// Delete

	@Test
	public void testDeleteShouldDelete() throws QuizZzException {
		when(quizRepository.findOne(quiz.getId())).thenReturn(quiz);
		service.delete(quiz.getId());
		
		verify(quizRepository, times(1)).delete(quiz);
	}
	
	@Test(expected = ResourceUnavailableException.class)
	public void testDeleteUnexistentQuiz() throws QuizZzException {
		when(quizRepository.findOne(quiz.getId())).thenReturn(null);
		
		service.delete(quiz.getId());
	}
	
	@Test(expected = UnauthorizedActionException.class)
	public void testDeleteFromWrongUser() throws QuizZzException {
		when(quizRepository.findOne(quiz.getId())).thenReturn(quiz);
		doThrow(new UnauthorizedActionException())
			.when(quizRepository).delete(quiz);
		
		service.delete(quiz.getId());
	}
	
	// FindQuestionsById
	
	@Test
	public void testFindQuestionsByQuizWithAvailableQuizAndEmptyQuestions() throws ResourceUnavailableException {
		when(questionService.findQuestionsByQuiz(quiz)).thenReturn(new ArrayList<>());
		when(quizRepository.findOne(quiz.getId())).thenReturn(quiz);
		
		List<Question> questions = service.findQuestionsByQuiz(quiz.getId());
		
		assertEquals(0, questions.size());
	}

	@Test
	public void testFindQuestionsByQuizWithAvailableQuizAndQuestionsAvailable() throws ResourceUnavailableException {
		List<Question> mockedQuestions = new ArrayList<>();
		mockedQuestions.add(new Question());
		mockedQuestions.add(new Question());
		mockedQuestions.add(new Question());
		
		when(questionService.findQuestionsByQuiz(quiz)).thenReturn(mockedQuestions);
		when(quizRepository.findOne(quiz.getId())).thenReturn(quiz);
		
		List<Question> questions = service.findQuestionsByQuiz(quiz.getId());
		
		assertEquals(3, questions.size());
	}
	
	@Test(expected = ResourceUnavailableException.class)
	public void testFindQuestionsByQuizWithInvalidQuizID() throws ResourceUnavailableException {
		when(quizRepository.findOne(quiz.getId())).thenReturn(null);
		
		service.findQuestionsByQuiz(quiz.getId());
	}
	
	private static Pageable createDefaultPage() {
		return new PageRequest(0, DEFAULT_PAGE_SIZE);
	}
	
	@Test
	public void testCheckAnswers() {
		List<AnswersBundle> listBundles = generateAnswersBundle(DEFAULT_NUMBER_OF_QUESTIONS);
		
		Quiz quiz = new Quiz();
		List<Question> listQuestions = generateQuestions(DEFAULT_NUMBER_OF_QUESTIONS);
		quiz.setQuestions(listQuestions);
		when(quizRepository.findOne(1l)).thenReturn(quiz);
		
		when(questionService.checkAnswer(any(Question.class), any(Long.class)))
			.thenReturn(true)
			.thenReturn(true)
			.thenReturn(true)
			.thenReturn(true)
			.thenReturn(true)
			.thenReturn(false)
			.thenReturn(false)
			.thenReturn(false)
			.thenReturn(false)
			.thenReturn(false);
		
		
		Results results = service.checkAnswers(1l, listBundles);
		
		
		verify(questionService, times(DEFAULT_NUMBER_OF_QUESTIONS)).checkAnswer(any(Question.class), any(Long.class));
		assertEquals(5, results.getCorrectQuestions());
		assertEquals(DEFAULT_NUMBER_OF_QUESTIONS, results.getTotalQuestions());
	}
	
	@Test(expected = ResourceUnavailableException.class)
	public void testCheckAnswers_quizDoesntExsist_shouldForwardException() {
		service.checkAnswers(1l, null);
	}

	@Test(expected = InvalidParametersException.class)
	public void testCheckAnswers_answerIsMissing_shouldForwardException() {
		List<AnswersBundle> list = generateAnswersBundle(1);
		
		Quiz quiz = new Quiz();
		List<Question> listQuestions = generateQuestions(1);
		quiz.setQuestions(listQuestions);
		
		when(quizRepository.findOne(1l)).thenReturn(quiz);
		when(questionService.checkAnswer(any(Question.class), any(Long.class))).thenThrow(new InvalidParametersException());
		
		
		service.checkAnswers(1l, list);
	}
	
	@Test(expected = InvalidParametersException.class)
	public void testCheckAnswers_questionIsNotAnswered_shouldThrowException() {
		List<AnswersBundle> listBundles = generateAnswersBundle(DEFAULT_NUMBER_OF_QUESTIONS);
		
		Quiz quiz = new Quiz();
		List<Question> listQuestions = generateQuestions(DEFAULT_NUMBER_OF_QUESTIONS + 1);
		quiz.setQuestions(listQuestions);
		when(quizRepository.findOne(1l)).thenReturn(quiz);
		
		when(questionService.checkAnswer(any(Question.class), any(Long.class)))
			.thenReturn(true)
			.thenReturn(true)
			.thenReturn(true)
			.thenReturn(true)
			.thenReturn(true)
			.thenReturn(false)
			.thenReturn(false)
			.thenReturn(false)
			.thenReturn(false)
			.thenReturn(false);
		
		
		service.checkAnswers(1l, listBundles);
	}
	
	private List<Question> generateQuestions(int numberOfQuestions) {
		List<Question> list = new ArrayList<>();
		
		for (int i=0; i<numberOfQuestions; i++) {
			Question question = new Question();
			question.setId((long) i);
			list.add(question);
		}
		
		return list;
	}
	
	private List<AnswersBundle> generateAnswersBundle(int numberOfQuestions) {
		List<AnswersBundle> list = new ArrayList<>();
		
		for (int i=0; i<numberOfQuestions; i++) {
			AnswersBundle answersBundle = new AnswersBundle();
			answersBundle.setQuestion((long) i);
			answersBundle.setSelectedAnswer((long) i);
			list.add(answersBundle);
		}
		
		return list;
	}
	
}
