package jorge.rv.quizzz.exceptions;

public class UserAlreadyExistsException extends QuizZzException {

	private static final long serialVersionUID = 1L;

	public UserAlreadyExistsException() {
		super();
	}

	public UserAlreadyExistsException(String message) {
		super(message);
	}

}
