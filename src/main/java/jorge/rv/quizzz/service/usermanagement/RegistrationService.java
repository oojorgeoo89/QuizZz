package jorge.rv.quizzz.service.usermanagement;

import jorge.rv.quizzz.model.User;

public interface RegistrationService {
	User startRegistration(User user);

	User continueRegistration(User user, String token);

	boolean isRegistrationCompleted(User user);
}
