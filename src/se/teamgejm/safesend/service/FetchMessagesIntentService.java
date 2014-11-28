package se.teamgejm.safesend.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.spongycastle.util.encoders.Base64;

import se.teamgejm.safesend.database.dao.DbMessageDao;
import se.teamgejm.safesend.database.dao.DbUserDao;
import se.teamgejm.safesend.entities.Message;
import se.teamgejm.safesend.events.MessageFetchingDoneEvent;
import se.teamgejm.safesend.pgp.PgpHelper;
import se.teamgejm.safesend.rest.FetchMessageById;
import se.teamgejm.safesend.rest.FetchMessageList;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import de.greenrobot.event.EventBus;


public class FetchMessagesIntentService extends IntentService {

    private static final String TAG = "FetchMessagesIntentService";
    
    public static final String INTENT_SENDER_DISPLAYNAME = "sender";

    private DbMessageDao dbMessageDao;
    private DbUserDao dbUserDao;

    public FetchMessagesIntentService () {
        super(TAG);
    }

    private boolean errors = false;

    @Override
    protected void onHandleIntent (Intent intent) {
    	
    	if (!intent.hasExtra(INTENT_SENDER_DISPLAYNAME)) {
        	return;
        }
    	
        dbMessageDao = new DbMessageDao(getApplicationContext());
        dbMessageDao.open();

        dbUserDao = new DbUserDao(getApplicationContext());
        dbUserDao.open();

        try {
            final List<Message> messageList = FetchMessageList.callSynchronously();
            
            final String senderDisplayName = intent.getStringExtra(INTENT_SENDER_DISPLAYNAME);

            for (Message messageListItem : messageList) {
            	if (messageListItem.getSender().getDisplayName().equals(senderDisplayName)) {
            		final Message message = FetchMessageById.callSynchronously(messageListItem.getMessageId());

                    // Decode Base64 and create InputStream of the public key
                    byte[] decodedPublicKey = Base64.decode(message.getSenderPublicKey().getBytes());
                    InputStream keyIn = new ByteArrayInputStream(decodedPublicKey);

                    // Decode Base64
                    byte[] decodedEncryptedMessage = Base64.decode(message.getMessage().getBytes());

                    // Decrypt the message
                    String messagePlainText = PgpHelper.decryptAndVerify(getApplicationContext(), keyIn, decodedEncryptedMessage);
                    message.setMessage(messagePlainText);

                    dbMessageDao.addMessage(message);

                    dbUserDao.addUser(message.getReceiver());
                    dbUserDao.addUser(message.getSender());
            	}
            }
        }
        catch (Exception e) {
            errors = true;
            Log.e(TAG, e.getMessage());
        }
        finally {
            dbMessageDao.close();
            dbUserDao.close();
        }

    }

	@Override
	public void onDestroy() {
		super.onDestroy();
        EventBus.getDefault().post(new MessageFetchingDoneEvent());
	}
    
    
}
