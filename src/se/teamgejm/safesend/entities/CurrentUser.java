package se.teamgejm.safesend.entities;

/**
 * User credentials used to hold credentials.
 *
 * @author Emil Stjerneman
 */
public class CurrentUser extends User {

    private static final long serialVersionUID = 1L;

    private static CurrentUser instance;

    private transient String password;

    private CurrentUser () {
        // No instance.
    }

    public synchronized static CurrentUser getInstance () {
        if (CurrentUser.instance == null) {
            CurrentUser.instance = new CurrentUser();
        }

        return CurrentUser.instance;
    }

    public String getPassword () {
        return password;
    }

    public void setPassword (String password) {
        this.password = password;
    }
}
