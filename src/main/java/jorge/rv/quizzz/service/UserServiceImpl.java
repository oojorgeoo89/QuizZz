package jorge.rv.quizzz.service;

import java.util.Arrays;
import java.util.HashSet;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.exceptions.UserAlreadyExistsException;
import jorge.rv.quizzz.model.AuthenticatedUser;
import jorge.rv.quizzz.model.Role;
import jorge.rv.quizzz.model.Roles;
import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.repository.RoleRepository;
import jorge.rv.quizzz.repository.UserRepository;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private AccessControlService accessControlService;
	private PasswordEncoder passwordEncoder; 
    
	@Autowired
	public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, AccessControlService accessControlService, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.accessControlService = accessControlService;
		this.roleRepository = roleRepository;
	}
	
	@Override
	public User saveUser(User user) throws UserAlreadyExistsException {
		if (userRepository.findByEmail(user.getEmail()) != null) {
			logger.error("The mail " + user.getEmail() + " is already in use");
			throw new UserAlreadyExistsException("The mail " + user.getEmail() + " is already in use");
		}
		
		user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(1);
        Role userRole = roleRepository.findByRole(Roles.USER.toString());
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
		return userRepository.save(user);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(username);
		if (user == null) {
			logger.error("The user " + username + " can't be found");
			throw new UsernameNotFoundException("User " + username + " not found.");
		}
        
        return new AuthenticatedUser(user);
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
		accessControlService.checkCurrentUserPriviledges(userToDelete);
		
		userRepository.delete(userToDelete);
	}

	

}
