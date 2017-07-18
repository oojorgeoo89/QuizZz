package jorge.rv.QuizZz.unitTests.service.accesscontrol.aspects;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Before;
import org.junit.Test;

import jorge.rv.quizzz.model.Answer;
import jorge.rv.quizzz.service.accesscontrol.AccessControlService;
import jorge.rv.quizzz.service.accesscontrol.AccessControlServiceAnswer;
import jorge.rv.quizzz.service.accesscontrol.aspects.AccessControlAspectsAnswer;

public class AccessControlAspectsAnswerTests {
	private static final Long ID = 1l;

	// Class under test
	AccessControlAspectsAnswer aspect;

	// Mocks
	AccessControlService<Answer> accessControlService;
	ProceedingJoinPoint proceedingJoinPoint;

	Answer answer = new Answer();

	@Before
	public void before() {
		accessControlService = mock(AccessControlServiceAnswer.class);
		aspect = new AccessControlAspectsAnswer();
		aspect.setAccessControlService(accessControlService);

		proceedingJoinPoint = mock(ProceedingJoinPoint.class);
	}

	@Test
	public void create_shouldForward() throws Throwable {
		answer.setId(null);

		aspect.save(proceedingJoinPoint, answer);

		verify(accessControlService, times(1)).canCurrentUserCreateObject(answer);
		verify(accessControlService, never()).canCurrentUserUpdateObject(answer);
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
		answer.setId(ID);

		aspect.save(proceedingJoinPoint, answer);

		verify(accessControlService, never()).canCurrentUserCreateObject(answer);
		verify(accessControlService, times(1)).canCurrentUserUpdateObject(answer);
	}

	@Test
	public void delete_shouldForward() throws Throwable {
		aspect.delete(proceedingJoinPoint, answer);

		verify(accessControlService, times(1)).canCurrentUserDeleteObject(answer);
	}

}
