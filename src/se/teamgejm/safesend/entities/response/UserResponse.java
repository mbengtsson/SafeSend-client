package se.teamgejm.safesend.entities.response;

/**
 * User response class.
 *
 * Used when receiving a User from a REST call.
 *
 * @author Emil Stjerneman
 */
public class UserResponse {

    /**
     * User ID.
     */
    private long id;

    /**
     * User email address.
     */
    private String email;

    /**
     * User display name.
     */
    private String displayName;

    public long getId () {
        return id;
    }

    public String getEmail () {
        return email;
    }

    public String getDisplayName () {
        return displayName;
    }
}
