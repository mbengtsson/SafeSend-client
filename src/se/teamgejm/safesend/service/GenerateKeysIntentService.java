package se.teamgejm.safesend.service;

import java.io.IOException;

import se.teamgejm.safesend.activities.RegisterActivity;
import se.teamgejm.safesend.activities.OpenMessageActivity.DecryptMessageResponseReciever;
import se.teamgejm.safesend.activities.RegisterActivity.GenerateKeysResponseReciever;
import se.teamgejm.safesend.pgp.PgpHelper;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * 
 * @author Gustav
 *
 */
public class GenerateKeysIntentService extends IntentService {
	
	public static final String EXTRA_EMAIL = "email";
	public static final String EXTRA_PWD = "password";
	
	public static final String PUBLIC_KEY = "public_key";
	
	private static final String TAG = "GenerateKeysIntentService";

	public GenerateKeysIntentService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "OnHandleIntent");
		final String email = intent.getStringExtra(EXTRA_EMAIL);
		final String pwd = intent.getStringExtra(EXTRA_PWD);
		
		PgpHelper.generateKeyPair(getApplicationContext(), email, pwd);
		
		String publicKey = null;
		
		try {
			publicKey = PgpHelper.fileToString(PgpHelper.KEY_PUBLIC, getApplicationContext());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(GenerateKeysResponseReciever.ACTION_RESP);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(PUBLIC_KEY, publicKey);
		sendBroadcast(broadcastIntent);
	}

}
