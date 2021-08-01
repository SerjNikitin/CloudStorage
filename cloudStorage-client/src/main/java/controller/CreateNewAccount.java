package controller;

import dataBase.RequestDB;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import model.AbstractCommand;
import model.Registration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class CreateNewAccount implements Initializable {
    public TextField name;
    public TextField login;
    public PasswordField password;
    public AnchorPane anchorPane;
    private RequestDB requestDB = new RequestDB();
    private Thread readThread;
    private Stream stream;

    public void register(ActionEvent actionEvent) {
//        String nam = name.getText().trim();
//        String log = login.getText().trim();
//        String pass = password.getText().trim();
//        if (nam.length() > 0 && log.length() > 0 && pass.length() > 0) {
//            requestDB.createUser(nam, log, pass);
//            openNewScene("CloudStorage.fxml", actionEvent);
//        }


        String nam = name.getText().trim();
        String log = login.getText().trim();
        String pass = password.getText().trim();
        if (nam.length() > 0 && log.length() > 0 && pass.length() > 0) {
            try {
                stream.getOs().writeObject(new Registration(nam, log, pass));
            } catch (IOException e) {
                e.printStackTrace();
            }

            openNewScene("CloudStorage.fxml", actionEvent);
        }

//        else сделать реакцию на незаполненные поля


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
//            Button source = (Button) actionEvent.getSource();
            anchorPane.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        readThread = new Thread(() -> {

            while (!readThread.isInterrupted()) {
                try {
                    AbstractCommand command = (AbstractCommand) stream.getIs().readObject();
                    log.debug("received: {}", command);
                    switch (command.getType()) {
                        case REGISTRATION:
                            Registration registration=(Registration) command;
                            String name = registration.getName();
                            String login = registration.getLogin();
                            String password = registration.getPassword();
                            stream.getOs().writeObject(new Registration(name,login,password));
                            break;


                    }
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
            }

        });
    }
}
