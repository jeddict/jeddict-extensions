package ${package};

/**
 * Application constants.
 */
public final class Constants {

    public final static String BEARER_PREFIX = "Bearer ";

    //Regex for acceptable logins
    public static final String LOGIN_REGEX = "^[_'.@A-Za-z0-9-]*$";
    
    //Regex for acceptable emails
    public static final String EMAIL_REGEX = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
    
    public static final String EMAIL_REGEX_MESSAGE = "{invalid.email}";

    public static final String INVALID_PASSWORD_TYPE = "invalid-password";
    
    public static final String EMAIL_ALREADY_USED_TYPE = "email-already-used";
    
    public static final String LOGIN_ALREADY_USED_TYPE = "login-already-used";
    
    public static final String EMAIL_NOT_FOUND_TYPE = "email-not-found";

    public static final int PASSWORD_MIN_LENGTH = 4;

    public static final int PASSWORD_MAX_LENGTH = 100;
    
    private Constants() {
    }
}
