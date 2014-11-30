package se.teamgejm.safesend.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * A user of the application. See CurrentUser.class for the owner of the application.
 * @author Gustav
 *
 */
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + (int) (userId ^ (userId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (userId != other.userId)
			return false;
		return true;
	}

	@Override
    public int compareTo (User another) {
        return getDisplayName().compareToIgnoreCase(another.getDisplayName());
    }
}
