package client;


import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import server.*;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CloudStorage implements Initializable {
    public ListView<String> client;
    public Label output;
    public ListView<String> server;
    public TextField clientPath;
    public TextField serverPath;
    private Path clientDir;
    private ObjectEncoderOutputStream out;
    private ObjectDecoderInputStream in;

    public void send(ActionEvent actionEvent) throws IOException {
        String fileName = client.getSelectionModel().getSelectedItem();
        FileMassage message = new FileMassage(clientDir.resolve(fileName));
        out.writeObject(message);
        output.setText("Файл: " + fileName + ", отправлен на сервер");
        out.flush();
    }

    public void download(ActionEvent actionEvent) throws IOException {
        String fileName = server.getSelectionModel().getSelectedItem();
        out.writeObject(new FileRequest(fileName));
        output.setText("Файл: " + fileName + ", загружен");
        out.flush();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            clientDir = Paths.get("clientDir").toAbsolutePath();
            Socket socket = new Socket("localhost", 8080);
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
            in = new ObjectDecoderInputStream(socket.getInputStream());

            refreshClientView();
            addNavigationListeners();

            Thread readThread = new Thread(() -> {
                try {
                    while (true) {
                        AbstractCommand command = (AbstractCommand) in.readObject();
                        switch (command.getType()) {
                            case LIST_MESSAGE:
                                ListResponse response = (ListResponse) command;
                                List<String> names = response.getName();
                                refreshServerView(names);
                                break;
                            case PATH_RESPONSE:
                                PathUpResponse pathResponse = (PathUpResponse) command;
                                String path = pathResponse.getPath();
                                Platform.runLater(() -> serverPath.setText(path));
                                break;
                            case FILE_MESSAGE:
                                FileMassage message = (FileMassage) command;
                                Files.write(clientDir.resolve(message.getName()), message.getArr());
                                refreshClientView();
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            readThread.setDaemon(true);
            readThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addNavigationListeners() {

        client.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String item = client.getSelectionModel().getSelectedItem();
                Path newPath = clientDir.resolve(item);
                if (Files.isDirectory(newPath)) {
                    clientDir = newPath;
                    try {
                        refreshClientView();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });

        server.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String item = server.getSelectionModel().getSelectedItem();
                try {
                    out.writeObject(new PathInRequest(item));
                    out.flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }

    private void refreshServerView(List<String> name) {
        Platform.runLater(() -> {
            server.getItems().clear();
            server.getItems().addAll(name);
        });
    }

    private void refreshClientView() throws IOException {
        clientPath.setText(clientDir.toString());
        List<String> name = Files.list(clientDir)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
        Platform.runLater(() -> {
            client.getItems().clear();
            client.getItems().addAll(name);
        });
    }

    public void clientPathUp(ActionEvent actionEvent) throws IOException {
        clientDir = clientDir.getParent();
        clientPath.setText(clientDir.toString());
        refreshClientView();
    }

    public void serverPathUp(ActionEvent actionEvent) throws IOException {
        out.writeObject(new PathUpRequest());
        out.flush();
    }
}