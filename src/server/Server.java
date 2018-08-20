package server;


import controller.ServerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Server extends Application {

    public static void main(String... args){
        if(args.length == 0){
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
}
