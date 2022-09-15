package elecdisjunct.data.util;

import elecdisjunct.repo.UserDAO;

import java.sql.SQLException;

/**
 * Validates input received through the GUI
 *
 * @author Victoria Blichfeldt
 * @author Tore Bergebakken
 */
public class Validator {

    public static boolean isText(String input) {
        return input != null && !input.trim().equals("");
    }

    public static boolean isEmailRegistered(String email){
        try(UserDAO userDAO = new UserDAO()) {
            return userDAO.getUser(email.toLowerCase()) != null;
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return true;
    }

    public static boolean isPasswordEqual(String password, String reentered){
        return password.equals(reentered);
    }

    public static boolean isNicknameTooLong(String nickname){
        return nickname.length() <= 20;
    }

    public static boolean isPasswordTooLong(String password){
        return password.length() <= 40;
    }

    public static boolean isPasswordTooShort(String password){
        return password.length() >= 8;
    }
}
