package se.teamgejm.safesend.rest;

import android.util.Log;
import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.teamgejm.safesend.entities.Message;
import se.teamgejm.safesend.events.MessageListFailedEvent;
import se.teamgejm.safesend.events.MessageListSuccessEvent;

import java.util.List;

/**
 * @author Gustav
 */
public class FetchMessageList {

    private static final String TAG = "FetchMessageList";

    /**
     * Callback to handle service results.
     */
    private static Callback<List<Message>> callback = new Callback<List<Message>>() {

        @Override
        public void failure (RetrofitError error) {
            Log.d(TAG, "Failed to load messages : " + error.getMessage());
            EventBus.getDefault().post(new MessageListFailedEvent(error));
        }

        @Override
        public void success (List<Message> messages, Response response) {
            Log.d(TAG, "Successfully loaded messages.");
            EventBus.getDefault().post(new MessageListSuccessEvent(messages));
        }
    };

    public static void call () {
        ApiManager.getSafesendService().getMessages(FetchMessageList.callback);
    }

    public static List<Message> callSynchronously () {
        return ApiManager.getSafesendService().getMessagesSynchronously();
    }

}
