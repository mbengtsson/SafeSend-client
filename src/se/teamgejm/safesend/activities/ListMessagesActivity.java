package se.teamgejm.safesend.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import de.greenrobot.event.EventBus;
import se.teamgejm.safesend.R;
import se.teamgejm.safesend.adapters.MessageAdapter;
import se.teamgejm.safesend.database.dao.DbMessageDao;
import se.teamgejm.safesend.entities.Message;
import se.teamgejm.safesend.entities.User;
import se.teamgejm.safesend.events.MessageByIdFailedEvent;
import se.teamgejm.safesend.events.MessageByIdSuccessfulEvent;
import se.teamgejm.safesend.events.MessageListFailedEvent;
import se.teamgejm.safesend.events.MessageListSuccessEvent;
import se.teamgejm.safesend.rest.FetchMessageById;
import se.teamgejm.safesend.rest.FetchMessageList;
import se.teamgejm.safesend.service.DecryptMessageIntentService;

/**
 * @author Emil Stjerneman
 */
public class ListMessagesActivity extends Activity {

    private final static String TAG = "ListMessagesActivity";

    private DecryptMessageResponseReceiver decryptMessageResponseReceiver = new DecryptMessageResponseReceiver();

    public final static String INTENT_RECEIVER = "user";

    private DbMessageDao dbMessageDao;

    private MessageAdapter adapter;

    private User user;

    private ProgressBar messageListProgressBar;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_messages);

        if (!getIntent().hasExtra(INTENT_RECEIVER)) {
            this.finish();
        }

        registerResponseReciever();

        user = (User) getIntent().getSerializableExtra(INTENT_RECEIVER);

        messageListProgressBar = (ProgressBar) findViewById(R.id.message_list_progress_bar);

        final ListView messageListView = (ListView) findViewById(R.id.messageListView);
        adapter = new MessageAdapter(this);
        messageListView.setAdapter(adapter);

        dbMessageDao = new DbMessageDao(this);
        dbMessageDao.open();

        for (Message message : dbMessageDao.getAllMessage(user.getUserId())) {
            adapter.addMessage(message);
        }

        adapter.notifyDataSetChanged();

        startLoading();
    }

    @Override
    public void onStart () {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop () {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy () {
        unregisterReceiver(decryptMessageResponseReceiver);
        super.onDestroy();
    }

    public void onEvent (MessageListFailedEvent event) {
        Toast.makeText(this, getString(R.string.failed_message_list), Toast.LENGTH_SHORT).show();
        stopLoading();
    }

    public void onEvent (MessageListSuccessEvent event) {
        //adapter.clearMessages();
        //        for (Message message : event.getMessages()) {
        //            adapter.addMessage(message);
        //            Log.d(TAG, message.toString());
        //        }
        //        adapter.notifyDataSetChanged();
        for (Message message : event.getMessages()) {
            FetchMessageById.call(message.getMessageId());
        }

        if (event.getMessages().size() == 0) {
            stopLoading();
        }
    }

    public void onEvent (MessageByIdSuccessfulEvent event) {
        decryptAndVerify(event.getMessage());
    }

    public void onEvent (MessageByIdFailedEvent event) {
        Toast.makeText(getApplicationContext(), getString(R.string.failed_message_by_id), Toast.LENGTH_SHORT).show();
        stopLoading();
    }

    private void startLoading () {
        messageListProgressBar.setVisibility(View.VISIBLE);
        FetchMessageList.call();
    }

    private void stopLoading () {
        messageListProgressBar.setVisibility(View.GONE);
    }

    private void registerResponseReciever () {
        IntentFilter filter = new IntentFilter(DecryptMessageResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(decryptMessageResponseReceiver, filter);
    }

    /**
     * Decrypt and verify the encrypted message
     */
    private void decryptAndVerify (Message message) {
        Intent decryptIntent = new Intent(this, DecryptMessageIntentService.class);
        decryptIntent.putExtra(DecryptMessageIntentService.MESSAGE_IN, message);
        startService(decryptIntent);
    }

    /**
     * @author Gustav
     */
    public class DecryptMessageResponseReceiver extends BroadcastReceiver {

        public static final String ACTION_RESP = "se.teamgejm.intent.action.MESSAGE_PROCESSED";

        @Override
        public void onReceive (Context context, Intent intent) {

            final Message message = (Message) intent.getSerializableExtra(DecryptMessageIntentService.MESSAGE_OUT);
            Log.d(TAG, "Decrypted message:" + message.toString());

            if (message == null) {
                Toast.makeText(getApplicationContext(), getString(R.string.failed_decryption), Toast.LENGTH_SHORT).show();
                stopLoading();
                return;
            }


            message.setStatus(Message.STATUS_DECRYPTED);
            dbMessageDao.addMessage(message);

            stopLoading();
        }

    }
}
