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
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import de.greenrobot.event.EventBus;
import org.spongycastle.util.encoders.Base64;
import se.teamgejm.safesend.R;
import se.teamgejm.safesend.adapters.UserAdapter;
import se.teamgejm.safesend.database.dao.DbMessageDao;
import se.teamgejm.safesend.database.dao.DbUserDao;
import se.teamgejm.safesend.entities.CurrentUser;
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
 * View to send messages to other users.
 * @author Gustav
 */
public class SendMessageActivity extends Activity {

    private final static String TAG = "SendMessageActivity";

    public static final String INTENT_RECEIVER = "receiver";
    public static final int ACTION_SEND_MESSAGE = R.id.action_send_message;

    private EncryptMessageResponseReceiver encryptMessageResponseReceiver = new EncryptMessageResponseReceiver();

    private User receiver;

    private DbUserDao dbUserDao;
    private DbMessageDao dbMessageDao;

    private UserAdapter adapter;
    private Spinner userSprinner;

    private LinearLayout composeContainer;
    private LinearLayout progressContainer;


    private ProgressBar progressBar;
    private TextView statusMessage;

    private TextView messageText;

    private String stringToSend;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        final ActionBar actionBar = getActionBar();

        registerResponseReciever();

        composeContainer = (LinearLayout) findViewById(R.id.composeContainer);
        progressContainer = (LinearLayout) findViewById(R.id.progressContainer);

        progressBar = (ProgressBar) findViewById(R.id.send_progress_bar);
        statusMessage = (TextView) findViewById(R.id.send_status);

        userSprinner = (Spinner) findViewById(R.id.userSpinner);
        adapter = new UserAdapter(this, true);
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

        messageText = (TextView) findViewById(R.id.message_text);
        messageText.setMovementMethod(new ScrollingMovementMethod());
        
        messageText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		        imm.showSoftInput(messageText, InputMethodManager.SHOW_FORCED);
			}
		});

        dbMessageDao = new DbMessageDao(this);
        dbMessageDao.open();

        dbUserDao = new DbUserDao(this);
        dbUserDao.open();

        statusMessage.setText("Loading users.");
        showProgress();

        if (getIntent().hasExtra(INTENT_RECEIVER)) {
            setReceiver((User) getIntent().getSerializableExtra(INTENT_RECEIVER));
            userSprinner.setVisibility(View.GONE);
            hideProgress();
            if (actionBar != null) {
                actionBar.setTitle(getString(R.string.title_send_to) + " " + getReceiver().getDisplayName());
            }
        }
        else {
            FetchUserList.call();
            if (actionBar != null) {
                actionBar.setTitle(getString(R.string.title_send_message));
            }
        }
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();

        unregisterReceiver(encryptMessageResponseReceiver);

        dbMessageDao.close();
        dbUserDao.close();
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
                stringToSend = messageText.getText().toString();
                if (stringToSend.isEmpty()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.empty_message), Toast.LENGTH_SHORT).show();
                    return false;
                }
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
        for (final User user : event.getUsers()) {
        	if (user.getUserId() != (CurrentUser.getInstance().getUserId())) {
                adapter.addUser(user);
        	}
        }
        adapter.notifyDataSetChanged();
        hideProgress();
    }

    public void onEvent (UserPubkeyFailedEvent event) {
        Toast.makeText(getApplicationContext(), getString(R.string.failed_pub_key), Toast.LENGTH_SHORT).show();
        hideProgress();
    }

    public void onEvent (UserPubkeySuccessEvent event) {
        getReceiver().setPublicKey(event.getPubkey());

        signAndEncrypt(getReceiver().getPublicKey().getBytes());
    }

    public void onEvent (SendMessageFailedEvent event) {
        Toast.makeText(getApplicationContext(), getString(R.string.failed_send_message), Toast.LENGTH_SHORT).show();
        getApplicationContext().deleteFile(PgpHelper.MESSAGE_ENCRYPTED);
        hideProgress();
    }

    public void onEvent (SendMessageSuccessEvent event) {
        Toast.makeText(getApplicationContext(), getString(R.string.success_send_message), Toast.LENGTH_SHORT).show();
        getApplicationContext().deleteFile(PgpHelper.MESSAGE_ENCRYPTED);

        final User receiver = dbUserDao.addUser(getReceiver());

        dbMessageDao.addMessage(new Message(CurrentUser.getInstance(), receiver, stringToSend));

        hideProgress();

        this.finish();
    }


    /**
     * Signs and encrypt the message.
     */
    private void signAndEncrypt (byte[] receiverPublicKey) {
        statusMessage.setText(R.string.status_encrypting);

        Log.d(TAG, "Message to encrypt : " + stringToSend);

        Intent encryptIntent = new Intent(this, EncryptMessageIntentService.class);
        encryptIntent.putExtra(EncryptMessageIntentService.MESSAGE_IN, stringToSend);
        encryptIntent.putExtra(EncryptMessageIntentService.RECEIVER_KEY_PUBLIC, receiverPublicKey);
        startService(encryptIntent);
    }

    private void registerResponseReciever () {
        IntentFilter filter = new IntentFilter(EncryptMessageResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(encryptMessageResponseReceiver, filter);
    }

    /**
     * Fetches the public key of the receiver from the server.
     */
    private void getReceiverPublicKey () {
        showProgress();
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
     * Receives the encrypted message and sends it to the server.
     * @author Gustav
     */
    public class EncryptMessageResponseReceiver extends BroadcastReceiver {

        public static final String ACTION_RESP = "se.teamgejm.intent.action.MESSAGE_ENCRYPT";

        @Override
        public void onReceive (Context context, Intent intent) {

            String encryptedMessage = intent.getStringExtra(EncryptMessageIntentService.MESSAGE_OUT);

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
