package se.teamgejm.safesend.rest;

import android.util.Log;
import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.teamgejm.safesend.entities.request.ValidateCredentialsRequest;
import se.teamgejm.safesend.events.UserCredentialsFailedEvent;
import se.teamgejm.safesend.events.UserCredentialsSuccessEvent;

/**
 * @author Emil Stjerneman
 */
public class ValidateCredentials {

    private final static String TAG = "ValidateCredentials";

    /**
     * Callback to handle service results.
     */
    private final static Callback<String> CALLBACK = new Callback<String>() {

        @Override
        public void failure (final RetrofitError error) {
            Log.i(TAG, "Failed to verify credentials : " + error.getMessage());
            EventBus.getDefault().post(new UserCredentialsFailedEvent());
        }

        @Override
        public void success (String string, final Response response) {
            Log.i(TAG, "Successfully verified credentials.");
            EventBus.getDefault().post(new UserCredentialsSuccessEvent());
        }
    };

    public static void call (final ValidateCredentialsRequest request) {
        ApiManager.getSafesendServiceNoAuth().validateCredentials(request, ValidateCredentials.CALLBACK);
    }
}
