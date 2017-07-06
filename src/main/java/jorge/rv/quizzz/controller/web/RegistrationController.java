package jorge.rv.quizzz.controller.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import jorge.rv.quizzz.controller.utils.RestVerifier;
import jorge.rv.quizzz.controller.utils.WebHelper;
import jorge.rv.quizzz.exceptions.ModelVerificationException;
import jorge.rv.quizzz.exceptions.UserAlreadyExistsException;
import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.service.UserService;
import jorge.rv.quizzz.service.usermanagement.RegistrationService;

@Controller
@RequestMapping("/user")
public class RegistrationController {
	
	@Autowired
	private RegistrationService registrationService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private MessageSource messageSource;
	
	@RequestMapping(value = "/registration", method = RequestMethod.GET)
	@PreAuthorize("permitAll")
	public ModelAndView showRegistrationForm(@ModelAttribute User user) {
		return WebHelper.returnView("registration");
	}
	
	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	@PreAuthorize("permitAll")
	public ModelAndView signUp(@ModelAttribute @Valid User user, BindingResult result) {
		User newUser;
		
		try {
			RestVerifier.verifyModelResult(result);
			newUser = registrationService.startRegistration(user);
		} catch (ModelVerificationException e) {
			return WebHelper.returnView("registration");
		} catch (UserAlreadyExistsException e) {
			result.rejectValue("email", "label.user.emailInUse");
			return WebHelper.returnView("registration");
		}
		
		return registrationStepView(newUser);
	}
	
	@RequestMapping(value = "/{user_id}/continueRegistration", method = RequestMethod.GET)
	@PreAuthorize("permitAll")
	public ModelAndView nextRegistrationStep(@PathVariable Long user_id, String token) {
		User user = userService.find(user_id);
		registrationService.continueRegistration(user, token);
		
		return registrationStepView(user);
	}
	
	private ModelAndView registrationStepView(User user) {
		ModelAndView mav = new ModelAndView();
		if (!registrationService.isRegistrationCompleted(user)) {
			mav.addObject("header", messageSource.getMessage("label.registration.step1.header", null, null));
			mav.addObject("subheader", messageSource.getMessage("label.registration.step1.subheader", null, null));
			return WebHelper.returnView("simplemessage", mav);
		} else {
			mav.addObject("header", messageSource.getMessage("label.registration.step2.header", null, null));
			mav.addObject("subheader", messageSource.getMessage("label.registration.step2.subheader", null, null));
			return WebHelper.returnView("simplemessage", mav);
		}
	}
}
