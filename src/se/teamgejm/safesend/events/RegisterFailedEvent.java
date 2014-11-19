package se.teamgejm.safesend.events;

import retrofit.RetrofitError;

/**
 * @author Emil Stjerneman
 */
public class RegisterFailedEvent extends ErrorEvent {

    public RegisterFailedEvent (RetrofitError error) {
        super(error);
    }
}
