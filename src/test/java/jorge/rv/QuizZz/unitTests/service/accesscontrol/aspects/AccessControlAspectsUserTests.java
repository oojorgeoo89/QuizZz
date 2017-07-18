package jorge.rv.QuizZz.unitTests.service.accesscontrol.aspects;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Before;
import org.junit.Test;

import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.service.accesscontrol.AccessControlService;
import jorge.rv.quizzz.service.accesscontrol.AccessControlServiceUser;
import jorge.rv.quizzz.service.accesscontrol.aspects.AccessControlAspectsUser;

public class AccessControlAspectsUserTests {
	private static final Long ID = 1l;

	// Class under test
	AccessControlAspectsUser aspect;

	// Mocks
	AccessControlService<User> accessControlService;
	ProceedingJoinPoint proceedingJoinPoint;

	User user = new User();

	@Before
	public void before() {
		accessControlService = mock(AccessControlServiceUser.class);
		aspect = new AccessControlAspectsUser();
		aspect.setAccessControlService(accessControlService);

		proceedingJoinPoint = mock(ProceedingJoinPoint.class);
	}

	@Test
	public void create_shouldForward() throws Throwable {
		user.setId(null);

		aspect.save(proceedingJoinPoint, user);

		verify(accessControlService, times(1)).canCurrentUserCreateObject(user);
		verify(accessControlService, never()).canCurrentUserUpdateObject(user);
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
		user.setId(ID);

		aspect.save(proceedingJoinPoint, user);

		verify(accessControlService, never()).canCurrentUserCreateObject(user);
		verify(accessControlService, times(1)).canCurrentUserUpdateObject(user);
	}

	@Test
	public void delete_shouldForward() throws Throwable {
		aspect.delete(proceedingJoinPoint, user);

		verify(accessControlService, times(1)).canCurrentUserDeleteObject(user);
	}

}
