package com.cagurley;

import org.apache.commons.csv.CSVRecord;

import java.sql.*;
import java.util.ArrayList;

public class DBManager {
    private String dbFileName;
    private String dbDriverName;
    private String dbFullName;
    private Connection dbConn;
    private Statement dbStatement;

    public DBManager(String dbFileName, String dbDriverName) {
        this.dbFileName = dbFileName;
        this.dbDriverName = dbDriverName;
        this.dbFullName = (String.join(":", this.dbDriverName, this.getResourceString()));
        this.dbConn = null;
        this.dbStatement = null;
    }

    /* Database File Methods*/
    public boolean databaseExists() {
        if (this.getClass().getClassLoader().getResource(this.dbFileName) != null) {
            return true;
        } else {
            return false;
        }
    }

    private String getResourceString() {
        return (this.getClass().getClassLoader().getResource(".")
                    .toString().replace("file:/", "")
                + this.dbFileName);
    }

    /* Connection Methods */
    public boolean isConnected() { return this.dbConn != null; }

    public void connect() throws SQLException {
        this.dbConn = DriverManager.getConnection(dbFullName);
        this.dbConn.setAutoCommit(false);
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

    public ResultSet getColumns(String tableNamePattern, String columnNamePattern) throws SQLException {
        return this.dbConn.getMetaData().getColumns(null, null, tableNamePattern, columnNamePattern);
    }

    public ResultSet getTables(String tableNamePattern) throws SQLException {
        return this.dbConn.getMetaData().getTables(null, null, tableNamePattern, null);
    }

    public SQLWarning getWarnings() throws SQLException {
        return this.dbConn.getWarnings();
    }

    /* Statement Methods */
    public void addBatch(String statementString) throws SQLException {
        this.dbStatement.addBatch(statementString);
    }

    public void closeStatement() throws SQLException {
        this.dbStatement.close();
        this.dbStatement = null;
    }

    private void createStatement() throws SQLException {
        this.dbStatement = this.dbConn.createStatement();
    }

    private void executeStatement(String statementString) throws SQLException {
        this.dbStatement.execute(statementString);
        this.dbConn.commit();
        this.closeStatement();
    }

    private void executeBatch() throws SQLException {
        this.dbStatement.executeBatch();
        this.dbConn.commit();
        this.dbStatement.clearBatch();
    }

    /* DataSet Table Methods */
    private void createDSTable(DataSet dataSet) throws SQLException {
        if (dataSet.hasParser()) {
            System.out.println("Creating table...");
            String[] columns = dataSet.getHeaderKeySet().toArray(new String[0]);
            for (int i = 0; i < columns.length; i++) {
                if (columns[i].matches("^.*\\W.*$")) {
                    columns[i] = columns[i].replaceAll("\\W", "_");
                }
            }
            String createString = (
                    "CREATE TABLE IF NOT EXISTS " + dataSet.getTableName()
                    + " (["
                    + String.join("] TEXT, [", columns)
                    + "] TEXT);"
            );
            this.createStatement();
            this.executeStatement(createString);
            System.out.println("Created table " + dataSet.getTableName() + " successfully.");
        }
    }

    private void dropDSTable(DataSet dataSet) throws SQLException {
        System.out.println("Dropping table...");
        String dropString = "DROP TABLE IF EXISTS " + dataSet.getTableName() + ";";
        this.createStatement();
        this.executeStatement(dropString);
        System.out.println("Dropped table " + dataSet.getTableName() + " successfully.");
    }

    public void initDSTable(DataSet dataSet) throws SQLException {
        this.dropDSTable(dataSet);
        this.createDSTable(dataSet);
        this.insertDS(dataSet);
    }

    private void insertDS(DataSet dataSet) throws SQLException {
        ResultSet table = this.getTables(dataSet.getTableName());
        ResultSet columns = this.getColumns(dataSet.getTableName(), "%");
        int columnNum = 0;
        while (columns.next()) {
            columnNum += 1;
        }
        if (table.next()
                && columnNum == dataSet.getHeaderLength()
                && dataSet.getParser() != null) {
            System.out.println("Inserting rows...");
            int insertValueCount = 0;
            int rowsInserted = 0;
            this.createStatement();
            for (CSVRecord record : dataSet.getParser()) {
                String transactionString = "INSERT INTO " + dataSet.getTableName() + " VALUES";
                transactionString += "(";
                ArrayList<String> values = new ArrayList(columnNum);
                for (String field : record) {
                    if (field.length() > 0) {
                        field = field.replace("\"", "&quot;");
                        values.add("\"" + field + "\"");
                    } else {
                        values.add("NULL");
                    }
                }
                String valueString = String.join(",", values);
                transactionString += valueString + ");";
                this.addBatch(transactionString);
                insertValueCount += 1;
                rowsInserted += 1;
                if (insertValueCount == 1000) {
                    this.executeBatch();
                    System.out.println("Inserted " + rowsInserted + " rows into " + dataSet.getTableName() + ".");
                    insertValueCount = 0;
                }
            }
            this.executeBatch();
            this.closeStatement();
            System.out.println("Inserted a total of " + rowsInserted + " rows into " + dataSet.getTableName() + ".");
        }
    }

    /* Table Update Methods */
    public void updateColumnValue(String tableName, String columnName, String searchPattern, String replacementPattern) throws SQLException {
        if (!tableName.matches("^\\w+$") || !columnName.matches("^\\w+$")) {
            throw new IllegalArgumentException("Enter valid table and column names (word characters only).");
        } else if (!this.getColumns(tableName, columnName).next()) {
            throw new IllegalArgumentException("Table or column name does not exist.");
        } else {
            String sql = "UPDATE " + tableName + " SET " + columnName + " = ? WHERE " + columnName + " LIKE ?";
            PreparedStatement ps = this.dbConn.prepareStatement(sql);
            ps.setString(1, replacementPattern);
            ps.setString(2, searchPattern);
            ps.executeUpdate();
            this.dbConn.commit();
            System.out.println("Updated " + tableName + " values like '" + searchPattern + "' to value '" + replacementPattern + "'.");
        }
    }
}
