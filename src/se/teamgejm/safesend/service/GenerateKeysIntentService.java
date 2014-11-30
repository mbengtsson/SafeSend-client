package se.teamgejm.safesend.service;

import se.teamgejm.safesend.activities.RegisterActivity.GenerateKeysResponseReciever;
import se.teamgejm.safesend.pgp.PgpHelper;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Service for generating keys pairs.
 * @author Gustav
 *
 */
public class GenerateKeysIntentService extends IntentService {
	
	public static final String EMAIL_IN = "email";
	public static final String PASSWORD_IN = "password";
	
	public static final String PUBLIC_KEY_OUT = "public_key";
	
	private static final String TAG = "GenerateKeysIntentService";

	public GenerateKeysIntentService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "OnHandleIntent");
		final String email = intent.getStringExtra(EMAIL_IN);
		final String pwd = intent.getStringExtra(PASSWORD_IN);
		
		// Generate the keys
		String publicKey = PgpHelper.generateKeyPair(getApplicationContext(), email, pwd);
		
		// Send to broadcast receiver
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(GenerateKeysResponseReciever.ACTION_RESP);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(PUBLIC_KEY_OUT, publicKey);
		sendBroadcast(broadcastIntent);
	}

}
