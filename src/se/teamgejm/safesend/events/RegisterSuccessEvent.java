package se.teamgejm.safesend.events;

import se.teamgejm.safesend.entities.response.UserResponse;

/**
 * Event fired when a registration is complete.
 *
 * @author Emil Stjerneman
 */
public final class RegisterSuccessEvent {

    /**
     * The REST response object.
     */
    private UserResponse userResponse;

    public RegisterSuccessEvent (final UserResponse userResponse) {
        this.userResponse = userResponse;
    }

    public UserResponse getUserResponse () {
        return userResponse;
    }
}
