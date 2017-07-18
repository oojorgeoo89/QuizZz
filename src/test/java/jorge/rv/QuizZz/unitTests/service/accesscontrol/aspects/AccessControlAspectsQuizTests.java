package jorge.rv.QuizZz.unitTests.service.accesscontrol.aspects;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Before;
import org.junit.Test;

import jorge.rv.quizzz.model.Quiz;
import jorge.rv.quizzz.service.accesscontrol.AccessControlService;
import jorge.rv.quizzz.service.accesscontrol.AccessControlServiceQuiz;
import jorge.rv.quizzz.service.accesscontrol.aspects.AccessControlAspectsQuiz;

public class AccessControlAspectsQuizTests {
	private static final Long ID = 1l;

	// Class under test
	AccessControlAspectsQuiz aspect;

	// Mocks
	AccessControlService<Quiz> accessControlService;
	ProceedingJoinPoint proceedingJoinPoint;

	Quiz quiz = new Quiz();

	@Before
	public void before() {
		accessControlService = mock(AccessControlServiceQuiz.class);
		aspect = new AccessControlAspectsQuiz();
		aspect.setAccessControlService(accessControlService);

		proceedingJoinPoint = mock(ProceedingJoinPoint.class);
	}

	@Test
	public void create_shouldForward() throws Throwable {
		quiz.setId(null);

		aspect.save(proceedingJoinPoint, quiz);

		verify(accessControlService, times(1)).canCurrentUserCreateObject(quiz);
		verify(accessControlService, never()).canCurrentUserUpdateObject(quiz);
	}

	@Test
	public void read_shouldForward() throws Throwable {
		aspect.find(proceedingJoinPoint, ID);

		verify(accessControlService, times(1)).canCurrentUserReadObject(ID);
	}

	@Test
	public void readAll_shouldForward() throws Throwable {
		aspect.findAll(proceedingJoinPoint);

		verify(accessControlService, times(1)).canCurrentUserReadAllObjects();
	}

	@Test
	public void update_shouldForward() throws Throwable {
		quiz.setId(ID);

		aspect.save(proceedingJoinPoint, quiz);

		verify(accessControlService, never()).canCurrentUserCreateObject(quiz);
		verify(accessControlService, times(1)).canCurrentUserUpdateObject(quiz);
	}

	@Test
	public void delete_shouldForward() throws Throwable {
		aspect.delete(proceedingJoinPoint, quiz);

		verify(accessControlService, times(1)).canCurrentUserDeleteObject(quiz);
	}

}
