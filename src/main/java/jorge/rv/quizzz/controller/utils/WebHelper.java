package jorge.rv.quizzz.controller.utils;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;

import jorge.rv.quizzz.model.AuthenticatedUser;

public class WebHelper {

	public static ModelAndView returnView(String viewName) {
		return returnView(viewName, new ModelAndView());
	}
	
	public static ModelAndView returnView(String viewName, ModelAndView mav) {
		mav.addObject("authenticatedUser", getCurrentUser());
		mav.setViewName(viewName);
		
		return mav;
	}
	
	private static AuthenticatedUser getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication.getPrincipal() == null || authentication instanceof AnonymousAuthenticationToken) {
			return null;
		}
		
		return (AuthenticatedUser) authentication.getPrincipal();
	}
}
