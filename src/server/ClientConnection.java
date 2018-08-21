package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientConnection implements Runnable {

    private Socket connection;
    private ObjectInputStream is;
    private ObjectOutputStream os;
    private String userName;
    private ClientManager clientManager;


    public ClientConnection(Socket connection) {
        this.userName = "temp";
        clientManager = ClientManager.getInstance();
        this.connection = connection;

        if(initStreams()){
            Thread myThread = new Thread(this);
            myThread.start();
            System.out.println("Client "+userName+" added");
        }
    }

    public String getUserName() {
        return userName;
    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    private void changeUserName(String userName){
        if(clientManager.updateClientUsername(this.userName, userName))
            this.userName = userName;
    }

    private boolean initStreams() {
        try{
            if(connection == null)
                return false;

            os = new ObjectOutputStream(connection.getOutputStream());
            is = new ObjectInputStream(connection.getInputStream());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    @Override
    public void run() {

    }




    public void closeConnection(){
        close();
    }


    private void close(){
        try {
            os.close();
            is.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
