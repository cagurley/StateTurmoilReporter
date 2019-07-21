package com.cagurley;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class QueryRSManager {
    private String outDir;

    public QueryRSManager() {
        this.outDir = (this.getClass().getClassLoader()
                .getResource(".").toString().replace("file:/", "")
                .replaceFirst("(.*)/.+?/.+?/$", "$1/out/"));
    }

    private ArrayList<String> getHeaderData(ResultSet queryRS) throws SQLException {
        ResultSetMetaData meta = queryRS.getMetaData();
        ArrayList<String> headerData = new ArrayList<>();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            headerData.add(meta.getColumnLabel(i));
        }
        return headerData;
    }

    /* Render Methods */
    public void renderCSV(ResultSet queryRS, String fileName) throws IOException, SQLException {
        System.out.println("Rendering results to CSV...");
        File outDir = new File(this.outDir);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        File outFile = new File(outDir.toString() + File.separator + fileName + ".csv");
        CSVPrinter printer = new CSVPrinter(new FileWriter(outFile), CSVFormat.DEFAULT);
        printer.printRecord(this.getHeaderData(queryRS));
        printer.printRecords(queryRS);
        printer.close();
        System.out.println("Results rendered to " + outFile.toString() + ".");
    }

    public void renderJSON(ResultSet queryRS, String fileName) {}

    public void renderSOUT(ResultSet queryRS) throws SQLException {
        System.out.println("====================");
        System.out.println("| " + String.join(" | ", this.getHeaderData(queryRS)) + " |");
        System.out.println("====================");
        while (queryRS.next()) {
            ArrayList<String> rowData = new ArrayList<>();
            for (int i = 1; i <= queryRS.getMetaData().getColumnCount(); i++) {
                rowData.add(queryRS.getString(i));
            }
            System.out.println("| " + String.join(" | ", rowData) + " |");
        }
        System.out.println("====================");
    }
}
