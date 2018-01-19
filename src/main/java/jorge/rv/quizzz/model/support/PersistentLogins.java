package jorge.rv.quizzz.model.support;

import javax.persistence.Id;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "persistent_logins")
public class PersistentLogins {
	@NotNull
	@Column(name = "username", length = 64)
	String username;
	
	@NotNull
	@Id
	@Column(name = "series", length = 64)
    String series;
	
	@NotNull
	@Column(name = "token", length = 64)
    String token;
	
	@NotNull
	@Column(columnDefinition = "TIMESTAMP")
	Calendar last_used;
}
