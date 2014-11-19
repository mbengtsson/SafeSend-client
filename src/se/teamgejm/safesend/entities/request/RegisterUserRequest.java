package se.teamgejm.safesend.entities.request;

/**
 * @author Emil Stjerneman
 */
public class RegisterUserRequest {

    private String email;
    private String displayName;
    private String password;
    private String publicKey;

    public RegisterUserRequest (String email, String displayName, String password, String publicKey) {
        setEmail(email);
        setDisplayName(displayName);
        setPassword(password);
        setPublicKey(publicKey);
    }

    public String getEmail () {
        return email;
    }

    public void setEmail (String email) {
        this.email = email;
    }

    public String getDisplayName () {
        return displayName;
    }

    public void setDisplayName (String displayName) {
        this.displayName = displayName;
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
