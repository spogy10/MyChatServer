package usercontrol;

import communication.DC;
import communication.DataCarrier;
import model.User;

import java.util.HashMap;
import java.util.Map;

public class UserProcessor {

    private UserManager manager = UserManager.getInstance();
    private static UserProcessor instance = null;

    public final static UserProcessor getInstance(){
        if(instance == null)
            instance = new UserProcessor();

        return instance;
    }

    private UserProcessor() {
    }

    public DataCarrier loginUser(String[] loginDetails) {
        DataCarrier<User> response = new DataCarrier(false);

        String userName = loginDetails[0], password = loginDetails[1];

        if(manager.doesUserExist(userName)){
            User user = manager.retrieveUser(userName);
            if(user.getUserName().equals(""))
                response.setInfo(DC.GENERAL_ERROR);
            else{
                if(user.getPassword().equals(password)){
                    response.setInfo(DC.NO_ERROR);
                    response.setData(user);
                }else
                    response.setInfo(DC.INCORRECT_PASSWORD);
            }


        }else{
            response.setInfo(DC.USERNAME_DOES_NOT_EXIST);
        }

        return response;
    }

    public DataCarrier createUser(String[] loginDetails) {
        User user = new User(loginDetails[0], loginDetails[1]);
        DataCarrier<Boolean> response = new DataCarrier<>(false, false);

        response.setData(manager.saveUser(user));

        response.setInfo(DC.NO_ERROR);

        return response;
    }

    public Map<String, Boolean> getUserContacts(String userName){
        Map<String, Boolean> contacts = new HashMap<>();
        if(!manager.doesUserExist(userName))
            return contacts;

        User user = manager.retrieveUser(userName);
        contacts = user.getContacts();

        return contacts;
    }

    public DataCarrier addContact(String addingUserName, String addedUserName){
        DataCarrier<Boolean> response = new DataCarrier<Boolean>(false, false);
        if(!manager.doesUserExist(addedUserName)) {
            response.setInfo(DC.USERNAME_DOES_NOT_EXIST);
            return response;
        }

        User addingUser = manager.retrieveUser(addingUserName);
        addingUser.getContacts().put(addedUserName, false);

        User addedUser = manager.retrieveUser(addedUserName);
        addedUser.getContacts().put(addingUserName, false);

        response.setData(manager.saveUser(addingUser) && manager.saveUser(addedUser));

        response.setInfo(DC.NO_ERROR);

        return response;
    }
}
