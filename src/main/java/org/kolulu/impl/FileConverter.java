package org.kolulu.impl;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvWriteException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.kolulu.ConverterMode;
import org.kolulu.api.AbstractFileConfigure;
import org.kolulu.api.LineSeparatedJsonConverter;
import org.kolulu.api.StandardJsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Take json from a single file like {@code ~/foo/flat_array.json}, write csv into another file.
 *
 * @author kolulu
 * <br/>Created at 2022/1/8 15:15
 */
@SuppressWarnings("unused")
public class FileConverter extends AbstractFileConfigure implements StandardJsonConverter, LineSeparatedJsonConverter {

    private static final Logger log = LoggerFactory.getLogger(FileConverter.class);

    protected static final ConverterMode[] MODES = new ConverterMode[]{ConverterMode.LINE_SEPARATED, ConverterMode.STANDARD};

    private static final ObjectMapper objectMapper = new JsonMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final CsvMapper csvMapper = new CsvMapper();

    /**
     * Custom csv headers when not use json fields as csv headers
     */
    private Set<String> customHeaders;

    /**
     * Your csv file is {@code out.csv}
     */
    public FileConverter() {
        this.customHeaders = new HashSet<>();
    }

    /**
     * Constructor for default outputFile.
     * When calling method {@link #convertLineSeparated(java.io.InputStream, java.io.OutputStream)} and pass {@code null}
     * to outputStream, {@link #getOutputFileName()} will be used as output file.
     *
     * @param outputFileName Absolute path to the default output file
     */
    public FileConverter(String outputFileName) {
        this.setOutputFileName(outputFileName);
    }

    public FileConverter(String outputFileName, Set<String> customHeaders) {
        this.setOutputFileName(outputFileName);
        this.customHeaders = customHeaders;
    }

    @Override
    public ConverterMode[] getConverterMode() {
        return MODES;
    }

    /**
     * Read each line as a json node and convert it to a csv row.
     * Since line iterator iterates file line by line, large file can be handled nicely.
     * Please note that nested json is not supported!
     *
     * @param inputStream  Input stream that contains certain json data
     * @param outputStream Output stream for writing csv
     */
    @Override
    public void convertLineSeparated(InputStream inputStream, OutputStream outputStream) {
        try {
            if (outputStream == null) {
                outputStream = new BufferedOutputStream(FileUtils.openOutputStream(new File(this.getOutputFileName())));
            }
        } catch (IOException e) {
            log.error("Cannot create output file nor use given outputStream");
        }
        if (this.customHeaders != null && !this.customHeaders.isEmpty()) {
            convertLineSeparatedWithCustomHeader(inputStream, outputStream);
            return;
        }
        try {
            LineIterator lineIterator = IOUtils.lineIterator(inputStream, StandardCharsets.UTF_8.name());
            ObjectWriter csvWriter;
            CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder();
            if (lineIterator.hasNext()) {
                JsonNode node = objectMapper.readTree(lineIterator.nextLine());
                node.fieldNames().forEachRemaining(csvSchemaBuilder::addColumn);
                CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();
                csvWriter = csvMapper.writerFor(JsonNode.class)
                        .with(csvSchema)
                        .without(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
                csvWriter.writeValue(outputStream, node);
            } else {
                return;
            }
            csvWriter = csvWriter.with(csvSchemaBuilder.build().withoutHeader());
            while (lineIterator.hasNext()) {
                JsonNode node = objectMapper.readTree(lineIterator.nextLine());
                csvWriter.writeValue(outputStream, node);
            }
            if (outputStream != null) {
                outputStream.flush();
            }
        } catch (JacksonException e) {
            log.error("Json parse error", e);
        } catch (IOException e) {
            log.error("Cannot read lines from given input stream", e);
        } finally {
            log.debug("Closing input and output streams");
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }

    private void convertLineSeparatedWithCustomHeader(InputStream inputStream, OutputStream outputStream) {
        try {
            IOUtils.write(String.join(",", this.customHeaders), outputStream);
        } catch (IOException e) {
            log.error("Write Header failed", e);
        }
        try {
            List<String> headers = new ArrayList<>(this.customHeaders);
            LineIterator lineIterator = IOUtils.lineIterator(inputStream, StandardCharsets.UTF_8.name());
            while (lineIterator.hasNext()) {
                HashMap<String, Object> data = objectMapper.readValue(lineIterator.nextLine(), new TypeReference<HashMap<String, Object>>() {
                });

                StringBuilder stringBuilder = new StringBuilder(this.getRowSize());
                stringBuilder.append("\n");
                for (int i = 0; i < headers.size() - 1; i++) {
                    Object value = data.get(headers.get(i));
                    value = value == null ? "" : value;
                    stringBuilder.append(value).append(",");
                }
                Object value = data.get(headers.get(headers.size() - 1));
                value = value == null ? "" : value;
                stringBuilder.append(value);
                IOUtils.write(stringBuilder.toString(), outputStream);
            }
        } catch (JacksonException e) {
            log.error("Json parse error failed to read value into java map", e);
        } catch (IOException e) {
            log.error("Cannot get line iterator from given input stream", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }

    /**
     * Convert standard json to csv using input/output streams.
     * <p>
     * If inputStream is a single json object, then nested child json nodes will be added as csv columns.
     * However, nested json values are not handled so an exception would be thrown.<br/>
     * If inputStream is a json array, then it will take the first element as csv schema if no other schema is provided.
     * If elements of that json array is also nested json, then exception would also be thrown.
     *
     * @param inputStream  Input stream that contains certain json data
     * @param outputStream Output stream for writing csv
     */
    @Override
    public void convertStandard(InputStream inputStream, OutputStream outputStream) {
        try {
            if (outputStream == null) {
                outputStream = FileUtils.openOutputStream(new File(this.getOutputFileName()));
            }
        } catch (IOException e) {
            log.error("Cannot create output file nor use given outputStream");
        }
        try {
            JsonNode root = objectMapper.readTree(inputStream);
            CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder();
            if (root.isObject()) {
                useFlatJson(root, csvSchemaBuilder);
            } else if (root.isArray()) {
                root.elements().next().fieldNames().forEachRemaining(csvSchemaBuilder::addColumn);
            } else {
                log.info("Only json object and json array are suitable for this application to perform the conversion.");
                return;
            }
            CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();
            ObjectWriter csvWriter = csvMapper.writerFor(JsonNode.class).with(csvSchema);
            csvWriter.writeValue(outputStream, root);
        } catch (CsvWriteException e) {
            log.error("Write csv error", e);
        } catch (IOException e) {
            log.error("Json parse error", e);
        } finally {
            log.debug("Closing input and output streams");
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }

    /**
     * Write this json node into csv.
     * If node is nested, then the nested content is flattened(this may cause duplicated column name).
     *
     * @param node Flat json node
     */
    private void useFlatJson(JsonNode node, CsvSchema.Builder csvSchemaBuilder) {
        node.fields().forEachRemaining(nodeEntry -> {
            String fieldName = nodeEntry.getKey();
            JsonNode jsonNode = nodeEntry.getValue();
            csvSchemaBuilder.addColumn(fieldName);
            if (jsonNode.isContainerNode()) {
                useFlatJson(jsonNode, csvSchemaBuilder);
            }
        });
    }

    public Set<String> getCustomHeaders() {
        return customHeaders;
    }

    public void setCustomHeaders(Set<String> customHeaders) {
        this.customHeaders = customHeaders;
    }
}
