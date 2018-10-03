package server;

import javafx.application.Platform;
import usercontrol.UserProcessor;

import java.util.HashMap;
import java.util.Map;

 class ClientManager {

    private Map<String, ClientConnection> clients;
    private static ClientManager instance = null;
    private UserProcessor userProcessor = UserProcessor.getInstance();

     static ClientManager getInstance(){
        if(instance == null)
            instance = new ClientManager();
        return instance;
    }

    private ClientManager(){
        clients = new HashMap<>();
    }

     void addClient(ClientConnection con){
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


     void removeClient(String userName){

        handleUserGoingOffline(userName, userProcessor.getUserContacts(userName));

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

     ClientConnection getClient(String userName){
        return clients.get(userName);
    }

     boolean updateClientUsername(String oldUsername, String newUsername){
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

     boolean userExists(String receiver){ //is the user online
        return clients.containsKey(receiver);
    }

     boolean handleUserGoingOnline(String userName, String contactName){
        if(userExists(contactName)){

            clients.get(contactName).notifyContactOnline(userName, true);
            return true;
        }

        return false;
    }

    void addContact(String addingUser, String addedUser){
        boolean addingUserOnline = userExists(addingUser);
        boolean addedUserOnline = userExists(addedUser);

        if(addingUserOnline)
            clients.get(addingUser).addContact(addedUser, addedUserOnline);

        if(addedUserOnline)
            clients.get(addedUser).addContact(addingUser, addingUserOnline);
    }

    void removeContact(String removingUser, String removedUser){

        if(userExists(removingUser))
            clients.get(removingUser).removeContact(removedUser);

        if(userExists(removedUser))
            clients.get(removedUser).removeContact(removingUser);
    }

     private void handleUserGoingOffline(String userName, Map<String, Boolean> contacts){

        for(String contactName : contacts.keySet())
            if(userExists(contactName))
                clients.get(contactName).notifyContactOnline(userName, false);
    }

     void closeAllConnections(){
        for(ClientConnection c : clients.values())
            c.closeConnection();
    }

}
