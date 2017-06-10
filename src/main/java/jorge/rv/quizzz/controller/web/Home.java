package jorge.rv.quizzz.controller.web;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class Home {
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home() {
		return "home";
	}
	
	@RequestMapping(value = "/editQuiz", method = RequestMethod.GET)
	public String newQuiz(Map<String, Object> model) {
		model.put("quizId", 0);
		return "editQuiz";
	}
	
	@RequestMapping(value = "/editQuiz/{quiz_id}", method = RequestMethod.GET)
	public String editQuiz(Map<String, Object> model, @PathVariable long quiz_id) {
		model.put("quizId", quiz_id);
		return "editQuiz";
	}
}
