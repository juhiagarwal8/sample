package util;

import java.util.List;
import java.util.Map;

import v1.user.UserData;

public interface CassandraOperation {

	/**
	 * @desc This method is used to insert/update record in cassandra db (if primary
	 *       key exist in request ,it will update else will insert the record in
	 *       cassandra db. By default cassandra insert operation does upsert
	 *       operation. Upsert means that Cassandra will insert a row if a primary
	 *       key does not exist already otherwise if primary key already exists, it
	 *       will update that row.)
	 * @param keyspaceName
	 *            String (data base keyspace name)
	 * @param tableName
	 *            String
	 * @param request
	 *            Map<String,Object>(i.e map of column name and their value)
	 * @return Response Response
	 */
	public Response upsertRecord(String keyspaceName, String tableName, Map<String, Object> request) throws Exception;

	/**
	 * @desc This method is used to update record in cassandra db
	 * @param keyspaceName
	 *            Keyspace name
	 * @param tableName
	 *            Table name
	 * @param request
	 *            Map<String,Object>(i.e map of column name and their value)
	 * @return Response Response
	 */
	public Response updateRecord(String keyspaceName, String tableName, Map<String, Object> request) throws Exception;

	/**
	 * @desc This method is used to delete record in cassandra db by their primary
	 *       key(identifier)
	 * @param keyspaceName
	 *            Keyspace name
	 * @param tableName
	 *            Table name
	 * @param identifier
	 *            String
	 * @return Response Response
	 */
	public Response deleteRecord(String keyspaceName, String tableName, String identifier) throws Exception;

	/**
	 * @desc This method is used to fetch record based on given parameter and it's
	 *       value (it only fetch the record on indexed property or column or it
	 *       will throw exception.)
	 * @param keyspaceName
	 *            String (data base keyspace name)
	 * @param tableName
	 *            String
	 * @param propertyName
	 *            String
	 * @param propertyValue
	 *            Value to be used for matching in select query
	 * @return Response Response
	 */
	public Response getRecordsByProperty(String keyspaceName, String tableName, String propertyName,
			Object propertyValue) throws Exception;

	/**
	 * Fetch records with specified columns (select all if null) for given column
	 * name and value.
	 *
	 * @param keyspaceName
	 *            Keyspace name
	 * @param tableName
	 *            Table name
	 * @param propertyName
	 *            Column name
	 * @param propertyValue
	 *            Column value
	 * @param fields
	 *            List of columns to be returned in each record
	 * @return Response consisting of fetched records
	 */
	Response getRecordsByProperty(String keyspaceName, String tableName, String propertyName, Object propertyValue,
			List<String> fields) throws Exception;

	/**
	 * @desc This method is used to fetch record based on given parameter and it's
	 *       list of value (for In Query , for example : SELECT * FROM
	 *       mykeyspace.mytable WHERE id IN (‘A’,’B’,C’) )
	 * @param keyspaceName
	 *            String (data base keyspace name)
	 * @param tableName
	 *            String
	 * @param propertyName
	 *            String
	 * @param propertyValueList
	 *            List<Object>
	 * @return Response Response
	 */
	public Response getRecordsByProperty(String keyspaceName, String tableName, String propertyName,
			List<Object> propertyValueList) throws Exception;

	/**
	 * Fetch records with specified columns (select all if null) for given column
	 * name with matching value in the list.
	 *
	 * @param keyspaceName
	 *            Keyspace name
	 * @param tableName
	 *            Table name
	 * @param propertyName
	 *            Column name
	 * @param propertyValueList
	 *            List of values to be used for matching in select query
	 * @param fields
	 *            List of columns to be returned in each record
	 * @return Response consisting of fetched records
	 */
	Response getRecordsByProperty(String keyspaceName, String tableName, String propertyName,
			List<Object> propertyValueList, List<String> fields) throws Exception;

	/**
	 * Fetch records with specified indexed column
	 *
	 * @param keyspaceName
	 *            Keyspace name
	 * @param tableName
	 *            Table name
	 * @param propertyName
	 *            Indexed Column name
	 * @param propertyValue
	 *            Value to be used for matching in select query
	 * @return Response consisting of fetched records
	 */
	Response getRecordsByIndexedProperty(String keyspaceName, String tableName, String propertyName,
			Object propertyValue) throws Exception;

	/**
	 * @desc This method is used to fetch record based on given parameter list and
	 *       their values
	 * @param keyspaceName
	 *            String (data base keyspace name)
	 * @param tableName
	 *            String
	 * @param propertyMap
	 *            Map<String,Object> propertyMap)(i.e map of column name and their
	 *            value)
	 * @return Response Response
	 */
	public Response getRecordsByProperties(String keyspaceName, String tableName, Map<String, Object> propertyMap)
			throws Exception;

	/**
	 * Fetch records with specified columns (select all if null) for given column
	 * map (name, value pairs).
	 *
	 * @param keyspaceName
	 *            Keyspace name
	 * @param tableName
	 *            Table name
	 * @param propertyMap
	 *            Map describing columns to be used in where clause of select query.
	 * @param fields
	 *            List of columns to be returned in each record
	 * @return Response consisting of fetched records
	 */
	Response getRecordsByProperties(String keyspaceName, String tableName, Map<String, Object> propertyMap,
			List<String> fields) throws Exception;

	/**
	 * @desc This method is used to fetch properties value based on id
	 * @param keyspaceName
	 *            String (data base keyspace name)
	 * @param tableName
	 *            String
	 * @param id
	 *            String
	 * @param properties
	 *            String varargs
	 * @return Response.
	 */
	public Response getPropertiesValueById(String keyspaceName, String tableName, String id, String... properties)
			throws Exception;

	/**
	 * @desc This method is used to fetch all records for table(i.e Select * from
	 *       tableName)
	 * @param keyspaceName
	 *            String (data base keyspace name)
	 * @param tableName
	 *            String
	 * @return Response Response
	 */
	public List<UserData> getAllRecords(String keyspaceName, String tableName) throws Exception;

	/**
	 * Method to update the record on basis of composite primary key.
	 *
	 * @param keyspaceName
	 *            Keyspace name
	 * @param tableName
	 *            Table name
	 * @param updateAttributes
	 *            Column map to be used in set clause of update query
	 * @param compositeKey
	 *            Column map for composite primary key
	 * @return Response consisting of update query status
	 */
	Response updateRecord(String keyspaceName, String tableName, Map<String, Object> updateAttributes,
			Map<String, Object> compositeKey) throws Exception;

	/**
	 * Method to get record by primary key.
	 *
	 * @param keyspaceName
	 *            Keyspace name
	 * @param tableName
	 *            Table name
	 * @param key
	 *            Primary key
	 * @return Response consisting of matched record
	 */
	Response getRecordById(String keyspaceName, String tableName, String key) throws Exception;

	/**
	 * Method to get record by composite primary key.
	 *
	 * @param keyspaceName
	 *            Keyspace name
	 * @param tableName
	 *            Table name
	 * @param key
	 *            Column map representing composite primary key
	 * @return Response consisting of matched record
	 */
	Response getRecordById(String keyspaceName, String tableName, Map<String, Object> key) throws Exception;

	/**
	 * Method to get record by primary key consisting of only specified fields
	 * (return all if null).
	 *
	 * @param keyspaceName
	 *            Keyspace name
	 * @param tableName
	 *            Table name
	 * @param key
	 *            Primary key
	 * @param fields
	 *            List of columns to be returned in each record
	 * @return Response consisting of matched record
	 */
	Response getRecordById(String keyspaceName, String tableName, String key, List<String> fields) throws Exception;

	/**
	 * MethoF
	 */
	Response getRecordById(String keyspaceName, String tableName, Map<String, Object> key, List<String> fields)
			throws Exception;

	public Response getRecords(String keyspace, String table, Map<String, Object> filters, List<String> fields)
			throws Exception;

	Response insertRecord(String keyspaceName, String tableName, String query) throws Exception;

}
