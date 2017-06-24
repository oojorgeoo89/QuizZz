package jorge.rv.quizzz.controller.rest.v1;

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
import jorge.rv.quizzz.service.AnswerService;
import jorge.rv.quizzz.service.QuestionService;

@RestController
@RequestMapping(AnswerController.ROOT_MAPPING)
public class AnswerController {
	
	public static final String ROOT_MAPPING = "/answers";
	
	@Autowired
	AnswerService answerService;
	
	@Autowired
	QuestionService questionService;
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	@PreAuthorize("isAuthenticated()")
	@ResponseStatus(HttpStatus.CREATED)
	public Answer save(@Valid Answer answer, 
						BindingResult result, 
						@RequestParam long question_id) 
								throws ResourceUnavailableException, UnauthorizedActionException, ModelVerificationException {
		
		RestVerifier.verifyModelResult(result);
		
		Question question = questionService.find(question_id);
		answer.setQuestion(question);
		return answerService.save(answer);
	}
	
	@RequestMapping(value = "/{answer_id}", method = RequestMethod.GET)
	@PreAuthorize("permitAll")
	@ResponseStatus(HttpStatus.OK)
	public Answer find(@PathVariable Long answer_id) 
			throws ResourceUnavailableException {
		
		return answerService.find(answer_id);
	}
	
	@RequestMapping(value = "/{answer_id}", method = RequestMethod.POST)
	@PreAuthorize("isAuthenticated()")
	@ResponseStatus(HttpStatus.OK)
	public Answer update(@PathVariable Long answer_id, 
							@Valid Answer answer, 
							BindingResult result) 
									throws UnauthorizedActionException, ResourceUnavailableException, ModelVerificationException {
		
		RestVerifier.verifyModelResult(result);
		
		return answerService.update(answer_id, answer);
	}
	
	@RequestMapping(value = "/{answer_id}", method = RequestMethod.DELETE)
	@PreAuthorize("isAuthenticated()")
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable Long answer_id) 
			throws UnauthorizedActionException, ResourceUnavailableException {
		
		answerService.delete(answer_id);
	}
}
