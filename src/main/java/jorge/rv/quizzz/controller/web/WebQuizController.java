package jorge.rv.quizzz.controller.web;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import jorge.rv.quizzz.controller.utils.RestVerifier;
import jorge.rv.quizzz.exceptions.ModelVerificationException;
import jorge.rv.quizzz.model.AuthenticatedUser;
import jorge.rv.quizzz.model.Question;
import jorge.rv.quizzz.model.Quiz;
import jorge.rv.quizzz.service.QuestionService;
import jorge.rv.quizzz.service.QuizService;
import jorge.rv.quizzz.service.accesscontrol.AccessControlService;

@Controller
public class WebQuizController {

	@Autowired
	QuizService quizService;

	@Autowired
	QuestionService questionService;

	@Autowired
	AccessControlService<Quiz> accessControlServiceQuiz;

	@Autowired
	AccessControlService<Question> accessControlServiceQuestion;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home() {
		return "home";
	}

	@RequestMapping(value = "/createQuiz", method = RequestMethod.GET)
	@PreAuthorize("isAuthenticated()")
	public String newQuiz(Map<String, Object> model) {
		return "createQuiz";
	}

	@RequestMapping(value = "/createQuiz", method = RequestMethod.POST)
	@PreAuthorize("isAuthenticated()")
	public String newQuiz(@AuthenticationPrincipal AuthenticatedUser user, @Valid Quiz quiz, BindingResult result,
			Map<String, Object> model) {
		Quiz newQuiz;

		try {
			RestVerifier.verifyModelResult(result);
			newQuiz = quizService.save(quiz, user.getUser());
		} catch (ModelVerificationException e) {
			return "createQuiz";
		}

		return "redirect:/editQuiz/" + newQuiz.getId();
	}

	@RequestMapping(value = "/editQuiz/{quiz_id}", method = RequestMethod.GET)
	@PreAuthorize("isAuthenticated()")
	public ModelAndView editQuiz(@PathVariable long quiz_id) {
		Quiz quiz = quizService.find(quiz_id);
		accessControlServiceQuiz.canCurrentUserUpdateObject(quiz);

		ModelAndView mav = new ModelAndView();
		mav.addObject("quiz", quiz);
		mav.setViewName("editQuiz");

		return mav;
	}

	@RequestMapping(value = "/editAnswer/{question_id}", method = RequestMethod.GET)
	@PreAuthorize("isAuthenticated()")
	public ModelAndView editAnswer(@PathVariable long question_id) {
		Question question = questionService.find(question_id);
		accessControlServiceQuestion.canCurrentUserUpdateObject(question);

		ModelAndView mav = new ModelAndView();
		mav.addObject("question", question);
		mav.setViewName("editAnswers");

		return mav;
	}

	@RequestMapping(value = "/quiz/{quiz_id}", method = RequestMethod.GET)
	@PreAuthorize("permitAll")
	public ModelAndView getQuiz(@PathVariable long quiz_id) {
		Quiz quiz = quizService.find(quiz_id);

		ModelAndView mav = new ModelAndView();
		mav.addObject("quiz", quiz);
		mav.setViewName("quizView");

		return mav;
	}

	@RequestMapping(value = "/quiz/{quiz_id}/play", method = RequestMethod.GET)
	@PreAuthorize("permitAll")
	public ModelAndView playQuiz(@PathVariable long quiz_id) {
		Quiz quiz = quizService.find(quiz_id);

		ModelAndView mav = new ModelAndView();
		mav.addObject("quiz", quiz);
		mav.setViewName("playQuiz");

		return mav;
	}
}
