package se.teamgejm.safesend.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.spongycastle.util.encoders.Base64;

import se.teamgejm.safesend.activities.SendMessageActivity.EncryptMessageResponseReciever;
import se.teamgejm.safesend.pgp.PgpHelper;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Service for encrypting a message.
 * @author Gustav
 *
 */
public class EncryptMessageIntentService extends IntentService {
	
	public static final String KEY_PUBLIC_IN = "public_key";
	public static final String MESSAGE_IN = "plain_text_message";
	
	public static final String MESSAGE_OUT = "encrypted_message";
	
	private static final String TAG = "EncryptMessageIntentService";

	public EncryptMessageIntentService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "OnHandleIntent");
		String message = intent.getStringExtra(MESSAGE_IN);
		byte[] publicKey = intent.getByteArrayExtra(KEY_PUBLIC_IN);
		
		// Decode Base64 and InputStream of the public key
		byte[] decodedPublicKey = Base64.decode(publicKey);
		InputStream publicKeyIn = new ByteArrayInputStream(decodedPublicKey);
		
		// Encrypt the message
		String encryptedMessage = PgpHelper.signAndEncrypt(getApplicationContext(), publicKeyIn, message);
		
		// Send to broadcast receiver
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(EncryptMessageResponseReciever.ACTION_RESP);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(MESSAGE_OUT, encryptedMessage);
		sendBroadcast(broadcastIntent);
	}
	
	

}
