/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel76.blogspot.kr
        http://lempel.egloos.com
 */

package blueprint.sdk.core.jayson;

import blueprint.sdk.util.TypeChecker;
import blueprint.sdk.util.Validator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;

/**
 * JSON wrapper built on top of Jackson.<br>
 * Provides E4X like approach.<br>
 * <br>
 * Basically, gives ways to access JSON with path.<br>
 * ex 1) jayson.json("profile.characters");<br>
 * ex 2) jayson.json("profile.characters.simon.id", "simon's id");<br>
 * <br>
 * Setting another child JSON is allowed but must be in serialized form.<br>
 * ex 3) jayson.json("profile.characters.simon", "{\"comment\":\"just been replaced\"}");<br>
 * Also, JSON array can be set.<br>
 * ex 4) jayson.json("profile.characters.simon.items", "[{\"name\":\"item_1\"}, {\"name\":\"item_2\"}, {\"name\":\"item_3\"}]");<br>
 * <br>
 * Path can contain variables.<br>
 * Variables must be notated with braces.<br>
 * ex 5) profile.characters[{name}]<br>
 * Variable must be set with {@link Jayson#let(String, Object)}.<br>
 * Variable name must be consists of alphabets, numbers and under score.<br>
 * ex 6) jayson.let("name", "simon");<br>
 * <br>
 * Children can be accessed with either brackets or dots.<br>
 * ex 7) profile.characters.{name}.items.{i}.prop.{j}.value<br>
 * ex 8) profile[characters][{name}][items][{i}][prop][{j}][value]<br>
 * <br>
 *
 * @author lempel@gmail.com
 * @since 2021. 4. 20.
 */
public class Jayson extends HashMap<String, Object> {
    protected final Map<String, Object> values = new HashMap<>();

    /**
     * Serialize Map as JSON String
     *
     * @param target Map or Jayson
     * @return JSON String
     * @throws JsonProcessingException Jackson's Exception
     */
    public static String stringify(Map<String, ? extends Object> target) throws JsonProcessingException {
        return stringify(target, false);
    }

    /**
     * Serialize Map as JSON String
     *
     * @param target Map or Jayson
     * @param pretty pretty format
     * @return JSON String
     * @throws JsonProcessingException Jackson's Exception
     */
    public static String stringify(Map<String, ? extends Object> target, boolean pretty) throws JsonProcessingException {
        String ret;
        ObjectMapper mapper = new ObjectMapper();
        if (pretty) {
            ret = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(target);
        } else {
            ret = mapper.writeValueAsString(target);
        }
        return ret;
    }

    // TODO Map<String, Object> 를 argument 로 받는 constructor 구현

    /**
     * Serialize as JSON String
     *
     * @return JSON String
     * @throws JsonProcessingException Jackson's Exception
     */
    public String stringify() throws JsonProcessingException {
        return stringify(this, false);
    }

    /**
     * Serialize as JSON String
     *
     * @param pretty pretty format
     * @return JSON String
     * @throws JsonProcessingException Jackson's Exception
     */
    public String stringify(boolean pretty) throws JsonProcessingException {
        return stringify(this, pretty);
    }

    /**
     * Parse JSON String
     *
     * @param jsonStr target
     * @return parsed Jayson
     * @throws IOException Jackson's Exception
     */
    public static Jayson parse(String jsonStr) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonStr, Jayson.class);
    }

    /**
     * Get current value of designated varaible.
     *
     * @param name variable name
     * @return current value
     */
    public Object let(String name) {
        return values.get(name);
    }

    /**
     * Set new value for a variable
     *
     * @param name  variable name
     * @param value variable value
     */
    public void let(String name, Object value) {
        values.put(name, value);
    }

    /**
     * Evaluates given path and returns designated value
     *
     * @param path target
     * @return designated value
     * @throws JaysonException invalid path
     */
    public Object json(String path) throws JaysonException {
        return json(path, false, null);
    }

    /**
     * Evaluates given path and sets new value
     *
     * @param path  target
     * @param value new value
     * @return designated value
     * @throws JaysonException invalid path
     */
    public Object json(String path, Object value) throws JaysonException {
        // TODO 자동생성 flag 를 두고, map 에 한정해서 자동으로 child 를 생성할수 있게 하자

        return json(path, true, value);
    }

    /**
     * Evaluates given path and do get or set
     *
     * @param path  target
     * @param doSet true: do set
     * @param value new value
     * @return designated value
     * @throws JaysonException invalid path
     */
    protected Object json(String path, boolean doSet, Object value) throws JaysonException {
        Object ret;

        String[] tokens = tokenizePath(path);
        StringBuilder processed = new StringBuilder();

        Object target = this;
        Object lastTarget = null;
        String lastToken = null;

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];

            if (token.isEmpty()) {
                continue;
            }

            // check variables
            if (token.matches("\\{[a-z|A-Z|\\_|\\d]+\\}")) {
                String name = token.substring(1, token.length() - 1);
                if (values.containsKey(name)) {
                    token = String.valueOf(values.get(name));
                } else {
                    throw new JaysonException(token + " is not defined - " + processed);
                }
            }

            if (target instanceof List) {
                // search List
                if (TypeChecker.isInteger(token)) {
                    List list = (List) target;
                    int index = Integer.parseInt(token);
                    if (index < list.size()) {
                        lastTarget = target;
                        lastToken = token;
                        target = list.get(index);

                        // compile path for messages
                        processed.append("[").append(token).append("]");
                    } else {
                        throw new JaysonException(index + " is out of bounds - " + processed);
                    }
                } else {
                    // creating new child on List is not allowed

                    throw new JaysonException(token + " is not an index - " + processed);
                }
            } else {
                // search Map
                Map map = (Map) target;
                if (map.containsKey(token)) {
                    lastTarget = target;
                    lastToken = token;
                    target = map.get(token);

                    // compile path for messages
                    if (processed.length() > 0) {
                        processed.append(".");
                    }
                    processed.append(token);
                } else {
                    if (doSet && i == tokens.length - 1) {
                        // creating new child on Map
                        lastTarget = target;
                        lastToken = token;
                    } else {
                        throw new JaysonException(token + " is not defined - " + processed);
                    }
                }
            }
        }

        if (doSet) {
            Object actualValue = value;
            if (value instanceof String) {
                String valueStr = ((String) value).trim();
                if (Validator.isNotEmpty(valueStr) && valueStr.startsWith("{") && valueStr.endsWith("}")) {
                    // parse value if it's an JSON string
                    try {
                        actualValue = Jayson.parse(valueStr);
                    } catch (IOException e) {
                        throw new JaysonException("value is not a proper jSON - " + processed);
                    }
                } else if (Validator.isNotEmpty(valueStr) && valueStr.startsWith("[") && valueStr.endsWith("]")) {
                    // parse value if it's an JSON array
                    try {
                        Jayson newChild = Jayson.parse("{\"array\":" + valueStr + "}");
                        actualValue = newChild.json("array");
                    } catch (IOException e) {
                        throw new JaysonException("\"" + value + "\" is not a proper JSON array - " + processed);
                    }
                }
            }

            if (TypeChecker.isInteger(lastToken) && lastTarget instanceof List) {
                ((List) lastTarget).set(Integer.parseInt(lastToken), actualValue);
            } else {
                ((Map) lastTarget).put(lastToken, actualValue);
            }
            ret = actualValue;
        } else {
            ret = target;
        }

        return ret;
    }

    /**
     * Push a value to target array
     *
     * @param path  target array
     * @param value value to push
     * @return target array
     * @throws JaysonException invalid target
     */
    public Object push(String path, Object value) throws JaysonException {
        List ret = new ArrayList();

        Object actualValue = value;
        if (value instanceof String) {
            String valueStr = ((String) value).trim();
            if (Validator.isNotEmpty(valueStr) && valueStr.startsWith("{") && valueStr.endsWith("}")) {
                // parse value if it's an JSON string
                try {
                    actualValue = Jayson.parse(valueStr);
                } catch (IOException e) {
                    throw new JaysonException("value is not a proper jSON - " + value);
                }
            } else if (Validator.isNotEmpty(valueStr) && valueStr.startsWith("[") && valueStr.endsWith("]")) {
                // parse value if it's an JSON array
                try {
                    Jayson newChild = Jayson.parse("{\"array\":" + valueStr + "}");
                    actualValue = newChild.json("array");
                } catch (IOException e) {
                    throw new JaysonException("\"" + value + "\" is not a proper JSON array - " + value);
                }
            }
        }

        // get as a list
        List list = null;
        Object arr = json(path);
        if (arr instanceof Object[]) {
            list = Arrays.asList(arr);
        } else if (arr instanceof List) {
            list = (List) arr;
        } else {
            throw new JaysonException("target is not an array - " + path);
        }

        // append value
        ret.addAll(list);
        ret.add(actualValue);

        // replace
        json(path, ret);

        return ret;
    }

    /**
     * See if given path exists or not
     *
     * @param path target
     * @return true: exists
     */
    public boolean exists(String path) {
        boolean ret = false;

        try {
            json(path);

            ret = true;
        } catch (RuntimeException ignored) {
        }

        return ret;
    }

    /**
     * Gets the length of designated array
     *
     * @param path target
     * @return array length
     * @throws JaysonException target is not an array
     */
    public int length(String path) {
        int ret = -1;

        Object target = json(path);
        if (target instanceof List) {
            ret = ((List) target).size();
        } else {
            throw new JaysonException("not an array - " + path);
        }

        return ret;
    }

    /**
     * Tokenize given path
     *
     * @param path target
     * @return tokens
     */
    protected String[] tokenizePath(String path) {
        ArrayList<String> ret = new ArrayList<>();

        if (!Validator.isEmpty(path)) {
            String[] tokens = path.split("[\\.|\\[|\\]]");

            for (String token : tokens) {
                if (token.isEmpty()) {
                    continue;
                }

                ret.add(token);
            }
        }

        return ret.toArray(new String[0]);
    }
}
