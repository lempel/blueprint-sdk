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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * <br>
 * Path can contain variables.<br>
 * Variables must be notated with braces.<br>
 * ex 4) profile.characters[{name}]<br>
 * Variable must be set with {@link Jayson#let(String, Object)}.<br>
 * ex 5) jayson.let("name", "simon");<br>
 * <br>
 * Children can be accessed with either brackets or dots.<br>
 * ex 6) profile.characters.{name}.items.{i}.prop.{j}.value<br>
 * ex 7) profile[characters][{name}][items][{i}][prop][{j}][value]<br>
 *
 * @author lempel@gmail.com
 * @since 2021. 4. 20.
 */
public class Jayson extends HashMap {
    protected final Map<String, Object> values = new HashMap<>();

    // -------------------------------------------------------------------
    //
    //
    // FIXME after finishing this, update library versions before publish.
    //
    //
    // -------------------------------------------------------------------

    /**
     * Serialize JSON
     *
     * @param jayson target
     * @return JSON String
     * @throws JsonProcessingException Jackson's Exception
     */
    public static String stringify(Jayson jayson) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(jayson);
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
     * @throws RuntimeException invalid path
     */
    public Object json(String path) throws RuntimeException {
        return json(path, false, null);
    }

    /**
     * Evaluates given path and sets new value
     *
     * @param path  target
     * @param value new value
     * @return designated value
     * @throws RuntimeException invalid path
     */
    public Object json(String path, Object value) throws RuntimeException {
        return json(path, true, value);
    }

    /**
     * Evaluates given path and do get or set
     *
     * @param path  target
     * @param doSet true: do set
     * @param value new value
     * @return designated value
     * @throws RuntimeException invalid path
     */
    protected Object json(String path, boolean doSet, Object value) throws RuntimeException {
        Object ret;

        String[] tokens = tokenizePath(path);
        StringBuilder processed = new StringBuilder();

        Object target = this;
        Object lastTarget = null;
        String lastToken = null;

        for (String token : tokens) {
            if (token.isEmpty()) {
                continue;
            }

            if (token.matches("\\{[a-z|A-Z|\\d]+\\}")) {
                String name = token.substring(1, token.length() - 1);
                if (values.containsKey(name)) {
                    token = String.valueOf(values.get(name));
                } else {
                    throw new RuntimeException(token + " is not defined - " + processed.toString());
                }
            }

            if (TypeChecker.isInteger(token)) {
                List list = (List) target;
                int index = Integer.parseInt(token);
                if (index < list.size()) {
                    lastTarget = target;
                    lastToken = token;
                    target = list.get(index);

                    processed.append("[").append(token).append("]");
                } else {
                    throw new RuntimeException(index + " is out of bounds - " + processed.toString());
                }
            } else {
                Map map = (Map) target;
                if (map.containsKey(token)) {
                    lastTarget = target;
                    lastToken = token;
                    target = map.get(token);

                    if (processed.length() > 0) {
                        processed.append(".");
                    }
                    processed.append(token);
                } else {
                    throw new RuntimeException(token + " is not defined - " + processed.toString());
                }
            }
        }

        if (doSet) {
            Object actualValue = value;
            if (value instanceof String) {
                String valueStr = ((String) value).trim();
                if (Validator.isNotEmpty(valueStr) && valueStr.startsWith("{") && valueStr.endsWith("}")) {
                    try {
                        actualValue = Jayson.parse(valueStr);
                    } catch (IOException e) {
                        throw new RuntimeException("value is not a proper jSON - " + processed.toString());
                    }
                }
            }

            if (TypeChecker.isInteger(lastToken)) {
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
     * Gets the length of designated array
     *
     * @param path target
     * @return array length
     * @throws RuntimeException target is not an array
     */
    public int length(String path) {
        int ret = -1;

        Object target = json(path);
        if (target instanceof List) {
            ret = ((List) target).size();
        } else {
            throw new RuntimeException("not an array - " + path);
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
