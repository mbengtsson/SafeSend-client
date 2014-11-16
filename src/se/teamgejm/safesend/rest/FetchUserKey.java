package se.teamgejm.safesend.rest;

import android.util.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.teamgejm.safesend.entities.User;

/**
 * @author Emil Stjerneman
 */
public class FetchUserKey {

    private final static String TAG = "FetchUserKey";

    /**
     * Callback to handle service results.
     */
    private static Callback<User> callback = new Callback<User>() {

        @Override
        public void failure (RetrofitError error) {
            Log.d(TAG, "Failed to load user pubkey : " + error.getMessage());
        }

        @Override
        public void success (User user, Response response) {
            Log.d(TAG, "Successfully loaded users pubkey.");
            Log.d(TAG, "Pubkey : " + user.getPublicKey());
        }
    };

    public static void call (long id) {
        ApiManager.getSafesendService().getUserKey(id, FetchUserKey.callback);
    }

}
