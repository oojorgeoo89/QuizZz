package jorge.rv.QuizZz.unitTests.service.usermanagement;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import jorge.rv.quizzz.exceptions.InvalidTokenException;
import jorge.rv.quizzz.model.ForgotPasswordToken;
import jorge.rv.quizzz.model.RegistrationToken;
import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.repository.RegistrationTokenRepository;
import jorge.rv.quizzz.service.usermanagement.token.TokenServiceRegistration;
import jorge.rv.quizzz.service.usermanagement.utils.DateHelper;
import jorge.rv.quizzz.service.usermanagement.utils.TokenGenerator;

public class TokenServiceMailRegistrationTests {

	private static final String TOKEN = "token";

	private static final int EXPIRATION_DELAY = 5;
	private static final Date EXPIRATION_DATE = new Date(123456);

	TokenServiceRegistration tokenService;

	// Mocks
	RegistrationTokenRepository tokenRepository;
	TokenGenerator tokenGenerator;
	DateHelper dateHelper;

	// Models
	User user = new User();
	RegistrationToken token;

	@Before
	public void before() {
		tokenRepository = mock(RegistrationTokenRepository.class);
		tokenGenerator = mock(TokenGenerator.class);
		dateHelper = mock(DateHelper.class);

		tokenService = new TokenServiceRegistration(tokenRepository, tokenGenerator);
		tokenService.setDateHelper(dateHelper);

		user.setEmail("a@a.com");
		user.setPassword("Password");
		token = new RegistrationToken();
	}

	@Test
	public void generateTokenForUser() {
		when(tokenGenerator.generateRandomToken()).thenReturn(TOKEN);
		when(dateHelper.getExpirationDate(any(Date.class), eq(EXPIRATION_DELAY))).thenReturn(EXPIRATION_DATE);
		tokenService.setExpirationTimeInMinutes(EXPIRATION_DELAY);

		when(tokenRepository.save((RegistrationToken) any())).thenAnswer(new Answer<RegistrationToken>() {
			@Override
			public RegistrationToken answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				return (RegistrationToken) args[0];
			}
		});

		RegistrationToken token = tokenService.generateTokenForUser(user);

		assertEquals(token.getToken(), TOKEN);
		assertEquals(token.getUser(), user);
		assertEquals(EXPIRATION_DATE, token.getExpirationDate());
		verify(tokenRepository, times(1)).save(token);
	}

	@Test(expected = InvalidTokenException.class)
	public void validateInexistentToken() {
		doReturn(null).when(tokenRepository).findByToken(TOKEN);

		tokenService.validateTokenForUser(user, TOKEN);
	}

	@Test(expected = InvalidTokenException.class)
	public void validateTokenWithoutMatchingUser() {
		User user2 = new User();
		user2.setId(33l);
		token.setUser(user2);

		doReturn(token).when(tokenRepository).findByToken(TOKEN);

		tokenService.validateTokenForUser(user, TOKEN);
	}

	@Test
	public void validateValidToken() {
		ForgotPasswordToken token = new ForgotPasswordToken();
		token.setUser(user);

		doReturn(token).when(tokenRepository).findByToken(TOKEN);

		tokenService.validateTokenForUser(user, TOKEN);
	}

	@Test(expected = InvalidTokenException.class)
	public void invalidateInexistentToken() {
		doThrow(new InvalidTokenException()).when(tokenRepository).findByToken(TOKEN);

		tokenService.invalidateToken(TOKEN);
	}

	@Test
	public void invalidateValidToken() {
		doReturn(token).when(tokenRepository).findByToken(TOKEN);

		tokenService.invalidateToken(TOKEN);

		verify(tokenRepository, times(1)).delete(token);
	}

}
