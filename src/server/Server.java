package server;


import JavaFXHelper.FXHelper;
import controller.ServerController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import usercontrol.UserManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Server extends Application {

    private static boolean ISGIU = false;
    public static BindableAtomicInteger numberOfClients = new BindableAtomicInteger(0);

    public static void main(String... args) throws Exception {



        if(args.length == 0){

            new Thread(new Runnable() {
                @Override
                public void run() {
                    ServerManager.startServer();
                }
            }).start();
            ISGIU = true;
            launch(args);
        } else if(args[0].equals("3")){
            Map<String, Boolean> map = new HashMap<>();
            map.put("poliver", false);
            UserManager.mutateUserFile("poliverjr", map);
            System.exit(0);

        }else{
            ServerManager.startServer();
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
        if(ISGIU){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        FXHelper.alertPopup(this, title, message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else
            System.out.println(title+": "+message);
    }

    public static class BindableAtomicInteger extends AtomicInteger{
        public static SimpleIntegerProperty number = new SimpleIntegerProperty(0);

        public BindableAtomicInteger(int initialValue){
            super(initialValue);
            number.set(0);
        }


        public void increment(){
            number.set(super.incrementAndGet());
        }

        public void decrement(){
            number.set(super.decrementAndGet());
        }
    }
}
