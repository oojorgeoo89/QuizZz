package jorge.rv.quizzz.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import jorge.rv.quizzz.controller.utils.WebHelper;
import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.service.UserService;
import jorge.rv.quizzz.service.usermanagement.UserManagementService;

@Controller
@RequestMapping("/user")
public class UserManagementController {

	@Autowired
	private UserManagementService userManagementService;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	@PreAuthorize("permitAll")
	public ModelAndView login(@ModelAttribute User user) {
		return WebHelper.returnView("login");
	}
	
	@RequestMapping(value = "/forgotPassword", method = RequestMethod.GET)
	@PreAuthorize("permitAll")
	public ModelAndView forgotPassword() {
		return WebHelper.returnView("forgotPassword");
	}
	
	@RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
	@PreAuthorize("permitAll")
	public ModelAndView forgotPassword(String email) {
		try {
			User user = userService.findByEmail(email);
			userManagementService.ResendPassword(user);
		} catch (ResourceUnavailableException e) {
			// Ignoring Username not found to avoid showing whether the user exists or not
		}
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("header", messageSource.getMessage("label.forgotpassword.success.header", null, null));
		mav.addObject("subheader", messageSource.getMessage("label.forgotpassword.success.subheader", null, null));
		return WebHelper.returnView("simplemessage", mav);
	}
	
	@RequestMapping(value = "/{user_id}/resetPassword", method = RequestMethod.GET)
	@PreAuthorize("permitAll")
	public ModelAndView resetPassword(@PathVariable Long user_id, String token) {
		User user = userService.find(user_id);
		userManagementService.verifyResetPasswordToken(user, token);
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("user", user);
		mav.addObject("token", token);
		return WebHelper.returnView("resetPassword", mav);
	}
	
	@RequestMapping(value = "/{user_id}/resetPassword", method = RequestMethod.POST)
	@PreAuthorize("permitAll")
	public ModelAndView resetPassword(@PathVariable Long user_id, String token, String password) {
		User user = userService.find(user_id);
		userManagementService.verifyResetPasswordToken(user, token);
		
		userManagementService.updatePassword(user, password);
		
		return WebHelper.returnView("login");
	}
}
