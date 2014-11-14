package se.teamgejm.safesend.events;

import se.teamgejm.safesend.entities.User;

import java.util.List;

/**
 * Event class that handles a successful service call that fetches all users.
 *
 * @author Emil Stjerneman
 */
public class UserListSuccessEvent {

    private List<User> users;

    public UserListSuccessEvent (List<User> users) {
        this.users = users;
    }

    public List<User> getUsers () {
        return users;
    }
}
