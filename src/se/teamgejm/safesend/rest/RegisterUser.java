package se.teamgejm.safesend.rest;

import android.util.Log;
import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.teamgejm.safesend.entities.request.RegisterUserRequest;
import se.teamgejm.safesend.entities.response.UserResponse;
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
    private final static Callback<UserResponse> CALLBACK = new Callback<UserResponse>() {

        @Override
        public void failure (final RetrofitError error) {
            Log.i(TAG, "Failed to register user : " + error.getMessage());
            EventBus.getDefault().post(new RegisterFailedEvent(error));
        }

        @Override
        public void success (final UserResponse userResponse, final Response response) {
            Log.i(TAG, "Successfully registered user.");
            EventBus.getDefault().post(new RegisterSuccessEvent(userResponse));
        }
    };

    public static void call (final RegisterUserRequest request) {
        ApiManager.getSafesendServiceNoAuth().registerUser(request, RegisterUser.CALLBACK);
    }
}
