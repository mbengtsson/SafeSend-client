package se.teamgejm.safesend.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import de.greenrobot.event.EventBus;
import se.teamgejm.safesend.R;
import se.teamgejm.safesend.adapters.MessageAdapter;
import se.teamgejm.safesend.database.dao.DbMessageDao;
import se.teamgejm.safesend.entities.Message;
import se.teamgejm.safesend.entities.User;
import se.teamgejm.safesend.events.MessageFetchingDoneEvent;
import se.teamgejm.safesend.service.FetchMessagesIntentService;

/**
 * @author Emil Stjerneman
 */
public class ListMessagesActivity extends Activity {

    private final static String TAG = "ListMessagesActivity";

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

        user = (User) getIntent().getSerializableExtra(INTENT_RECEIVER);

        messageListProgressBar = (ProgressBar) findViewById(R.id.message_list_progress_bar);

        final ListView messageListView = (ListView) findViewById(R.id.messageListView);
        adapter = new MessageAdapter(this);
        messageListView.setAdapter(adapter);

        Intent intent = new Intent(this, FetchMessagesIntentService.class);
        startService(intent);

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
        dbMessageDao.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_send_new_message:
                Intent intent = new Intent(this, SendMessageActivity.class);
                intent.putExtra(SendMessageActivity.INTENT_RECEIVER, user);
                startActivity(intent);
                return true;

            case R.id.action_update_messages:
                startService(new Intent(this, FetchMessagesIntentService.class));
                startLoading();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onEvent (MessageFetchingDoneEvent event) {
        stopLoading();
    }


    private void startLoading () {
        messageListProgressBar.setVisibility(View.VISIBLE);
    }

    private void stopLoading () {
        adapter.clearMessages();

        dbMessageDao = new DbMessageDao(this);
        dbMessageDao.open();

        for (Message message : dbMessageDao.getAllMessage(user.getUserId())) {
            Log.d(TAG, message.toString());
            adapter.addMessage(message);
        }

        dbMessageDao.close();

        adapter.notifyDataSetChanged();

        messageListProgressBar.setVisibility(View.GONE);
    }
}
