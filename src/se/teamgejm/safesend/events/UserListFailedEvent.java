package se.teamgejm.safesend.events;

import retrofit.RetrofitError;

/**
 * @author Emil Stjerneman
 */
public class UserListFailedEvent extends ErrorEvent {

    public UserListFailedEvent (RetrofitError error) {
        super(error);
    }
}
