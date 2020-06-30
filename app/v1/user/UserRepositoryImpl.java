package v1.user;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jodah.failsafe.CircuitBreaker;
import util.CassandraOperation;

/**
 * A repository that provides a non-blocking API with a custom execution context
 * and circuit breaker.
 */
public class UserRepositoryImpl implements UserRepository {
	private static UserExecutionContext ec;
	private static CassandraOperation co;
	private Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);
	private final CircuitBreaker<Optional<UserData>> circuitBreaker = new CircuitBreaker<Optional<UserData>>()
			.withFailureThreshold(1).withSuccessThreshold(3);

	@Inject
	public UserRepositoryImpl(UserExecutionContext ec, CassandraOperation co) {
		UserRepositoryImpl.ec = ec;
		UserRepositoryImpl.co = co;
	}

	@Override
	public CompletionStage<Stream<UserData>> getAllUsers() {
		return CompletableFuture.supplyAsync(() -> (select()), ec);
	}

	private Stream<UserData> select() {
		try {
			return co.getAllRecords("sample", "user").stream();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public CompletionStage<UserData> createUser(UserData postData) {
		return null;
	}

	@Override
	public CompletionStage<Optional<UserData>> getUserById(Long id) {

		return null;
	}

	@Override
	public CompletionStage<Optional<UserData>> update(Long id, UserData postData) {

		return null;
	}
}