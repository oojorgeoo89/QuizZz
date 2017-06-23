package jorge.rv.quizzz.controller.rest.v1;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	
	public static final String ROOT_MAPPING = "/questions";
	private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);
	
	@Autowired
	private QuestionService questionService;
	
	@Autowired
	private QuizService quizService;
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> save(@Valid Question question, 
							BindingResult result, 
							@RequestParam Long quiz_id)
									throws ResourceUnavailableException, UnauthorizedActionException {
		
		if (result.hasErrors()) {
			logger.error("Invalid question provided");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getAllErrors());
		}
		
		Quiz quiz = quizService.find(quiz_id);
		question.setQuiz(quiz);
		Question newQuestion =  questionService.save(question);
		return ResponseEntity.status(HttpStatus.CREATED).body(newQuestion);
	}
	
	@RequestMapping(value = "/{question_id}", method = RequestMethod.GET)
	@PreAuthorize("permitAll")
	public ResponseEntity<?> find(@PathVariable Long question_id)
			throws ResourceUnavailableException {
		
		Question question = questionService.find(question_id);
		return ResponseEntity.status(HttpStatus.OK).body(question);
	}
	
	@RequestMapping(value = "/{question_id}", method = RequestMethod.POST)
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> update(@PathVariable Long question_id, 
							@Valid Question question, 
							BindingResult result)
									throws ResourceUnavailableException, UnauthorizedActionException {
		
		if (result.hasErrors()) {
			logger.error("Invalid question provided");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getAllErrors());
		}
		
		Question updatedQuestion = questionService.update(question_id, question);
		return ResponseEntity.status(HttpStatus.OK).body(updatedQuestion);	
	}
	
	@RequestMapping(value = "/{question_id}", method = RequestMethod.DELETE)
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> delete(@PathVariable Long question_id)
			throws ResourceUnavailableException, UnauthorizedActionException {
		
		questionService.delete(question_id);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@RequestMapping(value = "/{question_id}/answers", method = RequestMethod.GET)
	@PreAuthorize("permitAll")
	public ResponseEntity<?> findAnswers(@PathVariable Long question_id)
			throws ResourceUnavailableException {
		
		List<Answer> answers = questionService.findAnswersByQuestion(question_id);
		return ResponseEntity.status(HttpStatus.OK).body(answers);
	}

}
