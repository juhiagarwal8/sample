package v1.user;

import com.palominolabs.http.url.UrlBuilder;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;

import javax.inject.Inject;
import java.nio.charset.CharacterCodingException;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

/**
 * Handles presentation of Post resources, which map to JSON.
 */
public class UserResourceHandler {

	private final UserRepository repository;
	private final HttpExecutionContext ec;

	@Inject
	public UserResourceHandler(UserRepository repository, HttpExecutionContext ec) {
		this.repository = repository;
		this.ec = ec;
	}

	public CompletionStage<Stream<UserResource>> getAllUsers(Http.Request request) {
		return repository.getAllUsers().thenApplyAsync(userDataStream -> {
			return userDataStream.map(data -> new UserResource(data, link(request, data)));
		}, ec.current());
	}

	public CompletionStage<UserResource> createUser(Http.Request request, UserResource resource) {
		final UserData data = new UserData(resource.getName(), resource.getPhone(), resource.getEmail());
		return repository.createUser(data).thenApplyAsync(savedData -> {
			return new UserResource(savedData, link(request, savedData));
		}, ec.current());
	}

	public CompletionStage<Optional<UserResource>> getUserById(Http.Request request, String id) {
		return repository.getUserById(Long.parseLong(id)).thenApplyAsync(optionalData -> {
			return optionalData.map(data -> new UserResource(data, link(request, data)));
		}, ec.current());
	}

	public CompletionStage<Optional<UserResource>> updateUser(Http.Request request, String id, UserResource resource) {
		final UserData data = new UserData(resource.getName(), resource.getPhone(), resource.getEmail());
		return repository.update(Long.parseLong(id), data).thenApplyAsync(optionalData -> {
			return optionalData.map(op -> new UserResource(op, link(request, op)));
		}, ec.current());
	}

	private String link(Http.Request request, UserData data) {
		final String[] hostPort = request.host().split(":");
		String host = hostPort[0];
		int port = (hostPort.length == 2) ? Integer.parseInt(hostPort[1]) : -1;
		final String scheme = request.secure() ? "https" : "http";
		try {
			return UrlBuilder.forHost(scheme, host, port).pathSegments("v1", "user", data.id.toString()).toUrlString();
		} catch (CharacterCodingException e) {
			throw new IllegalStateException(e);
		}
	}
}
