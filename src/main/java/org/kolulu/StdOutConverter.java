package org.kolulu;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Take json from file, write csv into standard output.
 *
 * @author kolulu
 * <br/>Created at 2022/1/8 15:14
 */
public class StdOutConverter implements StandardJsonConverter, LineSeparatedJsonConverter {

    protected static final ConverterMode[] MODES = new ConverterMode[]{ConverterMode.LINE_SEPARATED, ConverterMode.STANDARD};

    @Override
    public ConverterMode[] getConverterMode() {
        return MODES;
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
