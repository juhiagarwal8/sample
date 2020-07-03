package v1.user;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import cassandra.operations.CassandraOperation;
import util.Response;

/**
 * A repository that provides a non-blocking API with a custom execution context
 * and circuit breaker.
 */
public class UserRepositoryImpl implements UserRepository {
	private static final String USER = "user";
	private static final String SAMPLE = "sample";
	private static UserExecutionContext ec;
	private static CassandraOperation co;
	private Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);

	@Inject
	public UserRepositoryImpl(UserExecutionContext ec, CassandraOperation co) {
		UserRepositoryImpl.ec = ec;
		UserRepositoryImpl.co = co;
	}

	@Override
	public CompletionStage<Stream<UserData>> getAllUsers() throws Exception {
		Stream<UserData> data = co.getAllRecords(SAMPLE, USER).stream();
		try {
			return CompletableFuture.supplyAsync(() -> (data), ec);
		} catch (Exception e) {
			logger.error("Encountered an exception", e.getMessage());
		}
		return null;
	}

	@Override
	public CompletionStage<Response> createUser(UserData postData) throws Exception {
		return upsertRecord(postData);
	}

	@Override
	public CompletionStage<UserData> getUserById(String id) throws Exception {
		UserData data = co.getRecordByIdentifier(SAMPLE, USER, id);
		return CompletableFuture.supplyAsync(() -> (data), ec);
	}

	@Override
	public CompletionStage<Response> update(int id, UserData postData) throws Exception {
		return upsertRecord(postData);
	}

	private CompletionStage<Response> upsertRecord(UserData postData) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		@SuppressWarnings("unchecked")
		Map<String, Object> map = mapper.convertValue(postData, Map.class);
		Response data = co.upsertRecord(SAMPLE, USER, map);
		return CompletableFuture.supplyAsync(() -> (data), ec);
	}

	@Override
	public CompletionStage<Stream<UserData>> getAllUserFromGroup(String id) throws Exception {
		Stream<UserData> data = co.getAllUserFromGroup(id).stream();
		try {
			return CompletableFuture.supplyAsync(() -> (data), ec);
		} catch (Exception e) {
			logger.error("Encountered an exception", e.getMessage());
		}
		return null;
	}

	@Override
	public CompletionStage<Response> addUserToGroup(String userId, String groupId, String id) throws Exception {
		logger.info(userId + groupId + id);
		Response data = co.addUserToGroup(userId, groupId, id);
		return CompletableFuture.supplyAsync(() -> (data), ec);
	}

}