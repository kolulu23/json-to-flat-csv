package org.kolulu;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author kolulu
 * <br/>Created at 2022/1/8 15:47
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static final String COMPRESSED_FILE = "D:\\Workspace\\Projects\\JavaProjects\\json-to-flat-csv\\data\\ff.txt";
    public static final String OUT_FILE = "D:\\Workspace\\Projects\\JavaProjects\\json-to-flat-csv\\data\\output.csv";

    public static void main(String[] args) throws IOException {
        log.info("Read file from " + COMPRESSED_FILE);
        Set<String> headers = new HashSet<>(Arrays.asList("foo,count,fake,null,not,number,bool".split(",")));
        FileConverter converter = new FileConverter(OUT_FILE, headers);
        InputStream inputStream = FileUtils.openInputStream(new File(COMPRESSED_FILE));
        converter.convertLineSeparated(inputStream, null);
    }
}
