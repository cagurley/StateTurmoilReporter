package com.cagurley;

import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

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
        if (this.dbConn != null) {
            this.dbConn.setAutoCommit(false);
        } else {
            throw new SQLException("Connection failed.");
        }
    }

    public void closeConnection() throws SQLException {
        if (this.isConnected()) {
            this.dbConn.close();
            this.dbConn = null;
        }
    }

    public void refreshConnection() throws SQLException {
        this.closeConnection();
        this.connect();
    }

    /*v IMPORTANT: Must call this.refreshConnection() after completion of either of the metadata methods below to prevent database lockup v*/
    public ResultSet getColumns(String tableNamePattern, String columnNamePattern) throws SQLException {
        return this.dbConn.getMetaData().getColumns(null, null, tableNamePattern, columnNamePattern);
    }

    public ResultSet getTables(String tableNamePattern) throws SQLException {
        return this.dbConn.getMetaData().getTables(null, null, tableNamePattern, null);
    }
    /*^ IMPORTANT: Must call this.refreshConnection() after completion of either of the metadata methods below to prevent database lockup ^*/

    public SQLWarning getWarnings() throws SQLException {
        return this.dbConn.getWarnings();
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
            Statement stmt = this.dbConn.createStatement();
            stmt.executeUpdate(createString);
            this.dbConn.commit();
            System.out.println("Created table " + dataSet.getTableName() + " successfully.");
        }
    }

    private void dropDSTable(DataSet dataSet) throws SQLException {
        System.out.println("Dropping table...");
        String dropString = "DROP TABLE IF EXISTS " + dataSet.getTableName() + ";";
        Statement stmt = this.dbConn.createStatement();
        stmt.executeUpdate(dropString);
        this.dbConn.commit();
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
            this.refreshConnection();
            System.out.println("Inserting rows...");
            int insertValueCount = 0;
            int rowsInserted = 0;
            Statement stmt = this.dbConn.createStatement();
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
                stmt.addBatch(transactionString);
                insertValueCount += 1;
                rowsInserted += 1;
                if (insertValueCount == 1000) {
                    stmt.executeBatch();
                    stmt.clearBatch();
                    this.dbConn.commit();
                    System.out.println("Inserted " + rowsInserted + " rows into " + dataSet.getTableName() + ".");
                    insertValueCount = 0;
                }
            }
            stmt.executeBatch();
            stmt.clearBatch();
            this.dbConn.commit();
            System.out.println("Inserted a total of " + rowsInserted + " rows into " + dataSet.getTableName() + ".");
        }
    }

    /* Database Methods */
    public void createIndex(String tableName, String columnName) throws SQLException {
            if (this.getColumns(tableName, columnName).next()) {
                this.refreshConnection();
                System.out.println("Creating index...");
                String indexName = tableName + "_" + columnName;
                String indexString = ("CREATE INDEX IF NOT EXISTS " + indexName
                        + " ON " + tableName
                        + " (" + columnName + ")");
                Statement stmt = this.dbConn.createStatement();
                stmt.executeUpdate(indexString);
                this.dbConn.commit();
                System.out.println("Created index " + indexName + " successfully.");
            } else {
                this.refreshConnection();
                throw new IllegalArgumentException("Invalid table or column specified.");
            }
    }

    protected void initDerivedTable(String tableName, Iterable<String> columnNames, Iterable<String> insertQueries) throws SQLException {
        System.out.println("Creating derived table...");
        Statement stmt = this.dbConn.createStatement();
        stmt.executeUpdate("DROP TABLE IF EXISTS " + tableName + ";");
        dbConn.commit();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + tableName
                + " (["
                + String.join("] TEXT, [", columnNames)
                + "] TEXT);");
        dbConn.commit();
        for (String q : insertQueries) {
            stmt.executeUpdate("INSERT INTO " + tableName + "\n" + q);
            dbConn.commit();
        }
        System.out.println("Created derived table " + tableName + " successfully.");
    }

    public ResultSet executeQuery(String queryString) throws SQLException {
        QueryRSManager qRSM = new QueryRSManager();
        Statement stmt = this.dbConn.createStatement();
        ResultSet queryResults = stmt.executeQuery(queryString);
        return queryResults;
    }

    public void updateColumnValue(String tableName, String columnName, String searchPattern, String replacementPattern) throws SQLException {
        if (!tableName.matches("^\\w+$") || !columnName.matches("^\\w+$")) {
            throw new IllegalArgumentException("Enter valid table and column names (word characters only).");
        } else if (!this.getColumns(tableName, columnName).next()) {
            this.refreshConnection();
            throw new IllegalArgumentException("Table or column name does not exist.");
        } else {
            this.refreshConnection();
            String sql = "UPDATE " + tableName + " SET " + columnName + " = ? WHERE " + columnName + " LIKE ?";
            PreparedStatement ps = this.dbConn.prepareStatement(sql);
            ps.setString(1, replacementPattern);
            ps.setString(2, searchPattern);
            ps.executeUpdate();
            this.dbConn.commit();
            ps.close();
            System.out.println("Updated " + tableName + " values like '" + searchPattern + "' to value '" + replacementPattern + "'.");
        }
    }
}
