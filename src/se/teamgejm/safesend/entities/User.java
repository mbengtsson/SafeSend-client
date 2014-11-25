package se.teamgejm.safesend.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@SuppressWarnings("serial")
public class User implements Comparable<User>, Serializable {

    private transient long id;

    @SerializedName("id")
    private long userId;

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

    public long getUserId () {
        return userId;
    }

    public void setUserId (long userId) {
        this.userId = userId;
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
    public String toString () {
        return "User{" +
                "id=" + id +
                ", userId=" + userId +
                ", email='" + email + '\'' +
                ", displayName='" + displayName + '\'' +
                ", publicKey='" + publicKey + '\'' +
                '}';
    }

    @Override
    public int compareTo (User another) {
        return getDisplayName().compareToIgnoreCase(another.getDisplayName());
    }


}
