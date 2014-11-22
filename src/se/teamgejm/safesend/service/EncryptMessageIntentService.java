package se.teamgejm.safesend.service;

import se.teamgejm.safesend.activities.SendMessageActivity.EncryptResponseReciever;
import se.teamgejm.safesend.pgp.PgpHelper;
import android.app.IntentService;
import android.content.Intent;

public class EncryptMessageIntentService extends IntentService {
	
	public static final String MESSAGE_IN = "plain_text_message";
	public static final String MESSAGE_OUT = "encrypted_message";
	
	private static final String TAG = "EncryptMessageIntentService";

	public EncryptMessageIntentService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String message = intent.getStringExtra(MESSAGE_IN);
		PgpHelper.createFile(getApplicationContext(), message, PgpHelper.MESSAGE_PLAINTEXT);
		String encryptedMessage = PgpHelper.signAndEncrypt(getApplicationContext());
		
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(EncryptResponseReciever.ACTION_RESP);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(MESSAGE_OUT, encryptedMessage);
		sendBroadcast(broadcastIntent);
	}
	
	

}
