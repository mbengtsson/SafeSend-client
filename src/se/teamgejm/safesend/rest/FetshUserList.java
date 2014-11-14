package se.teamgejm.safesend.rest;

import android.util.Log;
import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.teamgejm.safesend.entities.User;
import se.teamgejm.safesend.events.UserListFailedEvent;
import se.teamgejm.safesend.events.UserListSuccessEvent;

import java.util.List;

/**
 * @author Emil Stjerneman
 */
public class FetshUserList {

    private final static String TAG = "FetshUserList";

    /**
     * Callback to handle service results.
     */
    private static Callback<List<User>> callback = new Callback<List<User>>() {

        @Override
        public void failure (RetrofitError error) {
            Log.d(TAG, "Failed to load users : " + error.getMessage());
            EventBus.getDefault().post(new UserListFailedEvent());
        }

        @Override
        public void success (List<User> users, Response response) {
            Log.d(TAG, "Successfully loaded users.");
            EventBus.getDefault().post(new UserListSuccessEvent(users));
        }
    };

    public static void call () {
        ApiManager.getSafesendService().getUsers(FetshUserList.callback);
    }

}
