package jorge.rv.quizzz.controller.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jorge.rv.quizzz.controller.utils.RestVerifier;
import jorge.rv.quizzz.exceptions.ModelVerificationException;
import jorge.rv.quizzz.exceptions.UserAlreadyExistsException;
import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.service.UserService;

@Controller
@RequestMapping("/user/")
public class UserManagementController {
	
	@Autowired
	UserService userService;
	
	@RequestMapping(value = "/registration", method = RequestMethod.GET)
	public String showRegistrationForm(Model model) {
	    User user = new User();
	    model.addAttribute("user", user);
	    
	    return "registration";
	}
	
	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	@PreAuthorize("permitAll")
	public String save(@Valid User user, BindingResult result, Model model) {
		
		try {
			RestVerifier.verifyModelResult(result);
			userService.saveUser(user);
		} catch (ModelVerificationException e) {
		    model.addAttribute("user", user);
		    return "registration";
		} catch (UserAlreadyExistsException e) {
		    model.addAttribute("user", user);
		    return "registration";
		}
		
		return "home";
	}
	
}
