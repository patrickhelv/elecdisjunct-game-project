package elecdisjunct.data.user;

/**
 * Class to keep track of who is logged in.
 *
 * @author Victoria Blichfeldt
 */

public class UserHandler {
    private static User user;

    public static User getUser() {
        return user;
    }

    public static void setUser(User u){user = u;}
}
