package com.cagurley;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class DataSet {
    private String fileName;
    private String fileExt;
    private File file;
    private String tableName;
    private CSVParser parser;

    public DataSet(String fileName, String tableName) {
        if (tableName.matches("^\\w+$")) {
            this.fileName = fileName;
            String[] splitName = fileName.split("\\.(?=[^.]+$)");
            if (splitName.length == 2) {
                this.fileExt = splitName[1].toLowerCase();
            }
            this.file = this.getFileFromResources();
            this.tableName = tableName;
            this.parser = null;
        } else {
            throw new IllegalArgumentException("Table name is invalid, should be a string of word characters.");
        }
    }

    public boolean hasParser() { return this.parser != null; }

    public int getHeaderLength() { return this.parser.getHeaderMap().size(); }

    public Set<String> getHeaderKeySet() { return this.parser.getHeaderMap().keySet(); }

    public String getTableName() { return this.tableName; }

    public void closeParser() throws IOException {
        if (this.parser != null) {
            this.parser.close();
            this.parser = null;
        }
    }

    public void parse() throws IOException {
        if (this.fileExt.equals("csv")) {
            this.parser = CSVParser.parse(this.getFile(), StandardCharsets.UTF_8, CSVFormat.DEFAULT.withHeader());
        }
    }

    private File getFileFromResources() {
        URL resource = this.getClass().getClassLoader().getResource(this.fileName);
        if (resource == null) {
            throw new IllegalArgumentException("File is not accessible.");
        } else {
            return new File(resource.getFile());
        }
    }

    public File getFile() {
        return this.file;
    }

    public CSVParser getParser() {
        return this.parser;
    }
}
