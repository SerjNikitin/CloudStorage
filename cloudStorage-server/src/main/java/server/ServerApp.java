package server;

import org.flywaydb.core.Flyway;

public class ServerApp {
    public static void main(String[] args) {
        Flyway flyway = Flyway.configure().dataSource(
                "jdbc:mysql://localhost:3306/cloud_storage", "root", "root").load();

        flyway.migrate();
        Server server = new Server();
    }
}