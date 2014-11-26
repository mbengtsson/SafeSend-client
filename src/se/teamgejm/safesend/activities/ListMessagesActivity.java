package se.teamgejm.safesend.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import se.teamgejm.safesend.R;
import se.teamgejm.safesend.adapters.MessageAdapter;
import se.teamgejm.safesend.database.dao.DbMessageDao;
import se.teamgejm.safesend.database.dao.DbUserDao;
import se.teamgejm.safesend.entities.Message;
import se.teamgejm.safesend.entities.User;
import se.teamgejm.safesend.entities.UserCredentials;

import java.util.List;

/**
 * Created by anon on 11/26/14.
 */
public class ListMessagesActivity extends Activity {

    public final static String INTENT_RECEIVER = "user";

    private DbMessageDao dbMessageDao;

    private User user;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_messages);
        Log.d("INNE", "HÄR");

        if (getIntent().hasExtra(INTENT_RECEIVER)) {
            user = (User) getIntent().getSerializableExtra(INTENT_RECEIVER);
        }

        Log.d("USER INT", user.toString());

        ListView messageListView = (ListView) findViewById(R.id.messageListView);

        dbMessageDao = new DbMessageDao(this);
        dbMessageDao.open();

        final List<Message> allMessage = dbMessageDao.getAllMessage(user.getUserId());

        MessageAdapter adapter = new MessageAdapter(this);
        messageListView.setAdapter(adapter);
        adapter.clearMessages();

        for (Message message : allMessage) {
            Log.d("Message", message.toString());
            adapter.addMessage(message);
        }

        adapter.notifyDataSetChanged();

    }
}
