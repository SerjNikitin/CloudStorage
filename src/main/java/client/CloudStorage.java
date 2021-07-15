package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class CloudStorage implements Initializable {
    public ListView<String> client;
    public Label output;
    private Path path= Paths.get("saveFiles").toAbsolutePath();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Socket socket = new Socket("127.0.0.1", 8080);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            File file = new File(dirClient);
            client.getItems().addAll(file.list());
            Thread thread = new Thread(() -> {
                try {
                    while (true) {
                        String status = in.readUTF();
                        Platform.runLater(() -> output.setText(status));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.setDaemon(true);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(ActionEvent actionEvent) throws IOException {
        String fileName = client.getSelectionModel().getSelectedItem();
        Path file =Paths.get(
                path + "\\" + fileName);
        long size = file.length();
        out.writeUTF(fileName);
        out.writeLong(size);
        Files.copy(file.toPath(), out);
        output.setText("Файл: " + fileName + ", отправлен на сервер");
    }
}
//  public ListView<String> client;
//    public Label output;
//    private DataOutputStream out;
//    private DataInputStream in;
//    private final String dirClient = "D:\\учеба\\JAVA\\CloudStorage\\lesson1\\src\\main\\java\\client\\saveFiles";

// @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//        try {
//            Socket socket = new Socket("127.0.0.1", 8080);
//            in = new DataInputStream(socket.getInputStream());
//            out = new DataOutputStream(socket.getOutputStream());
//            File file = new File(dirClient);
//            client.getItems().addAll(file.list());
//            Thread thread = new Thread(() -> {
//                try {
//                    while (true) {
//                        String status = in.readUTF();
//                        Platform.runLater(() -> output.setText(status));
//                    }
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            });
//            thread.setDaemon(true);
//            thread.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void send(ActionEvent actionEvent) throws IOException {
//        String fileName = client.getSelectionModel().getSelectedItem();
//        File file = new File(
//                dirClient+"\\" + fileName);
//        long size = file.length();
//        out.writeUTF(fileName);
//        out.writeLong(size);
//        Files.copy(file.toPath(), out);
//        output.setText("Файл: " + fileName + ", отправлен на сервер");
//    }
//}