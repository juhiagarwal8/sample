
import javax.inject.Singleton;

import cassandra.connection.DBUtil;

@Singleton
public class ApplicationStart {

	public ApplicationStart() throws Exception {
		checkCassandraConnections();
	}

	private static void checkCassandraConnections() throws Exception {
		DBUtil.checkCassandraDbConnections();
	}

}
