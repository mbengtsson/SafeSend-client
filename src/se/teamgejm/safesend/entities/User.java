package se.teamgejm.safesend.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Comparable<User>, Serializable {

    private static final long serialVersionUID = 1L;

    private long _id;

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

    public User (long _id, long userId, String email, String displayName, String publicKey) {
        this._id = _id;
        this.userId = userId;
        this.email = email;
        this.displayName = displayName;
        this.publicKey = publicKey;
    }

    public long getId () {
        return _id;
    }

    public void setId (long _id) {
        this._id = _id;
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
                "_id=" + _id +
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
