package server;

import communication.DataCarrier;
import communication.DC;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientConnection extends ServerRequestProcessor implements Runnable {

    private Socket connection;
    DataCarrier tempResponseHolder;
    AtomicBoolean unreadResponse = new AtomicBoolean(false);

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
                if(carrier.isRequest()){
                    action = carrier.getInfo();
                    response = new DataCarrier(DC.NO_ERROR,false);
                    caseStatements();
                }else {//it is a response
                    tempResponseHolder = carrier;
                    unreadResponse.compareAndSet(false, true);

                }
            }
        } catch (IOException e) {
            if(e.toString().equals("java.net.SocketException: Connection reset") || e.toString().equals("java.io.EOFException"))
                System.out.println(userName+" disconnected from server");
            else
                e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
            clientManager.removeClient(userName);
        }


    }

    private void caseStatements() throws IOException {
        switch (action){
            case DC.LOGIN_USER:
                loginUser();
                break;

            case DC.REGISTER_USER:
                registerUser();
                break;
        }
    }

    private DataCarrier sendRequest(DataCarrier request, boolean responseRequired){
        DataCarrier response = new DataCarrier(DC.SERVER_CONNECTION_ERROR, false);
        try{
            os.writeObject(request);
            System.out.println(userName+": Request: "+request.getInfo()+" sent");
            if(responseRequired) {
                response = waitForResponse();
                System.out.println(userName+": Response for "+request.getInfo()+" received");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return response;
    }

    private DataCarrier waitForResponse() {
        while(!unreadResponse.get()){
            /*wait until response comes in*/
            Thread.onSpinWait();
        }

        unreadResponse.compareAndSet(true, false);
        return tempResponseHolder;
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
