package server;

import communication.DataCarrier;
import communication.DC;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

 class ClientConnection extends ServerRequestProcessor implements Runnable {

    private Socket connection;
    private DataCarrier tempResponseHolder;
    private AtomicBoolean unreadResponse = new AtomicBoolean(false);

     ClientConnection(Socket connection) {

        this.userName = "temp";
        clientManager = ClientManager.getInstance();
        this.connection = connection;

        if(initStreams()){
            Thread myThread = new Thread(this);
            myThread.start();
            System.out.println("Client "+userName+" added");
        }
    }

     void notifyContactOnline(String contactName, boolean online){
        String info = online? DC.CONTACT_ONLINE : DC.CONTACT_OFFLINE;
        DataCarrier<String> request = new DataCarrier<>(info, contactName, true);
        sendRequest(request, false);
    }

     void addContact(String contactName, Boolean online){
        Object[] contact = new Object[]{contactName, online};
        DataCarrier<Object[]> request = new DataCarrier<Object[]>(DC.ADD_CONTACT, contact, true);
        sendRequest(request, false);
    }

     void removeContact(String contactName){
        DataCarrier<String> request = new DataCarrier<>(DC.REMOVE_CONTACT, contactName, true);
        sendRequest(request, false);
    }

     String getUserName() {
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
                    notifyRequest(action);
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

    private void caseStatements() throws IOException { //todo DC.ADD_CONTACT, DC.REMOVE_CONTACT
        switch (action){
            case DC.LOGIN_USER:

                loginUser();
                break;

            case DC.REGISTER_USER:
                registerUser();
                break;

            case DC.ADD_CONTACT:
                addContact();
                break;

            case DC.REMOVE_CONTACT:
                removeContact();
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


     void closeConnection(){
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
