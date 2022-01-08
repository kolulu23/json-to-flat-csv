package org.kolulu;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Take json from standard input, write csv into standard output.
 *
 * @author kolulu
 * <br/>Created at 2022/1/8 15:14
 */
public class StdConverter implements StandardJsonConverter, LineSeparatedJsonConverter {
    @Override
    public void convert(InputStream inputStream, OutputStream outputStream) {
        // TODO document why this method is empty
    }

    @Override
    public void convertLineSeparated(InputStream inputStream, OutputStream outputStream) {
        // TODO document why this method is empty
    }

    @Override
    public void convertStandard(InputStream inputStream, OutputStream outputStream) {
        // TODO document why this method is empty
    }
}
