package org.kolulu.api;

import java.io.File;
import java.nio.file.Paths;

/**
 * @author kolulu
 * <br/>Created at 2022/1/8 23:43
 */
public class AbstractFileConfigure {
    /**
     * Output csv file name, absolute path
     */
    private String outputFileName = Paths.get("").toAbsolutePath() + File.separator + "out.csv";

    /**
     * Initial size for each line of that file
     */
    private int rowSize = 100;

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public int getRowSize() {
        return rowSize;
    }

    public void setRowSize(int rowSize) {
        this.rowSize = rowSize;
    }
}
