/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import blueprint.sdk.util.Validator;

/**
 * Configuration Handler
 * 
 * @author Simon Lee
 * @since 2013. 6. 18.
 */
public class Config {
	/** logger */
	private static final Logger L = Logger.getLogger(Config.class);

	/** configuration의 uri */
	protected String uri;

	protected DocumentBuilder builder;

	/** config의 Document */
	protected Document doc;

	/** XPath evaluator */
	protected XPath eval = XPathFactory.newInstance().newXPath();

	/**
	 * @return configuration의 uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * @param uri
	 *            path/uri of config xml
	 * @throws ConfigException
	 *             load failure
	 */
	public void load(String uri) throws ConfigException {
		synchronized (this) {
			this.uri = uri;

			if (builder == null) {
				try {
					builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				} catch (ParserConfigurationException e) {
					throw new ConfigException("XML parser error", e);
				}
			}
		}

		try {
			doc = builder.parse(uri);
		} catch (Exception e) {
			throw new ConfigException("Can't parse config xml", e);
		}

		String configName = null;
		try {
			configName = eval.evaluate("config/@name", doc);
			if (Validator.isEmpty(configName)) {
				configName = "no-name";
			}
		} catch (XPathExpressionException e) {
			throw new ConfigException("Wrong XPath", e);
		}

		L.info("configuration '" + configName + "' is loaded");
	}

	/**
	 * Get value from given XPath
	 * 
	 * @param xpath
	 * @return null or String value
	 * @throws XPathExpressionException
	 */
	public String getString(String xpath) throws XPathExpressionException {
		String result = eval.evaluate(xpath, doc);
		result = resolveProperty(result);

		warnEmptyValue(xpath, result);

		return result;
	}

	/**
	 * Print warning message (actual log level is INFO) if designated value is
	 * empty.
	 * 
	 * @param xpath
	 *            value's XPath
	 * @param result
	 *            value to check
	 */
	protected void warnEmptyValue(String xpath, String result) {
		if (result == null || result.isEmpty()) {
			L.info("evaluated result of '" + xpath + "' is empty");
		}
	}

	/**
	 * Get value from given XPath
	 * 
	 * @param xpath
	 * @return boolean value
	 * @throws XPathExpressionException
	 */
	public boolean getBoolean(String xpath) throws XPathExpressionException {
		try {
			return Boolean.parseBoolean(getString(xpath));
		} catch (NumberFormatException e) {
			throw new XPathExpressionException("evaluated result of '" + xpath + "' is not an Integer");
		}
	}

	/**
	 * Get value from given XPath
	 * 
	 * @param xpath
	 * @return int value
	 * @throws XPathExpressionException
	 */
	public int getInt(String xpath) throws XPathExpressionException {
		try {
			return Integer.parseInt(getString(xpath));
		} catch (NumberFormatException e) {
			throw new XPathExpressionException("evaluated result of '" + xpath + "' is not an Integer");
		}
	}

	/**
	 * Get value from given XPath
	 * 
	 * @param xpath
	 * @return values as String[]
	 * @throws XPathExpressionException
	 */
	public String[] getStringArray(String xpath) throws XPathExpressionException {
		List<String> result = new ArrayList<String>(10);

		NodeList nodes = (NodeList) eval.evaluate(xpath, doc, XPathConstants.NODESET);
		if (nodes == null) {
			warnEmptyValue(xpath, null);
		} else {
			int iMax = nodes.getLength();
			for (int i = 0; i < iMax; i++) {
				String value = nodes.item(i).getTextContent();
				value = resolveProperty(value);
				result.add(value);
			}
		}

		return result.toArray(new String[result.size()]);
	}

	/**
	 * Get value from given XPath
	 * 
	 * @param xpath
	 * @return values as Boolean[]
	 * @throws XPathExpressionException
	 */
	public Boolean[] getBooleanArray(String xpath) throws XPathExpressionException {
		List<Boolean> result = new ArrayList<Boolean>(10);

		NodeList nodes = (NodeList) eval.evaluate(xpath, doc, XPathConstants.NODESET);
		if (nodes == null) {
			warnEmptyValue(xpath, null);
		} else {
			int iMax = nodes.getLength();
			for (int i = 0; i < iMax; i++) {
				try {
					String value = nodes.item(i).getTextContent();
					value = resolveProperty(value);
					result.add(Boolean.parseBoolean(value));
				} catch (NumberFormatException e) {
					throw new XPathExpressionException("evaluated result of '" + xpath + "' is not a Boolean");
				}
			}
		}

		return result.toArray(new Boolean[result.size()]);
	}

	/**
	 * Get value from given XPath
	 * 
	 * @param xpath
	 * @return values Integer[]
	 * @throws XPathExpressionException
	 */
	public Integer[] getIntArray(String xpath) throws XPathExpressionException {
		List<Integer> result = new ArrayList<Integer>(10);

		NodeList nodes = (NodeList) eval.evaluate(xpath, doc, XPathConstants.NODESET);
		if (nodes == null) {
			warnEmptyValue(xpath, null);
		} else {
			int iMax = nodes.getLength();
			for (int i = 0; i < iMax; i++) {
				try {
					String value = nodes.item(i).getTextContent();
					value = resolveProperty(value);
					result.add(Integer.parseInt(value));
				} catch (NumberFormatException e) {
					throw new XPathExpressionException("evaluated result of '" + xpath + "' is not an Integer");
				}
			}
		}

		return result.toArray(new Integer[result.size()]);
	}

	/**
	 * Replace '$' enclosed value with system property.
	 * 
	 * @param value
	 * @return system property or value itself(no such property)
	 */
	protected String resolveProperty(String value) {
		String result = value;

		if (!Validator.isEmpty(value) && value.charAt(0) == '$' && value.endsWith("$")) {
			String key = value.substring(1, value.length() - 1);

			Properties props = System.getProperties();
			if (props.containsKey(key)) {
				result = props.getProperty(key);
			}
		}

		return result;
	}
}
