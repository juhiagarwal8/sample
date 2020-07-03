package v1.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Data returned from the database
 */
@Entity
@Table(name = "user")
public class UserData {

	public UserData() {
	}

	public UserData(String name, String phone, String email) {
		this.name = name;
		this.phone = phone;
		this.email = email;
	}

	public UserData(String id, String name, String phone, String email) {
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.email = email;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public String id;
	public String name;
	public String phone;
	public String email;
}
