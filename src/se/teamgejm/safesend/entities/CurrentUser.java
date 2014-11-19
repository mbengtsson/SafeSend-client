package se.teamgejm.safesend.entities;

/**
 * Created by anon on 11/19/14.
 */
public class CurrentUser {

    private static CurrentUser currentUser;

    private String email;

    private String password;

    private String displayName;

    public static CurrentUser getInstance() {
        if (CurrentUser.currentUser == null) {
            return new CurrentUser();
        }

        return CurrentUser.currentUser;
    }

    private CurrentUser () {
        // No instance
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

    @Override
    public String toString () {
        return "CurrentUser{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
