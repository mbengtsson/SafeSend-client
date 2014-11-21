package se.teamgejm.safesend.entities;

import java.io.Serializable;

/**
 * Created by anon on 11/19/14.
 */
public class UserCredentials implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;

    private String email;

    private String password;

    private String displayName;

    public long getId () {
        return id;
    }

    public void setId (long id) {
        this.id = id;
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

    public String getDisplayName () {
        return displayName;
    }

    public void setDisplayName (String displayName) {
        this.displayName = displayName;
    }
}
