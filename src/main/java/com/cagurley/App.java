package com.cagurley;

import org.apache.commons.csv.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class App 
{
    public static void main( String[] args )
    {
        DataSet setOne = new DataSet("globalterrorismdb_0718dist.csv");
        try {
            CSVParser parser = CSVParser.parse(setOne.getFile(), StandardCharsets.UTF_8, CSVFormat.DEFAULT.withHeader());
            System.out.println(parser.iterator().next().toString());
            parser.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        DBManager manager = new DBManager("database.db", "jdbc:sqlite");
        manager.connect();
    }
}
