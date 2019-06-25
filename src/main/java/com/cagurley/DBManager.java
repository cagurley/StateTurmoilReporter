package com.cagurley;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
    private String dbFileName;
    private String dbDriverName;
    private String dbFullName;

    public DBManager(String dbFileName, String dbDriverName) {
        this.dbFileName = dbFileName;
        this.dbDriverName = dbDriverName;
        this.dbFullName = (this.dbDriverName + ":" + this.getResourceString());
    }

    private String getResourceString() {
        return this.getClass().getClassLoader().getResource(this.dbFileName).toString();
    }

    public void connect() {
        Connection conn = null;
        try {
            // db parameters
            // String url = "jdbc:sqlite:C:/sqlite/db/chinook.db";
            // create a connection to the database
            conn = DriverManager.getConnection(dbFullName);

            System.out.println("Connection has been established.");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
