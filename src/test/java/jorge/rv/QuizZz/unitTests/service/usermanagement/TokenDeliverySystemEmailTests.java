package jorge.rv.QuizZz.unitTests.service.usermanagement;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import jorge.rv.quizzz.model.TokenModel;
import jorge.rv.quizzz.model.TokenType;
import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.service.usermanagement.token.TokenDeliverySystem;
import jorge.rv.quizzz.service.usermanagement.token.TokenDeliverySystemEmail;

public class TokenDeliverySystemEmailTests {

	private static final String CONFIG_URI = "quizzz.tokens.%s";
	private static final String TOKEN = "token";

	TokenDeliverySystem tokenDeliverySystem;

	@Captor
	private ArgumentCaptor<SimpleMailMessage> captor;

	// Mocks
	MessageSource messageSource;
	TokenModel token;
	JavaMailSender mailServer;

	// Models
	User user = new User();

	@Before
	public void before() {
		messageSource = mock(MessageSource.class);
		token = mock(TokenModel.class);
		mailServer = mock(JavaMailSender.class);

		tokenDeliverySystem = new TokenDeliverySystemEmail(messageSource, mailServer);

		captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

		user.setId(1l);
	}

	@Test
	public void sendEmailRegistrationToken() {
		String registrationConfigUri = String.format(CONFIG_URI, TokenType.REGISTRATION_MAIL.toString().toLowerCase());
		doReturn(TOKEN).when(token).getToken();

		doReturn("dummyUrl/%1$d/%2$s").when(messageSource).getMessage(registrationConfigUri + ".url", null, null);
		doReturn("Subject").when(messageSource).getMessage(registrationConfigUri + ".subject", null, null);
		doReturn("Body %1$s %2$s").when(messageSource).getMessage(registrationConfigUri + ".body", null, null);

		tokenDeliverySystem.sendTokenToUser(token, user, TokenType.REGISTRATION_MAIL);

		verify(messageSource, times(1)).getMessage(registrationConfigUri + ".url", null, null);
		verify(mailServer, times(1)).send(captor.capture());
		assertThat(captor.getValue().getText(), containsString("dummyUrl/1/" + TOKEN));
		assertThat(captor.getValue().getText(), containsString(user.getId().toString()));
		assertThat(captor.getValue().getSubject(), containsString("Subject"));
	}

	@Test
	public void failToSendEmailRegistrationToken_shouldNotThrowException() {
		String registrationConfigUri = String.format(CONFIG_URI, TokenType.REGISTRATION_MAIL.toString().toLowerCase());
		doReturn(TOKEN).when(token).getToken();
		doThrow(new MailSendException("")).when(mailServer).send((SimpleMailMessage) any());

		doReturn("dummyUrl/%1$d/%2$s").when(messageSource).getMessage(registrationConfigUri + ".url", null, null);
		doReturn("Subject").when(messageSource).getMessage(registrationConfigUri + ".subject", null, null);
		doReturn("Body %1$s %2$s").when(messageSource).getMessage(registrationConfigUri + ".body", null, null);

		tokenDeliverySystem.sendTokenToUser(token, user, TokenType.REGISTRATION_MAIL);
	}

	@Test
	public void sendForgotPasswordToken() {
		String forgotPasswordConfigUri = String.format(CONFIG_URI, TokenType.FORGOT_PASSWORD.toString().toLowerCase());
		doReturn(TOKEN).when(token).getToken();

		doReturn("dummyUrl/%1$d/%2$s").when(messageSource).getMessage(forgotPasswordConfigUri + ".url", null, null);
		doReturn("Subject").when(messageSource).getMessage(forgotPasswordConfigUri + ".subject", null, null);
		doReturn("Body %1$s %2$s").when(messageSource).getMessage(forgotPasswordConfigUri + ".body", null, null);

		tokenDeliverySystem.sendTokenToUser(token, user, TokenType.FORGOT_PASSWORD);

		verify(messageSource, times(1)).getMessage(forgotPasswordConfigUri + ".url", null, null);
		verify(mailServer, times(1)).send(captor.capture());
		assertThat(captor.getValue().getText(), containsString("dummyUrl/1/" + TOKEN));
		assertThat(captor.getValue().getText(), containsString(user.getId().toString()));
		assertThat(captor.getValue().getSubject(), containsString("Subject"));
	}

}
