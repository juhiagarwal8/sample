package util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

import v1.user.UserData;

public class DBQueries {
	private Logger logger = LoggerFactory.getLogger(DBQueries.class);

	public boolean createKeySpace() {
		try (CqlSession session = CqlSession.builder().build()) {
			session.execute("CREATE KEYSPACE sample WITH replication "
					+ "= {'class':'SimpleStrategy', 'replication_factor':1};");
			session.execute("USE sample");
			System.out.println("Keyspace created");
			session.close();
		}
		return true;
	}

	public List<UserData> getAllUsers() {
		List<UserData> users = new ArrayList<UserData>();
		try (CqlSession session = CqlSession.builder().withKeyspace("sample")
				.withAuthCredentials("cassandra", "password").build()) {
			ResultSet rs = session.execute("SELECT * FROM user");
			for (Row row : rs) {
				System.out.println(row.getLong("id"));
				System.out.println(row.getString("name"));
				System.out.println(row.getString("email"));
				System.out.println(row.getString("phone"));
				users.add(new UserData(row.getLong("id"), row.getString("name"), row.getString("phone"),
						row.getString("email")));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return users;
	}

}