package me.boykev.kingdom;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDatabase {
    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private final int port;

    private Connection connection;

    public MySQLDatabase(String host, String database, String username, String password, int port) {
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
        this.port = port;

        this.connection = null;
    }

    public boolean connect() {
        try {
            String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database;
            connection = DriverManager.getConnection(jdbcUrl, username, password);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
