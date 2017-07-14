package jorge.rv.quizzz.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jorge.rv.quizzz.exceptions.InvalidParametersException;
import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Answer;
import jorge.rv.quizzz.model.Question;
import jorge.rv.quizzz.model.Quiz;
import jorge.rv.quizzz.repository.QuestionRepository;

@Service("QuestionService")
public class QuestionServiceImpl implements QuestionService {
	
	private static final Logger logger = LoggerFactory.getLogger(QuestionServiceImpl.class);
	private QuestionRepository questionRepository;
	
	private AnswerService answerService;
	
	@Autowired
	public QuestionServiceImpl(QuestionRepository questionRepository, AnswerService answerService) {
		this.questionRepository = questionRepository;
		this.answerService = answerService;
	}
	
	@Override
	@Transactional
	public Question save(Question question) throws UnauthorizedActionException {
		int count = questionRepository.countByQuiz(question.getQuiz());
		question.setOrder(count + 1);
		
		return questionRepository.save(question);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Question find(Long id) throws ResourceUnavailableException {
		Question question = questionRepository.findOne(id);
		
		if (question == null) {
			logger.error("Question " + id + " not found");
			throw new ResourceUnavailableException("Question " + id + " not found");
		}
		
		return question;
	}

	@Override
	@Transactional
	public Question update(Question newQuestion) throws ResourceUnavailableException, UnauthorizedActionException {
		Question currentQuestion = find(newQuestion.getId());
		
		mergeQuestions(currentQuestion, newQuestion);
		return questionRepository.save(currentQuestion);
	}
	
	@Override
	@Transactional
	public void delete(Long id) throws ResourceUnavailableException, UnauthorizedActionException {
		Question currentQuestion = find(id);
		
		questionRepository.delete(currentQuestion);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Answer> findAnswersByQuestion(Long id) throws ResourceUnavailableException {
		Question question = find(id);
		
		return answerService.findQuestionsByQuiz(question);
	}

	private void mergeQuestions(Question currentQuestion, Question newQuestion) {
		currentQuestion.setText(newQuestion.getText());
		
		if (newQuestion.getOrder() != null)
			currentQuestion.setOrder(newQuestion.getOrder());
	}

	@Override
	public Boolean checkAnswer(Question question, Long selectedAnswer) {
		for (Answer answer : question.getAnswers()) {
			if (answer.getId().equals(selectedAnswer)) {
				return answerService.checkAnswer(answer);
			}
		}
		
		throw new InvalidParametersException("The answer '" + selectedAnswer + "' is not available");
	}

	@Override
	public List<Question> findQuestionsByQuiz(Quiz quiz) {
		return questionRepository.findByQuizOrderByOrderAsc(quiz);
	}

}
