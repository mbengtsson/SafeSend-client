package se.teamgejm.safesend.activities;


import se.teamgejm.safesend.R;
import se.teamgejm.safesend.entities.User;
import se.teamgejm.safesend.events.UserPubkeyFailedEvent;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
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
    
    private Button sendBtn;
    private ProgressBar progressBar;
    private RelativeLayout sendForm;
    private TextView statusMessage;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        setOnClickListeners();
        
        progressBar = (ProgressBar) findViewById(R.id.send_progress_bar);
        
        sendForm = (RelativeLayout) findViewById(R.id.send_form_layout);
        
        statusMessage = (TextView) findViewById(R.id.send_status);

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
    
    public void onEvent(UserPubkeyFailedEvent event) {
    	Toast.makeText(getApplicationContext(), getString(R.string.failed_pub_key) , Toast.LENGTH_SHORT).show();
    	hideProgress();
    }
    
    /**
     * Sign and encrypt the message
     */
    private void signAndEncrypt() {
        statusMessage.setText(R.string.status_encrypting);
    	TextView messageView = (TextView) findViewById(R.id.message_text);
        final String plainMessage = messageView.getText().toString();
        
        Log.d(TAG, "Message : " + plainMessage);
        
        if (plainMessage.isEmpty()) {
        	Toast.makeText(getApplicationContext(), getString(R.string.empty_message) , Toast.LENGTH_SHORT).show();
        	hideProgress();
        	return;
        }
        
        registerResponseReciever();
        
        Intent encryptIntent = new Intent(this, EncryptMessageIntentService.class);
        encryptIntent.putExtra(EncryptMessageIntentService.MESSAGE_IN, plainMessage);
        startService(encryptIntent);
    }

    private void setOnClickListeners () {
        sendBtn = (Button) findViewById(R.id.message_send_button);
        sendBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick (View v) {
                Log.d(TAG, "Send button clicked");
                showProgress();
                getReceiverPublicKey();
            }
        });
    }
    
    private void registerResponseReciever() {
    	IntentFilter filter = new IntentFilter(EncryptMessageResponseReciever.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(new EncryptMessageResponseReciever(), filter);
    }

    private void getReceiverPublicKey () {
        Log.d(TAG, "Getting public key");
        statusMessage.setText(R.string.status_get_pubkey);
        FetchUserKey.call(getReceiver().getId());
    }
    
    private void showProgress () {
        progressBar.setVisibility(View.VISIBLE);
        statusMessage.setVisibility(View.VISIBLE);
        sendForm.setVisibility(View.GONE);
    }

    private void hideProgress () {
        progressBar.setVisibility(View.GONE);
        statusMessage.setVisibility(View.GONE);
        sendForm.setVisibility(View.VISIBLE);
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
    		unregisterReceiver(this);
    		final String encryptedMessage = intent.getStringExtra(EncryptMessageIntentService.MESSAGE_OUT);
    		Log.d(TAG, "Encrypted message:" + encryptedMessage);
    		
    		if (encryptedMessage.isEmpty()) {
    			Toast.makeText(getApplicationContext(), getString(R.string.failed_encryption) , Toast.LENGTH_SHORT).show();
    			hideProgress();
            	return;
    		}

            statusMessage.setText(R.string.status_sending);
    		
    		// TODO: Send the message.
            //        SendMessageRequest sendMessageRequest = new SendMessageRequest();
            //        sendMessageRequest.setMessage();
            //        sendMessageRequest.setPassword("password");
            //        sendMessageRequest.setReceiverId(getReceiver().getId());
            //        sendMessageRequest.setSenderId(1L);
            //        SendMessage.call(sendMessageRequest);
    	}

    }	
}
