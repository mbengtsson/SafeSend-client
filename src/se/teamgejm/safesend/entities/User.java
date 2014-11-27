package se.teamgejm.safesend.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Comparable<User>, Serializable {

    private static final long serialVersionUID = 1L;

    private transient long id;

    @SerializedName("id")
    private long userId;

    @SerializedName("email")
    private String email;

    @SerializedName("displayName")
    private String displayName;

    @SerializedName("publicKey")
    private String publicKey;

    public User () {
    }

    public User (long id, long userId, String email, String displayName, String publicKey) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.displayName = displayName;
        this.publicKey = publicKey;
    }

    public long getId () {
        return id;
    }

    public long getUserId () {
        return userId;
    }

    public String getEmail () {
        return email;
    }

    public String getDisplayName () {
        return displayName;
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
