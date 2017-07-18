package jorge.rv.QuizZz.integration.utils;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;

import com.icegreen.greenmail.util.GreenMail;

public class MailHelper {

	public static String extractUrlFromMail(Message message) throws IOException, MessagingException {
		String body = (String) message.getContent();

		Pattern pattern = Pattern.compile(".*http://localhost:8080(.*)((\r\n)|(\n)).*");
		Matcher matcher = pattern.matcher(body);

		if (matcher.find()) {
			return matcher.group(1);
		}

		return null;
	}

	public static String waitForEmailAndExtractUrl(GreenMail smtpServer) throws IOException, MessagingException {
		smtpServer.waitForIncomingEmail(1);
		Message[] messages = smtpServer.getReceivedMessages();
		smtpServer.reset();
		assertEquals(1, messages.length);

		return extractUrlFromMail(messages[0]);
	}

}
