package jorge.rv.QuizZz.unitTests.service.usermanagement;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.env.Environment;

import jorge.rv.quizzz.model.TokenModel;
import jorge.rv.quizzz.model.TokenType;
import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.service.usermanagement.TokenDeliverySystem;
import jorge.rv.quizzz.service.usermanagement.TokenDeliverySystemConsole;

public class TokenDeliverySystemConsoleTests {

	private static final String CONFIG_URI = "quizzz.tokens.urls.";
	private static final String TOKEN = "token";

	TokenDeliverySystem tokenDeliverySystem;
	
	// Mocks
	Environment env;
	TokenModel token;
	
	// Models
	User user = new User();
	
	@Before
	public void before() {
		env = mock(Environment.class);
		token = mock(TokenModel.class);

		tokenDeliverySystem = new TokenDeliverySystemConsole(env);
		
		token.setToken(TOKEN);		
		user.setId(1l);
	}
	/* 
	 * Dummy tests to make sure there are no exceptions thrown.
	 */
	@Test
	public void sendEmailToken() {
		doReturn(TOKEN).when(token).getToken();
		doReturn("dummyUrl/%1$d/%2$s").when(env).getProperty(CONFIG_URI + TokenType.REGISTRATION_MAIL.toString().toLowerCase());
		
		tokenDeliverySystem.sendTokenToUser(token, user, TokenType.REGISTRATION_MAIL);
		
		verify(env, times(1)).getProperty(CONFIG_URI + TokenType.REGISTRATION_MAIL.toString().toLowerCase());
	}
	
	@Test
	public void sendForgotPasswordToken() {
		doReturn(TOKEN).when(token).getToken();
		doReturn("dummyUrl/%1$d/%2$s").when(env).getProperty(CONFIG_URI + TokenType.FORGOT_PASSWORD.toString().toLowerCase());
		
		tokenDeliverySystem.sendTokenToUser(token, user, TokenType.FORGOT_PASSWORD);
		
		verify(env, times(1)).getProperty(CONFIG_URI + TokenType.FORGOT_PASSWORD.toString().toLowerCase());
	}
}
