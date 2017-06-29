package jorge.rv.quizzz.controller.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import jorge.rv.quizzz.controller.utils.WebHelper;
import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Quiz;
import jorge.rv.quizzz.service.AccessControlService;
import jorge.rv.quizzz.service.QuizService;

@Controller
public class Quizzes {

	@Autowired
	QuizService quizService;
	
	@Autowired
	AccessControlService accessControlService;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home() {
		return WebHelper.returnView("home");
	}
	
	@RequestMapping(value = "/createQuiz", method = RequestMethod.GET)
	@PreAuthorize("isAuthenticated()")
	public ModelAndView newQuiz(Map<String, Object> model) {
		return WebHelper.returnView("createQuiz");
	}
	
	@RequestMapping(value = "/editQuiz/{quiz_id}", method = RequestMethod.GET)
	@PreAuthorize("isAuthenticated()")
	public ModelAndView editQuiz(@PathVariable long quiz_id) throws ResourceUnavailableException, UnauthorizedActionException {
			Quiz quiz = quizService.find(quiz_id);
			accessControlService.checkCurrentUserPriviledges(quiz);
			
			ModelAndView mav = new ModelAndView();
			mav.addObject("quizId", quiz);
			return WebHelper.returnView("editQuiz");
	}
}
