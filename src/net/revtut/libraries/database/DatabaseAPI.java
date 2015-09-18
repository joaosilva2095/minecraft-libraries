package net.revtut.libraries.database;

import net.revtut.libraries.database.utils.DataType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Database API
 * Methods related to databases
 */
public class DatabaseAPI {

    /**
     * Create a table in a database
     * @param database database to be created the table
     * @param tableName name of the table to be created
     * @param columns columns of the database
     */
    public static void createTable(Database database, String tableName, Map<String, DataType> columns) {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (";
        int index = 1;
        for(Map.Entry<String, DataType> entry : columns.entrySet()) {
            sql += entry.getKey() + " " + entry.getValue();

            if(index < columns.size())
                sql += ", ";
            index++;
        }
        sql += ");";

        database.executeUpdate(sql, new ArrayList<>());
    }

    /**
     * Execute a update in a database
     * @param database database to be updated the table
     * @param tableName name of the table to be updated
     * @param columns columns to be updated
     * @param where columns of the "Where" condition
     * @param whereValues values of the where columns
     */
    public static ResultSet executeQuery(Database database, String tableName, List<String> columns, List<String> where, List<String> whereValues) {
        String sql = "SELECT ";

        // Columns to select
        int index = 1;
        for(String column : columns) {
            sql += column;

            if(index < columns.size())
                sql += ", ";
            index++;
        }
        sql += " FROM " + tableName + " WHERE ";

        // Where
        index = 1;
        for(String column : where) {
            sql += column + " = ?";

            if(index < where.size())
                sql += " AND ";
            index++;
        }
        sql += ";";

        List<Object> parameters = new ArrayList<>(whereValues);
        return database.executeQuery(sql, parameters);
    }

    /**
     * Execute a insertion into a database
     * @param database database to execute the insertion
     * @param tableName name of the table to execute the insertion
     * @param columns columns to set the values
     */
    public static void executeInsertion(Database database, String tableName, Map<String, Object> columns) {
        List<Object> parameters = new ArrayList<>();

        // Columns
        String sql = "INSERT INTO " + tableName + " (";
        int index = 1;
        for(Map.Entry<String, Object> entry : columns.entrySet()) {
            sql += entry.getKey();
            parameters.add(entry.getValue());

            if(index < columns.size())
                sql += ", ";
            index++;
        }
        sql += " VALUES (";

        // Values
        index = 1;
        for(Map.Entry<String, Object> ignored : columns.entrySet()) {
            sql += "?";

            if(index < columns.size())
                sql += ", ";
            index++;
        }
        sql += ");";

        database.executeUpdate(sql, parameters);
    }

    /**
     * Execute a update in a database
     * @param database database to be updated the table
     * @param tableName name of the table to be updated
     * @param columns columns to be updated
     * @param values values of the columns
     * @param where columns of the "Where" condition
     * @param whereValues values of the where columns
     */
    public static void executeUpdate(Database database, String tableName, List<String> columns, List<Object> values, List<String> where, List<String> whereValues) {
        String sql = "UPDATE " + tableName + " SET ";

        // Set
        int index = 1;
        for(String column : columns) {
            sql += column + " = ?";

            if(index < columns.size())
                sql += ", ";
            index++;
        }
        sql += " WHERE ";

        // Where
        index = 1;
        for(String column : where) {
            sql += column + " = ?";

            if(index < where.size())
                sql += " AND ";
            index++;
        }
        sql += ";";

        // Merge values and whereValues
        List<Object> parameters = new ArrayList<>(values);
        parameters.addAll(whereValues);
        database.executeUpdate(sql, parameters);
    }

    /**
     * Execute a update in a database
     * @param database database to be updated the table
     * @param tableName name of the table to be updated
     * @param where columns of the "Where" condition
     * @param whereValues values of the where columns
     */
    public static void executeDeletation(Database database, String tableName, List<String> where, List<String> whereValues) {
        String sql = "DELETE FROM " + tableName + " WHERE ";

        // Where
        int index = 1;
        for(String column : where) {
            sql += column + " = ?";

            if(index < where.size())
                sql += " AND ";
            index++;
        }
        sql += ";";

        List<Object> parameters = new ArrayList<>(whereValues);
        database.executeUpdate(sql, parameters);
    }
}
