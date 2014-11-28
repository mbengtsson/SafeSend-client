package se.teamgejm.safesend.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import se.teamgejm.safesend.R;
import se.teamgejm.safesend.adapters.UserAdapter;
import se.teamgejm.safesend.database.dao.DbUserDao;
import se.teamgejm.safesend.entities.User;

import java.security.Security;

/**
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
            actionBar.setDisplayShowTitleEnabled(false);
        }

        userListView = (ListView) findViewById(R.id.userListView);
        adapter = new UserAdapter(this);
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
    protected void onResume () {
        super.onResume();

        dbUserDao = new DbUserDao(this);
        dbUserDao.open();

        adapter.clearUsers();

        for (final User user : dbUserDao.getUsersWithMessages()) {
            adapter.addUser(user);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause () {
        super.onPause();

        dbUserDao.close();
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
        }

        return super.onOptionsItemSelected(item);
    }

}
