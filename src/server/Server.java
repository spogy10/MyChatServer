package server;


import JavaFXHelper.FXHelper;
import controller.ServerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Server extends Application {

    private static boolean ISGIU = false;

    public static void main(String... args){
        ServerManager.startServer();
        if(args.length == 0){
            ISGIU = true;
            launch(args);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource(ServerController.FXML));

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);

        primaryStage.setTitle(ServerController.TITLE);

        primaryStage.show();

    }


    public static boolean isISGIU() {
        return ISGIU;
    }

    public static void alert(String title, String message) throws IOException {
        if(ISGIU)
            FXHelper.alertPopup(new Object(), title, message);
        else
            System.out.println(title+": "+message);
    }
}
