package client;


import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import server.FileMassage;
import server.FileRequest;


import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class CloudStorage implements Initializable {
    public ListView<String> client;
    public Label output;
    public ListView<String> server;
    //    private Path path = Paths.get("saveFiles").toAbsolutePath();
    private ObjectEncoderOutputStream out;
    private ObjectDecoderInputStream in;

    public void send(ActionEvent actionEvent) throws IOException {
        String fileName = client.getSelectionModel().getSelectedItem();
        out.writeObject(new FileMassage(Paths.get("D:\\учеба\\JAVA\\CloudStorage\\lesson1\\src\\main\\java\\client\\saveFiles" + "\\" + fileName)));
        output.setText("Файл: " + fileName + ", отправлен на сервер");
        out.flush();

    }

    public void download(ActionEvent actionEvent) throws IOException {
        String fileName = server.getSelectionModel().getSelectedItem();
        out.writeObject(new FileRequest(Paths.get("D:\\учеба\\JAVA\\CloudStorage\\lesson1\\src\\main\\java\\server\\saveFiles" + "\\" + fileName)));
        output.setText("Файл: " + fileName + ", загружен");
        out.flush();


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Socket socket = new Socket("localhost", 8080);
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
            in = new ObjectDecoderInputStream(socket.getInputStream());
            File fileClient = new File("D:\\учеба\\JAVA\\CloudStorage\\lesson1\\src\\main\\java\\client\\saveFiles");
            File fileServer = new File("D:\\учеба\\JAVA\\CloudStorage\\lesson1\\src\\main\\java\\server\\saveFiles");
            client.getItems().addAll(fileClient.list());
            server.getItems().addAll(fileServer.list());

            Thread thread = new Thread(() -> {
//                try {
//                    while (true) {
////                        Object object = in.readObject();
////                        out.writeObject();
////                       на уроке
////                        Massage status = (Massage) in.readObject();
////                        Platform.runLater(() -> output.setText(object.toString()));
//                    }
//
//                } catch (IOException | ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
            });
            thread.setDaemon(true);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
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