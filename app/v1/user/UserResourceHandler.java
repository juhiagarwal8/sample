package v1.user;

import java.nio.charset.CharacterCodingException;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

import javax.inject.Inject;

import com.palominolabs.http.url.UrlBuilder;

import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import util.Response;

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

	public CompletionStage<Stream<UserResource>> getAllUsers(Http.Request request) throws Exception {
		return repository.getAllUsers().thenApplyAsync(userDataStream -> {
			return userDataStream.map(data -> new UserResource(data, link(request, data)));
		}, ec.current());
	}

	public CompletionStage<Response> createUser(Http.Request request, UserResource resource) throws Exception {
		final UserData data = new UserData(resource.getId(), resource.getName(), resource.getPhone(),
				resource.getEmail());
		return repository.createUser(data).thenApplyAsync(rs -> {
			return new Response(rs);
		}, ec.current());
	}

	public CompletionStage<UserResource> getUserById(Http.Request request, String id)
			throws NumberFormatException, Exception {
		return repository.getUserById(id).thenApplyAsync(data -> {
			return new UserResource(data, link(request, data));
		}, ec.current());
	}

	public CompletionStage<Response> updateUser(Http.Request request, String id, UserResource resource)
			throws NumberFormatException, Exception {
		final UserData data = new UserData(resource.getName(), resource.getPhone(), resource.getEmail());
		return repository.update(Integer.parseInt(id), data).thenApplyAsync(rs -> {
			return new Response(rs);
		}, ec.current());
	}

	private String link(Http.Request request, UserData data) {
		final String[] hostPort = request.host().split(":");
		String host = hostPort[0];
		int port = (hostPort.length == 2) ? Integer.parseInt(hostPort[1]) : -1;
		final String scheme = request.secure() ? "https" : "http";
		try {
			return UrlBuilder.forHost(scheme, host, port).pathSegments("v1", "user", String.valueOf(data.id))
					.toUrlString();
		} catch (CharacterCodingException e) {
			throw new IllegalStateException(e);
		}
	}

	public CompletionStage<Stream<UserResource>> getAllUserFromGroup(Http.Request request, String id) throws Exception {
		return repository.getAllUserFromGroup(id).thenApplyAsync(userDataStream -> {
			return userDataStream.map(data -> new UserResource(data, link(request, data)));
		}, ec.current());

	}

	public CompletionStage<Response> addUserToGroup(Http.Request request, String userId, String groupId, String id)
			throws Exception {
		return repository.addUserToGroup(userId, groupId, id).thenApplyAsync(rs -> {
			return new Response(rs);
		}, ec.current());
	}
}
