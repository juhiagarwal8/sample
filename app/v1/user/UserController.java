package v1.user;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.*;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@With(UserAction.class)
public class UserController extends Controller {

	private HttpExecutionContext ec;
	private UserResourceHandler handler;

	@Inject
	public UserController(HttpExecutionContext ec, UserResourceHandler handler) {
		this.ec = ec;
		this.handler = handler;
	}

	public CompletionStage<Result> getAllUsers(Http.Request request) {
		return handler.getAllUsers(request).thenApplyAsync(users -> {
			final List<UserResource> userList = users.collect(Collectors.toList());
			return ok(Json.toJson(userList));
		}, ec.current());
	}

	public CompletionStage<Result> getUserById(Http.Request request, String id) {
		return handler.getUserById(request, id).thenApplyAsync(optionalResource -> {
			return optionalResource.map(resource -> ok(Json.toJson(resource))).orElseGet(Results::notFound);
		}, ec.current());
	}

	public CompletionStage<Result> updateUser(Http.Request request, String id) {
		JsonNode json = request.body().asJson();
		UserResource resource = Json.fromJson(json, UserResource.class);
		return handler.updateUser(request, id, resource).thenApplyAsync(optionalResource -> {
			return optionalResource.map(r -> ok(Json.toJson(r))).orElseGet(Results::notFound);
		}, ec.current());
	}

	public CompletionStage<Result> createUser(Http.Request request) {
		JsonNode json = request.body().asJson();
		final UserResource resource = Json.fromJson(json, UserResource.class);
		return handler.createUser(request, resource).thenApplyAsync(savedResource -> {
			return created(Json.toJson(savedResource));
		}, ec.current());
	}
}
