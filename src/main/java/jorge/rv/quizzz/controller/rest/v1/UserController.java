package jorge.rv.quizzz.controller.rest.v1;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jorge.rv.quizzz.controller.utils.RestVerifier;
import jorge.rv.quizzz.model.Quiz;
import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.service.QuizService;
import jorge.rv.quizzz.service.UserService;
import jorge.rv.quizzz.service.usermanagement.RegistrationService;

@RestController
@RequestMapping(UserController.ROOT_MAPPING)
public class UserController {

	public static final String ROOT_MAPPING = "/api/users";

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private UserService userService;

	@Autowired
	private QuizService quizService;

	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	@PreAuthorize("permitAll")
	public ResponseEntity<?> signUp(@ModelAttribute @Valid User user, BindingResult result) {

		RestVerifier.verifyModelResult(result);
		User newUser = registrationService.startRegistration(user);

		if (!registrationService.isRegistrationCompleted(newUser)) {
			return ResponseEntity.status(HttpStatus.OK).build();
		} else {
			return ResponseEntity.status(HttpStatus.CREATED).build();
		}
	}

	@RequestMapping(value = "/{user_id}/continueRegistration", method = RequestMethod.GET)
	@PreAuthorize("permitAll")
	public ResponseEntity<?> nextRegistrationStep(@PathVariable Long user_id, String token) {
		User user = userService.find(user_id);
		registrationService.continueRegistration(user, token);

		if (!registrationService.isRegistrationCompleted(user)) {
			return ResponseEntity.status(HttpStatus.OK).build();
		} else {
			return ResponseEntity.status(HttpStatus.CREATED).build();
		}
	}

	@RequestMapping(value = "/{user_id}", method = RequestMethod.DELETE)
	@PreAuthorize("isAuthenticated()")
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable Long user_id) {

		userService.delete(user_id);
	}

	@RequestMapping(value = "/{user_id}/quizzes", method = RequestMethod.GET)
	@PreAuthorize("permitAll")
	@ResponseStatus(HttpStatus.OK)
	public Page<Quiz> getQuizzesByUser(Pageable pageable, @PathVariable Long user_id) {

		User user = userService.find(user_id);
		return quizService.findQuizzesByUser(user, pageable);
	}

}
