package se.teamgejm.safesend.activities;

import se.teamgejm.safesend.R;
import se.teamgejm.safesend.entities.Message;
import se.teamgejm.safesend.entities.User;
import se.teamgejm.safesend.events.UserPubkeyFailedEvent;
import se.teamgejm.safesend.events.UserPubkeySuccessEvent;
import se.teamgejm.safesend.pgp.PgpHelper;
import se.teamgejm.safesend.rest.FetchUserKey;
import se.teamgejm.safesend.service.DecryptMessageIntentService;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Gustav
 *
 */
public class OpenMessageActivity extends Activity {
	
	public static final String INTENT_MESSAGE = "message";
	
	private static final String TAG = "OpenMessageActivity";
	
	private Message message;
	
	private User sender;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_message);
        
        setOnClickListeners();
        
        if (getIntent().hasExtra(INTENT_MESSAGE)) {
        	message = (Message) getIntent().getSerializableExtra(INTENT_MESSAGE);
        }
        
        TextView origin = (TextView) findViewById(R.id.message_origin);
        origin.setText(getString(R.string.from) + " " + getMessage().getOrigin().getDisplayName());
        
        TextView time = (TextView) findViewById(R.id.message_time);
        time.setText(getMessage().getTimestamp());
        
        TextView type = (TextView) findViewById(R.id.message_type);
        type.setText(getString(R.string.type) + " " + getMessage().getMessageType());
    }
	
    public void onEvent (UserPubkeySuccessEvent event) {
    	// Recieve public key (this might change)
        getSender().setPublicKey(event.getPubkey());

    	// Create a file of the public key
        PgpHelper.createFile(getApplicationContext(), getSender().getPublicKey(), PgpHelper.KEY_PUBLIC);

        decryptAndVerify();
    }
    
    /**
     * Decrypt and verify the encrypted message
     */
    private void decryptAndVerify() {
    	// TODO: Get the encrypted message from the local database
        final String encryptedMessage = null;
        
        // Start decrypt and verify
        Intent decryptIntent = new Intent(this, DecryptMessageIntentService.class);
        decryptIntent.putExtra(DecryptMessageIntentService.MESSAGE_IN, encryptedMessage);
        startService(decryptIntent);
    }

	private void setOnClickListeners() {
		ImageButton openBtn = (ImageButton) findViewById(R.id.message_open_button);
		openBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Open message button clicked");
				registerResponseReciever();
				getSenderPublicKey();
			}
		});
	}
	
	private void registerResponseReciever() {
		IntentFilter filter = new IntentFilter(DecryptMessageResponseReciever.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(new DecryptMessageResponseReciever(), filter);
	}
	
    private void getSenderPublicKey () {
        Log.d(TAG, "Getting public key");
        FetchUserKey.call(getSender().getId());
    }
	
    public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}
	
    public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

    /**
     * 
     * @author Gustav
     *
     */
	public class DecryptMessageResponseReciever extends BroadcastReceiver {
    	
    	public static final String ACTION_RESP = "se.teamgejm.intent.action.MESSAGE_PROCESSED";

    	@Override
    	public void onReceive(Context context, Intent intent) {
			unregisterReceiver(this);
    		final String message = intent.getStringExtra(DecryptMessageIntentService.MESSAGE_OUT);
    		Log.d(TAG, "Decrypted message:" + message);
    		
    		//TODO: Show message and save to local database
    	}

    }	
}
