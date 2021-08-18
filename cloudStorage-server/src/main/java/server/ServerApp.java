package server;

import org.flywaydb.core.Flyway;

public class ServerApp {
    public static void main(String[] args) {
        Flyway flyway = Flyway.configure().dataSource(
                "jdbc:postgresql://localhost:5445/cloud_storage", "postgres", "postgrespass").load();
        flyway.migrate();
        Server server = new Server();
    }
}