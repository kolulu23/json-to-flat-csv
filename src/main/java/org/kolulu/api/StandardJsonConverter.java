package org.kolulu.api;

import org.kolulu.ConverterMode;
import org.kolulu.api.IConverter;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author kolulu
 * <br/>Created at 2022/1/8 15:34
 */
public interface StandardJsonConverter extends IConverter {
    /**
     * Convert standard json to csv using input/output streams.
     * Standard json is considered as a single json object.
     *
     * @param inputStream  Input stream that contains certain json data
     * @param outputStream Output stream for writing csv
     */
    void convertStandard(InputStream inputStream, OutputStream outputStream);

    @Override
    default ConverterMode[] getConverterMode() {
        return new ConverterMode[]{ConverterMode.STANDARD};
    }
}
