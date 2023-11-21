package org.astemir.sqlite;

import org.astemir.uniblend.misc.Pair;
import org.astemir.uniblend.UniblendCorePlugin;

import javax.annotation.Nullable;
import java.io.File;
import java.sql.*;
import java.util.*;

public class Database {
    private Connection connection;
    private String name;

    public Database(String databaseName) {
        this.name = databaseName;
    }

    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            String path = UniblendCorePlugin.getPlugin().getDataFolder().getAbsolutePath() + "/data";
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            connection = DriverManager.getConnection("jdbc:sqlite:" + path + "/" + name + ".db");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void createTable(boolean createIfNotExists, DatabaseField... fields){
        try {
            StringBuilder sql = new StringBuilder("CREATE TABLE ");
            if (createIfNotExists) {
                sql.append("IF NOT EXISTS ");
            }
            sql.append(name).append(" (");

            for (int i = 0; i < fields.length; i++) {
                DatabaseField field = fields[i];
                sql.append(field.toString());

                if (i != fields.length - 1) {
                    sql.append(",");
                }
            }
            sql.append(")");

            try (Statement statement = connection.createStatement()) {
                statement.execute(sql.toString());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public DataValues select(String whereKey, Object whereValue, String... keys) {
        try {
            StringBuilder sql = new StringBuilder("SELECT ");
            for (int i = 0; i < keys.length; i++) {
                sql.append(keys[i]);
                if (i != keys.length - 1) {
                    sql.append(",");
                }
            }
            sql.append(" FROM ").append(name);

            if (whereKey != null && whereValue != null) {
                sql.append(" WHERE ").append(whereKey).append(" = ?");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
                if (whereKey != null && whereValue != null) {
                    statement.setObject(1, whereValue);
                }
                try (ResultSet resultSet = statement.executeQuery()) {
                    DataValues result = new DataValues();
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    while (resultSet.next()) {
                        DataRow row = new DataRow();
                        for (int i = 1; i <= columnCount; i++) {
                            String columnName = metaData.getColumnName(i);
                            Object columnValue = resultSet.getObject(i);
                            row.add(columnName, columnValue);
                        }
                        result.addRow(row);
                    }
                    return result;
                }
            }
        }catch (Exception e){
            return new DataValues();
        }
    }

    public DataValues selectAll() {
        return select(null, null,"*");
    }

    public DataValues selectAll(String whereKey, Object whereValue) {
        return select(whereKey, whereValue, "*");
    }

    public void update(String whereKey, Object whereValue, Pair<String, Object>... pairs) {
        try {
            StringBuilder sql = new StringBuilder("UPDATE ").append(name).append(" SET ");
            for (int i = 0; i < pairs.length; i++) {
                sql.append(pairs[i].key).append(" = ?");
                if (i != pairs.length - 1) {
                    sql.append(",");
                }
            }
            sql.append(" WHERE ").append(whereKey).append(" = ?");

            try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
                int parameterIndex = 1;
                for (Pair<String, Object> pair : pairs) {
                    statement.setObject(parameterIndex++, pair.value);
                }
                statement.setObject(parameterIndex, whereValue);
                statement.executeUpdate();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void delete(String whereKey, Object whereValue) {
        try {
            String sql = "DELETE FROM " + name + " WHERE " + whereKey + " = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setObject(1, whereValue);
                statement.executeUpdate();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void insert(Pair<String, Object>... pairs) {
        try {
            StringBuilder sql = new StringBuilder("INSERT INTO ").append(name).append(" (");
            StringBuilder keys = new StringBuilder();
            StringBuilder values = new StringBuilder();

            for (int i = 0; i < pairs.length; i++) {
                keys.append(pairs[i].key);
                values.append("?");
                if (i != pairs.length - 1) {
                    keys.append(",");
                    values.append(",");
                }
            }

            sql.append(keys).append(") VALUES (").append(values).append(")");

            try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
                for (int i = 0; i < pairs.length; i++) {
                    statement.setObject(i + 1, pairs[i].value);
                }
                statement.executeUpdate();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            if (connection != null) {
                connection.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
