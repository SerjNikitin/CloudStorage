package controller;

import factory.Factory;
import animations.Shake;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;
import domaine.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import domaine.abstarctCommandImpl.AuthenticationResponse;
import domaine.abstarctCommandImpl.AuthorizationRequest;
import domaine.abstarctCommandImpl.SimpleMessage;
import service.impl.NetworkSettingImpl;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class Authorization implements Initializable {

    public TextField login;
    public PasswordField password;
    public AnchorPane anchorPane;
    private Thread readThread;
    private final User user = new User();


    public void register(ActionEvent actionEvent) {
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
        if (NetworkSettingImpl.signUpStage == null) {
            NetworkSettingImpl.signUpStage = openNewScene("CreateNewAccount.fxml", "");
        }
        NetworkSettingImpl.signUpStage.show();
    }

    public void entrance(ActionEvent actionEvent) {
        String loginText = login.getText().trim();
        String passText = password.getText().trim();
        if (!loginText.equals("") && !passText.equals("")) {
            try {
                Factory.getNetworkService().sendCommand(new AuthorizationRequest(loginText, passText));
            } catch (Exception e) {
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
                    AbstractCommand command = Factory.getNetworkService().readCommandResult();
                    log.debug("received: {}", command);
                    switch (command.getType()) {

                        case AUTH_RESPONSE:
                            authResponse((AuthenticationResponse) command);
                            break;

                        case SIMPLE_MESSAGE:
                            simpleMassage((SimpleMessage) command);
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

    private void authResponse(AuthenticationResponse command) {
        AuthenticationResponse authResponse = command;
        user.setName(authResponse.getName());
        user.setLogin(authResponse.getLogin());
        user.setPassword(authResponse.getPassword());
        switchToCloud();
        readThread.interrupt();
    }

    private void simpleMassage(SimpleMessage command) {
        SimpleMessage message = command;
        if (message.toString().equals("Registration successful")) {
            Platform.runLater(() -> {
                NetworkSettingImpl.signUpStage.hide();
                NetworkSettingImpl.signInStage.show();
            });
        }
        Platform.runLater(() -> {
            NetworkSettingImpl.loader.getController();
        });
    }

    private Stage openNewScene(String sceneName, String userName) {
        NetworkSettingImpl.loader = new FXMLLoader();
        NetworkSettingImpl.loader.setLocation(getClass().getResource(sceneName));
        try {
            NetworkSettingImpl.loader.load();
        } catch (IOException e) {
            log.debug("Error scene loading: {}", e.getClass());
        }
        Parent root = NetworkSettingImpl.loader.getRoot();
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
            openNewScene("CloudStorage.fxml", user.getLogin()).showAndWait();
        });
    }
}