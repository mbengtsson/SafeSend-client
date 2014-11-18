package se.teamgejm.safesend.events;

import se.teamgejm.safesend.entities.User;

/**
 * @author Emil Stjerneman
 */
public final class RegisterSuccessEvent {

    private User user;

    public RegisterSuccessEvent (final User user) {
        setUser(user);
    }

    public User getUser () {
        return user;
    }

    public void setUser (final User user) {
        this.user = user;
    }
}
