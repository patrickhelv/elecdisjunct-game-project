package elecdisjunct.data.user;

/**
 * Class for User object
 *
 * @author Victoria Blichfeldt
 */

public class User {
    private String email, nickname;
    private int userId;

    public User(int userID, String email, String nickname){
        this.email = email;
        this.nickname = nickname;
        this.userId = userID;
    }

    public User(){
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "Username: " + email + "\nNickname: " + nickname;
    }
}
