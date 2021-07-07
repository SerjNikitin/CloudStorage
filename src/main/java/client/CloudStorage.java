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
import java.util.ResourceBundle;

public class CloudStorage implements Initializable {
    public ListView<String> client;
    public Label output;
    private DataOutputStream out;
    private DataInputStream in;
    private final String dirClient = "D:\\учеба\\JAVA\\CloudStorage\\lesson1\\src\\main\\java\\client\\saveFiles";

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
        File file = new File(
                dirClient+"\\" + fileName);
        long size = file.length();
        out.writeUTF(fileName);
        out.writeLong(size);
        Files.copy(file.toPath(), out);
        output.setText("Файл: " + fileName + ", отправлен на сервер");
    }
}

//    public void outputFile() {
//        File file = new File("D:\\учеба\\JAVA\\client.CloudStorage\\client.CloudStorage\\src\\main\\java\\client\\saveFiles\\photo.jpg");
//        try {
//            FileInputStream fis = new FileInputStream(file);
//                int readCheck = 0;
//                try {
//                    while ((readCheck = fis.read(buffer)) != -1) {
//                        out.write(buffer, 0, readCheck);
//                        out.flush();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    System.out.println("Server down...");
//                    System.out.println("Connection closed.");
//                }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public void inputFile() {
//        try {
//            FileOutputStream fos = new FileOutputStream("адрес файла куда скачивать");
//            int readCheck = 0;
//            try {
//                while ((readCheck = in.read(buffer)) != -1) {
//                    fos.write(buffer, 0, readCheck);
//                    fos.flush();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
////        System.out.println("Incoming message chanel closed.");
////        System.out.println("Please double press ENTER...");
//    }
//}