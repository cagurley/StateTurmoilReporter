State Turmoil Reporter
======

## Background

In this current period of very visible strife in nations across the globe, I was curious to see the ways in which symptoms of strife might show patterns or trends over time. Given that terrorism, political institutions, and corruption are three of the predominant sources, I sought data on these three phenomena to see what could be gleaned relative to individual political states over time.

## Methodology

The data utilized are a combination of three separate sources that have been coalesced into a SQLite database for CRUD operations necessary for comparison. The sources and data used are as follows:

+ [University of Maryland's Global Terrorism Database](https://www.start.umd.edu/gtd/using-gtd): A database attempting to catalog all incidents of terrorism. (Actually downloaded from [Kaggle](https://www.kaggle.com/START-UMD/gtd/downloads/gtd.zip/3).) This data was used as is for the `GLOBAL_TERRORISM` table.
+ [The World Bank's Database of Political Institutions](https://datacatalog.worldbank.org/dataset/wps2283-database-political-institutions): A database detailing political institutions and governance in the modern era. This data was used as is for the `POLITICAL_INSTITUTIONS` table.
+ [Transparency International's Corruption Perceptions Index data](https://www.transparency.org/permissions): Data describing Transparency International's Corruption Perceptions Index, a measure of public perspectives on state corruption. This data was transformed from two separate Transparency International source files due to inconsistent and inappropriate formats and used in this modified form for the `CORRUPTION_PERCEPTIONS_INDEX` table. (The original files, an accompanying analytical file, and my working file used to generate the modified source can be found in the `misc` directory at the project root.)

Each of sources were used to populate SQLite tables, which were then updated to ensure consistency among country name designations. These country name columns were then indexed (as this is the primary join column intended), and a derived table consisting of a single index column containing all unique country name values across all tables was created to enable ease when querying.

The application allows viewing of system table and table column meta data, pre-loaded queries that show interesting fusions of the data, and custom queries as written by the user. Data can be output to file as CSV or JSON or flushed to standard output. Output files are stored in the `/out/` directory in the project root for organization and access.

## Instructions on Running

To retrieve project files necessary to build this project, you will need git with [Large File Support (LFS)](https://git-lfs.github.com/) installed/enabled. Run `git clone https://https://github.com/cagurley/StateTurmoilReporter.git` in your selected parent directory to download all relevant files, including the large source data files through LFS. Since, this project uses Java's JDK version 12.0.1 and Maven for dependency management, both should be downloaded next at https://www.oracle.com/technetwork/java/javase/downloads/jdk12-downloads-5295953.html and https://maven.apache.org/download.cgi, respectively. Afterwards, open your favorite Java IDE, register the JDK, and run the `main` method in `src/main/java/com.cagurley/App.java`.

### Thanks for reviewing my project! Please let me know where I can most improve.

###### Project &copy; Colton Atticus Gurley
