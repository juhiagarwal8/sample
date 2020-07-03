package v1.user;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static play.mvc.Http.Status.GATEWAY_TIMEOUT;
import static play.mvc.Http.Status.NOT_ACCEPTABLE;
import static play.mvc.Http.Status.SERVICE_UNAVAILABLE;

import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jodah.failsafe.FailsafeException;
import play.libs.concurrent.Futures;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

public class UserAction extends play.mvc.Action.Simple {
	private final Logger logger = LoggerFactory.getLogger("application.UserAction");

	private final HttpExecutionContext ec;
	private final Futures futures;

	@Singleton
	@Inject
	public UserAction(HttpExecutionContext ec, Futures futures) {
		this.ec = ec;
		this.futures = futures;
	}

	public CompletionStage<Result> call(Http.Request request) {
		if (logger.isTraceEnabled()) {
			logger.trace("call: request = " + request);
		}

		if (request.accepts("application/json")) {
			return futures.timeout(doCall(request), 1L, TimeUnit.SECONDS).exceptionally(e -> {
				return (Results.status(GATEWAY_TIMEOUT, views.html.timeout.render()));
			});
		} else {
			return completedFuture(status(NOT_ACCEPTABLE, "We only accept application/json"));
		}
	}

	private CompletionStage<Result> doCall(Http.Request request) {
		return delegate.call(request).handleAsync((result, e) -> {
			if (e != null) {
				if (e instanceof CompletionException) {
					Throwable completionException = e.getCause();
					if (completionException instanceof FailsafeException) {
						logger.error("Circuit breaker is open!", completionException);
						return Results.status(SERVICE_UNAVAILABLE, "Service has timed out");
					} else {
						logger.error("Direct exception " + e.getMessage(), e);
						return internalServerError();
					}
				} else {
					logger.error("Unknown exception " + e.getMessage(), e);
					return internalServerError();
				}
			} else {
				return result;
			}
		}, ec.current());
	}
}
