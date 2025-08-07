package pl.edu.uws.lw89233.managers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public final class DatabaseManager {

    private static HikariDataSource dataSource;

    public DatabaseManager(String host, String port, String dbName, String user, String password) {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig();

            config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + dbName);
            config.setUsername(user);
            config.setPassword(password);

            config.setMaximumPoolSize(10);

            config.setMinimumIdle(5);

            config.setConnectionTimeout(30000);

            config.setIdleTimeout(600000);

            config.setMaxLifetime(1800000);

            dataSource = new HikariDataSource(config);
            System.out.println("Pula połączeń z bazą danych " + dbName + " została pomyślnie zainicjalizowana.");
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}