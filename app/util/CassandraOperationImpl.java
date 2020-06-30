package util;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.datastax.driver.core.exceptions.QueryExecutionException;
import com.datastax.driver.core.exceptions.QueryValidationException;
import com.datastax.driver.core.querybuilder.Clause;
import com.datastax.driver.core.querybuilder.Delete;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Select.Builder;
import com.datastax.driver.core.querybuilder.Select.Where;
import com.datastax.driver.core.querybuilder.Update;
import com.datastax.driver.core.querybuilder.Update.Assignments;

import cassandra.connection.CassandraConnectionManager;
import cassandra.connection.CassandraConnectionMngrFactory;
import v1.user.UserData;

public class CassandraOperationImpl implements CassandraOperation {

	private static CassandraConnectionManager connectionManager;

	private Logger logger = LoggerFactory.getLogger(CassandraOperationImpl.class);

	public CassandraOperationImpl() {
		connectionManager = CassandraConnectionMngrFactory.getInstance();
	}

	@Inject
	public CassandraOperationImpl(CassandraConnectionManager connectionManager) {
		CassandraOperationImpl.connectionManager = connectionManager;
	}

	@Override
	public Response insertRecord(String keyspaceName, String tableName, String query) throws Exception {
		long startTime = System.currentTimeMillis();
		logger.info("Cassandra Service insertRecord method started at == {}", startTime);
		Response response = new Response();
		try {
			connectionManager.getSession(keyspaceName).execute(query);
			response.put(Constants.RESPONSE, Constants.SUCCESS);
		} catch (Exception e) {
			logger.info("Exception occured while inserting record to " + tableName + " : " + e.getMessage(), e);
			throw new Exception();
		}
		logQueryElapseTime("insertRecord", startTime);
		return response;
	}

	@Override
	public Response updateRecord(String keyspaceName, String tableName, Map<String, Object> request) throws Exception {
		long startTime = System.currentTimeMillis();
		logger.info("Cassandra Service updateRecord method started at == {}", startTime);
		Response response = new Response();
		try {
			String query = CassandraUtil.getUpdateQueryStatement(keyspaceName, tableName, request);
			PreparedStatement statement = connectionManager.getSession(keyspaceName).prepare(query);
			Object[] array = new Object[request.size()];
			int i = 0;
			String str = "";
			int index = query.lastIndexOf(Constants.SET.trim());
			str = query.substring(index + 4);
			str = str.replace(Constants.EQUAL_WITH_QUE_MARK, "");
			str = str.replace(Constants.WHERE_ID, "");
			str = str.replace(Constants.SEMICOLON, "");
			String[] arr = str.split(",");
			for (String key : arr) {
				array[i++] = request.get(key.trim());
			}
			array[i] = request.get(Constants.IDENTIFIER);
			BoundStatement boundStatement = statement.bind(array);
			connectionManager.getSession(keyspaceName).execute(boundStatement);
			response.put(Constants.RESPONSE, Constants.SUCCESS);
		} catch (Exception e) {
			logger.error(Constants.EXCEPTION_MSG_UPDATE + tableName + " : " + e.getMessage(), e);
			throw new Exception();
		}
		logQueryElapseTime("updateRecord", startTime);
		return response;
	}

	@Override
	public Response deleteRecord(String keyspaceName, String tableName, String identifier) throws Exception {
		long startTime = System.currentTimeMillis();
		logger.info("Cassandra Service deleteRecord method started at == {}", startTime);
		Response response = new Response();
		try {
			Delete.Where delete = QueryBuilder.delete().from(keyspaceName, tableName)
					.where(eq(Constants.IDENTIFIER, identifier));
			connectionManager.getSession(keyspaceName).execute(delete);
			response.put(Constants.RESPONSE, Constants.SUCCESS);
		} catch (Exception e) {
			logger.error(Constants.EXCEPTION_MSG_DELETE + tableName + " : " + e.getMessage(), e);
			throw new Exception();
		}
		logQueryElapseTime("deleteRecord", startTime);
		return response;
	}

	@Override
	public Response getRecordsByProperty(String keyspaceName, String tableName, String propertyName,
			Object propertyValue) throws Exception {
		return getRecordsByProperty(keyspaceName, tableName, propertyName, propertyValue, null);
	}

	@Override
	public Response getRecordsByProperty(String keyspaceName, String tableName, String propertyName,
			Object propertyValue, List<String> fields) throws Exception {
		Response response = new Response();
		Session session = connectionManager.getSession(keyspaceName);
		try {
			Builder selectBuilder;
			if (CollectionUtils.isNotEmpty(fields)) {
				selectBuilder = QueryBuilder.select((String[]) fields.toArray());
			} else {
				selectBuilder = QueryBuilder.select().all();
			}
			Statement selectStatement = selectBuilder.from(keyspaceName, tableName)
					.where(eq(propertyName, propertyValue));
			ResultSet results = null;
			results = session.execute(selectStatement);
			response = CassandraUtil.createResponse(results);
		} catch (Exception e) {
			logger.error(Constants.EXCEPTION_MSG_FETCH + tableName + " : " + e.getMessage(), e);
			throw new Exception();
		}
		return response;
	}

	@Override
	public Response getRecordsByProperty(String keyspaceName, String tableName, String propertyName,
			List<Object> propertyValueList) throws Exception {
		return getRecordsByProperty(keyspaceName, tableName, propertyName, propertyValueList, null);
	}

	@Override
	public Response getRecordsByProperty(String keyspaceName, String tableName, String propertyName,
			List<Object> propertyValueList, List<String> fields) throws Exception {
		long startTime = System.currentTimeMillis();
		logger.info("Cassandra Service getRecordsByProperty method started at == {}", startTime);
		Response response = new Response();
		try {
			Builder selectBuilder;
			if (CollectionUtils.isNotEmpty(fields)) {
				selectBuilder = QueryBuilder.select(fields.toArray(new String[fields.size()]));
			} else {
				selectBuilder = QueryBuilder.select().all();
			}
			Statement selectStatement = selectBuilder.from(keyspaceName, tableName)
					.where(QueryBuilder.in(propertyName, propertyValueList));
			ResultSet results = connectionManager.getSession(keyspaceName).execute(selectStatement);
			response = CassandraUtil.createResponse(results);
		} catch (Exception e) {
			logger.error(Constants.EXCEPTION_MSG_FETCH + tableName + " : " + e.getMessage(), e);
			throw new Exception();
		}
		logQueryElapseTime("getRecordsByProperty", startTime);
		return response;
	}

	@Override
	public Response getRecordsByProperties(String keyspaceName, String tableName, Map<String, Object> propertyMap)
			throws Exception {
		return getRecordsByProperties(keyspaceName, tableName, propertyMap, null);
	}

	@Override
	public Response getRecordsByProperties(String keyspaceName, String tableName, Map<String, Object> propertyMap,
			List<String> fields) throws Exception {
		long startTime = System.currentTimeMillis();
		logger.info("Cassandra Service getRecordsByProperties method started at == {}", startTime);
		Response response = new Response();
		try {
			Builder selectBuilder;
			if (CollectionUtils.isNotEmpty(fields)) {
				String[] dbFields = fields.toArray(new String[fields.size()]);
				selectBuilder = QueryBuilder.select(dbFields);
			} else {
				selectBuilder = QueryBuilder.select().all();
			}
			Select selectQuery = selectBuilder.from(keyspaceName, tableName);
			if (MapUtils.isNotEmpty(propertyMap)) {
				Where selectWhere = selectQuery.where();
				for (Entry<String, Object> entry : propertyMap.entrySet()) {
					if (entry.getValue() instanceof List) {
						@SuppressWarnings("unchecked")
						List<Object> list = (List<Object>) entry.getValue();
						if (null != list) {
							Object[] propertyValues = list.toArray(new Object[list.size()]);
							Clause clause = QueryBuilder.in(entry.getKey(), propertyValues);
							selectWhere.and(clause);
						}
					} else {
						Clause clause = eq(entry.getKey(), entry.getValue());
						selectWhere.and(clause);
					}
				}
			}
			ResultSet results = connectionManager.getSession(keyspaceName).execute(selectQuery.allowFiltering());
			response = CassandraUtil.createResponse(results);
		} catch (Exception e) {
			logger.error(Constants.EXCEPTION_MSG_FETCH + tableName + " : " + e.getMessage(), e);
			throw new Exception();
		}
		logQueryElapseTime("getRecordsByProperties", startTime);
		return response;
	}

	@Override
	public Response getPropertiesValueById(String keyspaceName, String tableName, String id, String... properties)
			throws Exception {
		long startTime = System.currentTimeMillis();
		logger.info("Cassandra Service getPropertiesValueById method started at == {}", startTime);
		Response response = new Response();
		try {
			String selectQuery = CassandraUtil.getSelectStatement(keyspaceName, tableName, properties);
			PreparedStatement statement = connectionManager.getSession(keyspaceName).prepare(selectQuery);
			BoundStatement boundStatement = new BoundStatement(statement);
			ResultSet results = connectionManager.getSession(keyspaceName).execute(boundStatement.bind(id));
			response = CassandraUtil.createResponse(results);
		} catch (Exception e) {
			logger.error(Constants.EXCEPTION_MSG_FETCH + tableName + " : " + e.getMessage(), e);
			throw new Exception();
		}
		logQueryElapseTime("getPropertiesValueById", startTime);
		return response;
	}

	@Override
	public List<UserData> getAllRecords(String keyspaceName, String tableName) throws Exception {
		long startTime = System.currentTimeMillis();
		logger.info("Cassandra Service getAllRecords method started at == {}", startTime);
		try {
			Select selectQuery = QueryBuilder.select().all().from(keyspaceName, tableName);
			ResultSet rs = connectionManager.getSession(keyspaceName).execute(selectQuery);
			List<UserData> users = new ArrayList<>();
			for (Row row : rs) {
				System.out.println(row.getLong("id"));
				System.out.println(row.getString("name"));
				System.out.println(row.getString("email"));
				System.out.println(row.getString("phone"));
				users.add(new UserData(row.getLong("id"), row.getString("name"), row.getString("phone"),
						row.getString("email")));
			}
			return users;
		} catch (Exception e) {
			logger.error(Constants.EXCEPTION_MSG_FETCH + tableName + " : " + e.getMessage(), e);
			logQueryElapseTime("getAllRecords", startTime);
			throw new Exception();
		}
	}

	@Override
	public Response upsertRecord(String keyspaceName, String tableName, Map<String, Object> request) throws Exception {
		long startTime = System.currentTimeMillis();
		logger.info("Cassandra Service upsertRecord method started at == {}", startTime);
		Response response = new Response();
		try {
			String query = CassandraUtil.getPreparedStatement(keyspaceName, tableName, request);
			PreparedStatement statement = connectionManager.getSession(keyspaceName).prepare(query);
			BoundStatement boundStatement = new BoundStatement(statement);
			Iterator<Object> iterator = request.values().iterator();
			Object[] array = new Object[request.keySet().size()];
			int i = 0;
			while (iterator.hasNext()) {
				array[i++] = iterator.next();
			}
			connectionManager.getSession(keyspaceName).execute(boundStatement.bind(array));
			response.put(Constants.RESPONSE, Constants.SUCCESS);

		} catch (Exception e) {
			if (e.getMessage().contains(Constants.UNKNOWN_IDENTIFIER)) {
				logger.error(Constants.EXCEPTION_MSG_UPSERT + tableName + " : " + e.getMessage(), e);
				throw new Exception();
			}
			logger.error(Constants.EXCEPTION_MSG_UPSERT + tableName + " : " + e.getMessage(), e);
			throw new Exception();
		}
		logQueryElapseTime("upsertRecord", startTime);
		return response;
	}

	@Override
	public Response updateRecord(String keyspaceName, String tableName, Map<String, Object> request,
			Map<String, Object> compositeKey) throws Exception {

		long startTime = System.currentTimeMillis();
		logger.info("Cassandra Service updateRecord method started at == {}", startTime);
		Response response = new Response();
		try {
			Session session = connectionManager.getSession(keyspaceName);
			Update update = QueryBuilder.update(keyspaceName, tableName);
			Assignments assignments = update.with();
			Update.Where where = update.where();
			request.entrySet().stream().forEach(x -> {
				assignments.and(QueryBuilder.set(x.getKey(), x.getValue()));
			});
			compositeKey.entrySet().stream().forEach(x -> {
				where.and(eq(x.getKey(), x.getValue()));
			});
			Statement updateQuery = where;
			session.execute(updateQuery);
		} catch (Exception e) {
			logger.error(Constants.EXCEPTION_MSG_UPDATE + tableName + " : " + e.getMessage(), e);
			if (e.getMessage().contains(Constants.UNKNOWN_IDENTIFIER)) {
				throw new Exception();
			}
			throw new Exception();
		}
		logQueryElapseTime("updateRecord", startTime);
		return response;
	}

	private Response getRecordByIdentifier(String keyspaceName, String tableName, Object key, List<String> fields)
			throws Exception {
		long startTime = System.currentTimeMillis();
		logger.info("Cassandra Service getRecordBy key method started at == {}", startTime);
		Response response = new Response();
		try {
			Session session = connectionManager.getSession(keyspaceName);
			Builder selectBuilder;
			if (CollectionUtils.isNotEmpty(fields)) {
				selectBuilder = QueryBuilder.select(fields.toArray(new String[fields.size()]));
			} else {
				selectBuilder = QueryBuilder.select().all();
			}
			Select selectQuery = selectBuilder.from(keyspaceName, tableName);
			Where selectWhere = selectQuery.where();
			if (key instanceof String) {
				selectWhere.and(eq(Constants.IDENTIFIER, key));
			} else if (key instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, Object> compositeKey = (Map<String, Object>) key;
				compositeKey.entrySet().stream().forEach(x -> {
					CassandraUtil.createQuery(x.getKey(), x.getValue(), selectWhere);
				});
			}
			ResultSet results = session.execute(selectWhere);
			response = CassandraUtil.createResponse(results);
		} catch (Exception e) {
			logger.error(Constants.EXCEPTION_MSG_FETCH + tableName + " : " + e.getMessage(), e);
			throw new Exception();
		}
		logQueryElapseTime("getRecordByIdentifier", startTime);
		return response;
	}

	@Override
	public Response getRecordById(String keyspaceName, String tableName, String key) throws Exception {
		return getRecordByIdentifier(keyspaceName, tableName, key, null);
	}

	@Override
	public Response getRecordById(String keyspaceName, String tableName, Map<String, Object> key) throws Exception {
		return getRecordByIdentifier(keyspaceName, tableName, key, null);
	}

	@Override
	public Response getRecordById(String keyspaceName, String tableName, String key, List<String> fields)
			throws Exception {
		return getRecordByIdentifier(keyspaceName, tableName, key, fields);
	}

	@Override
	public Response getRecordById(String keyspaceName, String tableName, Map<String, Object> key, List<String> fields)
			throws Exception {
		return getRecordByIdentifier(keyspaceName, tableName, key, fields);
	}

	/**
	 * This method updates all the records in a batch
	 *
	 * @param keyspaceName
	 * @param tableName
	 * @param records
	 * @return
	 */
	// @Override
	public Response batchUpdateById(String keyspaceName, String tableName, List<Map<String, Object>> records)
			throws Exception {

		long startTime = System.currentTimeMillis();
		logger.info("Cassandra Service batchUpdateById method started at == {}", startTime);
		Session session = connectionManager.getSession(keyspaceName);
		Response response = new Response();
		BatchStatement batchStatement = new BatchStatement();
		ResultSet resultSet = null;

		try {
			for (Map<String, Object> map : records) {
				Update update = createUpdateStatement(keyspaceName, tableName, map);
				batchStatement.add(update);
			}
			resultSet = session.execute(batchStatement);
			response.put(Constants.RESPONSE, Constants.SUCCESS);
		} catch (QueryExecutionException | QueryValidationException | NoHostAvailableException
				| IllegalStateException e) {
			logger.error("Cassandra Batch Update Failed." + e.getMessage(), e);
			throw new Exception();
		}
		logQueryElapseTime("batchUpdateById", startTime);
		return response;
	}

	private Update createUpdateStatement(String keySpaceName, String tableName, Map<String, Object> record) {
		Update update = QueryBuilder.update(keySpaceName, tableName);
		Assignments assignments = update.with();
		Update.Where where = update.where();
		record.entrySet().stream().forEach(x -> {
			if (Constants.ID.equals(x.getKey())) {
				where.and(eq(x.getKey(), x.getValue()));
			} else {
				assignments.and(QueryBuilder.set(x.getKey(), x.getValue()));
			}
		});
		return update;
	}

	private void logQueryElapseTime(String operation, long startTime) {

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		String message = "Cassandra operation {0} started at {1} and completed at {2}. Total time elapsed is {3}.";
		MessageFormat mf = new MessageFormat(message);
		logger.info(mf.format(new Object[] { operation, startTime, stopTime, elapsedTime }));
	}

	@Override
	public Response getRecordsByIndexedProperty(String keyspaceName, String tableName, String propertyName,
			Object propertyValue) throws Exception {
		long startTime = System.currentTimeMillis();
		logger.info("CassandraOperationImpl:getRecordsByIndexedProperty called at {}", startTime);
		Response response = new Response();
		try {
			Select selectQuery = QueryBuilder.select().all().from(keyspaceName, tableName);
			selectQuery.where().and(eq(propertyName, propertyValue));
			ResultSet results = connectionManager.getSession(keyspaceName).execute(selectQuery.allowFiltering());
			response = CassandraUtil.createResponse(results);
		} catch (Exception e) {
			logger.error("CassandraOperationImpl:getRecordsByIndexedProperty: " + Constants.EXCEPTION_MSG_FETCH
					+ tableName + " : " + e.getMessage(), e);
			throw new Exception();
		}
		logQueryElapseTime("getRecordsByIndexedProperty", startTime);
		return response;
	}

	@Override
	public Response getRecords(String keyspace, String table, Map<String, Object> filters, List<String> fields)
			throws Exception {
		Response response = new Response();
		Session session = connectionManager.getSession(keyspace);

		try {
			Select select;
			if (CollectionUtils.isNotEmpty(fields)) {
				select = QueryBuilder.select((String[]) fields.toArray()).from(keyspace, table);
			} else {
				select = QueryBuilder.select().all().from(keyspace, table);
			}

			if (MapUtils.isNotEmpty(filters)) {
				Select.Where where = select.where();
				for (Map.Entry<String, Object> filter : filters.entrySet()) {
					Object value = filter.getValue();
					if (value instanceof List) {
						where = where.and(QueryBuilder.in(filter.getKey(), ((List) filter.getValue())));
					} else {
						where = where.and(QueryBuilder.eq(filter.getKey(), filter.getValue()));
					}
				}
			}

			ResultSet results = null;
			results = session.execute(select);
			response = CassandraUtil.createResponse(results);
		} catch (Exception e) {
			logger.error(Constants.EXCEPTION_MSG_FETCH + table + " : " + e.getMessage(), e);
			throw new Exception();
		}
		return response;
	}

}