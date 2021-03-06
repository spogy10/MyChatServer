package usercontrol;

import model.User;
import server.Server;

import java.io.*;
import java.util.Map;

public class UserManager {

    public static final String FOLDERNAME = "User Folder";
    private static UserManager instance = null;

    private static final File FOLDER = new File(FOLDERNAME);


    public final static UserManager getInstance() {
        if (instance == null)
            instance = new UserManager();

        return instance;
    }


    private UserManager() {

        try {

            if (!FOLDER.exists() || !FOLDER.isDirectory()) {
                if (FOLDER.mkdir()) {
                    Server.alert("Alert", "Folder Created");
                } else {
                    Server.alert("Error", "Unable to create folder");
                }

            } else {
                Server.alert("Alert", "Folder Already Exists");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean saveUser(User user) {

        boolean success = false;
        ObjectOutputStream oos = null;
        try {
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

    public User retrieveUser(String userName) { //empty user if there is an error
        User user = new User();
        ObjectInputStream ois = null;

        try {
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
            if (ois != null)
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }
        return user;
    }

    public boolean doesUserExist(String userName) {
        return new File(FOLDER, userName).exists();
    }

    public static void mutateUserFile(String userName, Map<String, Boolean> contacts) {
        User user = new User();
        ObjectInputStream ois = null;

        try {
            File userFile = new File(FOLDER, userName);
            ois = new ObjectInputStream(new FileInputStream(userFile));

            user = (User) ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        user.getContacts().putAll(contacts);

        ObjectOutputStream oos = null;
        try {
            File userFile = new File(FOLDER, user.getUserName());

            oos = new ObjectOutputStream(new FileOutputStream(userFile));

            oos.writeObject(user);
            oos.flush();

            oos.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
