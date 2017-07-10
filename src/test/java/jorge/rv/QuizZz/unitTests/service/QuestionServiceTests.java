package jorge.rv.QuizZz.unitTests.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import jorge.rv.quizzz.exceptions.QuizZzException;
import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Answer;
import jorge.rv.quizzz.model.Question;
import jorge.rv.quizzz.model.Quiz;
import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.repository.QuestionRepository;
import jorge.rv.quizzz.service.QuestionService;
import jorge.rv.quizzz.service.QuestionServiceImpl;

public class QuestionServiceTests {

	QuestionService service;
	
	//Mocks
	QuestionRepository questionRepository;
	
	User user = new User();
	Quiz quiz = new Quiz();
	Question question = new Question();
	
	@Before
	public void before() {
		questionRepository = mock(QuestionRepository.class);
		service = new QuestionServiceImpl(questionRepository);
		
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

}
