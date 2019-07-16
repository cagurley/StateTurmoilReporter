package com.cagurley;

import java.io.IOException;
import java.sql.SQLException;

public class App 
{
    public static void main( String[] args )
    {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        DBManager manager = new DBManager("database.db", "jdbc:sqlite");
        try {
            manager.connect();
            if (manager.isConnected()) {
                DataSet dataSet = new DataSet("globalterrorismdb_0718dist.csv", "GLOBAL_TERRORISM");
                dataSet.parse();
                manager.initDSTable(dataSet);
                dataSet.closeParser();
                dataSet = new DataSet("DPI2012.csv", "POLITICAL_INSTITUTIONS");
                dataSet.parse();
                manager.initDSTable(dataSet);
                dataSet.closeParser();
                manager.closeConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
