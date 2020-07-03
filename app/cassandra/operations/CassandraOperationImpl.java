package cassandra.operations;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.AtomicMonotonicTimestampGenerator;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;

import util.Constants;
import util.PropertiesCache;
import util.Response;
import v1.user.UserData;

public class CassandraOperationImpl implements CassandraOperation {

	private static Cluster cluster;

	private static Logger logger = LoggerFactory.getLogger(CassandraOperationImpl.class);

	public CassandraOperationImpl() {
	}

	private void createCassandraConnection(String[] hosts) throws Exception {
		try {
			cluster = createCluster(hosts);
		} catch (Exception e) {
			logger.info("Error occured while creating cassandra connection :", e);
			throw new Exception();
		}
	}

	private static Cluster createCluster(String[] hosts) {
		PropertiesCache cache = PropertiesCache.getInstance();
		String username = cache.getProperty(Constants.USERNAME);
		String password = cache.getProperty(Constants.PASSWORD);
		Cluster builder = Cluster.builder().addContactPoints(hosts).withCredentials(username, password)
				.withProtocolVersion(ProtocolVersion.V3).withRetryPolicy(DefaultRetryPolicy.INSTANCE)
				.withTimestampGenerator(new AtomicMonotonicTimestampGenerator()).build();

		return builder;
	}

	private Session getSession(String keyspace) throws Exception {
		return cluster.connect(keyspace);
	}

	@Override
	public List<UserData> getAllRecords(String keyspaceName, String tableName) throws Exception {
		long startTime = System.currentTimeMillis();
		List<UserData> users = new ArrayList<>();
		logger.info("Cassandra Service getAllRecords method started at == {}", startTime);
		try {
			createCassandraConnection(new String[] { Constants.LOCALHOST });
			Select selectQuery = QueryBuilder.select().all().from(keyspaceName, tableName);
			ResultSet rs = getSession(keyspaceName).execute(selectQuery);
			for (Row row : rs) {
				System.out.println(row.getString(Constants.ID));
				System.out.println(row.getString(Constants.NAME));
				System.out.println(row.getString(Constants.EMAIL));
				System.out.println(row.getString(Constants.PHONE));
				users.add(new UserData(row.getString(Constants.ID), row.getString(Constants.NAME),
						row.getString(Constants.PHONE), row.getString(Constants.EMAIL)));
			}
			cluster.close();
		} catch (Exception e) {
			logger.error(Constants.EXCEPTION_MSG_FETCH + tableName + " : " + e.getMessage(), e);
			logQueryElapseTime(Constants.GET_ALL_RECORDS, startTime);
			cluster.close();
			throw new Exception();
		}
		return users;
	}

	public static String getPreparedStatement(String keyspaceName, String tableName, Map<String, Object> map) {
		StringBuilder query = new StringBuilder();
		query.append(Constants.INSERT_INTO + keyspaceName + Constants.DOT + tableName + Constants.OPEN_BRACE);
		Set<String> keySet = map.keySet();
		query.append(String.join(",", keySet) + Constants.VALUES_WITH_BRACE);
		StringBuilder commaSepValueBuilder = new StringBuilder();
		for (int i = 0; i < keySet.size(); i++) {
			commaSepValueBuilder.append(Constants.QUE_MARK);
			if (i != keySet.size() - 1) {
				commaSepValueBuilder.append(Constants.COMMA);
			}
		}
		query.append(commaSepValueBuilder + Constants.CLOSING_BRACE);
		logger.info(query.toString());
		return query.toString();
	}

	@Override
	public Response upsertRecord(String keyspaceName, String tableName, Map<String, Object> request) throws Exception {
		long startTime = System.currentTimeMillis();
		logger.info(Constants.CASSANDRA_SERVICE_UPSERT_RECORD_METHOD_STARTED_AT, startTime);
		Response response = new Response();
		try {
			createCassandraConnection(new String[] { Constants.LOCALHOST });
			String query = getPreparedStatement(keyspaceName, tableName, request);
			logger.info(query);
			PreparedStatement statement = getSession(keyspaceName).prepare(query);
			BoundStatement boundStatement = new BoundStatement(statement);
			Iterator<Object> iterator = request.values().iterator();
			Object[] array = new Object[request.keySet().size()];
			int i = 0;
			while (iterator.hasNext()) {
				array[i++] = iterator.next();
			}
			BoundStatement q = boundStatement.bind(array);
			logger.info(request.toString());
			getSession(keyspaceName).execute(q);
			response.put(Constants.RESPONSE, Constants.SUCCESS);

		} catch (Exception e) {
			cluster.close();
			logger.error(Constants.EXCEPTION_MSG_UPSERT + tableName + " : " + e.getMessage(), e);
			throw new Exception();
		}
		cluster.close();
		logQueryElapseTime(Constants.UPSERT_RECORD, startTime);
		return response;
	}

	@Override
	public UserData getRecordByIdentifier(String keyspaceName, String tableName, String key) throws Exception {
		long startTime = System.currentTimeMillis();
		UserData u = new UserData();
		logger.info("Cassandra Service getRecordBy key method started at == {}", startTime);
		try {
			createCassandraConnection(new String[] { Constants.LOCALHOST });
			Session session = getSession(keyspaceName);
			String query = Constants.SELECT_FROM + keyspaceName + "." + tableName + Constants.WHERE_ID_ + key
					+ Constants.STRING;
			logger.info(query);
			ResultSet results = session.execute(query);
			for (Row row : results) {
				System.out.println(row.getString(Constants.ID));
				System.out.println(row.getString(Constants.NAME));
				System.out.println(row.getString(Constants.EMAIL));
				System.out.println(row.getString(Constants.PHONE));
				u = new UserData(row.getString(Constants.ID), row.getString(Constants.NAME),
						row.getString(Constants.PHONE), row.getString(Constants.EMAIL));
			}
		} catch (Exception e) {
			logger.error(Constants.EXCEPTION_MSG_FETCH + tableName + " : " + e.getMessage(), e);
			throw new Exception();
		} finally {
			cluster.close();
		}
		logQueryElapseTime(Constants.GET_RECORD_BY_IDENTIFIER, startTime);
		return u;
	}

	private void logQueryElapseTime(String operation, long startTime) {

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		String message = "Cassandra operation {0} started at {1} and completed at {2}. Total time elapsed is {3}.";
		MessageFormat mf = new MessageFormat(message);
		logger.info(mf.format(new Object[] { operation, startTime, stopTime, elapsedTime }));
	}

	@Override
	public Collection<UserData> getAllUserFromGroup(String id) throws Exception {
		long startTime = System.currentTimeMillis();
		List<UserData> users = new ArrayList<>();
		logger.info(id);
		logger.info("Cassandra Service getAllRecords method started at == {}", startTime);
		try {
			createCassandraConnection(new String[] { Constants.LOCALHOST });
			String query = Constants.SELECT_FROM_SAMPLE_USER_GROUP_WHERE_GROUPID + id;
			query = query.concat(Constants.ALLOW_FILTERING);
			query = query.replace("\"", "\'");
			logger.info(query);
			ResultSet rs = getSession(Constants.KEYSPACE).execute(query);
			List<String> userIds = new ArrayList<>();
			for (Row row : rs) {
				id = row.getString(Constants.USERID);
				System.out.println(id);
				userIds.add(id);
			}
			for (String ids : userIds)
				users.add(getRecordByIdentifier(Constants.KEYSPACE, Constants.TABLENAME, ids));
			cluster.close();
		} catch (Exception e) {
			logger.error(Constants.EXCEPTION_MSG_FETCH + Constants.USER_GROUP + " : " + e.getMessage(), e);
			logQueryElapseTime(Constants.GET_ALL_RECORDS, startTime);
			cluster.close();
			throw new Exception();
		}
		return users;
	}

	@Override
	public Response addUserToGroup(String userId, String groupId, String id) throws Exception {
		long startTime = System.currentTimeMillis();
		logger.info("Cassandra Service add User to group method started at == {}", startTime);
		Response response = new Response();
		try {
			createCassandraConnection(new String[] { Constants.LOCALHOST });
			String query = Constants.INSERT_INTO_ + id + "," + userId + "," + groupId + ");";
			logger.info(query);
			getSession(Constants.KEYSPACE).execute(query);
			response.put(Constants.RESPONSE, Constants.SUCCESS);
		} catch (Exception e) {
			logger.info(Constants.EXCEPTION_OCCURED_WHILE_INSERTING_RECORD_TO + Constants.USER_GROUP + " : "
					+ e.getMessage(), e);
			throw new Exception();
		}
		logQueryElapseTime("addUserToGroup", startTime);
		cluster.close();
		return response;
	}

}