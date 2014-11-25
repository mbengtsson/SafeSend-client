package se.teamgejm.safesend.rest;

import de.greenrobot.event.EventBus;
import android.util.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.teamgejm.safesend.entities.request.SendMessageRequest;
import se.teamgejm.safesend.events.SendMessageFailedEvent;
import se.teamgejm.safesend.events.SendMessageSuccessEvent;

/**
 * @author Emil Stjerneman
 */
public class SendMessage {

    private final static String TAG = "SendMessage";

    /**
     * Callback to handle service results.
     */
    private static Callback<String> callback = new Callback<String>() {

        @Override
        public void failure (RetrofitError error) {
            Log.d(TAG, "Failed to send message : " + error.getMessage());
            EventBus.getDefault().post(new SendMessageFailedEvent(error));
        }

        @Override
        public void success (String res, Response response) {
            Log.d(TAG, "Successfully sent the message.");
            EventBus.getDefault().post(new SendMessageSuccessEvent());
        }
    };

    public static void call (SendMessageRequest sendMessageRequest) {
        ApiManager.getSafesendService().sendMessage(sendMessageRequest, SendMessage.callback);
    }
}
