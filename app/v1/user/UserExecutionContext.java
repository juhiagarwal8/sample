package v1.user;

import javax.inject.Inject;

import akka.actor.ActorSystem;
import play.libs.concurrent.CustomExecutionContext;

/**
 * Custom execution context wired to "user.repository" thread pool
 */
public class UserExecutionContext extends CustomExecutionContext {

	@Inject
	public UserExecutionContext(ActorSystem actorSystem) {
		super(actorSystem, "user.repository");
	}
}
