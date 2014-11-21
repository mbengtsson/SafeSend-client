package se.teamgejm.safesend.entities.request;

/**
 * @author Emil Stjerneman
 */
public class ValidateCredentialsRequest {

    private String email;
    private String password;

    public ValidateCredentialsRequest (String email, String password) {
        setEmail(email);
        setPassword(password);
    }

    public String getEmail () {
        return email;
    }

    public void setEmail (String email) {
        this.email = email;
    }

    public String getPassword () {
        return password;
    }

    public void setPassword (String password) {
        this.password = password;
    }
}
