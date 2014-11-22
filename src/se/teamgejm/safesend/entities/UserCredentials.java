package se.teamgejm.safesend.entities;

import java.io.Serializable;

/**
 * User credentials used to hold credentials.
 *
 * @author Emil Stjerneman
 */
public class UserCredentials implements Serializable {

    private static final long serialVersionUID = 1L;

    private static UserCredentials instance;

    private String email;

    private transient String password;

    private UserCredentials () {
        // No instance.
    }

    public synchronized static UserCredentials getInstance () {
        if (UserCredentials.instance == null) {
            UserCredentials.instance = new UserCredentials();
        }

        return UserCredentials.instance;
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

    @Override
    public String toString () {
        return "UserCredentials{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
