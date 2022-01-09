package org.kolulu.cli;

import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.AllowedRawValues;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.kolulu.ConverterMode;
import org.kolulu.impl.FileConverter;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @author kolulu
 * <br/>Created at 2022/1/10 1:58
 */
@Command(name = "convert-flat", description = "Convert flat json into csv")
public class FlatCommand implements Runnable {

    @Inject
    private HelpOption<MetricCommand> helpOption;

    @Option(name = {"-i", "--input"},
            description = "Absolute path of the input json file")
    private String inputFilePath;

    @Option(name = {"-o", "--output"},
            description = "Absolute path of the output csv file. If not set, then output will be out.csv at current working directory")
    private String outputFilePath;

    @Option(name = {"-c", "--headers"},
            description = "Comma separated customHeaders. If <HandleMode> is set to 'STANDARD', custom headers will not be used")
    private String customHeaders;

    @AllowedRawValues(allowedValues = {"STANDARD", "LINE_SEPARATED", "COMMA_SEPARATED"})
    @Option(name = {"-m", "--mode"},
            description = "How to handle input file.",
            title = "HandleMode")
    private String mode;

    @Override
    public void run() {
        if (StringUtils.isBlank(inputFilePath) || StringUtils.isBlank(mode)) {
            helpOption.showHelp();
            return;
        }
        FileConverter fileConverter = new FileConverter();
        if (StringUtils.isNotBlank(outputFilePath)) {
            fileConverter.setOutputFileName(outputFilePath);
        }
        try {
            File inputFile = new File(inputFilePath);
            FileInputStream inputStream = FileUtils.openInputStream(inputFile);
            if (ConverterMode.STANDARD.name().equals(mode)) {
                fileConverter.convertStandard(inputStream, null);
            } else if (ConverterMode.LINE_SEPARATED.name().equals(mode)) {
                if (StringUtils.isNotBlank(customHeaders)) {
                    fileConverter.setCustomHeaders(new HashSet<>(Arrays.asList(customHeaders.split(","))));
                }
                fileConverter.convertLineSeparated(FileUtils.openInputStream(inputFile), null);
            } else {
                System.out.println("Not implemented mode: " + mode);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
