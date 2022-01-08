package org.kolulu;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;

/**
 * Take json from a single file like {@code ~/foo/input.json}, write csv into another file.
 *
 * @author kolulu
 * <br/>Created at 2022/1/8 15:15
 */
public class FileConverter implements StandardJsonConverter, LineSeparatedJsonConverter {

    private String outputFileName;

    /**
     * Your csv file is {@code out.csv}
     */
    public FileConverter() {
        this.outputFileName = Paths.get("").toAbsolutePath() + File.separator + "out.csv";
    }

    public FileConverter(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    /**
     * Output stream can be written to a file.
     *
     * @param inputStream  Input stream that contains certain json data
     * @param outputStream Output stream for writing csv
     */
    @Override
    public void convert(InputStream inputStream, OutputStream outputStream) {
        // TODO
    }

    @Override
    public void convertLineSeparated(InputStream inputStream, OutputStream outputStream) {
        // TODO document why this method is empty
    }

    @Override
    public void convertStandard(InputStream inputStream, OutputStream outputStream) {
        // TODO document why this method is empty
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }
}
