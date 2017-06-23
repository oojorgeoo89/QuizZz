package jorge.rv.quizzz.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedActionException extends QuizZzException {

	private static final long serialVersionUID = 1L;

	public UnauthorizedActionException() {
		super();
	}
	
	public UnauthorizedActionException(String message) {
		super(message);
	}
}
