package jorge.rv.quizzz.service.usermanagement;

import jorge.rv.quizzz.model.User;

public interface RegistrationService {
	void startRegistration(User user);
	void continueRegistration(User user, String token);
	boolean isRegistrationCompleted(User user);
}
