package se.teamgejm.safesend.events;

import retrofit.RetrofitError;

/**
 * @author Emil Stjerneman
 */
public abstract class ErrorEvent {

    protected RetrofitError error;

    public ErrorEvent (RetrofitError error) {
        setError(error);
    }

    public RetrofitError getError () {
        return error;
    }

    public void setError (RetrofitError error) {
        this.error = error;
    }
}
