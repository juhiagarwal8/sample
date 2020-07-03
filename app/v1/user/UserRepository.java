package v1.user;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

import util.Response;

public interface UserRepository {

	CompletionStage<Stream<UserData>> getAllUsers() throws Exception;

	CompletionStage<Response> createUser(UserData postData) throws Exception;

	CompletionStage<Response> update(int id, UserData postData) throws Exception;

	CompletionStage<Stream<UserData>> getAllUserFromGroup(String id) throws Exception;

	CompletionStage<Response> addUserToGroup(String userId, String groupId, String id) throws Exception;

	CompletionStage<UserData> getUserById(String id) throws Exception;
}
