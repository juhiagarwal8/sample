package v1.user;

/**
 * Resource for the API. This is a presentation class for frontend work.
 */
public class UserResource {
	private String id;
	private String name;
	private String link;

	public String getName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}

	public String getEmail() {
		return email;
	}

	private String phone;
	private String email;

	public UserResource() {
	}

	public UserResource(String id, String name, String phone, String email) {
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.email = email;
	}

	public UserResource(UserData data, String link) {
		this.id = data.id.toString();
		this.name = data.name;
		this.phone = data.phone;
		this.email = data.email;
		this.link = link;
	}

	public String getId() {
		return id;
	}

	public String getLink() {
		return link;
	}

}
