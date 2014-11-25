package se.teamgejm.safesend.database.model;

/**
 * @author Emil Stjerneman
 */
public class DbUser {

    private long id;

    private long userId;

    private String email;

    private String displayName;

    private String publicKey;

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
        return "DbUser{" +
                "id=" + id +
                ", userId=" + userId +
                ", email='" + email + '\'' +
                ", displayName='" + displayName + '\'' +
                ", publicKey='" + publicKey + '\'' +
                '}';
    }
}