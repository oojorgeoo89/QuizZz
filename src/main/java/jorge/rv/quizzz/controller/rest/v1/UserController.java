package jorge.rv.quizzz.controller.rest.v1;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jorge.rv.quizzz.controller.utils.RestVerifier;
import jorge.rv.quizzz.exceptions.ModelVerificationException;
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
	
	@Autowired
	UserService userService;
	
	@Autowired
	QuizService quizService;
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	@PreAuthorize("permitAll")
	@ResponseStatus(HttpStatus.CREATED)
	public User save(@Valid User user, BindingResult result) 
			throws UserAlreadyExistsException, ModelVerificationException {
		
		RestVerifier.verifyModelResult(result);
		return userService.saveUser(user);
	}
	
	@RequestMapping(value = "/{user_id}", method = RequestMethod.DELETE)
	@PreAuthorize("isAuthenticated()")
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable Long user_id) 
			throws UnauthorizedActionException, ResourceUnavailableException {
		
		userService.delete(user_id);	
	}
	
	@RequestMapping(value = "/{user_id}/quizzes", method = RequestMethod.GET)
	@PreAuthorize("permitAll")
	@ResponseStatus(HttpStatus.OK)
	public Page<Quiz> getQuizzesByUser(Pageable pageable, @PathVariable Long user_id) 
			throws ResourceUnavailableException {
		
		User user = userService.find(user_id);
		return quizService.findQuizzesByUser(user, pageable);
	}

}
