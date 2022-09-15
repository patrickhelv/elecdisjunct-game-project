package elecdisjunct.repo;

import elecdisjunct.data.user.User;
import elecdisjunct.repo.UserDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Victoria Blichfeldt
 * @author Tore Bergebakken
 */
public class UserDAOTest {
    private static final String EMAIL = "test@email.com";
    private static final String NICKNAME = "test2";
    private static final char[] PASSWORD = "testtest2".toCharArray();

    private UserDAO userDAO;

    @BeforeEach
    void setUp() throws SQLException {
        userDAO = new UserDAO();
    }

    @AfterEach
    void tearDown() throws SQLException {
        userDAO.close();
        userDAO = null;
    }

    @Test
    void logIn() throws SQLException {
        assertTrue(userDAO.logIn(EMAIL));
    }

    @Test
    void logOut() throws SQLException {
        assertTrue(userDAO.logOut(EMAIL));
    }

    @Test
    void getUser() throws SQLException { //måtte hente ut toString for at det skulle fungere, fikk bare melding om at det var identisk men ville ikke bli likt
        assertEquals((new User(6, EMAIL, NICKNAME)).toString(), userDAO.getUser(EMAIL).toString());
    }

    @Test
    void addUser() throws SQLException {
        assertTrue(userDAO.addUser(EMAIL, NICKNAME, PASSWORD));
    }

    @Test
    void updateNickname() throws SQLException {
        String newNickname = "tester";
        User user = new User(5,"test@email.com", "test");
        assertTrue(userDAO.updateNickname(user, newNickname));

    }

    @Test
    void setPassword() throws SQLException {
        assertTrue(userDAO.setPassword(5, "testTEST".toCharArray()));
    }

    @Test
    void isPasswordCorrect() throws SQLException {
        assertTrue(userDAO.isPasswordCorrect("test2@email.com", PASSWORD));
    }
}