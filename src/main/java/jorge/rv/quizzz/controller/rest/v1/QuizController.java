package jorge.rv.quizzz.controller.rest.v1;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.AuthenticatedUser;
import jorge.rv.quizzz.model.Question;
import jorge.rv.quizzz.model.Quiz;
import jorge.rv.quizzz.service.QuizService;

@RestController
@RequestMapping(QuizController.ROOT_MAPPING)
public class QuizController {
	
	public static final String ROOT_MAPPING = "/quizzes";
	private static final Logger logger = LoggerFactory.getLogger(QuizController.class);

	@Autowired
	private QuizService quizService;
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	@PreAuthorize("permitAll")
	public ResponseEntity<?> findAll(Pageable pageable,
										@RequestParam(required = false) String filter) {
		
		Page<Quiz> quizzes = null;
		
		if (filter == null) {
			quizzes = quizService.findAll(pageable);
		} else {
			quizzes = quizService.search(filter, pageable);
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(quizzes);
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> save(@AuthenticationPrincipal AuthenticatedUser user, @Valid Quiz quiz, BindingResult result) {
		
		if (result.hasErrors()) {
			logger.error("Invalid quiz provided");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getAllErrors());
		}
		
		Quiz newQuiz = quizService.save(quiz, user.getUser());
		return ResponseEntity.status(HttpStatus.CREATED).body(newQuiz);
	}
	
	@RequestMapping(value = "/{quiz_id}", method = RequestMethod.GET)
	@PreAuthorize("permitAll")
	public ResponseEntity<?> find(@PathVariable Long quiz_id) throws ResourceUnavailableException {	
		Quiz quiz = quizService.find(quiz_id);
		return ResponseEntity.status(HttpStatus.OK).body(quiz);
	}
	
	@RequestMapping(value = "/{quiz_id}", method = RequestMethod.POST)
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> update(@PathVariable Long quiz_id, @Valid Quiz quiz, BindingResult result) 
			throws ResourceUnavailableException, UnauthorizedActionException {
		
		if (result.hasErrors()) {
			logger.error("Invalid quiz provided");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getAllErrors());
		}
		
		Quiz returnedQuiz = quizService.update(quiz_id, quiz);
		return ResponseEntity.status(HttpStatus.OK).body(returnedQuiz);
	}
	
	@RequestMapping(value = "/{quiz_id}", method = RequestMethod.DELETE)
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> delete(@PathVariable Long quiz_id) 
			throws ResourceUnavailableException, UnauthorizedActionException {
		
		quizService.delete(quiz_id);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@RequestMapping(value = "/{quiz_id}/questions", method = RequestMethod.GET)
	@PreAuthorize("permitAll")
	public ResponseEntity<?> findQuestions(@PathVariable Long quiz_id) 
			throws ResourceUnavailableException {
		
		List<Question> questions =  quizService.findQuestionsByQuiz(quiz_id);
		return ResponseEntity.status(HttpStatus.OK).body(questions);
	}
	
}
