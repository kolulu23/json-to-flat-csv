package org.kolulu.impl;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.kolulu.ConverterMode;
import org.kolulu.api.AbstractFileConfigure;
import org.kolulu.api.LineSeparatedJsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Take json from file, write csv into standard output.
 * A very specific converter that converts a series POJO.
 *
 * @author kolulu
 * <br/>Created at 2022/1/8 15:14
 */
public class MetricServiceConverter extends AbstractFileConfigure implements LineSeparatedJsonConverter {

    protected static final ConverterMode[] MODES = new ConverterMode[]{ConverterMode.LINE_SEPARATED, ConverterMode.STANDARD};

    private static final ObjectMapper objectMapper = new JsonMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final String MORE_INFO_KEY = "info";

    private static final Logger log = LoggerFactory.getLogger(MetricServiceConverter.class);

    /**
     * Metric codes you want them to be added as csv headers
     */
    private List<String> metricCodeList = new LinkedList<>();

    /**
     * Other nested properties in "data"
     */
    private List<String> otherFieldNames = new LinkedList<>();

    /**
     * Keys for metric result object in {@link MetricResponse#getData()} map.
     */
    private String metricResultArrKey = "metricresult";

    private String metricCodeKey = "metricCode";

    private String resultValueKey = "resultValue";

    private String metricNameKey = "metricName";

    private String metricTypeKey = "metricType";

    private String templateCodeKey = "templateType";

    /**
     * Whether include more info, currently not implemented
     */
    private boolean includeMoreInfo = false;

    /**
     * If includeMoreInfo is true, different info will be separated with this character
     */
    private String infoSeparator = "#";

    public MetricServiceConverter() {
    }

    public MetricServiceConverter(List<String> metricCodeList) {
        this.metricCodeList = metricCodeList;
    }

    public MetricServiceConverter(List<String> metricCodeList, List<String> otherFieldNames) {
        this.metricCodeList = metricCodeList;
        this.otherFieldNames = otherFieldNames;
    }

    public MetricServiceConverter(List<String> metricCodeList, String outputFileName) {
        this.metricCodeList = metricCodeList;
        this.setOutputFileName(outputFileName);
    }

    public MetricServiceConverter(List<String> metricCodeList, List<String> otherFieldNames, String outputFileName) {
        this.metricCodeList = metricCodeList;
        this.otherFieldNames = otherFieldNames;
        this.setOutputFileName(outputFileName);
    }

    @Override
    public ConverterMode[] getConverterMode() {
        return MODES;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void convertLineSeparated(InputStream inputStream, OutputStream outputStream) {
        try {
            if (outputStream == null) {
                outputStream = new BufferedOutputStream(FileUtils.openOutputStream(new File(this.getOutputFileName())));
            }
        } catch (IOException e) {
            log.error("Cannot create output file nor use given outputStream");
        }
        LinkedList<String> headers = this.getHeaders();
        try {
            IOUtils.write(String.join(",", headers), outputStream);
        } catch (IOException e) {
            log.error("Write Header failed", e);
        }
        try {
            LineIterator lineIterator = IOUtils.lineIterator(inputStream, StandardCharsets.UTF_8.name());
            StringBuilder stringBuilder = new StringBuilder(this.getRowSize());
            StringBuilder infoBuilder = new StringBuilder(this.getRowSize());
            while (lineIterator.hasNext()) {
                stringBuilder.append("\n");
                MetricResponse metricResponse = objectMapper.readValue(lineIterator.nextLine(), MetricResponse.class);
                if (metricResponse == null) {
                    continue;
                }
                Map<String, ?> dataMap = metricResponse.getData();
                for (String column : this.getMetricCodeList()) {
                    Object resultArr = dataMap.get(this.getMetricResultArrKey());
                    if (resultArr instanceof List<?>) {
                        boolean found = false;
                        for (Object item : (List<?>) resultArr) {
                            Object metricCode = ((Map<String, Object>) item).get(this.getMetricCodeKey());
                            if (column.equals(metricCode)) {
                                Object val = ((Map<String, Object>) item).get(this.getResultValueKey());
                                val = val == null ? "" : val;
                                stringBuilder.append(val).append(',');
                                found = true;
                                if (this.includeMoreInfo) {
                                    Object metricName = ((Map<String, Object>) item).get(this.getMetricNameKey());
                                    Object metricType = ((Map<String, Object>) item).get(this.getMetricTypeKey());
                                    Object templateCode = ((Map<String, Object>) item).get(this.getTemplateCodeKey());
                                    metricName = metricName == null ? "" : metricName;
                                    metricType = metricType == null ? "" : metricType;
                                    templateCode = templateCode == null ? "" : templateCode;
                                    infoBuilder.append('(').append(metricCode).append(this.infoSeparator)
                                            .append(metricName).append(this.infoSeparator)
                                            .append(metricType).append(this.infoSeparator)
                                            .append(templateCode).append(')');
                                }
                                break;
                            }
                        }
                        if (!found) {
                            stringBuilder.append(',');
                        }
                    } else {
                        // resultArr is null or not an array-like structure
                        stringBuilder.append(",");
                    }
                }
                for (String column : this.getOtherFieldNames()) {
                    Object val = dataMap.get(column);
                    val = val == null ? "" : val;
                    stringBuilder.append(val).append(',');
                }
                if (this.includeMoreInfo) {
                    infoBuilder.append(',');
                    stringBuilder.append(infoBuilder);
                }
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                IOUtils.write(stringBuilder.toString(), outputStream);
                stringBuilder.delete(0, stringBuilder.length());
                infoBuilder.delete(0, infoBuilder.length());
            }
        } catch (ClassCastException e) {
            log.error("Invalid metric result structure found", e);
        } catch (JacksonException e) {
            log.error("Not a valid metric response json", e);
        } catch (IOException e) {
            log.error("Cannot read lines from given input stream", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }

    /**
     * Get all needed headers. Metric codes first, then other fields.
     *
     * @return All columns of output csv file
     */
    private LinkedList<String> getHeaders() {
        LinkedList<String> headers = new LinkedList<>();
        headers.addAll(this.getMetricCodeList());
        headers.addAll(this.getOtherFieldNames());
        if (this.includeMoreInfo) {
            headers.add(MORE_INFO_KEY);
        }
        return headers;
    }

    public List<String> getMetricCodeList() {
        return metricCodeList;
    }

    public void setMetricCodeList(List<String> metricCodeList) {
        this.metricCodeList = metricCodeList;
    }

    public List<String> getOtherFieldNames() {
        return otherFieldNames;
    }

    public void setOtherFieldNames(List<String> otherFieldNames) {
        this.otherFieldNames = otherFieldNames;
    }

    public boolean isIncludeMoreInfo() {
        return includeMoreInfo;
    }

    public void setIncludeMoreInfo(boolean includeMoreInfo) {
        this.includeMoreInfo = includeMoreInfo;
    }

    public String getMetricResultArrKey() {
        return metricResultArrKey;
    }

    public void setMetricResultArrKey(String metricResultArrKey) {
        this.metricResultArrKey = metricResultArrKey;
    }

    public String getMetricCodeKey() {
        return metricCodeKey;
    }

    public void setMetricCodeKey(String metricCodeKey) {
        this.metricCodeKey = metricCodeKey;
    }

    public String getResultValueKey() {
        return resultValueKey;
    }

    public void setResultValueKey(String resultValueKey) {
        this.resultValueKey = resultValueKey;
    }

    public String getMetricNameKey() {
        return metricNameKey;
    }

    public void setMetricNameKey(String metricNameKey) {
        this.metricNameKey = metricNameKey;
    }

    public String getMetricTypeKey() {
        return metricTypeKey;
    }

    public void setMetricTypeKey(String metricTypeKey) {
        this.metricTypeKey = metricTypeKey;
    }

    public String getTemplateCodeKey() {
        return templateCodeKey;
    }

    public void setTemplateCodeKey(String templateCodeKey) {
        this.templateCodeKey = templateCodeKey;
    }

    public String getInfoSeparator() {
        return infoSeparator;
    }

    public void setInfoSeparator(String infoSeparator) {
        this.infoSeparator = infoSeparator;
    }
}
