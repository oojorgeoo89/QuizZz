package jorge.rv.quizzz.service.usermanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jorge.rv.quizzz.model.RegistrationToken;
import jorge.rv.quizzz.model.TokenType;
import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.service.UserService;

@Service
public class RegistrationServiceMail implements RegistrationService {

	private UserService userService;
	private TokenServiceRegistration tokenService;
	private TokenDeliverySystem tokenDeliveryService;
	
	@Autowired
	public RegistrationServiceMail(UserService userService, 
			TokenServiceRegistration tokenService, 
			TokenDeliverySystem tokenDeliveryService) {
		this.userService = userService;
		this.tokenService = tokenService;
		this.tokenDeliveryService = tokenDeliveryService;
	}

	@Override
	public void startRegistration(User user) {
		User newUser = userService.saveUser(user);
		
		RegistrationToken mailToken = tokenService.generateTokenForUser(newUser);
		
		tokenDeliveryService.sendTokenToUser(mailToken, newUser, TokenType.REGISTRATION_MAIL);
	}

	@Override
	public void continueRegistration(User user, String token) {
		tokenService.validateTokenForUser(user, token);
		
		userService.enableUser(user);
		tokenService.invalidateToken(token);
	}

	@Override
	public boolean isRegistrationCompleted(User user) {
		return userService.isUserEnabled(user);
	}

}
