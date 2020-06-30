package cassandra.connection;

import java.util.List;

import com.datastax.driver.core.Session;

public interface CassandraConnectionManager {

	void createConnection(String[] hosts) throws Exception;

	Session getSession(String keyspaceName);

	List<String> getTableList(String keyspaceName);
}