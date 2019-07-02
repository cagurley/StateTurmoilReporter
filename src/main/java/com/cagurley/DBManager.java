package com.cagurley;

import java.sql.*;

public class DBManager {
    private String dbFileName;
    private String dbDriverName;
    private String dbFullName;
    private Connection dbConn;

    public DBManager(String dbFileName, String dbDriverName) {
        this.dbFileName = dbFileName;
        this.dbDriverName = dbDriverName;
        this.dbFullName = (String.join(":", this.dbDriverName, this.getResourceString()));
        this.dbConn = null;
    }

    public boolean isConnected() { return this.dbConn != null; }

    private String getResourceString() {
        return (this.getClass().getClassLoader().getResource(".")
                    .toString().replace("file:/", "")
                + this.dbFileName);
    }

    public void connect() throws SQLException {
        this.dbConn = DriverManager.getConnection(dbFullName);
        if (this.dbConn != null) {
            System.out.println("Connection has been established.");
        } else {
            System.out.println("Connection failed.");
        }
    }

    public void closeConnection() throws SQLException {
        if (this.dbConn != null) {
            this.dbConn.close();
            this.dbConn = null;
        }
    }

    public void createTable(DataSet dataSet) throws SQLException {
        if (dataSet.hasParser()) {
            String createString = (
                    "CREATE TABLE IF NOT EXISTS " + dataSet.getTableName()
                    + " ("
                    + (String.join(" TEXT, ", dataSet.getHeaderMap().keySet()) + " TEXT")
                    + ");"
            );
            this.dbConn.createStatement().execute(createString);
        }
    }

    public ResultSet getTables(String tableNamePattern) throws SQLException {
        return this.dbConn.getMetaData().getTables(null, null, tableNamePattern, null);
    }
}
