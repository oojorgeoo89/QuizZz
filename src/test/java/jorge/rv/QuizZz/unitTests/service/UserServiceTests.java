package jorge.rv.QuizZz.unitTests.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jorge.rv.quizzz.exceptions.QuizZzException;
import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.exceptions.UserAlreadyExistsException;
import jorge.rv.quizzz.model.Role;
import jorge.rv.quizzz.model.Roles;
import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.repository.RoleRepository;
import jorge.rv.quizzz.repository.UserRepository;
import jorge.rv.quizzz.service.AccessControlService;
import jorge.rv.quizzz.service.UserService;
import jorge.rv.quizzz.service.UserServiceImpl;

public class UserServiceTests {
	
	UserService service;
	
	// Mocks
	UserRepository userRepository;
	RoleRepository roleRepository;
	AccessControlService accessControlService;
	
	User user = new User();
	
	@Before
	public void before() {
		userRepository = mock(UserRepository.class);
		roleRepository = mock(RoleRepository.class);
		accessControlService = mock(AccessControlService.class);
		service = new UserServiceImpl(userRepository, roleRepository, accessControlService, new BCryptPasswordEncoder());
		
		user.setEmail("a@a.com");
		user.setPassword("Password");
	}

	@Test
	public void saveNewUserShouldSucceed() throws UserAlreadyExistsException {
		when(userRepository.findByEmail(user.getEmail())).thenReturn(null);
		when(roleRepository.findByRole(Roles.USER.toString())).thenReturn(new Role());
		when(userRepository.save(user)).thenReturn(user);
		
		User returned = service.saveUser(user);
		
		verify(userRepository, times(1)).save(user);
		assertTrue(user.equals(returned));
	}
	
	@Test(expected = UserAlreadyExistsException.class)
	public void saveNewUserMailExistsShouldFail() throws UserAlreadyExistsException {
		when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
		when(roleRepository.findByRole(Roles.USER.toString())).thenReturn(new Role());
		when(userRepository.save(user)).thenReturn(user);
		
		service.saveUser(user);
	}

	@Test
	public void deleteUser() throws UnauthorizedActionException, ResourceUnavailableException {
		when(userRepository.findOne(user.getId())).thenReturn(user);
		service.delete(user.getId());
		
		verify(userRepository, times(1)).delete(user);;
	}
	
	@Test(expected = ResourceUnavailableException.class)
	public void testDeleteUnexistentUser() throws QuizZzException {
		when(userRepository.findOne(user.getId())).thenReturn(null);
		
		service.delete(user.getId());
	}
	
	@Test(expected = UnauthorizedActionException.class)
	public void testDeleteFromWrongUser() throws QuizZzException {
		when(userRepository.findOne(user.getId())).thenReturn(user);
		doThrow(new UnauthorizedActionException())
			.when(accessControlService).checkCurrentUserPriviledges(user);
		
		service.delete(user.getId());
	}
}
