package jorge.rv.quizzz.controller.rest.v1;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.exceptions.UserAlreadyExistsException;
import jorge.rv.quizzz.model.UserInfo;
import jorge.rv.quizzz.service.UserService;

@RestController
@RequestMapping(UserController.ROOT_MAPPING)
public class UserController {
	public static final String ROOT_MAPPING = "/users";
	
	@Autowired
	UserService userService;
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	@PreAuthorize("permitAll")
	public ResponseEntity<?> save(@Valid UserInfo user, BindingResult result) {
		if (result.hasErrors()){
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
			
		try {
			UserInfo createdUser = userService.saveUser(user);
			return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
		} catch (UserAlreadyExistsException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}
	
	@RequestMapping(value = "/{user_id}", method = RequestMethod.DELETE)
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> delete(@AuthenticationPrincipal UserInfo user, @PathVariable Long user_id) {
		try {
			userService.delete(user_id, user);
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (UnauthorizedActionException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} catch (ResourceUnavailableException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

}
