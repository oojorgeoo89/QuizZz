package jorge.rv.quizzz.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.exceptions.UserAlreadyExistsException;
import jorge.rv.quizzz.model.UserInfo;

public interface UserService extends UserDetailsService {
	public UserInfo saveUser(UserInfo user) throws UserAlreadyExistsException;
	UserInfo find(Long id) throws ResourceUnavailableException;
	public void delete(Long user_id, UserInfo user) throws UnauthorizedActionException, ResourceUnavailableException;
}