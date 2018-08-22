package server;

import lib.communication.DataCarrier;
import lib.communication.DC;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientConnection extends ServerRequestProcessor implements Runnable {

    private Socket connection;

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
        action = "";
        try{
            while (!action.equals(DC.DISCONNECT)){
                carrier = (DataCarrier) is.readObject();
                action = carrier.getInfo();
                response = new DataCarrier(DC.NO_ERROR,false);
                if(carrier.isRequest()){
                    switch (action){
                        case DC.LOGIN_USER:
                            loginUser();
                            break;

                        case DC.REGISTER_USER:
                            registerUser();
                            break;
                    }



                }else {//it is a response

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


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
