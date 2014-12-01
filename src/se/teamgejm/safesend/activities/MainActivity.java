package se.teamgejm.safesend.activities;

import java.security.Security;
import java.util.Map;

import se.teamgejm.safesend.R;
import se.teamgejm.safesend.adapters.UserAdapter;
import se.teamgejm.safesend.database.dao.DbUserDao;
import se.teamgejm.safesend.entities.User;
import se.teamgejm.safesend.events.CheckNewMessagesDoneEvent;
import se.teamgejm.safesend.service.CheckNewMessagesIntentService;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import de.greenrobot.event.EventBus;

/**
 * Lists conversations between the application user and another user.
 * @author Gustav
 */
public class MainActivity extends Activity {

    private final static String TAG = "MainActivity";

    private DbUserDao dbUserDao;

    private ListView userListView;

    private UserAdapter adapter;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);

        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.title_conversations));
        }

        userListView = (ListView) findViewById(R.id.userListView);
        
        adapter = new UserAdapter(this, false);
        userListView.setAdapter(adapter);

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick (AdapterView<?> parent, View view, int pos, long id) {
                final User user = adapter.getUser(pos);

                final Intent intent = new Intent(getApplicationContext(), ListMessagesActivity.class);
                intent.putExtra(ListMessagesActivity.INTENT_RECEIVER, user);
                startActivity(intent);
            }
        });
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
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
	        startLoading();
		}
	}

	@Override
    protected void onPause () {
        super.onPause();
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
                startActivity(intent);
                return true;

            case R.id.action_update_messages:
            	startLoading();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
    private void startLoading() {
    	Log.d(TAG, "Checking for new messages");
        startService(new Intent(this, CheckNewMessagesIntentService.class));
    }
    
    private void stopLoading(Map<Long, Integer> newMessages) {
    	adapter.setNewMessagesByUserId(newMessages);

    	adapter.clearUsers();
    	
    	dbUserDao = new DbUserDao(this);
        dbUserDao.open();
    	
        for (final User user : dbUserDao.getUsersWithMessages()) {
            adapter.addUser(user);
        }
        
    	for (Long userId : newMessages.keySet()) {
            adapter.addUser(dbUserDao.getUser(userId));
        }
    	
    	dbUserDao.close();

        adapter.notifyDataSetChanged();
    }
    
    public void onEvent(CheckNewMessagesDoneEvent event) {
    	Log.d(TAG, "Checking for new messages - done");
    	stopLoading(event.getNewMessagesByUserId());
    }
}
