package org.kolulu;

import org.apache.commons.io.FileUtils;
import org.kolulu.impl.MetricServiceConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * @author kolulu
 * <br/>Created at 2022/1/8 15:47
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static final String COMPRESSED_FILE = "D:\\Workspace\\Projects\\JavaProjects\\json-to-flat-csv\\data\\flat_line_separated.txt";
    public static final String METRIC_FILE = "D:\\Workspace\\Projects\\JavaProjects\\json-to-flat-csv\\data\\metric_result_short.txt";
    public static final String OUT_FILE = "D:\\Workspace\\Projects\\JavaProjects\\json-to-flat-csv\\data\\result.csv";

    public static void main(String[] args) throws IOException {
        log.info("Read file from " + COMPRESSED_FILE);
//        Set<String> headers = new HashSet<>(Arrays.asList("foo,count,fake,null,not,number,bool".split(",")));
//        FileConverter converter = new FileConverter(OUT_FILE, headers);
//        converter.convertLineSeparated(inputStream, null);

        List<String> metricCodes = new LinkedList<>(Arrays.asList("wyaujqnfiv,ikhsmigbga,yoi78silk,ln5qz00kag".split(",")));
        List<String> otherFields = new LinkedList<>(Arrays.asList("jylsh,zrzh,zczh,zchh,transdate".split(",")));
        InputStream inputStream = FileUtils.openInputStream(new File(METRIC_FILE));
        MetricServiceConverter metricServiceConverter = new MetricServiceConverter(metricCodes, otherFields, OUT_FILE);
        metricServiceConverter.setIncludeMoreInfo(true);
        metricServiceConverter.convertLineSeparated(inputStream, null);
    }
}
