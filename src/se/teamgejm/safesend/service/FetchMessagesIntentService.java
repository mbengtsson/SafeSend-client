package se.teamgejm.safesend.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import de.greenrobot.event.EventBus;
import org.spongycastle.util.encoders.Base64;
import se.teamgejm.safesend.database.dao.DbMessageDao;
import se.teamgejm.safesend.database.dao.DbUserDao;
import se.teamgejm.safesend.entities.Message;
import se.teamgejm.safesend.events.MessageFetchingDoneEvent;
import se.teamgejm.safesend.pgp.PgpHelper;
import se.teamgejm.safesend.rest.FetchMessageById;
import se.teamgejm.safesend.rest.FetchMessageList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class FetchMessagesIntentService extends IntentService {

    private static final String TAG = "FetchMessagesIntentService";

    private DbMessageDao dbMessageDao;
    private DbUserDao dbUserDao;

    public FetchMessagesIntentService () {
        super(TAG);
    }

    private boolean errors = false;

    @Override
    protected void onHandleIntent (Intent intent) {
        dbMessageDao = new DbMessageDao(this);
        dbMessageDao.open();

        dbUserDao = new DbUserDao(this);
        dbUserDao.open();

        try {
            final List<Message> messages = new ArrayList<>();

            final List<Message> messageList = FetchMessageList.callSynchronously();

            for (Message messageListItem : messageList) {
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
    public void onDestroy () {
        super.onDestroy();
        EventBus.getDefault().post(new MessageFetchingDoneEvent());
    }
}
