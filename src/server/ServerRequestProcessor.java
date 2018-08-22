package server;

import lib.communication.DC;
import lib.communication.DataCarrier;
import lib.model.User;
import usercontrol.UserProcessor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
        notifyRequest(action);
        String[] loginDetails = (String[]) carrier.getData();
        response = userProcessor.loginUser(loginDetails);
        if(response.getData() != null){
            String userName = ((User) response.getData()).getUserName();
            if(clientManager.userExists(userName))
                response.setInfo(DC.ALREADY_LOGGED_IN);
            else
                changeUserName(userName);
        }
        os.writeObject(response);
        notifyResponse(action);
    }

    protected void registerUser() throws IOException {
        notifyRequest(action);
        String[] loginDetails = (String[]) carrier.getData();
        response = userProcessor.createUser(loginDetails);
        if(((Boolean) response.getData()))
            changeUserName(loginDetails[0]);

        os.writeObject(response);
        notifyResponse(action);
    }
}
