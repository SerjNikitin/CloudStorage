package dataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectorDB {

    public static Connection getConnect() {
        try {
            return DriverManager.getConnection("jdbc:postgresql://localhost:5445/cloud_storage",
                    "postgres",
                    "postgrespass");
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
}