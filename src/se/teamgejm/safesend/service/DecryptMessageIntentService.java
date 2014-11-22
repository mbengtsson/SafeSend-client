package se.teamgejm.safesend.service;

import se.teamgejm.safesend.activities.OpenMessageActivity.DecryptMessageResponseReciever;
import se.teamgejm.safesend.pgp.PgpHelper;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * 
 * @author Gustav
 *
 */
public class DecryptMessageIntentService extends IntentService {
	
	public static final String MESSAGE_IN = "encrypted_message";
	public static final String MESSAGE_OUT = "plain_text_message";
	
	private static final String TAG = "DecryptMessageIntentService";

	public DecryptMessageIntentService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "OnHandleIntent");
		String encryptedMessage = intent.getStringExtra(MESSAGE_IN);
		PgpHelper.createFile(getApplicationContext(), encryptedMessage, PgpHelper.MESSAGE_ENCRYPTED);
		String message = PgpHelper.decryptAndVerify(getApplicationContext());
		
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(DecryptMessageResponseReciever.ACTION_RESP);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(MESSAGE_OUT, message);
		sendBroadcast(broadcastIntent);
	}

}
