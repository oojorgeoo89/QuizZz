package jorge.rv.quizzz.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.exceptions.UserAlreadyExistsException;
import jorge.rv.quizzz.model.User;

public interface UserService extends UserDetailsService {
	public User saveUser(User user) throws UserAlreadyExistsException;
	User find(Long id) throws ResourceUnavailableException;
	public void delete(Long user_id) throws UnauthorizedActionException, ResourceUnavailableException;
}