package org.kolulu.api;

import org.kolulu.ConverterMode;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author kolulu
 * <br/>Created at 2022/1/8 15:37
 */
public interface LineSeparatedJsonConverter extends IConverter {

    /**
     * Convert line separated json that looks like:
     * <pre>
     *     {"foo": "bar"}\n
     *     {"foo": "bar"}\n
     *     {"foo": "bar"}
     * </pre>
     * into csv using input/output streams.
     *
     * @param inputStream  Input stream that contains certain json data
     * @param outputStream Output stream for writing csv
     */
    void convertLineSeparated(InputStream inputStream, OutputStream outputStream);

    @Override
    default ConverterMode[] getConverterMode() {
        return new ConverterMode[]{ConverterMode.LINE_SEPARATED};
    }
}
