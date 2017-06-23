package jorge.rv.quizzz.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceUnavailableException extends QuizZzException {
	
	private static final long serialVersionUID = 1L;

	public ResourceUnavailableException() {
		super();
	}
	
	public ResourceUnavailableException(String message) {
		super(message);
	}
}
