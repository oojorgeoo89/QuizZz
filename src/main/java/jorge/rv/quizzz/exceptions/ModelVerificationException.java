package jorge.rv.quizzz.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ModelVerificationException extends QuizZzException {
	
	private static final long serialVersionUID = 1L;

	public ModelVerificationException()  {
		super();
	}
	
	public ModelVerificationException(String message) {
		super(message);
	}
}
