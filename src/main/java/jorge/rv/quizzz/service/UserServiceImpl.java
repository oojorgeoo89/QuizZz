package jorge.rv.quizzz.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.exceptions.UserAlreadyExistsException;
import jorge.rv.quizzz.model.Role;
import jorge.rv.quizzz.model.Roles;
import jorge.rv.quizzz.model.UserInfo;
import jorge.rv.quizzz.repository.RoleRepository;
import jorge.rv.quizzz.repository.UserRepository;

@Service("userService")
public class UserServiceImpl implements UserService {

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
	public UserInfo saveUser(UserInfo user) throws UserAlreadyExistsException {
		if (userRepository.findByEmail(user.getEmail()) != null) {
			throw new UserAlreadyExistsException();
		}
		
		user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(1);
        Role userRole = roleRepository.findByRole(Roles.USER.toString());
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
		return userRepository.save(user);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserInfo user = userRepository.findByEmail(username);
		
		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (Role role : user.getRoles()){
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getRole()));
        }
        
        return user;
		
	}
	
	@Override
	public UserInfo find(Long id) throws ResourceUnavailableException {
		UserInfo user = userRepository.findOne(id);
		
		if (user == null) {
			throw new ResourceUnavailableException();
		}
		
		return user;
	}

	@Override
	public void delete(Long user_id, UserInfo user) throws UnauthorizedActionException, ResourceUnavailableException {
		UserInfo userToDelete = find(user_id);
		accessControlService.checkUserPriviledges(user, userToDelete);
		
		userRepository.delete(userToDelete);
	}

	

}
