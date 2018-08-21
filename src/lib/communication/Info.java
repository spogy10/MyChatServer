package lib.communication;

public interface Info {
    String SERVER_CONNECTION_ERROR = "-404";
    String GENERAL_ERROR = "-999";
    String ALREADY_LOGGED_IN = "-1000";
    String NO_ERROR = "";
    String USER_OFFLINE = "-989";

    String LOGIN_USER = "LOGIN USER";
    String CHECK_IF_USERNAME_ALREADY_EXISTS = " CHECK IF USERNAME ALREADY EXISTS";
    String CREATE_USER = "CREATE USER";

    String LOGOUT = "LOGOUT";
    String DISCONNECT = "DISCONNECT";

    String FORWARD_MESSAGE = "FORWARD MESSAGE";
    String RECEIVE_MESSAGE = "RECEIVE MESSAGE";
}
