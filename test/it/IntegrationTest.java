package it;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.GATEWAY_TIMEOUT;
import static play.mvc.Http.Status.SERVICE_UNAVAILABLE;
import static play.test.Helpers.GET;
import static play.test.Helpers.PUT;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;

import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;
import v1.user.UserData;
import v1.user.UserRepository;
import v1.user.UserResource;

public class IntegrationTest extends WithApplication {

	@Override
	protected Application provideApplication() {
		return new GuiceApplicationBuilder().build();
	}

	@Test
	public void testList() {
		UserRepository repository = app.injector().instanceOf(UserRepository.class);
		repository.createUser(new UserData("title-of-post-123", "body-123", "n"));

		Http.RequestBuilder request = new Http.RequestBuilder().method(GET).uri("/v1/users");

		Result result = route(app, request);

		assertEquals(200, result.status());

		JsonNode listOfusers = contentAsJson(result);
		Optional<UserResource> post = findPostByTitle(listOfusers, "title-of-post-123");
		assertTrue(post.isPresent());
	}

	private Optional<UserResource> findPostByTitle(JsonNode listOfusers, String postTitle) {
		Iterator<JsonNode> elements = listOfusers.elements();
		// spliterator dance to build a Stream from an Iterator
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(elements, Spliterator.ORDERED), false)
				.map(jsonNode -> Json.fromJson(jsonNode, UserResource.class)).filter(p -> {
					return p.getName().equals(postTitle);
				}).findFirst();
	}

	private JsonNode contentAsJson(Result result) {
		final String responseBody = contentAsString(result);
		return Json.parse(responseBody);
	}

	@Test
	public void testListWithTrailingSlashIsUnknown() {
		UserRepository repository = app.injector().instanceOf(UserRepository.class);
		repository.createUser(new UserData("title-of-another-post", "body-456", null));

		Http.RequestBuilder request = new Http.RequestBuilder().method(GET).uri("/v1/users/");

		Result result = route(app, request);
		assertEquals(404, result.status());
	}

	@Test
	public void testTimeoutOnUpdate() {
		UserRepository repository = app.injector().instanceOf(UserRepository.class);
		repository.createUser(new UserData("title-testTimeoutOnUpdate", "body-testTimeoutOnUpdate", null));

		JsonNode json = Json
				.toJson(new UserResource("1", "http://localhost:9000/v1/users/1", "some title", "somebody"));

		Http.RequestBuilder request = new Http.RequestBuilder().method(PUT).bodyJson(json).uri("/v1/users/1");

		Result result = route(app, request);
		org.hamcrest.MatcherAssert.assertThat(result.status(), equalTo(GATEWAY_TIMEOUT));
	}

	@Test
	public void testCircuitBreakerOnShow() {
		UserRepository repository = app.injector().instanceOf(UserRepository.class);
		repository.createUser(new UserData("title-testCircuitBreakerOnShow", "body-testCircuitBreakerOnShow", null));

		Http.RequestBuilder request = new Http.RequestBuilder().method(GET).uri("/v1/users/1");

		Result result = route(app, request);
		org.hamcrest.MatcherAssert.assertThat(result.status(), equalTo(SERVICE_UNAVAILABLE));
	}

}
