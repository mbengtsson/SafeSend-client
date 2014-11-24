package se.teamgejm.safesend.activities;

import java.text.DateFormat;
import java.util.Date;

import org.spongycastle.util.encoders.Base64;

import se.teamgejm.safesend.R;
import se.teamgejm.safesend.entities.Message;
import se.teamgejm.safesend.events.MessageByIdFailedEvent;
import se.teamgejm.safesend.events.MessageByIdSuccessfulEvent;
import se.teamgejm.safesend.pgp.PgpHelper;
import se.teamgejm.safesend.rest.FetchMessageById;
import se.teamgejm.safesend.service.DecryptMessageIntentService;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
public class OpenMessageActivity extends Activity {
	
	public static final String INTENT_MESSAGE = "message";
	
	private static final String TAG = "OpenMessageActivity";
	
	private Message incomingMessage;
	
	private ProgressBar progressBar;
	private RelativeLayout openForm;
	private TextView statusMessage;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_message);
        
        if (getIntent().hasExtra(INTENT_MESSAGE)) {
        	incomingMessage = (Message) getIntent().getSerializableExtra(INTENT_MESSAGE);
        }
		
		progressBar = (ProgressBar) findViewById(R.id.open_progressBar);
		
		openForm = (RelativeLayout) findViewById(R.id.open_form_layout);
		
		statusMessage = (TextView) findViewById(R.id.open_status);
        
        TextView origin = (TextView) findViewById(R.id.message_origin);
        origin.setText(getString(R.string.from) + " " + getIncomingMessage().getSender().getDisplayName());
        
        Date date = new Date(getIncomingMessage().getTimeStamp());
        
        TextView time = (TextView) findViewById(R.id.message_time);
        time.setText(DateFormat.getDateInstance().format(date));
        
        TextView type = (TextView) findViewById(R.id.message_type);
        type.setText(getString(R.string.type) + " " + getIncomingMessage().getMessageType());
        
        showProgress();
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
    
    public void onEvent(MessageByIdSuccessfulEvent event) {
    	getIncomingMessage().setMessage(event.getMessage());
    	
    	final String publicKey = event.getSenderPublicKey();
    	
    	byte[] decodedPublicKey = Base64.decode(publicKey.getBytes());
    	PgpHelper.createFile(getApplicationContext(), decodedPublicKey, PgpHelper.KEY_PUBLIC);
    	
    	decryptAndVerify(getIncomingMessage().getMessage());
    }
    
    public void onEvent(MessageByIdFailedEvent event) {
    	Toast.makeText(getApplicationContext(), getString(R.string.failed_message_by_id), Toast.LENGTH_SHORT).show();
    	hideProgress();
    }
    
    /**
     * Decrypt and verify the encrypted message
     */
    private void decryptAndVerify(String encryptedMessage) {
    	statusMessage.setText(R.string.status_decrypting);
        Intent decryptIntent = new Intent(this, DecryptMessageIntentService.class);
        decryptIntent.putExtra(DecryptMessageIntentService.MESSAGE_IN, encryptedMessage);
        startService(decryptIntent);
    }
	
    public Message getIncomingMessage() {
		return incomingMessage;
	}

	public void setIncomingMessage(Message message) {
		this.incomingMessage = message;
	}
	
	private void registerResponseReciever() {
		IntentFilter filter = new IntentFilter(DecryptMessageResponseReciever.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(new DecryptMessageResponseReciever(), filter);
	}
	
	private void showProgress() {
		registerResponseReciever();
		
		progressBar.setVisibility(View.VISIBLE);
		statusMessage.setVisibility(View.VISIBLE);
		openForm.setVisibility(View.GONE);
    	statusMessage.setText(R.string.status_fetching_message);
		FetchMessageById.call(incomingMessage.getMessageId());
	}
	
	private void hideProgress() {
		progressBar.setVisibility(View.GONE);
		statusMessage.setVisibility(View.GONE);
		openForm.setVisibility(View.VISIBLE);
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
    		
    		if (message == null) {
    			Toast.makeText(getApplicationContext(), getString(R.string.failed_decryption), Toast.LENGTH_SHORT).show();
    			hideProgress();
    			return;
    		}
    		
    		TextView messageContent = (TextView) findViewById(R.id.message_content);
    		messageContent.setText(message);
    		
    		//TODO: Save message to local database
    		
    		hideProgress();
    	}

    }	
}
