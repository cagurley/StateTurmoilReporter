package com.cagurley;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

public class DataSet {
    private String fileName;
    private String fileExt;
    private File file;
    private String tableName;
    private CSVParser parser;

    public DataSet(String fileName, String tableName) {
        this.fileName = fileName;
        String[] splitName = fileName.split("\\.(?=[^.]+$)");
        if (splitName.length == 2) {
            this.fileExt = splitName[1].toLowerCase();
        }
        this.file = this.getFileFromResources();
        this.tableName = tableName;
        this.parser = null;
    }

    public boolean hasParser() { return this.parser != null; }

    public int getHeaderLength() {
        return this.parser.getHeaderMap().size();
    }

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

    public Iterator getIterator() {
        return this.parser.iterator();
    }

    public Map<String, Integer> getHeaderMap() { return this.parser.getHeaderMap(); }
}
