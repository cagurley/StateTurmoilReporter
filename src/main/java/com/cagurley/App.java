package com.cagurley;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class App 
{
    public static void main( String[] args )
    {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        final DBManager dbManager = new DBManager("database.db", "jdbc:sqlite");
        try {
            final InputManager iManager = new InputManager();
            boolean initDB;
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
                    // Initializing first table
                    DataSet dataSet = new DataSet("globalterrorismdb_0718dist.csv", "GLOBAL_TERRORISM");
                    dataSet.parse();
                    dbManager.initDSTable(dataSet);
                    dataSet.closeParser();
                    // Initializing second table
                    dataSet = new DataSet("DPI2012.csv", "POLITICAL_INSTITUTIONS");
                    dataSet.parse();
                    dbManager.initDSTable(dataSet);
                    dataSet.closeParser();
                    // Initializing third table
                    dataSet = new DataSet("cpi_coerced.csv", "CORRUPTION_PERCEPTIONS_INDEX");
                    dataSet.parse();
                    dbManager.initDSTable(dataSet);
                    dataSet.closeParser();

                    // Updating column values to enable proper query joins
                    dbManager.updateColumnValue("POLITICAL_INSTITUTIONS", "countryname",
                            "Bosnia-Herz", "Bosnia-Herzegovina");
                    dbManager.updateColumnValue("POLITICAL_INSTITUTIONS", "countryname",
                            "Cent. Af. Rep.", "Central African Republic");
                    dbManager.updateColumnValue("POLITICAL_INSTITUTIONS", "countryname",
                            "PRC", "China");
                    dbManager.updateColumnValue("POLITICAL_INSTITUTIONS", "countryname",
                            "Comoro Is.", "Comoros");
                    dbManager.updateColumnValue("POLITICAL_INSTITUTIONS", "countryname",
                            "Czech Rep.", "Czech Republic");
                    dbManager.updateColumnValue("POLITICAL_INSTITUTIONS", "countryname",
                            "Congo (DRC)", "Democratic Republic of the Congo");
                    dbManager.updateColumnValue("POLITICAL_INSTITUTIONS", "countryname",
                            "Dom. Rep.", "Dominican Republic");
                    dbManager.updateColumnValue("POLITICAL_INSTITUTIONS", "countryname",
                            "GDR", "East Germany (GDR)");
                    dbManager.updateColumnValue("POLITICAL_INSTITUTIONS", "countryname",
                            "Timor-Leste", "East Timor");
                    dbManager.updateColumnValue("POLITICAL_INSTITUTIONS", "countryname",
                            "Eq. Guinea", "Equatorial Guinea");
                    dbManager.updateColumnValue("POLITICAL_INSTITUTIONS", "countryname",
                            "Cote d'Ivoire", "Ivory Coast");
                    dbManager.updateColumnValue("POLITICAL_INSTITUTIONS", "countryname",
                            "PRK", "North Korea");
                    dbManager.updateColumnValue("POLITICAL_INSTITUTIONS", "countryname",
                            "Yemen (AR)", "North Yemen");
                    dbManager.updateColumnValue("POLITICAL_INSTITUTIONS", "countryname",
                            "P. N. Guinea", "Papua New Guinea");
                    dbManager.updateColumnValue("POLITICAL_INSTITUTIONS", "countryname",
                            "Congo", "Republic of the Congo");
                    dbManager.updateColumnValue("POLITICAL_INSTITUTIONS", "countryname",
                            "Slovakia", "Slovak Republic");
                    dbManager.updateColumnValue("POLITICAL_INSTITUTIONS", "countryname",
                            "Solomon Is.", "Solomon Islands");
                    dbManager.updateColumnValue("POLITICAL_INSTITUTIONS", "countryname",
                            "S. Africa", "South Africa");
                    dbManager.updateColumnValue("POLITICAL_INSTITUTIONS", "countryname",
                            "ROK", "South Korea");
                    dbManager.updateColumnValue("POLITICAL_INSTITUTIONS", "countryname",
                            "Yemen (PDR)", "South Yemen");
                    dbManager.updateColumnValue("POLITICAL_INSTITUTIONS", "countryname",
                            "Trinidad-Tobago", "Trinidad and Tobago");
                    dbManager.updateColumnValue("POLITICAL_INSTITUTIONS", "countryname",
                            "UAE", "United Arab Emirates");
                    dbManager.updateColumnValue("POLITICAL_INSTITUTIONS", "countryname",
                            "UK", "United Kingdom");
                    dbManager.updateColumnValue("POLITICAL_INSTITUTIONS", "countryname",
                            "USA", "United States");
                    dbManager.updateColumnValue("POLITICAL_INSTITUTIONS", "countryname",
                            "FRG/Germany", "West Germany (FRG)");
                    dbManager.updateColumnValue("POLITICAL_INSTITUTIONS", "countryname",
                            "C. Verde Is.", "Cape Verde");
                    dbManager.updateColumnValue("CORRUPTION_PERCEPTIONS_INDEX", "country",
                            "Bosnia and Herzegovina", "Bosnia-Herzegovina");
                    dbManager.updateColumnValue("CORRUPTION_PERCEPTIONS_INDEX", "country",
                            "Brunei Darussalam", "Brunei");
                    dbManager.updateColumnValue("CORRUPTION_PERCEPTIONS_INDEX", "country",
                            "Cabo Verde", "Cape Verde");
                    dbManager.updateColumnValue("CORRUPTION_PERCEPTIONS_INDEX", "country",
                            "Timor-Leste", "East Timor");
                    dbManager.updateColumnValue("CORRUPTION_PERCEPTIONS_INDEX", "country",
                            "Guinea Bissau", "Guinea-Bissau");
                    dbManager.updateColumnValue("CORRUPTION_PERCEPTIONS_INDEX", "country",
                            "Cote d'Ivoire", "Ivory Coast");
                    dbManager.updateColumnValue("CORRUPTION_PERCEPTIONS_INDEX", "country",
                            "Korea, North", "North Korea");
                    dbManager.updateColumnValue("CORRUPTION_PERCEPTIONS_INDEX", "country",
                            "Congo", "Republic of the Congo");
                    dbManager.updateColumnValue("CORRUPTION_PERCEPTIONS_INDEX", "country",
                            "Slovakia", "Slovak Republic");
                    dbManager.updateColumnValue("CORRUPTION_PERCEPTIONS_INDEX", "country",
                            "Korea, South", "South Korea");
                    dbManager.updateColumnValue("CORRUPTION_PERCEPTIONS_INDEX", "country",
                            "Saint Lucia", "St. Lucia");
                    dbManager.updateColumnValue("CORRUPTION_PERCEPTIONS_INDEX", "country",
                            "United States of America", "United States");
                    dbManager.updateColumnValue("CORRUPTION_PERCEPTIONS_INDEX", "country",
                            "Saint Vincent and the Grenadines", "St. Vincent and the Grenadines");
                    // Creating indices on joinable columns
                    dbManager.createIndex("GLOBAL_TERRORISM", "country_txt");
                    dbManager.createIndex("POLITICAL_INSTITUTIONS", "countryname");
                    dbManager.createIndex("CORRUPTION_PERCEPTIONS_INDEX", "country");

                    System.out.println("Database initiated.");
                }
                /* Main Menu */
                System.out.println("\nBooting to menu in three seconds.");
                Thread.sleep(3000);
                clearScreen();
                dbManager.executeQuery(("SELECT \"table\" as \"Table\", value as \"Country Value\""
                        + "\nFROM ("
                        + "\n  SELECT DISTINCT 'GT' AS \"table\", country_txt AS \"value\""
                        + "\n  FROM GLOBAL_TERRORISM"
                        + "\n  UNION ALL"
                        + "\n  SELECT DISTINCT 'PI', countryname"
                        + "\n  FROM POLITICAL_INSTITUTIONS"
                        + "\n  UNION ALL"
                        + "\n  SELECT DISTINCT 'CPI', country"
                        + "\n  FROM CORRUPTION_PERCEPTIONS_INDEX"
                        + "\n)"
                        + "\nGROUP BY value"
                        + "\nHAVING COUNT(value) < 3"
                        + "\nORDER BY 1, 2"), "CSV", "country_values");
                System.out.println("Welcome to the State Turmoil Reporter,"
                        + " a queryable database of aggregate terrorism and political institution data.\n");
                while (true) {
                    iManager.storePrompt(("What would you like to do? Press the key corresponding to a selection below.\n"
                            + "\n1. View available tables and indices"
                            + "\n2. View available table columns"
                            + "\n3. Browse predefined queries"
                            + "\n4. Execute custom query"
                            + "\n\nE. Exit State Turmoil Reporter"
                            + "\n"), "mainSelection");
                    if (iManager.evaluate("mainSelection", "^[1-4].*$")) {
                        QueryRSManager qRSM = new QueryRSManager();
                        switch (iManager.popInput("mainSelection").charAt(0)) {
                            case '1':
                                clearScreen();
                                qRSM.renderSOUT(dbManager.getTables("%"));
                                dbManager.refreshConnection();
                                iManager.waitForInput();
                                break;
                            case '2':
                                clearScreen();
                                /* Sub Menu */
                                while (true) {
                                    ResultSet gt = dbManager.getTables("%");
                                    ArrayList<String> tableNames = new ArrayList<>();
                                    while (gt.next()) {
                                        if (gt.getString("TABLE_TYPE").equals("TABLE")) {
                                            tableNames.add(gt.getString("TABLE_NAME"));
                                        }
                                    }
                                    String tablePrompt = "VIEW AVAILABLE TABLE COLUMNS\n"
                                            + "\nPress the key corresponding to a table below to see its columns.\n";
                                    for (int i = 0; i < tableNames.size(); i++) {
                                        tablePrompt += "\n" + (i + 1) + ". " + tableNames.get(i);
                                    }
                                    tablePrompt += "\n\nR. Return to main menu";
                                    iManager.storePrompt(tablePrompt, "tableSelection");
                                    String tSel = iManager.getInput("tableSelection").replaceFirst("^(\\d+).*$", "$1");
                                    if (iManager.evaluate("tableSelection", "^\\d+.*$")
                                            && Integer.parseInt(tSel) >= 1 && Integer.parseInt(tSel) < tableNames.size() + 1) {
                                        qRSM.renderSOUT(dbManager.getColumns(tableNames.get(Integer.parseInt(tSel) - 1), "%"));
                                        dbManager.refreshConnection();
                                        iManager.waitForInput();
                                    } else if (iManager.evaluate("tableSelection", "^[rR].*$")) {
                                        clearScreen();
                                        break;
                                    } else {
                                        System.out.println("Sorry, invalid selection; try again.\n");
                                    }
                                }
                                break;
                            case '3':
                                clearScreen();
                                iManager.waitForInput();
                                break;
                            case '4':
                                break;
                        }
                    } else if (iManager.evaluate("mainSelection", "^[eE].*$")) {
                        break;
                    } else {
                        System.out.println("Sorry, invalid selection; try again.\n");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                dbManager.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void clearScreen() throws IOException, InterruptedException {
        if (System.getProperty("os.name").contains("Windows")) {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } else {
            Runtime.getRuntime().exec("clear");
        }
    }
}
