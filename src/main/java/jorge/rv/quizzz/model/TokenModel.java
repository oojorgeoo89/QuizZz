package jorge.rv.quizzz.model;

import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public abstract class TokenModel extends BaseModel {
	
	@NotNull
	String token;
	
	@OneToOne
	User user;
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@Override
	public String toString() {
		return getToken();
	}
}
