package se.teamgejm.safesend.rest;

import android.util.Log;
import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.teamgejm.safesend.entities.Message;
import se.teamgejm.safesend.events.MessageByIdFailedEvent;
import se.teamgejm.safesend.events.MessageByIdSuccessfulEvent;

public class FetchMessageById {

    private static final String TAG = "FetchMessageById";

    /**
     * Callback to handle service results.
     */
    private static Callback<Message> callback = new Callback<Message>() {

        @Override
        public void failure (RetrofitError error) {
            Log.d(TAG, "Failed to load message : " + error.getMessage());
            EventBus.getDefault().post(new MessageByIdFailedEvent(error));
        }

        @Override
        public void success (Message message, Response response) {
            Log.d(TAG, "Successfully loaded message: " + message.getMessage());
            EventBus.getDefault().post(new MessageByIdSuccessfulEvent(message));
        }
    };

    public static void call (long id) {
        ApiManager.getSafesendService().getMessageById(id, FetchMessageById.callback);
    }

}
