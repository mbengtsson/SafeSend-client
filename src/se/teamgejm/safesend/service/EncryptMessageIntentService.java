package se.teamgejm.safesend.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import org.spongycastle.util.encoders.Base64;
import se.teamgejm.safesend.activities.SendMessageActivity;
import se.teamgejm.safesend.pgp.PgpHelper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Service for encrypting a message.
 *
 * @author Gustav
 */
public class EncryptMessageIntentService extends IntentService {

    public static final String RECEIVER_KEY_PUBLIC = "receiver_public_key";
    public static final String MESSAGE_IN = "plain_text_message";

    public static final String MESSAGE_OUT = "encrypted_message";

    private static final String TAG = "EncryptMessageIntentService";

    public EncryptMessageIntentService () {
        super(TAG);
    }

    @Override
    protected void onHandleIntent (Intent intent) {
        Log.d(TAG, "EncryptMessageIntentService :: OnHandleIntent");
        String message = intent.getStringExtra(MESSAGE_IN);
        byte[] receiverPublicKey = intent.getByteArrayExtra(RECEIVER_KEY_PUBLIC);

        // Decode Base64 and InputStream of the public key
        byte[] decodedReceiverPublicKey = Base64.decode(receiverPublicKey);
        InputStream receiverPublicKeyIn = new ByteArrayInputStream(decodedReceiverPublicKey);

        // Encrypt the message
        String encryptedMessage = PgpHelper.signAndEncrypt(getApplicationContext(), receiverPublicKeyIn, message);

        // Send to broadcast receiver
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SendMessageActivity.EncryptMessageResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(MESSAGE_OUT, encryptedMessage);
        sendBroadcast(broadcastIntent);
    }
}
