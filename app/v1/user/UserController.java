package v1.user;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;

@With(UserAction.class)
public class UserController extends Controller {

	private HttpExecutionContext ec;
	private UserResourceHandler handler;
	private Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);

	@Inject
	public UserController(HttpExecutionContext ec, UserResourceHandler handler) {
		this.ec = ec;
		this.handler = handler;
	}

	public CompletionStage<Result> getAllUsers(Http.Request request) throws Exception {
		return handler.getAllUsers(request).thenApplyAsync(users -> {
			final List<UserResource> userList = users.collect(Collectors.toList());
			return ok(Json.toJson(userList));
		}, ec.current());
	}

	public CompletionStage<Result> getUserById(Http.Request request, String id)
			throws NumberFormatException, Exception {
		return handler.getUserById(request, id).thenApplyAsync(resource -> {
			return ok(Json.toJson(resource));
		}, ec.current());
	}

	public CompletionStage<Result> updateUser(Http.Request request, String id) throws NumberFormatException, Exception {
		JsonNode json = request.body().asJson();
		UserResource resource = Json.fromJson(json, UserResource.class);
		return handler.updateUser(request, id, resource).thenApplyAsync(r -> {
			return ok(Json.toJson(r));
		}, ec.current());
	}

	public CompletionStage<Result> createUser(Http.Request request) throws Exception {
		JsonNode json = request.body().asJson();
		final UserResource resource = Json.fromJson(json, UserResource.class);
		return handler.createUser(request, resource).thenApplyAsync(savedResource -> {
			return created(Json.toJson(savedResource));
		}, ec.current());
	}

	public CompletionStage<Result> getAllUserFromGroup(Http.Request request, String id)
			throws NumberFormatException, Exception {
		return handler.getAllUserFromGroup(request, id).thenApplyAsync(users -> {
			final List<UserResource> userList = users.collect(Collectors.toList());
			return ok(Json.toJson(userList));
		}, ec.current());
	}

	public CompletionStage<Result> addUserToGroup(Http.Request request, String userId, String groupId, String id)
			throws NumberFormatException, Exception {
		logger.info(userId + groupId + id);
		return handler.addUserToGroup(request, userId, groupId, id).thenApplyAsync(r -> {
			return ok(Json.toJson(r));
		}, ec.current());
	}

}
