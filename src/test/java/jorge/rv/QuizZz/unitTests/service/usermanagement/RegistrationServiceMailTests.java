package jorge.rv.QuizZz.unitTests.service.usermanagement;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import jorge.rv.quizzz.exceptions.InvalidTokenException;
import jorge.rv.quizzz.exceptions.UserAlreadyExistsException;
import jorge.rv.quizzz.model.MailRegistrationToken;
import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.service.UserService;
import jorge.rv.quizzz.service.usermanagement.RegistrationService;
import jorge.rv.quizzz.service.usermanagement.RegistrationServiceMail;
import jorge.rv.quizzz.service.usermanagement.TokenDeliverySystem;
import jorge.rv.quizzz.service.usermanagement.TokenServiceMailRegistration;

public class RegistrationServiceMailTests {
	
	private static final String TOKEN = "token";

	RegistrationService registrationService;
	
	// Mocks
	UserService userService;
	TokenServiceMailRegistration tokenService;
	TokenDeliverySystem tokenDeliverySystem;
	
	// Models
	User user = new User();
	
	@Before
	public void before() {
		userService = mock(UserService.class);
		tokenService = mock(TokenServiceMailRegistration.class);
		tokenDeliverySystem = mock(TokenDeliverySystem.class);
		
		registrationService = new RegistrationServiceMail(userService, tokenService, tokenDeliverySystem);
		
		user.setEmail("a@a.com");
		user.setPassword("Password");
	}
	
	@Test(expected = UserAlreadyExistsException.class)
	public void startRegistrationWithExistingUser_shouldThrowException() {
		when(userService.saveUser(user)).thenThrow(new UserAlreadyExistsException());
		
		registrationService.startRegistration(user);
	}
	
	@Test
	public void startRegistrationWithNewUser_shouldCreateToken() {
		when(userService.saveUser(user)).thenReturn(user);
		when(tokenService.generateTokenForUser(user)).thenReturn(new MailRegistrationToken());
		
		registrationService.startRegistration(user);
		
		verify(tokenService, times(1)).generateTokenForUser(user);
	}
	
	@Test(expected = InvalidTokenException.class)
	public void continueRegistrationWithInvalidToken_shouldThrowException() {
		doThrow(new InvalidTokenException()).when(tokenService).validateTokenForUser(user, TOKEN);
		
		registrationService.continueRegistration(user, TOKEN);
	}
	
	@Test
	public void continueRegistration_shouldEnableUserAndDestroyToken() {
		registrationService.continueRegistration(user, TOKEN);
		
		verify(userService, times(1)).enableUser(user);
		verify(tokenService, times(1)).invalidateToken(TOKEN);
	}
	
	@Test
	public void registrationIsCompleted() {
		when(userService.isUserEnabled(user)).thenReturn(true);
		
		Boolean isCompleted = registrationService.isRegistrationCompleted(user);
		
		assertEquals(true, isCompleted);
	}

	@Test
	public void registrationIsNotCompleted() {
		when(userService.isUserEnabled(user)).thenReturn(false);
		
		Boolean isCompleted = registrationService.isRegistrationCompleted(user);
		
		assertEquals(false, isCompleted);
	}
	
}
