package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesCache {

	Logger logger = LoggerFactory.getLogger(PropertiesCache.class);
	private final String fileName = "config.properties";
	private final Properties configProp = new Properties();
	private static PropertiesCache propertiesCache = null;

	private PropertiesCache() {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName);
		try {
			configProp.load(in);
		} catch (IOException e) {
			logger.error("Error in properties cache", e);
		}
	}

	public static PropertiesCache getInstance() {

		if (null == propertiesCache) {
			synchronized (PropertiesCache.class) {
				if (null == propertiesCache) {
					propertiesCache = new PropertiesCache();
				}
			}
		}
		return propertiesCache;
	}

	/**
	 * Method to read value from resource file .
	 *
	 * @param key
	 *            property value to read
	 * @return value corresponding to given key if found else will return key
	 *         itself.
	 */
	public String getProperty(String key) {
		return configProp.getProperty(key) != null ? configProp.getProperty(key) : key;
	}

	/**
	 * Method to read value from resource file .
	 *
	 * @param key
	 * @return
	 */
	public String readProperty(String key) {
		String value = System.getenv(key);
		if (StringUtils.isNotBlank(value))
			return value;
		return configProp.getProperty(key);
	}

	public static String getConfigValue(String key) {
		if (StringUtils.isNotBlank(System.getenv(key))) {
			return System.getenv(key);
		}
		return propertiesCache.readProperty(key);
	}
}