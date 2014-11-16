package se.teamgejm.safesend.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import se.teamgejm.safesend.R;
import se.teamgejm.safesend.entities.User;
import se.teamgejm.safesend.rest.FetchUserKey;

public class SendMessageActivity extends Activity {

    private final static String TAG = "SendMessageActivity";

    public static final String INTENT_RECEIVER = "receiver";

    private User receiver;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        setOnClickListeners();

        if (getIntent().hasExtra(INTENT_RECEIVER)) {
            setReceiver((User) getIntent().getSerializableExtra(INTENT_RECEIVER));
        }

        TextView username = (TextView) findViewById(R.id.message_send_to);
        username.setText(getString(R.string.message_to) + " " + getReceiver().getUsername());

    }

    private void setOnClickListeners () {
        Button sendBtn = (Button) findViewById(R.id.message_send_button);
        sendBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick (View v) {
                Log.d(TAG, "Send button clicked");
                encryptAndSend();
            }
        });
    }

    private void encryptAndSend () {
        // TODO sign and encrypt the message and send to server
        TextView message = (TextView) findViewById(R.id.message_text);
        Log.d(TAG, "Message : " + message.getText().toString());

        // Fetch the receivers pub key.
        Log.d(TAG, "Receiver : " + getReceiver().toString());
        FetchUserKey.call(getReceiver().getId());

        // Encrypt the message.

        // Send the message.
        //        SendMessageRequest sendMessageRequest = new SendMessageRequest();
        //        sendMessageRequest.setMessage(message.getText().toString());
        //        sendMessageRequest.setPassword("password");
        //        sendMessageRequest.setReceiverId(getReceiver().getId());
        //        sendMessageRequest.setSenderId(1L);
        //        SendMessage.call(sendMessageRequest);
    }

    private User getReceiver () {
        return receiver;
    }

    private void setReceiver (User receiver) {
        this.receiver = receiver;
    }
}
