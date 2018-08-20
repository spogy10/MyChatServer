package manager;

import lib.model.User;
import server.Server;

import java.io.*;

public class UserManager {

    public static final String FOLDERNAME = "User Folder";
    private static UserManager instance = null;

    private static final File FOLDER = new File(FOLDERNAME);


    public static UserManager getInstance(){
        if(instance == null)
            instance = new UserManager();

        return instance;
    }


    private UserManager() {

        try {

            if (!FOLDER.exists() || !FOLDER.isDirectory()) {
                if (FOLDER.mkdir()) {
                    Server.alert("Alert", "Folder Created");
                }else{
                    Server.alert("Error", "Unable to create folder");
                }

            } else {
                Server.alert("Alert", "Folder Already Exists");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean saveUser(User user){

        boolean success = false;
        ObjectOutputStream oos = null;
        try{
            File userFile = new File(FOLDER, user.getUserName());

            oos = new ObjectOutputStream(new FileOutputStream(userFile));

            oos.writeObject(user);
            oos.flush();

            success = true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (oos == null)
                success = false;
            else {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    success = false;
                }
            }
        }

        return success;
    }

    public User retrieveUser(String userName){
        User user = new User();
        ObjectInputStream ois = null;

        try{
            File userFile = new File(FOLDER, userName);
            ois = new ObjectInputStream(new FileInputStream(userFile));

            user = (User) ois.readObject();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(ois != null)
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }
        return user;
    }

}
