package jorge.rv.quizzz.controller.rest.v1;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jorge.rv.quizzz.controller.utils.RestVerifier;
import jorge.rv.quizzz.exceptions.ModelVerificationException;
import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Answer;
import jorge.rv.quizzz.model.Question;
import jorge.rv.quizzz.model.Quiz;
import jorge.rv.quizzz.service.QuestionService;
import jorge.rv.quizzz.service.QuizService;

@RestController
@RequestMapping(QuestionController.ROOT_MAPPING)
public class QuestionController {
	
	public static final String ROOT_MAPPING = "/api/questions";
	
	@Autowired
	private QuestionService questionService;
	
	@Autowired
	private QuizService quizService;
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	@PreAuthorize("isAuthenticated()")
	@ResponseStatus(HttpStatus.CREATED)
	public Question save(@Valid Question question, 
							BindingResult result, 
							@RequestParam Long quiz_id)
									throws ResourceUnavailableException, UnauthorizedActionException, ModelVerificationException {
		
		RestVerifier.verifyModelResult(result);
		
		Quiz quiz = quizService.find(quiz_id);
		question.setQuiz(quiz);
		return questionService.save(question);
	}
	
	@RequestMapping(value = "/{question_id}", method = RequestMethod.GET)
	@PreAuthorize("permitAll")
	@ResponseStatus(HttpStatus.OK)
	public Question find(@PathVariable Long question_id)
			throws ResourceUnavailableException {
		
		return questionService.find(question_id);
	}
	
	@RequestMapping(value = "/{question_id}", method = RequestMethod.POST)
	@PreAuthorize("isAuthenticated()")
	@ResponseStatus(HttpStatus.OK)
	public Question update(@PathVariable Long question_id, 
							@Valid Question question, 
							BindingResult result)
									throws ResourceUnavailableException, UnauthorizedActionException, ModelVerificationException {
		
		RestVerifier.verifyModelResult(result);
		return questionService.update(question_id, question);
			
	}
	
	@RequestMapping(value = "/{question_id}", method = RequestMethod.DELETE)
	@PreAuthorize("isAuthenticated()")
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable Long question_id)
			throws ResourceUnavailableException, UnauthorizedActionException {
		
		questionService.delete(question_id);
	}
	
	@RequestMapping(value = "/{question_id}/answers", method = RequestMethod.GET)
	@PreAuthorize("permitAll")
	@ResponseStatus(HttpStatus.OK)
	public List<Answer> findAnswers(@PathVariable Long question_id)
			throws ResourceUnavailableException {
		
		return questionService.findAnswersByQuestion(question_id);
	}

}
