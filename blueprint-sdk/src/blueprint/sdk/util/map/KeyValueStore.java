/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util.map;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple Key/Value Store with JSON file synchronization
 *
 * @param <T> Must be a simple bean class with zero argument constructors.
 * @author lempel@gmail.com
 * @since 2014. 4. 15.
 */
public class KeyValueStore<T> {
    /**
     * JSON file
     */
    private final File jsonFile;

    /**
     * Jackson Mapper
     */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * key/value map
     */
    private final Map<String, T> kvMap = new HashMap<>(10000);
    /**
     * Lock for kvMap
     */
    private final Object lock = new Object();

    /**
     * @param path path of JSON file
     */
    public KeyValueStore(String path) {
        this(new File(path));
    }

    /**
     * @param jsonFile JSON file for synchronization
     */
    @SuppressWarnings("WeakerAccess")
    public KeyValueStore(File jsonFile) {
        this.jsonFile = jsonFile;
    }

    /**
     * @param key key of element
     * @return current value of given key
     */
    public T get(String key) {
        return kvMap.get(key);
    }

    /**
     * Put a new key/value pair
     *
     * @param key key of element
     * @param value value of element
     * @return old value or null(new key)
     */
    public T put(String key, T value) {
        synchronized (lock) {
            return kvMap.put(key, value);
        }
    }

    /**
     * Load JSON file and populate key/value map
     *
     * @throws IOException          Can't read JSON file
     * @throws JsonMappingException Invalid JSON mapping
     * @throws JsonParseException   Invalid JSON file
     */
    public void load() throws IOException {
        if (jsonFile == null) {
            throw new NullPointerException("JSON file is not specified");
        }

        if (jsonFile.exists()) {
            TypeReference<HashMap<String, T>> typeRef = new TypeReference<HashMap<String, T>>() {
            };
            HashMap<String, T> map = mapper.readValue(jsonFile, typeRef);

            synchronized (lock) {
                kvMap.clear();
                kvMap.putAll(map);
            }
        } else {
            throw new FileNotFoundException("JSON file is not exist");
        }
    }

    /**
     * @throws JsonGenerationException Can't map to JSON
     * @throws JsonMappingException    Can't map to JSON
     * @throws IOException             Can't write to specified file
     */
    public void save() throws IOException {
        synchronized (lock) {
            mapper.writeValue(jsonFile, kvMap);
        }
    }
}