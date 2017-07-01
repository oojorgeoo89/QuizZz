package jorge.rv.quizzz.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "role")
public class Role extends BaseModel {
	
	@Column(name="role")
	private String role;
		
	public String getRole() {
		return role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}
	
	@Override
	public String toString() {
		return "ID: " + getId() + " Role: " + getRole();
	}
}