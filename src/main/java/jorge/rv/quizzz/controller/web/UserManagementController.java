package jorge.rv.quizzz.controller.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import jorge.rv.quizzz.controller.utils.RestVerifier;
import jorge.rv.quizzz.controller.utils.WebHelper;
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
	public ModelAndView showRegistrationForm(@ModelAttribute User user) {
		return WebHelper.returnView("registration");
	}
	
	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	@PreAuthorize("permitAll")
	public ModelAndView save(@ModelAttribute @Valid User user, BindingResult result) {
		
		try {
			RestVerifier.verifyModelResult(result);
			userService.saveUser(user);
		} catch (ModelVerificationException e) {
			return WebHelper.returnView("registration");
		} catch (UserAlreadyExistsException e) {
			result.rejectValue("email", "label.user.emailInUse");
			return WebHelper.returnView("registration");
		}
		
		return WebHelper.returnView("home");
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	@PreAuthorize("permitAll")
	public ModelAndView login(@ModelAttribute User user) {
	    return WebHelper.returnView("login");
	}
	
	@RequestMapping(value = "/registration/step1", method = RequestMethod.GET)
	@PreAuthorize("permitAll")
	public ModelAndView step1() {
	    return WebHelper.returnView("registration-step1");
	}
	
	@RequestMapping(value = "/registration/step2", method = RequestMethod.GET)
	@PreAuthorize("permitAll")
	public ModelAndView step2() {
	    return WebHelper.returnView("registration-step2");
	}
	
}
