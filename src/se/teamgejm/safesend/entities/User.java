package se.teamgejm.safesend.entities;

public class User implements Comparable<User> {

    private long id;
    private String username;
    private String publicKey;

    public User () {

    }

    public User (String username) {
        setUsername(username);
    }

    public long getId () {
        return id;
    }

    public void setId (long id) {
        this.id = id;
    }

    public String getUsername () {
        return username;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public String getPublicKey () {
        return publicKey;
    }

    public void setPublicKey (String publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public String toString () {
        return "User: " + username + ", id: " + id;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || ((Object) this).getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        if (!username.equals(user.username)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode () {
        return username.hashCode();
    }


    @Override
    public int compareTo (User another) {
        return getUsername().compareToIgnoreCase(another.getUsername());
    }
}
