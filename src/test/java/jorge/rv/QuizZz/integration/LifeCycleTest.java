package jorge.rv.QuizZz.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import jorge.rv.quizzz.QuizZzApplication;
import jorge.rv.quizzz.controller.rest.v1.AnswerController;
import jorge.rv.quizzz.controller.rest.v1.QuestionController;
import jorge.rv.quizzz.controller.rest.v1.QuizController;
import jorge.rv.quizzz.controller.rest.v1.UserController;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = QuizZzApplication.class)
@ActiveProfiles("test")
public class LifeCycleTest {
	private static final String EMAIL_KEY = "email";
	private static final String USERNAME_KEY = "name";
	private static final String PASSWORD_KEY = "password";
	private static final String EMAIL_1 = "a@a.com";
	private static final String USERNAME_1 = "User 1";
	private static final String PASSWORD_1 = "Password1";
	private static final String EMAIL_2 = "b@b.com";
	private static final String USERNAME_2 = "User 2";
	private static final String PASSWORD_2 = "Password2";
	
	private static final String QUIZ_NAME_KEY = "name";
	private static final String QUIZ_DESCRIPTION_KEY = "description";
	private static final String QUIZ_NAME_1 = "Quiz 1";
	private static final String QUIZ_DESCRIPTION_1 = "User 1";
	private static final String QUIZ_NAME_2 = "Quiz 2";
	private static final String QUIZ_DESCRIPTION_2 = "User 2";
	
	private static final String QUESTION_TEXT_KEY = "text";
	private static final String QUESTION_QUIZ_KEY = "quiz_id";
	private static final String QUESTION_TEXT_1 = "Question 1";
	private static final String QUESTION_TEXT_2 = "Question 2";
	
	private static final String ANSWER_TEXT_KEY = "text";
	private static final String ANSWER_IS_CORRECT_KEY = "iscorrect";
	private static final String ANSWER_QUESTION_KEY = "question_id";
	private static final String ANSWER_TEXT_1 = "Answer 1";
	private static final String ANSWER_IS_CORRECT_1 = "true";
	private static final String ANSWER_TEXT_2 = "Answer 2";
	private static final String ANSWER_IS_CORRECT_2 = "false";
	
	@Autowired
	private WebApplicationContext context;
	
	private MockMvc mvc;

	@Before
	public void setup() {
		mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.build();
	}

	@Test
	@Sql({"/dbInit.sql"})
	public void testLifeCycle() throws Exception {
		
		/***********************
		 * 
		 * User Controller
		 * 
		 ***********************/
		
		// Attempt to create an User with invalid parameters
		mvc.perform(post(UserController.ROOT_MAPPING)
				.param(USERNAME_KEY, USERNAME_1)
				.param(PASSWORD_KEY, PASSWORD_1)
				.param(EMAIL_KEY, "aom"))
					.andExpect(status().isBadRequest());
		
		// Create a new User
		mvc.perform(post(UserController.ROOT_MAPPING)
				.param(USERNAME_KEY, USERNAME_1)
				.param(PASSWORD_KEY, PASSWORD_1)
				.param(EMAIL_KEY, EMAIL_1))
					.andExpect(status().isCreated());
		
		// Attempt to create a new user with the same parameters
		mvc.perform(post(UserController.ROOT_MAPPING)
				.param(USERNAME_KEY, USERNAME_1)
				.param(PASSWORD_KEY, PASSWORD_1)
				.param(EMAIL_KEY, EMAIL_1))
					.andExpect(status().isConflict());
		
		// Create a second User
		mvc.perform(post(UserController.ROOT_MAPPING)
				.param(USERNAME_KEY, USERNAME_2)
				.param(PASSWORD_KEY, PASSWORD_2)
				.param(EMAIL_KEY, EMAIL_2))
					.andExpect(status().isCreated());
		
		/***********************
		 * 
		 * Quiz
		 * 
		 ***********************/
		
		// Attempt to create a new Quiz without being logged on
		mvc.perform(post(QuizController.ROOT_MAPPING)
				.param(QUIZ_NAME_KEY, QUIZ_NAME_1)
				.param(QUIZ_DESCRIPTION_KEY, QUIZ_DESCRIPTION_1))
					.andExpect(status().isUnauthorized());
		
		// Attempt to create a new Quiz with wrong credentials
		mvc.perform(post(QuizController.ROOT_MAPPING)
				.param(QUIZ_NAME_KEY, QUIZ_NAME_1)
				.param(QUIZ_DESCRIPTION_KEY, QUIZ_DESCRIPTION_1)
				.with(httpBasic("Rand", PASSWORD_1)))	
					.andExpect(status().isUnauthorized());

		// Attempt to create a new Quiz with wrong parameters
		mvc.perform(post(QuizController.ROOT_MAPPING)
				.param(QUIZ_NAME_KEY, "a")
				.param(QUIZ_DESCRIPTION_KEY, QUIZ_DESCRIPTION_1)
				.with(httpBasic(EMAIL_1, PASSWORD_1)))	
					.andExpect(status().isBadRequest());
		
		// Create a new Quiz with the first user
		mvc.perform(post(QuizController.ROOT_MAPPING)
				.param(QUIZ_NAME_KEY, QUIZ_NAME_1)
				.param(QUIZ_DESCRIPTION_KEY, QUIZ_DESCRIPTION_1)
				.with(httpBasic(EMAIL_1, PASSWORD_1)))	
					.andExpect(status().isCreated());
		
		// Create a new Quiz with the second user
		mvc.perform(post(QuizController.ROOT_MAPPING)
				.param(QUIZ_NAME_KEY, QUIZ_NAME_2)
				.param(QUIZ_DESCRIPTION_KEY, QUIZ_DESCRIPTION_2)
				.with(httpBasic(EMAIL_2, PASSWORD_2)))	
					.andExpect(status().isCreated());
		
		// List all Quizzes without providing credentials
		mvc.perform(get(QuizController.ROOT_MAPPING))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", Matchers.hasSize(2)));
		
		// List all Quizzes providing credentials
		mvc.perform(get(QuizController.ROOT_MAPPING)
				.with(httpBasic(EMAIL_1, PASSWORD_1)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", Matchers.hasSize(2)));
		
		// Search Quizzes
		mvc.perform(get(QuizController.ROOT_MAPPING + "?filter=" + QUIZ_NAME_1.substring(2, QUIZ_NAME_1.length())))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", Matchers.hasSize(1)));
		
		// Search Quizzes
		mvc.perform(get(QuizController.ROOT_MAPPING + "?filter="))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", Matchers.hasSize(2)));
		
		// Search Quizzes no results
		mvc.perform(get(QuizController.ROOT_MAPPING + "?filter=" + "testString"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", Matchers.hasSize(0)));
				
		// Search Quizzes providing credentials
		mvc.perform(get(QuizController.ROOT_MAPPING + "?filter=" + QUIZ_NAME_1.substring(2, QUIZ_NAME_1.length()))
				.with(httpBasic(EMAIL_1, PASSWORD_1)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", Matchers.hasSize(1)));
		
		// List all quizzes by Invalid User
		mvc.perform(get(UserController.ROOT_MAPPING + "/55/quizzes"))
				.andExpect(status().isNotFound());
		
		// List all quizzes from the first user
		mvc.perform(get(UserController.ROOT_MAPPING + "/1/quizzes"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", Matchers.hasSize(1)));
		
		// List a quiz that doesn't exist
		mvc.perform(get(QuizController.ROOT_MAPPING + "/55"))
				.andExpect(status().isNotFound());
		
		// List a quiz that exists
		mvc.perform(get(QuizController.ROOT_MAPPING + "/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", Matchers.is(1)));
				
		// Update a quiz without being authenticated
		mvc.perform(post(QuizController.ROOT_MAPPING + "/2")
				.param(QUIZ_NAME_KEY, QUIZ_NAME_1 + " updated")
				.param(QUIZ_DESCRIPTION_KEY, QUIZ_DESCRIPTION_1 + " updated"))	
					.andExpect(status().isUnauthorized());
		
		// Update a quiz with invalid parameters
		mvc.perform(post(QuizController.ROOT_MAPPING + "/2")
				.param(QUIZ_NAME_KEY, "a")
				.param(QUIZ_DESCRIPTION_KEY, "a")
				.with(httpBasic(EMAIL_1, PASSWORD_1)))	
					.andExpect(status().isBadRequest());
		
		// Update a quiz that doesn't exist
		mvc.perform(post(QuizController.ROOT_MAPPING + "/55")
				.param(QUIZ_NAME_KEY, QUIZ_NAME_1 + " updated")
				.param(QUIZ_DESCRIPTION_KEY, QUIZ_DESCRIPTION_1 + " updated")
				.with(httpBasic(EMAIL_1, PASSWORD_1)))	
					.andExpect(status().isNotFound());

		// Update a quiz from another user
		mvc.perform(post(QuizController.ROOT_MAPPING + "/1")
				.param(QUIZ_NAME_KEY, QUIZ_NAME_1 + " updated")
				.param(QUIZ_DESCRIPTION_KEY, QUIZ_DESCRIPTION_1 + " updated")
				.with(httpBasic(EMAIL_2, PASSWORD_2)))	
					.andExpect(status().isUnauthorized());
		
		// Update a quiz
		mvc.perform(post(QuizController.ROOT_MAPPING + "/1")
				.param(QUIZ_NAME_KEY, QUIZ_NAME_1 + " updated")
				.param(QUIZ_DESCRIPTION_KEY, QUIZ_DESCRIPTION_1 + " updated")
				.with(httpBasic(EMAIL_1, PASSWORD_1)))	
					.andExpect(status().isOk())
					.andExpect(jsonPath("$." + QUIZ_NAME_KEY, Matchers.containsString(QUIZ_NAME_1 + " updated")))
					.andExpect(jsonPath("$." + QUIZ_DESCRIPTION_KEY, Matchers.containsString(QUIZ_DESCRIPTION_1 + " updated")));
		
		// List all Questions for an invalid Quiz
		mvc.perform(get(QuizController.ROOT_MAPPING + "/55/questions")
				.with(httpBasic(EMAIL_1, PASSWORD_1)))
				.andExpect(status().isNotFound());
		
		// List all Questions for the first Quiz
		mvc.perform(get(QuizController.ROOT_MAPPING + "/2/questions")
				.with(httpBasic(EMAIL_1, PASSWORD_1)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(0)));
		
		/***********************
		 * 
		 * Question Controller
		 * 
		 ***********************/
		
		// Attempt to create a new Question without being logged on
		mvc.perform(post(QuestionController.ROOT_MAPPING)
				.param(QUESTION_TEXT_KEY, QUESTION_TEXT_1)
				.param(QUESTION_QUIZ_KEY, Integer.toString(1)))
					.andExpect(status().isUnauthorized());
		
		// Attempt to create a new Quiz with wrong credentials
		mvc.perform(post(QuestionController.ROOT_MAPPING)
				.param(QUESTION_TEXT_KEY, QUESTION_TEXT_1)
				.param(QUESTION_QUIZ_KEY, Integer.toString(1))
				.with(httpBasic("Rand", PASSWORD_1)))	
					.andExpect(status().isUnauthorized());
		
		// Attempt to create a question for an inexistent quiz
		mvc.perform(post(QuestionController.ROOT_MAPPING)
				.param(QUESTION_TEXT_KEY, QUESTION_TEXT_1)
				.param(QUESTION_QUIZ_KEY, Integer.toString(55))
				.with(httpBasic(EMAIL_1, PASSWORD_1)))	
					.andExpect(status().isNotFound());
		
		// Attempt to question for a quiz that doesn't belong to the user/
		mvc.perform(post(QuestionController.ROOT_MAPPING)
				.param(QUESTION_TEXT_KEY, QUESTION_TEXT_1)
				.param(QUESTION_QUIZ_KEY, Integer.toString(1))
				.with(httpBasic(EMAIL_2, PASSWORD_2)))	
					.andExpect(status().isUnauthorized());
		
		// Create a question for the first Quiz
		mvc.perform(post(QuestionController.ROOT_MAPPING)
				.param(QUESTION_TEXT_KEY, QUESTION_TEXT_1)
				.param(QUESTION_QUIZ_KEY, Integer.toString(1))
				.with(httpBasic(EMAIL_1, PASSWORD_1)))	
					.andExpect(status().isCreated());
		
		// Get a question that doesn't exist
		mvc.perform(get(QuestionController.ROOT_MAPPING + "/55"))	
					.andExpect(status().isNotFound());
		
		// Get a question that exists
		mvc.perform(get(QuestionController.ROOT_MAPPING + "/1"))	
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id", Matchers.is(1)));
		
		// Add two more questions (one per user) and check we can get them through the Quiz Controller
		mvc.perform(post(QuestionController.ROOT_MAPPING)
				.param(QUESTION_TEXT_KEY, QUESTION_TEXT_2)
				.param(QUESTION_QUIZ_KEY, Integer.toString(1))
				.with(httpBasic(EMAIL_1, PASSWORD_1)))	
					.andExpect(status().isCreated());

		mvc.perform(post(QuestionController.ROOT_MAPPING)
				.param(QUESTION_TEXT_KEY, QUESTION_TEXT_1)
				.param(QUESTION_QUIZ_KEY, Integer.toString(2))
				.with(httpBasic(EMAIL_2, PASSWORD_2)))	
					.andExpect(status().isCreated());
		
		mvc.perform(get(QuizController.ROOT_MAPPING + "/1/questions")
				.with(httpBasic(EMAIL_1, PASSWORD_1)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(2)));
		
		mvc.perform(get(QuizController.ROOT_MAPPING + "/2/questions")
				.with(httpBasic(EMAIL_1, PASSWORD_1)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(1)));
		
		// Update a question without being authenticated
		mvc.perform(post(QuestionController.ROOT_MAPPING + "/1")
				.param(QUESTION_TEXT_KEY, QUESTION_TEXT_1 + " updated"))
					.andExpect(status().isUnauthorized());
		
		// Update a question with invalid parameters
		mvc.perform(post(QuestionController.ROOT_MAPPING + "/1")
				.param(QUESTION_TEXT_KEY, "a")
				.with(httpBasic(EMAIL_1, PASSWORD_1)))	
					.andExpect(status().isBadRequest());
		
		// Update a question that doesn't exist
		mvc.perform(post(QuestionController.ROOT_MAPPING + "/55")
				.param(QUESTION_TEXT_KEY, QUESTION_TEXT_1 + " updated")
				.with(httpBasic(EMAIL_1, PASSWORD_1)))
					.andExpect(status().isNotFound());

		// Update a question from another user
		mvc.perform(post(QuestionController.ROOT_MAPPING + "/1")
				.param(QUESTION_TEXT_KEY, QUESTION_TEXT_1 + " updated")
				.with(httpBasic(EMAIL_2, PASSWORD_2)))	
					.andExpect(status().isUnauthorized());
		
		// Update a question
		mvc.perform(post(QuestionController.ROOT_MAPPING + "/1")
				.param(QUESTION_TEXT_KEY, QUESTION_TEXT_1 + " updated")
				.with(httpBasic(EMAIL_1, PASSWORD_1)))	
					.andExpect(status().isOk())
					.andExpect(jsonPath("$." + QUESTION_TEXT_KEY, Matchers.containsString(QUESTION_TEXT_1 + " updated")));
		
		// List all Answers for an invalid Quiz
		mvc.perform(get(QuestionController.ROOT_MAPPING + "/55/answers"))
				.andExpect(status().isNotFound());
		
		// List all Answers for the first Quiz
		mvc.perform(get(QuestionController.ROOT_MAPPING + "/2/answers"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(0)));
		
		/***********************
		 * 
		 * Answer Controller
		 * 
		 ***********************/
		
		// Attempt to create an new Answer without being logged on
		mvc.perform(post(AnswerController.ROOT_MAPPING)
				.param(ANSWER_TEXT_KEY, ANSWER_TEXT_1)
				.param(ANSWER_IS_CORRECT_KEY, ANSWER_IS_CORRECT_1)
				.param(ANSWER_QUESTION_KEY, Integer.toString(1)))
					.andExpect(status().isUnauthorized());
		
		// Attempt to create an new Answer with wrong credentials
		mvc.perform(post(AnswerController.ROOT_MAPPING)
				.param(ANSWER_TEXT_KEY, ANSWER_TEXT_1)
				.param(ANSWER_IS_CORRECT_KEY, ANSWER_IS_CORRECT_1)
				.param(ANSWER_QUESTION_KEY, Integer.toString(1))
				.with(httpBasic("Rand", PASSWORD_1)))	
					.andExpect(status().isUnauthorized());
		
		// Attempt to create an Answer for an inexistent quiz
		mvc.perform(post(AnswerController.ROOT_MAPPING)
				.param(ANSWER_TEXT_KEY, ANSWER_TEXT_1)
				.param(ANSWER_IS_CORRECT_KEY, ANSWER_IS_CORRECT_1)
				.param(ANSWER_QUESTION_KEY, Integer.toString(55))
				.with(httpBasic(EMAIL_1, PASSWORD_1)))	
					.andExpect(status().isNotFound());
		
		// Attempt to question for an Answer that doesn't belong to the user/
		mvc.perform(post(AnswerController.ROOT_MAPPING)
				.param(ANSWER_TEXT_KEY, ANSWER_TEXT_1)
				.param(ANSWER_IS_CORRECT_KEY, ANSWER_IS_CORRECT_1)
				.param(ANSWER_QUESTION_KEY, Integer.toString(1))
				.with(httpBasic(EMAIL_2, PASSWORD_2)))	
					.andExpect(status().isUnauthorized());
		
		// Create an Answer for the first Question
		mvc.perform(post(AnswerController.ROOT_MAPPING)
				.param(ANSWER_TEXT_KEY, ANSWER_TEXT_1)
				.param(ANSWER_IS_CORRECT_KEY, ANSWER_IS_CORRECT_1)
				.param(ANSWER_QUESTION_KEY, Integer.toString(1))
				.with(httpBasic(EMAIL_1, PASSWORD_1)))	
					.andExpect(status().isCreated());
		
		// Get an Answer that doesn't exist
		mvc.perform(get(AnswerController.ROOT_MAPPING + "/55"))	
					.andExpect(status().isNotFound());
		
		// Get an Answer that exists
		mvc.perform(get(AnswerController.ROOT_MAPPING + "/1"))	
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id", Matchers.is(1)));
		
		// Add two more answers (one per question) and check we can get them through the Question Controller
		mvc.perform(post(AnswerController.ROOT_MAPPING)
				.param(ANSWER_TEXT_KEY, ANSWER_TEXT_2)
				.param(ANSWER_IS_CORRECT_KEY, ANSWER_IS_CORRECT_2)
				.param(ANSWER_QUESTION_KEY, Integer.toString(1))
				.with(httpBasic(EMAIL_1, PASSWORD_1)))	
					.andExpect(status().isCreated());

		mvc.perform(post(AnswerController.ROOT_MAPPING)
				.param(ANSWER_TEXT_KEY, ANSWER_TEXT_1)
				.param(ANSWER_IS_CORRECT_KEY, ANSWER_IS_CORRECT_1)
				.param(ANSWER_QUESTION_KEY, Integer.toString(2))
				.with(httpBasic(EMAIL_1, PASSWORD_1)))	
					.andExpect(status().isCreated());
		
		mvc.perform(get(QuestionController.ROOT_MAPPING + "/1/answers")
				.with(httpBasic(EMAIL_1, PASSWORD_1)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(2)));
		
		mvc.perform(get(QuestionController.ROOT_MAPPING + "/2/answers")
				.with(httpBasic(EMAIL_1, PASSWORD_1)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(1)));
		
		// Update an Answer without being authenticated
		mvc.perform(post(AnswerController.ROOT_MAPPING + "/1")
				.param(ANSWER_TEXT_KEY, ANSWER_TEXT_1 + " updated"))
					.andExpect(status().isUnauthorized());
		
		// Update an Answer with invalid parameters
		mvc.perform(post(AnswerController.ROOT_MAPPING + "/1")
				.param(ANSWER_TEXT_KEY, "")
				.with(httpBasic(EMAIL_1, PASSWORD_1)))	
					.andExpect(status().isBadRequest());
		
		// Update an Answer that doesn't exist
		mvc.perform(post(AnswerController.ROOT_MAPPING + "/55")
				.param(ANSWER_TEXT_KEY, ANSWER_TEXT_1 + " updated")
				.param(ANSWER_IS_CORRECT_KEY, ANSWER_IS_CORRECT_1)
				.with(httpBasic(EMAIL_1, PASSWORD_1)))
					.andExpect(status().isNotFound());

		// Update an Answer from another user
		mvc.perform(post(AnswerController.ROOT_MAPPING + "/1")
				.param(ANSWER_TEXT_KEY, ANSWER_TEXT_1 + " updated")
				.param(ANSWER_IS_CORRECT_KEY, ANSWER_IS_CORRECT_1)
				.with(httpBasic(EMAIL_2, PASSWORD_2)))	
					.andExpect(status().isUnauthorized());
		
		// Update an Answer
		mvc.perform(post(AnswerController.ROOT_MAPPING + "/1")
				.param(ANSWER_TEXT_KEY, ANSWER_TEXT_1 + " updated")
				.param(ANSWER_IS_CORRECT_KEY, ANSWER_IS_CORRECT_2)
				.with(httpBasic(EMAIL_1, PASSWORD_1)))	
					.andExpect(status().isOk())
					.andExpect(jsonPath("$." + ANSWER_TEXT_KEY, Matchers.containsString(ANSWER_TEXT_1 + " updated")));
		
		/***********************
		 * 
		 * Deleting assets
		 * 
		 ***********************/
		
		// Deleting a Answer by an invalid user
		mvc.perform(delete(AnswerController.ROOT_MAPPING + "/2")
				.with(httpBasic(EMAIL_2, PASSWORD_2)))	
					.andExpect(status().isUnauthorized());
		
		// Deleting a Answer
		mvc.perform(delete(AnswerController.ROOT_MAPPING + "/2")
				.with(httpBasic(EMAIL_1, PASSWORD_1)))	
					.andExpect(status().isOk());
		
		// Deleting a Question by an invalid user
		mvc.perform(delete(QuestionController.ROOT_MAPPING + "/2")
				.with(httpBasic(EMAIL_2, PASSWORD_2)))	
					.andExpect(status().isUnauthorized());
		
		// Deleting a Question
		mvc.perform(delete(QuestionController.ROOT_MAPPING + "/2")
				.with(httpBasic(EMAIL_1, PASSWORD_1)))	
					.andExpect(status().isOk());
		
		// Ensuring it got deleted
		mvc.perform(delete(QuestionController.ROOT_MAPPING + "/2")
				.with(httpBasic(EMAIL_1, PASSWORD_1)))	
					.andExpect(status().isNotFound());
		
		// Deleting a Quiz by an invalid user
		mvc.perform(delete(QuizController.ROOT_MAPPING + "/2")
				.with(httpBasic(EMAIL_1, PASSWORD_1)))	
					.andExpect(status().isUnauthorized());
		
		// Deleting a Quiz
		mvc.perform(delete(QuizController.ROOT_MAPPING + "/2")
				.with(httpBasic(EMAIL_2, PASSWORD_2)))	
					.andExpect(status().isOk());
		
		// Ensuring it got deleted
		mvc.perform(delete(QuizController.ROOT_MAPPING + "/2")
				.with(httpBasic(EMAIL_2, PASSWORD_2)))	
					.andExpect(status().isNotFound());
		
		// Deleting an User by an invalid user
		mvc.perform(delete(UserController.ROOT_MAPPING + "/2")
				.with(httpBasic(EMAIL_1, PASSWORD_1)))	
					.andExpect(status().isUnauthorized());
		
		// Deleting an User
		mvc.perform(delete(UserController.ROOT_MAPPING + "/2")
				.with(httpBasic(EMAIL_2, PASSWORD_2)))	
					.andExpect(status().isOk());
		
		// Ensuring it got deleted
		mvc.perform(delete(UserController.ROOT_MAPPING + "/2")
				.with(httpBasic(EMAIL_2, PASSWORD_2)))	
					.andExpect(status().isUnauthorized());
		
	}
	
}
