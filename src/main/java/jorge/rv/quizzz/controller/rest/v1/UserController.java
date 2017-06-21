package jorge.rv.quizzz.controller.rest.v1;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.exceptions.UserAlreadyExistsException;
import jorge.rv.quizzz.model.Quiz;
import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.service.QuizService;
import jorge.rv.quizzz.service.UserService;

@RestController
@RequestMapping(UserController.ROOT_MAPPING)
public class UserController {
	
	public static final String ROOT_MAPPING = "/users";
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	UserService userService;
	
	@Autowired
	QuizService quizService;
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	@PreAuthorize("permitAll")
	public ResponseEntity<?> save(@Valid User user, BindingResult result) {
		if (result.hasErrors()){
			logger.error("Invalid user provided");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
			
		try {
			User createdUser = userService.saveUser(user);
			return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
		} catch (UserAlreadyExistsException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}
	
	@RequestMapping(value = "/{user_id}", method = RequestMethod.DELETE)
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> delete(@PathVariable Long user_id) {
		try {
			userService.delete(user_id);
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (UnauthorizedActionException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} catch (ResourceUnavailableException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	@RequestMapping(value = "/{user_id}/quizzes", method = RequestMethod.GET)
	@PreAuthorize("permitAll")
	public ResponseEntity<?> getQuizzesByUser(Pageable pageable, @PathVariable Long user_id) {
		try {
			User user = userService.find(user_id);
			Page<Quiz> quizzes = quizService.findQuizzesByUser(user, pageable);
			return ResponseEntity.status(HttpStatus.OK).body(quizzes);
		} catch (ResourceUnavailableException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

}
