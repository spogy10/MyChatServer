package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import static java.lang.System.exit;

public class ServerController implements Initializable {

    @FXML Label lableNumberOfClients;
    @FXML Button btEndServer;

    public static final String TITLE="MYCHAT SERVER";
    public static final String FXML ="/view/server.fxml";

    @FXML
    private void endServer(){
        Stage stage = (Stage) btEndServer.getScene().getWindow();

        stage.close();
        exit(0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
