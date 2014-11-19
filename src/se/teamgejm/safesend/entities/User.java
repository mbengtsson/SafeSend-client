package se.teamgejm.safesend.entities;

import java.io.Serializable;

public class User implements Comparable<User>, Serializable {

    private long id;
    private String email;
    private String displayName;
    private String publicKey;

    public User () {

    }

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

    public String getDisplayName () {
        return displayName;
    }

    public void setDisplayName (String displayName) {
        this.displayName = displayName;
    }

    public String getPublicKey () {
        return publicKey;
    }

    public void setPublicKey (String publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public int compareTo (User another) {
        return getDisplayName().compareToIgnoreCase(another.getDisplayName());
    }

}
