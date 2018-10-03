package server;

import communication.DC;
import communication.DataCarrier;
import model.User;
import usercontrol.UserProcessor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

public class ServerRequestProcessor {

    protected ObjectInputStream is;
    protected ObjectOutputStream os;
    protected ClientManager clientManager;
    protected UserProcessor userProcessor = UserProcessor.getInstance();
    protected String action = "";
    protected DataCarrier carrier = null;
    protected DataCarrier response = null;
    protected String userName;

    protected ServerRequestProcessor(){

    }

    protected void notifyRequest(String request) {
        System.out.println(userName+" Request: "+request);
    }

    protected void notifyResponse(String request){
        System.out.println(userName+" "+request+" response sent");
    }

    protected void changeUserName(String userName){
        if(clientManager.updateClientUsername(this.userName, userName))
            this.userName = userName;
    }


    protected void loginUser() throws IOException {
        String[] loginDetails = (String[]) carrier.getData();
        response = userProcessor.loginUser(loginDetails);
        if(response.getData() != null){
            String userName = ((User) response.getData()).getUserName();
            if(clientManager.userExists(userName))
                response.setInfo(DC.ALREADY_LOGGED_IN);
            else{
                changeUserName(userName);
                ((User) response.getData()).setContacts(updateContactStatus(((User) response.getData()).getContacts()));
            }
        }
        os.writeObject(response);
        notifyResponse(action);
    }

    private Map<String, Boolean> updateContactStatus(Map<String, Boolean> contacts) {
        for(String contact : contacts.keySet()){
            contacts.put(contact, clientManager.handleUserGoingOnline(userName, contact));
        }

        return contacts;
    }

    protected void registerUser() throws IOException {
        String[] loginDetails = (String[]) carrier.getData();
        response = userProcessor.createUser(loginDetails);
        if(((Boolean) response.getData()))
            changeUserName(loginDetails[0]);

        os.writeObject(response);
        notifyResponse(action);
    }

    protected void addContact() throws IOException { //todo test it
        String contactName = (String) carrier.getData();
        response = userProcessor.addContact(userName, contactName);
        if((Boolean) response.getData()){
            clientManager.addContact(userName, contactName);
        }
        os.writeObject(response);
        notifyResponse(action);
    }

    protected void removeContact() throws IOException {
        String contactName = (String) carrier.getData();
        response = userProcessor.removeContact(userName, contactName);

        if((Boolean) response.getData()){
            clientManager.removeContact(userName, contactName);
        }

        os.writeObject(response);
        notifyResponse(action);
    }
}
