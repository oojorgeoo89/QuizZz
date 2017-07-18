package jorge.rv.quizzz.exceptions;

public class InvalidTokenException extends QuizZzException {

	private static final long serialVersionUID = 1L;

	public InvalidTokenException() {
		super();
	}

	public InvalidTokenException(String message) {
		super(message);
	}
}