package se.teamgejm.safesend.activities;


import se.teamgejm.safesend.R;
import se.teamgejm.safesend.entities.User;
import se.teamgejm.safesend.events.UserPubkeySuccessEvent;
import se.teamgejm.safesend.pgp.PgpHelper;
import se.teamgejm.safesend.rest.FetchUserKey;
import se.teamgejm.safesend.service.EncryptMessageIntentService;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import de.greenrobot.event.EventBus;

/**
 * 
 * @author Gustav
 *
 */
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
        username.setText(getString(R.string.message_to) + " " + getReceiver().getDisplayName());
    }

    @Override
    public void onStart () {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop () {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEvent (UserPubkeySuccessEvent event) {
    	// Recieve public key
        getReceiver().setPublicKey(event.getPubkey());

    	// Create a file of the public key
        PgpHelper.createFile(getApplicationContext(), getReceiver().getPublicKey(), PgpHelper.KEY_PUBLIC);
        
        signAndEncrypt();
    }
    
    /**
     * Sign and encrypt the message
     */
    private void signAndEncrypt() {
    	TextView messageView = (TextView) findViewById(R.id.message_text);
        final String plainMessage = messageView.getText().toString();
        
        Log.d(TAG, "Message : " + plainMessage);
        
    	// Start sign and encrypt
        Intent encryptIntent = new Intent(this, EncryptMessageIntentService.class);
        encryptIntent.putExtra(EncryptMessageIntentService.MESSAGE_IN, plainMessage);
        startService(encryptIntent);
    }

    private void setOnClickListeners () {
        Button sendBtn = (Button) findViewById(R.id.message_send_button);
        sendBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick (View v) {
                Log.d(TAG, "Send button clicked");
                IntentFilter filter = new IntentFilter(EncryptMessageResponseReciever.ACTION_RESP);
                filter.addCategory(Intent.CATEGORY_DEFAULT);
                registerReceiver(new EncryptMessageResponseReciever(), filter);
                getReceiverPublicKey();
            }
        });
    }


    private void getReceiverPublicKey () {
        Log.d(TAG, "Getting public key");
        FetchUserKey.call(getReceiver().getId());
    }

    private User getReceiver () {
        return receiver;
    }

    private void setReceiver (User receiver) {
        this.receiver = receiver;
    }
    
    /**
     * 
     * @author Gustav
     *
     */
    public class EncryptMessageResponseReciever extends BroadcastReceiver {
    	
    	public static final String ACTION_RESP = "se.teamgejm.intent.action.MESSAGE_PROCESSED";

    	@Override
    	public void onReceive(Context context, Intent intent) {
    		final String encryptedMessage = intent.getStringExtra(EncryptMessageIntentService.MESSAGE_OUT);
    		Log.d(TAG, "Encrypted message:" + encryptedMessage);
    		
    		// TODO: Send the message.
            //        SendMessageRequest sendMessageRequest = new SendMessageRequest();
            //        sendMessageRequest.setMessage();
            //        sendMessageRequest.setPassword("password");
            //        sendMessageRequest.setReceiverId(getReceiver().getId());
            //        sendMessageRequest.setSenderId(1L);
            //        SendMessage.call(sendMessageRequest);
    		
    		unregisterReceiver(this);
    	}

    }	
}
