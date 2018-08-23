package usercontrol;

import communication.DC;
import communication.DataCarrier;
import model.User;

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
}
