/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Simple JSON Store
 *
 * @param <T> JSON Object
 * @author lempel@gmail.com
 * @since 2015. 11. 04.
 */
public class SimpleJsonStore<T> {
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * '*.json' file's path
     */
    private Path path;
    /**
     * JSON Object's class
     */
    private Class<T> clazz;
    /**
     * JSON Object
     */
    private T json;

    /**
     * @param path  '*.json' file's path
     * @param clazz JSON Object class
     * @throws IOException            file open failed
     * @throws IllegalAccessException JSON Object creation failed
     * @throws InstantiationException no default constructor in JSON Object class
     */
    public SimpleJsonStore(String path, Class<T> clazz) throws IOException, IllegalAccessException, InstantiationException {
        File file = new File(path);
        this.path = file.toPath();
        this.clazz = clazz;

        if (file.exists()) {
            byte[] data = Files.readAllBytes(this.path);
            json = mapper.readValue(data, clazz);
        } else {
            json = clazz.newInstance();
        }

        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * @return JSON Object
     */
    public T getJson() {
        return json;
    }

    /**
     * Save JSON Object to file
     *
     * @throws IOException save failed
     */
    public void save() throws IOException {
        byte[] data = mapper.writeValueAsBytes(json);
        Files.write(path, data, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * Update with given JSON String and save to file
     *
     * @param jsonString JSON String
     * @throws IOException parse or save failed
     */
    public void save(String jsonString) throws IOException {
        T newJson = mapper.readValue(jsonString, clazz);

        json = newJson;
        save();
    }
}
