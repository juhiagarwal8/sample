package cassandra.operations;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import util.Response;
import v1.user.UserData;

public interface CassandraOperation {

	public Response upsertRecord(String keyspaceName, String tableName, Map<String, Object> request) throws Exception;

	public List<UserData> getAllRecords(String keyspaceName, String tableName) throws Exception;

	UserData getRecordByIdentifier(String keyspaceName, String tableName, String key) throws Exception;

	public Collection<UserData> getAllUserFromGroup(String string) throws Exception;

	public Response addUserToGroup(String userId, String groupId, String id) throws Exception;

}
