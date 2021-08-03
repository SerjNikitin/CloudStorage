package controller;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import model.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Slf4j
public class CloudStorage implements Initializable {
    public ListView<String> clientView;
    public ListView<String> serverView;
    public TextField clientField;
    public TextField serverField;
    public AnchorPane anchorPane;
    private Path clientDir;
    private Stream stream;
    private String fileName;

    public CloudStorage() {
        stream = NetworkSettings.stream;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            String userDir = System.getProperty("user.name");
            clientDir = Paths.get("/Users", userDir).toAbsolutePath();
            log.info("Current user: {}", System.getProperty("user.name"));

            refreshClientView();
            addNavigationListeners();

            stream.getOs().writeObject(new ListRequest());

            Thread readThread = new Thread(() -> {
                try {
                    while (true) {
                        AbstractCommand command = (AbstractCommand) stream.getIs().readObject();
                        switch (command.getType()) {
                            case LIST_RESPONSE:
                                ListResponse response = (ListResponse) command;
                                List<String> names = response.getName();
                                refreshServerView(names);
                                break;
                            case PATH_UP_RESPONSE:
                                PathUpResponse pathResponse = (PathUpResponse) command;
                                String path = pathResponse.getPath();
                                Platform.runLater(() -> serverField.setText(path));
                                break;
                            case FILE_MESSAGE:
                                FileMessage message = (FileMessage) command;
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

    public void send(ActionEvent actionEvent) {
        try {
            fileName = clientView.getSelectionModel().getSelectedItem();
            if (serverView.getItems().stream().noneMatch(p -> p.equals(fileName))) {
                FileMessage message = new FileMessage(clientDir.resolve(fileName));

                stream.getOs().writeObject(message);
                stream.getOs().flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void download(ActionEvent actionEvent) {
        try {
            fileName = serverView.getSelectionModel().getSelectedItem();
            if (clientView.getItems().stream().noneMatch(p -> p.equals(fileName))) {
                stream.getOs().writeObject(new FileRequest(fileName));
                stream.getOs().flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addNavigationListeners() {
        clientView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String item = clientView.getSelectionModel().getSelectedItem();
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
        serverView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String item = serverView.getSelectionModel().getSelectedItem();
                try {
                    stream.getOs().writeObject(new PathInRequest(item));
                    stream.getOs().flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }

    private void refreshServerView(List<String> name) {
        Platform.runLater(() -> {
            serverView.getItems().clear();
            serverView.getItems().addAll(name);
        });
    }

    private void refreshClientView() throws IOException {
        clientField.setText(clientDir.toString());
        List<String> name = Files.list(clientDir)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
        Platform.runLater(() -> {
            clientView.getItems().clear();
            clientView.getItems().addAll(name);
        });
    }

    public void clientPathUp(ActionEvent actionEvent) throws IOException {
        clientDir = clientDir.getParent();
        clientField.setText(clientDir.toString());
        refreshClientView();
    }

    public void serverPathUp(ActionEvent actionEvent) throws IOException {
        stream.getOs().writeObject(new PathUpRequest());
        stream.getOs().flush();
    }

    public void deleteClientFile(ActionEvent actionEvent) throws IOException {
        String clientItem = clientView.getSelectionModel().getSelectedItem();
        try {
            Files.delete(Paths.get(String.valueOf(clientDir.resolve(clientItem))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        refreshClientView();
    }

    public void deleteServerFile(ActionEvent actionEvent) {
        fileName = serverView.getSelectionModel().getSelectedItem();
        try {
            stream.getOs().writeObject(new DeleteRequest(fileName));
            stream.getOs().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openClientFile(ActionEvent actionEvent) {
        String selectedItem = clientView.getSelectionModel().getSelectedItem();
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

    public void exit(ActionEvent actionEvent) {
        anchorPane.getScene().getWindow().hide();
//        openNewScene("Authorization.fxml", "");
        NetworkSettings.signInStage.show();
    }

    private Stage openNewScene(String scene, String userName) {

        Stage stage = new Stage();
        try {
            Parent parent = FXMLLoader.load(getClass().getResource(scene));
            stage.setScene(new Scene(parent));
            stage.show();
            anchorPane.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!userName.equals("")) stage.setTitle("Cloud storage. User: " + userName);
        else stage.setTitle("Cloud storage");
        stage.setResizable(false);
        return stage;
    }

}