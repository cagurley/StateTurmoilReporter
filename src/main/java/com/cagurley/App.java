package com.cagurley;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLWarning;

public class App 
{
    public static void main( String[] args )
    {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        DBManager dbManager = new DBManager("database.db", "jdbc:sqlite");
        try {
            InputManager iManager = new InputManager();
            boolean initDB = false;
            if (dbManager.databaseExists()) {
                iManager.storePrompt("Database already exists. Reinitialize delivered tables? (y/N)", "initTables");
                initDB = iManager.popEvaluate("initTables", iManager.yRegex);
            } else {
                initDB = true;
            }
            dbManager.connect();
            if (dbManager.isConnected()) {
                if (initDB) {
                    System.out.println("Database initializing...");
                    DataSet dataSet = new DataSet("globalterrorismdb_0718dist.csv", "GLOBAL_TERRORISM");
                    dataSet.parse();
                    dbManager.initDSTable(dataSet);
                    dataSet.closeParser();
//                    SQLWarning warn = dbManager.getWarnings();
//                    while (warn != null) {
//                        System.out.println(warn);
//                        warn.getNextWarning();
//                    }
                    dataSet = new DataSet("DPI2012.csv", "POLITICAL_INSTITUTIONS");
                    dataSet.parse();
                    dbManager.initDSTable(dataSet);
                    dataSet.closeParser();

                    dbManager.updateColumnValue("POLITICAL_INSTITUTIONS", "countryname", "USA", "United States");
                }
//                SQLWarning warn = dbManager.getWarnings();
//                while (warn != null) {
//                    System.out.println(warn);
//                    warn.getNextWarning();
//                }
                dbManager.closeConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
