package util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Response implements Serializable, Cloneable {

	private static final long serialVersionUID = -3773253896160786443L;
	private int code = 200;
	private Map<String, Object> result = new HashMap<>();

	/** @return Map<String, Object> */
	public Map<String, Object> getResult() {
		return result;
	}

	public Response() {
	}

	public Response(Response rs) {
		this.result = rs.getResult();
	}

	/**
	 * @param key
	 *            String
	 * @return Object
	 */
	public Object get(String key) {
		return result.get(key);
	}

	/**
	 * @param key
	 *            String
	 * @param vo
	 *            Object
	 */
	public void put(String key, Object vo) {
		result.put(key, vo);
	}

	/**
	 * @param map
	 *            Map<String, Object>
	 */
	public void putAll(Map<String, Object> map) {
		result.putAll(map);
	}

	public boolean containsKey(String key) {
		return result.containsKey(key);
	}

	/**
	 * Set the response code for header.
	 *
	 * @param code
	 *            ResponseCode
	 */
	public void setResponseCode(int code) {
		this.code = code;
	}

	/**
	 * get the response code
	 *
	 * @return String
	 */
	public int getResponseCode() {
		return this.code;
	}

	public Response clone(Response response) {
		try {
			return (Response) response.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
