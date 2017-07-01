package jorge.rv.quizzz.service.usermanagement;

import jorge.rv.quizzz.model.User;

public interface UserManagementService {

	void ResendPassword(User user);
	void verifyResetPasswordToken(User user, String token);
	void updatePassword(User user, String password);
	
}
