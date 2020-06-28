package v1.user;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import net.jodah.failsafe.CircuitBreaker;
import util.DBQueries;

/**
 * A repository that provides a non-blocking API with a custom execution context
 * and circuit breaker.
 */
@Singleton
public class UserRepositoryImpl implements UserRepository, Provider<UserRepository> {
	private static UserExecutionContext ec;
	private static DBQueries db;
	private Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);
	private final CircuitBreaker<Optional<UserData>> circuitBreaker = new CircuitBreaker<Optional<UserData>>()
			.withFailureThreshold(1).withSuccessThreshold(3);

	@Inject
	public UserRepositoryImpl(UserExecutionContext ec, DBQueries db) {
		UserRepositoryImpl.ec = ec;
		UserRepositoryImpl.db = db;
	}

	@Override
	public CompletionStage<Stream<UserData>> getAllUsers() {
		return CompletableFuture.supplyAsync(() -> (select()), ec);
	}

	private Stream<UserData> select() {
		return db.getAllUsers().stream();
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

	@Override
	public UserRepository get() {
		final UserRepositoryImpl registry = new UserRepositoryImpl(ec, db);
		return registry;
	}
}
