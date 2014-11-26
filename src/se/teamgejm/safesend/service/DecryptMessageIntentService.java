package se.teamgejm.safesend.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.spongycastle.util.encoders.Base64;

import se.teamgejm.safesend.activities.OpenMessageActivity.DecryptMessageResponseReciever;
import se.teamgejm.safesend.pgp.PgpHelper;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Service for decrypting a message.
 * @author Gustav
 *
 */
public class DecryptMessageIntentService extends IntentService {
	
	public static final String KEY_PUBLIC_IN = "public_key";
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
		byte[] publicKey = intent.getByteArrayExtra(KEY_PUBLIC_IN);
		
		// Decode Base64 and create InputStream of the public key
		byte[] decodedPublicKey = Base64.decode(publicKey);
		InputStream keyIn = new ByteArrayInputStream(decodedPublicKey);

		// Decode Base64
    	byte[] decodedEncryptedMessage = Base64.decode(encryptedMessage.getBytes());
    	
    	// Decrypt the message
		String message = PgpHelper.decryptAndVerify(getApplicationContext(), keyIn, decodedEncryptedMessage);
		
		// Send to broadcast receiver
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(DecryptMessageResponseReciever.ACTION_RESP);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(MESSAGE_OUT, message);
		sendBroadcast(broadcastIntent);
	}

}
