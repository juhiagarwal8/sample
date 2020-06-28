package v1.user;

import javax.persistence.*;

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

	public UserData(Long id, String name, String phone, String email) {
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.email = email;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	public String name;
	public String phone;
	public String email;
}
