package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerManager {

    ServerSocket serverSocket;
    private ClientManager clientManager = ClientManager.getInstance();

    private static ServerManager ourInstance;

    public static ServerManager startServer() {
        if(ourInstance == null)
            ourInstance = new ServerManager();

        return ourInstance;
    }

    private ServerManager() {
        setUpConnection();
    }

    private void setUpConnection() {
        try{
            serverSocket = new ServerSocket(4000, 10);
            waitForRequests();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void waitForRequests() {
        System.out.println("Waiting for requests");

        try{
            while(true){
                clientManager.addClient(new ClientConnection(serverSocket.accept()));
                System.out.println("connection received");


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void endServer(){
        clientManager.closeAllConnections();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
