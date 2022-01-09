package org.kolulu.cli;

import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.RequiredOnlyIf;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.kolulu.impl.MetricServiceConverter;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author kolulu
 * <br/>Created at 2022/1/10 1:45
 */
@Command(name = "convert-metric",
        description = "Convert metric query result json into csv, some fields are flattened.")
public class MetricCommand implements Runnable {

    @Inject
    private HelpOption<MetricCommand> helpOption;

    @Option(name = {"-v", "--verbose"},
            description = "Append verbose info into output file")
    private boolean verbose = false;

    @Option(name = {"-i", "--input"},
            description = "Absolute path of the input json file")
    private String inputFilePath;

    @Option(name = {"-o", "--output"},
            description = "Absolute path of the output csv file. If not set, then output will be out.csv at current working directory")
    private String outputFilePath;

    @RequiredOnlyIf(names = {"-i", "--input"})
    @Option(name = {"-m", "--metric-codes"},
            description = "Comma separated metric codes that will be used as csv headers")
    private String metricCodes;

    @Option(name = {"-f", "--other-fields"},
            description = "Other json fields that you want to convert, nested fields are not supported")
    private String otherFields;

    @Override
    public void run() {
        if (StringUtils.isBlank(inputFilePath)) {
            helpOption.showHelp();
            return;
        }
        MetricServiceConverter metricServiceConverter = new MetricServiceConverter();
        if (StringUtils.isNotBlank(outputFilePath)) {
            metricServiceConverter.setOutputFileName(outputFilePath);
        }
        if (StringUtils.isNotBlank(metricCodes)) {
            metricServiceConverter.setMetricCodeList(new LinkedList<>(Arrays.asList(metricCodes.split(","))));
        }
        if (StringUtils.isNotBlank(otherFields)) {
            metricServiceConverter.setOtherFieldNames(new LinkedList<>(Arrays.asList(otherFields.split(","))));
        }
        metricServiceConverter.setIncludeMoreInfo(verbose);
        try {
            File inputFile = new File(inputFilePath);
            metricServiceConverter.convertLineSeparated(FileUtils.openInputStream(inputFile), null);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
