package jorge.rv.quizzz.controller.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Quiz;
import jorge.rv.quizzz.service.AccessControlService;
import jorge.rv.quizzz.service.QuizService;

@Controller
public class Home {

	@Autowired
	QuizService quizService;
	
	@Autowired
	AccessControlService accessControlService;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home() {
		return "home";
	}
	
	@RequestMapping(value = "/editQuiz", method = RequestMethod.GET)
	@PreAuthorize("isAuthenticated()")
	public String newQuiz(Map<String, Object> model) {
		model.put("quizId", 0);
		return "editQuiz";
	}
	
	@RequestMapping(value = "/editQuiz/{quiz_id}", method = RequestMethod.GET)
	@PreAuthorize("isAuthenticated()")
	public String editQuiz(Map<String, Object> model, @PathVariable long quiz_id) throws ResourceUnavailableException, UnauthorizedActionException {
			Quiz quiz = quizService.find(quiz_id);
			accessControlService.checkCurrentUserPriviledges(quiz);
			
			model.put("quizId", quiz_id);
			return "editQuiz";
	}
}
