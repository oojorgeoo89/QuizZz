package jorge.rv.quizzz.service;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.exceptions.UserAlreadyExistsException;
import jorge.rv.quizzz.model.AuthenticatedUser;
import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.repository.UserRepository;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;

	@Autowired
	public UserServiceImpl(UserRepository userRepository,
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public User saveUser(User user) throws UserAlreadyExistsException {
		if (userRepository.findByEmail(user.getEmail()) != null) {
			logger.error("The mail " + user.getEmail() + " is already in use");
			throw new UserAlreadyExistsException("The mail " + user.getEmail() + " is already in use");
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setEnabled(false);

		return userRepository.save(user);
	}

	@Override
	/*
	 * Look up by both Email and Username. Throw exception if it wasn't in
	 * either. TODO: Join Username and Email into one JPQL
	 */
	public AuthenticatedUser loadUserByUsername(String username) throws UsernameNotFoundException {
		User user;

		try {
			user = findByUsername(username);
		} catch (ResourceUnavailableException e) {
			try {
				user = findByEmail(username);
			} catch (ResourceUnavailableException e2) {
				throw new UsernameNotFoundException(username + " couldn't be resolved to any user");
			}
		}

		return new AuthenticatedUser(user);
	}

	@Override
	public User findByUsername(String username) throws ResourceUnavailableException {
		User user = userRepository.findByUsername(username);

		if (user == null) {
			logger.error("The user " + username + " doesn't exist");
			throw new ResourceUnavailableException("The user " + username + " doesn't exist");
		}

		return user;
	}

	@Override
	public User find(Long id) throws ResourceUnavailableException {
		User user = userRepository.findOne(id);

		if (user == null) {
			logger.error("The user " + id + " can't be found");
			throw new ResourceUnavailableException("User " + id + " not found.");
		}

		return user;
	}

	@Override
	public void delete(Long user_id) throws UnauthorizedActionException, ResourceUnavailableException {
		User userToDelete = find(user_id);

		userRepository.delete(userToDelete);
	}

	@Override
	public User setRegistrationCompleted(User user) {
		user.setEnabled(true);
		return userRepository.save(user);
	}

	@Override
	public boolean isRegistrationCompleted(User user) {
		return user.getEnabled();
	}

	@Override
	public User findByEmail(String email) throws ResourceUnavailableException {
		User user = userRepository.findByEmail(email);

		if (user == null) {
			logger.error("The mail " + email + " can't be found");
			throw new ResourceUnavailableException("The mail " + email + " can't be found");
		}

		return user;
	}

	@Override
	public User updatePassword(User user, String password) throws ResourceUnavailableException {
		user.setPassword(passwordEncoder.encode(password));
		return userRepository.save(user);
	}

}
