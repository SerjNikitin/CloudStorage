import lombok.extern.slf4j.Slf4j;
import server.Handler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class ServerApp {
    public static void main(String[] args) {
        try {
            ServerSocket socket = new ServerSocket(8080);
            System.out.println("Server is running");
            System.out.println("Waiting for connection");
            log.debug("Start");
            while (true) {
                Socket accept = socket.accept();
                System.out.println("Client connection");
                try {
                    new Thread(new Handler(accept)).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}