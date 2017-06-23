package jorge.rv.quizzz.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyExistsException extends QuizZzException {
	
	private static final long serialVersionUID = 1L;

	public UserAlreadyExistsException() {
		super();
	}
	
	public UserAlreadyExistsException(String message) {
		super(message);
	}

}
