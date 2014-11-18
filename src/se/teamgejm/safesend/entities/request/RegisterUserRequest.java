package se.teamgejm.safesend.entities.request;

/**
 * @author Emil Stjerneman
 */
public class RegisterUserRequest {

    private String username;
    private String password;
    private String publicKey;

    public RegisterUserRequest (String username, String password, String publicKey) {
        setUsername(username);
        setPassword(password);
        setPublicKey(publicKey);
    }

    public String getUsername () {
        return username;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public String getPassword () {
        return password;
    }

    public void setPassword (String password) {
        this.password = password;
    }

    public String getPublicKey () {
        return publicKey;
    }

    public void setPublicKey (String publicKey) {
        this.publicKey = publicKey;
    }
}
