package controller;

import animations.Shake;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;
import model.AbstractCommand;
import model.AuthenticationResponse;
import model.AuthorizationRequest;
import model.SimpleMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class Authorization implements Initializable {

    public TextField login;
    public PasswordField password;
    public AnchorPane anchorPane;
    private Thread readThread;

    public void register(ActionEvent actionEvent) {
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
//        openNewScene("CreateNewAccount.fxml", "");

//         Stage stage=(Stage) anchorPane.getScene().getWindow();
//         stage.setScene(openNewScene("CreateNewAccount.fxml",""));
//

        if (NetworkSettings.signUpStage == null) {
            NetworkSettings.signUpStage = openNewScene("CreateNewAccount.fxml", "");
        }
        NetworkSettings.signUpStage.show();
    }

    public void entrance(ActionEvent actionEvent) {
        String loginText = login.getText().trim();
        String passText = password.getText().trim();
        if (!loginText.equals("") && !passText.equals("")) {
            try {
                NetworkSettings.stream.getOs().writeObject(new AuthorizationRequest(loginText, passText));
//                Stream.getOs().writeObject(new AuthorizationRequest(loginText, passText));

            } catch (IOException e) {
                log.error("Error: {}", e.getMessage());
            }
        } else {
            Shake shakeLogin = new Shake(login);
            Shake shakePassword = new Shake(password);
            shakeLogin.play();
            shakePassword.play();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        readThread = new Thread(() -> {
            try {
                while (!readThread.isInterrupted()) {
                    AbstractCommand command = (AbstractCommand) NetworkSettings.stream.getIs().readObject();
//                    AbstractCommand command = (AbstractCommand) Stream.getIs().readObject();

                    log.debug("received: {}", command);
                    switch (command.getType()) {

                        case AUTH_RESPONSE:
                            AuthenticationResponse authResponse = (AuthenticationResponse) command;
                            NetworkSettings.user.setName(authResponse.getName());
                            NetworkSettings.user.setLogin(authResponse.getLogin());
                            NetworkSettings.user.setPassword(authResponse.getPassword());
                            switchToCloud();
                            readThread.interrupt();
                            break;

                        case SIMPLE_MESSAGE:
                            SimpleMessage message = (SimpleMessage) command;
                            if (message.toString().equals("Registration successful")) {
                                Platform.runLater(() -> {
//                                    openNewScene("CloudStorage.fxml", "");
                                    NetworkSettings.signUpStage.hide();
                                    NetworkSettings.signInStage.show();
                                });
                            }
                            Platform.runLater(() -> {
//                                FXMLLoader loader=new FXMLLoader();
//                                loader.getController();
                                NetworkSettings.loader.getController();
                            });
                            break;
                    }
                }
            } catch (Exception e) {
                log.error("Error: {}", e.getMessage());
                e.printStackTrace();
            }
        });
        readThread.setDaemon(true);
        readThread.start();
    }

//    private Stage openNewScene(String scene, String userName) {
//
////        Stage stage = new Stage();
//        Stage stage = (Stage) anchorPane.getScene().getWindow();
//
//        try {
//            Parent parent = FXMLLoader.load(getClass().getResource(scene));
//            stage.setScene(new Scene(parent));
//            stage.show();
////            anchorPane.getScene().getWindow().hide();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (!userName.equals("")) stage.setTitle("Cloud storage. User: " + userName);
//        else stage.setTitle("Cloud storage");
//        stage.setResizable(false);
//        return stage;
//    }

    private Stage openNewScene(String sceneName, String userName) {
        NetworkSettings.loader = new FXMLLoader();
        NetworkSettings.loader.setLocation(getClass().getResource(sceneName));
        try {
            NetworkSettings.loader.load();
        } catch (IOException e) {
            log.debug("Error scene loading: {}", e.getClass());
        }
        Parent root = NetworkSettings.loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        if (!userName.equals("")) stage.setTitle("Cloud storage. User: " + userName);
        else stage.setTitle("Cloud storage");
        stage.setResizable(false);
        return stage;
    }

    private void switchToCloud() {
        Platform.runLater(() -> {
            try {
                readThread.join();
            } catch (InterruptedException e) {
                log.error("Error: {}", e.getMessage());
            }
            anchorPane.getScene().getWindow().hide();
            openNewScene("CloudStorage.fxml", NetworkSettings.user.getLogin()).showAndWait();
        });
    }
}