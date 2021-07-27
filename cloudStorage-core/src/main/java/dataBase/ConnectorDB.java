package dataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectorDB {

    public static Connection getConnect() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/cloud_storage",
                    "root",
                    "root");
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
}