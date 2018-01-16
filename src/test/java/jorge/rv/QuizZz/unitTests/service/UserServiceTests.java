package jorge.rv.QuizZz.unitTests.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import jorge.rv.quizzz.exceptions.QuizZzException;
import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.exceptions.UserAlreadyExistsException;
import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.repository.UserRepository;
import jorge.rv.quizzz.service.UserService;
import jorge.rv.quizzz.service.UserServiceImpl;

public class UserServiceTests {

	UserService service;

	// Mocks
	UserRepository userRepository;
	PasswordEncoder passwordEncoder;

	User user = new User();

	@Before
	public void before() {
		userRepository = mock(UserRepository.class);
		passwordEncoder = mock(PasswordEncoder.class);

		service = new UserServiceImpl(userRepository, passwordEncoder);

		user.setEmail("a@a.com");
		user.setPassword("Password");
	}

	@Test
	public void saveNewUserShouldSucceed() throws UserAlreadyExistsException {
		when(userRepository.findByEmail(user.getEmail())).thenReturn(null);
		when(userRepository.save(user)).thenReturn(user);

		User returned = service.saveUser(user);

		verify(userRepository, times(1)).save(user);
		assertTrue(user.equals(returned));
	}

	@Test(expected = UserAlreadyExistsException.class)
	public void saveNewUserMailExistsShouldFail() throws UserAlreadyExistsException {
		when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
		when(userRepository.save(user)).thenReturn(user);

		service.saveUser(user);
	}

	@Test
	public void deleteUser() throws UnauthorizedActionException, ResourceUnavailableException {
		when(userRepository.findOne(user.getId())).thenReturn(user);
		service.delete(user.getId());

		verify(userRepository, times(1)).delete(user);
	}

	@Test(expected = ResourceUnavailableException.class)
	public void testDeleteUnexistentUser() throws QuizZzException {
		when(userRepository.findOne(user.getId())).thenReturn(null);

		service.delete(user.getId());
	}

	@Test(expected = UnauthorizedActionException.class)
	public void testDeleteFromWrongUser() throws QuizZzException {
		when(userRepository.findOne(user.getId())).thenReturn(user);
		doThrow(new UnauthorizedActionException()).when(userRepository).delete(user);

		service.delete(user.getId());
	}

	@Test(expected = UsernameNotFoundException.class)
	public void findUserByUsername_shouldntFind() {
		when(userRepository.findByEmail(user.getEmail())).thenThrow(new UsernameNotFoundException("test"));

		service.loadUserByUsername("test");
	}

	@Test
	public void findUserByUsername_shouldFind() {
		when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

		UserDetails localUser = service.loadUserByUsername(user.getEmail());

		verify(userRepository, times(1)).findByEmail(user.getEmail());
		assertNotNull(localUser);
	}

	@Test
	public void updatePasswordShouldEncrypt() {
		final String clearPass = "clearPassword";
		final String encodedPass = "encodedPassword";
		when(passwordEncoder.encode(clearPass)).thenReturn(encodedPass);
		when(userRepository.save(user)).thenReturn(user);

		User newUser = service.updatePassword(user, clearPass);

		verify(passwordEncoder, times(1)).encode(clearPass);
		verify(userRepository, times(1)).save(user);
		assertEquals(encodedPass, newUser.getPassword());
	}
}
