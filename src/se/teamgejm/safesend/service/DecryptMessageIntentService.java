package se.teamgejm.safesend.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import org.spongycastle.util.encoders.Base64;
import se.teamgejm.safesend.activities.ListMessagesActivity.DecryptMessageResponseReceiver;
import se.teamgejm.safesend.entities.Message;
import se.teamgejm.safesend.pgp.PgpHelper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Service for decrypting a message.
 *
 * @author Gustav
 */
public class DecryptMessageIntentService extends IntentService {

    public static final String MESSAGE_IN = "encrypted_message";

    public static final String MESSAGE_OUT = "plain_text_message";

    private static final String TAG = "DecryptMessageIntentService";

    public DecryptMessageIntentService () {
        super(TAG);
    }

    @Override
    protected void onHandleIntent (Intent intent) {
        Log.d(TAG, "OnHandleIntent");
        Message message = (Message) intent.getSerializableExtra(MESSAGE_IN);

        // Decode Base64 and create InputStream of the public key
        byte[] decodedPublicKey = Base64.decode(message.getSenderPublicKey().getBytes());
        InputStream keyIn = new ByteArrayInputStream(decodedPublicKey);

        // Decode Base64
        byte[] decodedEncryptedMessage = Base64.decode(message.getMessage().getBytes());

        // Decrypt the message
        String messagePlainText = PgpHelper.decryptAndVerify(getApplicationContext(), keyIn, decodedEncryptedMessage);
        message.setMessage(messagePlainText);

        // Send to broadcast receiver
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(DecryptMessageResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(MESSAGE_OUT, message);
        sendBroadcast(broadcastIntent);
    }

}
