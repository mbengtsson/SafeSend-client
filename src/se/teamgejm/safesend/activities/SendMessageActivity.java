package se.teamgejm.safesend.activities;


import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import de.greenrobot.event.EventBus;
import org.spongycastle.util.encoders.Base64;
import se.teamgejm.safesend.R;
import se.teamgejm.safesend.adapters.UserAdapter;
import se.teamgejm.safesend.database.dao.DbMessageDao;
import se.teamgejm.safesend.database.dao.DbUserDao;
import se.teamgejm.safesend.entities.Message;
import se.teamgejm.safesend.entities.User;
import se.teamgejm.safesend.entities.request.SendMessageRequest;
import se.teamgejm.safesend.events.*;
import se.teamgejm.safesend.pgp.PgpHelper;
import se.teamgejm.safesend.rest.FetchUserKey;
import se.teamgejm.safesend.rest.FetchUserList;
import se.teamgejm.safesend.rest.SendMessage;
import se.teamgejm.safesend.service.EncryptMessageIntentService;

/**
 * @author Gustav
 */
public class SendMessageActivity extends Activity {

    private final static String TAG = "SendMessageActivity";

    public static final String INTENT_RECEIVER = "receiver";
    public static final int ACTION_SEND_MESSAGE = R.id.action_send_message;

    private User receiver;

    private DbUserDao dbUserDao;
    private DbMessageDao dbMessageDao;

    private UserAdapter adapter;
    private Spinner userSprinner;

    private LinearLayout composeContainer;
    private LinearLayout progressContainer;


    private ProgressBar progressBar;
    private TextView statusMessage;

    private Button sendBtn;
    private RelativeLayout sendForm;

    private TextView messageTextView;

    private String message;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        final ActionBar actionBar = getActionBar();
        actionBar.setTitle("Send message");

        composeContainer = (LinearLayout) findViewById(R.id.composeContainer);
        progressContainer = (LinearLayout) findViewById(R.id.progressContainer);

        progressBar = (ProgressBar) findViewById(R.id.send_progress_bar);
        statusMessage = (TextView) findViewById(R.id.send_status);

        userSprinner = (Spinner) findViewById(R.id.userSpinner);
        adapter = new UserAdapter(this);
        userSprinner.setAdapter(adapter);
        userSprinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected (AdapterView<?> parent, View view, int position, long id) {
                final User user = adapter.getUser(position);
                setReceiver(user);
            }

            @Override
            public void onNothingSelected (AdapterView<?> parent) {
                // Do nothing.
            }
        });

        messageTextView = (TextView) findViewById(R.id.message_text);
        messageTextView.setMovementMethod(new ScrollingMovementMethod());

        setOnClickListeners();

        if (getIntent().hasExtra(INTENT_RECEIVER)) {
            setReceiver((User) getIntent().getSerializableExtra(INTENT_RECEIVER));
        }

        dbMessageDao = new DbMessageDao(this);
        dbMessageDao.open();

        dbUserDao = new DbUserDao(this);
        dbUserDao.open();

        statusMessage.setText("Loading users.");
        showProgress();
        FetchUserList.call();
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
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.send, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case ACTION_SEND_MESSAGE:
                Log.d(TAG, "Send button clicked");
                message = messageTextView.getText().toString();
                if (message.isEmpty()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.empty_message), Toast.LENGTH_SHORT).show();
                    return false;
                }
                showProgress();
                getReceiverPublicKey();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onEvent (UserListFailedEvent event) {
        Toast.makeText(this, "List of users could not be loaded.", Toast.LENGTH_LONG).show();
        hideProgress();
    }

    public void onEvent (UserListSuccessEvent event) {
        adapter.clearUsers();
        for (User user : event.getUsers()) {
            adapter.addUser(user);
        }
        adapter.notifyDataSetChanged();
        hideProgress();
    }

    public void onEvent (UserPubkeySuccessEvent event) {
        getReceiver().setPublicKey(event.getPubkey());

        signAndEncrypt(getReceiver().getPublicKey().getBytes());
    }

    public void onEvent (UserPubkeyFailedEvent event) {
        Toast.makeText(getApplicationContext(), getString(R.string.failed_pub_key), Toast.LENGTH_SHORT).show();
        hideProgress();
    }

    public void onEvent (SendMessageSuccessEvent event) {
        Toast.makeText(getApplicationContext(), getString(R.string.success_send_message), Toast.LENGTH_SHORT).show();
        getApplicationContext().deleteFile(PgpHelper.MESSAGE_ENCRYPTED);

        Log.d("RECEIVER", getReceiver().toString());

        final User receiver = dbUserDao.addUser(getReceiver());

        Message message = new Message();
        message.setSender(null);
        message.setReceiver(getReceiver());
        message.setTimeStamp(System.currentTimeMillis() / 1000);
        message.setMessage(this.message);
        dbMessageDao.addMessage(message);

        hideProgress();
    }

    public void onEvent (SendMessageFailedEvent event) {
        Toast.makeText(getApplicationContext(), getString(R.string.failed_send_message), Toast.LENGTH_SHORT).show();
        getApplicationContext().deleteFile(PgpHelper.MESSAGE_ENCRYPTED);
        hideProgress();
    }

    /**
     * Sign and encrypt the message
     */
    private void signAndEncrypt (byte[] publicKey) {
        statusMessage.setText(R.string.status_encrypting);

        Log.d(TAG, "Message : " + message);

        registerResponseReciever();

        Intent encryptIntent = new Intent(this, EncryptMessageIntentService.class);
        encryptIntent.putExtra(EncryptMessageIntentService.MESSAGE_IN, message);
        encryptIntent.putExtra(EncryptMessageIntentService.KEY_PUBLIC_IN, publicKey);
        startService(encryptIntent);
    }

    private void setOnClickListeners () {
        //        sendBtn = (Button) findViewById(R.id.message_send_button);
        //        sendBtn.setOnClickListener(new OnClickListener() {
        //
        //            @Override
        //            public void onClick (View v) {
        //                Log.d(TAG, "Send button clicked");
        //                final TextView text = (TextView) findViewById(R.id.message_text);
        //                message = text.getText().toString();
        //                if (message.isEmpty()) {
        //                    Toast.makeText(getApplicationContext(), getString(R.string.empty_message), Toast.LENGTH_SHORT).show();
        //                    return;
        //                }
        //                showProgress();
        //                getReceiverPublicKey();
        //            }
        //        });
    }

    private void registerResponseReciever () {
        IntentFilter filter = new IntentFilter(EncryptMessageResponseReciever.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(new EncryptMessageResponseReciever(), filter);
    }

    private void getReceiverPublicKey () {
        Log.d(TAG, "Getting public key");
        statusMessage.setText(R.string.status_get_pubkey);
        FetchUserKey.call(getReceiver().getUserId());
    }

    private void showProgress () {
        composeContainer.setVisibility(View.GONE);
        progressContainer.setVisibility(View.VISIBLE);
    }

    private void hideProgress () {
        composeContainer.setVisibility(View.VISIBLE);
        progressContainer.setVisibility(View.GONE);
    }

    private User getReceiver () {
        return receiver;
    }

    private void setReceiver (User receiver) {
        this.receiver = receiver;
    }

    /**
     * @author Gustav
     */
    public class EncryptMessageResponseReciever extends BroadcastReceiver {

        public static final String ACTION_RESP = "se.teamgejm.intent.action.MESSAGE_PROCESSED";

        @Override
        public void onReceive (Context context, Intent intent) {
            unregisterReceiver(this);
            String encryptedMessage = intent.getStringExtra(EncryptMessageIntentService.MESSAGE_OUT);
            Log.d(TAG, "Encrypted message:" + encryptedMessage);

            if (encryptedMessage == null) {
                Toast.makeText(getApplicationContext(), getString(R.string.failed_encryption), Toast.LENGTH_SHORT).show();
                hideProgress();
                return;
            }

            statusMessage.setText(R.string.status_sending);

            SendMessageRequest sendMessageRequest = new SendMessageRequest();
            sendMessageRequest.setMessage(Base64.toBase64String(encryptedMessage.getBytes()));
            sendMessageRequest.setReceiverId(getReceiver().getUserId());
            SendMessage.call(sendMessageRequest);
        }

    }
}
