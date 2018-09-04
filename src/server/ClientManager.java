package server;

import javafx.application.Platform;

import java.util.HashMap;
import java.util.Map;

public class ClientManager {

    private Map<String, ClientConnection> clients = null;
    private static ClientManager instance = null;

    public static ClientManager getInstance(){
        if(instance == null)
            instance = new ClientManager();
        return instance;
    }

    private ClientManager(){
        clients = new HashMap<>();
    }

    public void addClient(ClientConnection con){
        String userName = "";
        do{
            userName = generateRandomName();

        }while (clients.containsKey(userName));

        con.setUserName(userName);
        clients.put(userName, con);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Server.numberOfClients.increment();
            }
        });
        System.out.println("Username updated from temp to "+userName);
    }


    public void removeClient(String userName){
        clients.remove(userName);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Server.numberOfClients.decrement();
            }
        });
    }

    private String generateRandomName(){
        return (Math.random()*4000)+"";
    }

    public ClientConnection getClient(String userName){
        return clients.get(userName);
    }

    public boolean updateClientUsername(String oldUsername, String newUsername){
        if(clients.containsKey(oldUsername) && !clients.containsKey(newUsername)){
            ClientConnection temp = clients.get(oldUsername);
            clients.remove(oldUsername);
            clients.put(newUsername, temp);
            System.out.println("Username updated from "+oldUsername+" to "+newUsername);
            return true;
        }
        System.out.println("Error updating usernames");
        return  false;
    }

    public boolean userExists(String receiver){ //is the user online
        return clients.containsKey(receiver);
    }

    public boolean handleUserGoingOnline(String userName, String contactName){
        if(userExists(contactName)){

            clients.get(contactName).notifyContactOnline(userName, true);
            return true;
        }

        return false;
    }

    public void handleUserGoingOffline(String userName, Map<String, Boolean> contacts){ //todo: implement this

        for(String contactName : contacts.keySet())
            if(userExists(contactName))
                clients.get(contactName).notifyContactOnline(userName, false);
    }

    public void closeAllConnections(){
        for(ClientConnection c : clients.values())
            c.closeConnection();
    }

}
