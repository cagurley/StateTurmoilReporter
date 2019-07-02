package com.cagurley;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

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
                ResultSet results = manager.getTables("%");
                while (results.next()) {
                    System.out.println(results.getString("TABLE_NAME"));
                }
                DataSet setOne = new DataSet("globalterrorismdb_0718dist.csv", "GLOBAL_TERRORISM");
                setOne.parse();
                manager.createTable(setOne);
//                Iterator iterator = setOne.getIterator();
//                while (iterator.hasNext()) {
//
//                }
//                System.out.println(parser.iterator().next().toString());
                manager.closeConnection();
                setOne.closeParser();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
