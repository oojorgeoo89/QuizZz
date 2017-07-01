package jorge.rv.quizzz.service.usermanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.service.UserService;

@Service
@Profile("test")
public class RegistrationServiceSimple implements RegistrationService {

	@Autowired
	private UserService userService;
	
	@Override
	public void startRegistration(User user) {
		User newUser = userService.saveUser(user);
		userService.enableUser(newUser);
	}

	@Override
	public void continueRegistration(User user, String token) { }

	@Override
	public boolean isRegistrationCompleted(User user) {	
		return userService.isUserEnabled(user);
	}

}
