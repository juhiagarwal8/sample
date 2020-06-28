package v1.user;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public interface UserRepository {

	CompletionStage<Stream<UserData>> getAllUsers();

	CompletionStage<UserData> createUser(UserData postData);

	CompletionStage<Optional<UserData>> getUserById(Long id);

	CompletionStage<Optional<UserData>> update(Long id, UserData postData);
}
