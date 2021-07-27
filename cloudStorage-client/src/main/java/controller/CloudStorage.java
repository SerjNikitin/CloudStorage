package controller;


import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import model.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Slf4j
public class CloudStorage implements Initializable {
    public ListView<String> client;
    //    public Label output;
    public ListView<String> server;
    public TextField clientPath;
    public TextField serverPath;
    private Path clientDir;
    private ObjectEncoderOutputStream out;
    private ObjectDecoderInputStream in;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
//            String userDir=System.getProperty("user.name");
            clientDir = Paths.get("./").toAbsolutePath();
            log.info("Current user: {}", System.getProperty("user.name"));
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

    public void send(ActionEvent actionEvent) throws IOException {
        String fileName = client.getSelectionModel().getSelectedItem();
        FileMassage message = new FileMassage(clientDir.resolve(fileName));
        out.writeObject(message);
        out.flush();
    }

    public void download(ActionEvent actionEvent) throws IOException {
        String fileName = server.getSelectionModel().getSelectedItem();
        out.writeObject(new FileRequest(fileName));
//        output.setText("Файл: " + fileName + ", загружен");
        out.flush();
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

    public void deleteClientFile(ActionEvent actionEvent) throws IOException {
        String clientItem = client.getSelectionModel().getSelectedItem();
        try {
            Files.delete(Paths.get(String.valueOf(clientDir.resolve(clientItem))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        refreshClientView();
    }

    public void deleteServerFile(ActionEvent actionEvent) {
        String serverItem = server.getSelectionModel().getSelectedItem();
        try {
            Files.delete(Paths.get(String.valueOf(Paths.get("serverDir").resolve(serverItem))));
            server.getItems().remove(serverItem);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void openClientFile(ActionEvent actionEvent) {
        String selectedItem = client.getSelectionModel().getSelectedItem();
        Path path = clientDir.resolve(selectedItem);

        Desktop desktop = null;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }
        try {
            assert desktop != null;
            desktop.open(new File(String.valueOf(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openServerFile(ActionEvent actionEvent) {
        String selectedItem = server.getSelectionModel().getSelectedItem();
        Path path = Paths.get("serverDir").resolve(selectedItem);
        Desktop desktop = null;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }
        try {
            assert desktop != null;
            desktop.open(new File(String.valueOf(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exit(ActionEvent actionEvent) {
        openNewScene("Authorization.fxml", actionEvent);
    }

    private void openNewScene(String scene, ActionEvent actionEvent) {
        Stage stage = new Stage();
        try {
            Parent parent = FXMLLoader.load(getClass().getResource(scene));
            stage.setScene(new Scene(parent));
            stage.show();
            Button source = (Button) actionEvent.getSource();
            source.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}