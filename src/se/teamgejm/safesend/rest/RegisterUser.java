package se.teamgejm.safesend.rest;

import android.util.Log;
import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.teamgejm.safesend.entities.User;
import se.teamgejm.safesend.entities.request.RegisterUserRequest;
import se.teamgejm.safesend.events.RegisterFailedEvent;
import se.teamgejm.safesend.events.RegisterSuccessEvent;

/**
 * @author Emil Stjerneman
 */
public class RegisterUser {

    private final static String TAG = "RegisterUser";

    /**
     * Callback to handle service results.
     */
    private final static Callback<User> CALLBACK = new Callback<User>() {

        @Override
        public void failure (final RetrofitError error) {
            Log.d(TAG, "Failed to register user : " + error.getMessage());
            EventBus.getDefault().post(new RegisterFailedEvent());
        }

        @Override
        public void success (final User user, final Response response) {
            Log.d(TAG, "Successfully registered user.");
            EventBus.getDefault().post(new RegisterSuccessEvent(user));
        }
    };

    public static void call (final RegisterUserRequest request) {
        ApiManager.getSafesendService().registerUser(request, RegisterUser.CALLBACK);
    }
}
