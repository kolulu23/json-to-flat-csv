package org.kolulu;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Convert json to csv.
 *
 * @author kolulu
 * <br/>Created at 2022/1/8 14:57
 */
public interface IConverter {

    /**
     * Convert json to csv using input/output streams.
     *
     * @param inputStream  Input stream that contains certain json data
     * @param outputStream Output stream for writing csv
     */
    void convert(InputStream inputStream, OutputStream outputStream);

    /**
     * Convert a single json string into csv with a specific output stream.<br/>
     * Note: The default implementation creates a buffered input stream over that string with default constructor. Using a
     * custom implementation is recommended.
     *
     * @param jsonStr      Single json string like {@code {"foo": "bar"}}, UTF-8 is assumed
     * @param outputStream Output stream for writing csv
     */
    default void convertString(String jsonStr, OutputStream outputStream) {
        BufferedInputStream inputStream = new BufferedInputStream(
                new ByteArrayInputStream(jsonStr.getBytes(StandardCharsets.UTF_8))
        );
        convert(inputStream, outputStream);
    }
}
