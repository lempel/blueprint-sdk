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
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import blueprint.sdk.util.JXPathHelper;
import blueprint.sdk.util.Validator;

/**
 * Configuration Handler
 * 
 * @author Sangmin Lee
 * @since 2013. 6. 18.
 */
public class Config {
	/** logger */
	private static final Logger L = Logger.getLogger(Config.class);

	/** uri of configuration */
	protected String uri;

	protected DocumentBuilder builder;

	/** root Node of config xml */
	protected Node root;

	/** root as JXPathContext */
	protected JXPathContext context;

	/**
	 * Constructor
	 */
	public Config() {
		// NO-OP
	}

	/**
	 * Constructor
	 * 
	 * @param root
	 *            Document or Node of config
	 */
	public Config(Node root) {
		this.root = root;
	}

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
			root = builder.parse(uri);
		} catch (Exception e) {
			throw new ConfigException("Can't parse config xml", e);
		}

		String configName = JXPathHelper.evaluate("config/@name", context);
		if (Validator.isEmpty(configName)) {
			configName = "no-name";
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
		String result = JXPathHelper.evaluate(xpath, context);
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

		Iterator<Pointer> nodeIter = JXPathHelper.evaluateIteratorPointers(xpath, context);
		while (nodeIter.hasNext()) {
			Pointer nodePtr = nodeIter.next();

			String value = (String) nodePtr.getValue();
			value = resolveProperty(value);
			result.add(value);
		}

		if (result.size() == 0) {
			warnEmptyValue(xpath, null);
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

		Iterator<Pointer> nodeIter = JXPathHelper.evaluateIteratorPointers(xpath, context);
		while (nodeIter.hasNext()) {
			Pointer nodePtr = nodeIter.next();

			String value = (String) nodePtr.getValue();
			value = resolveProperty(value);
			try {
				result.add(Boolean.parseBoolean(value));
			} catch (NumberFormatException e) {
				throw new XPathExpressionException("evaluated result of '" + xpath + "' is not a Boolean");
			}
		}

		if (result.size() == 0) {
			warnEmptyValue(xpath, null);
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

		Iterator<Pointer> nodeIter = JXPathHelper.evaluateIteratorPointers(xpath, context);
		while (nodeIter.hasNext()) {
			Pointer nodePtr = nodeIter.next();

			String value = (String) nodePtr.getValue();
			value = resolveProperty(value);
			try {
				result.add(Integer.parseInt(value));
			} catch (NumberFormatException e) {
				throw new XPathExpressionException("evaluated result of '" + xpath + "' is not an Integer");
			}
		}

		if (result.size() == 0) {
			warnEmptyValue(xpath, null);
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
