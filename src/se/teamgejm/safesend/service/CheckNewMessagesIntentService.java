package se.teamgejm.safesend.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.teamgejm.safesend.database.dao.DbUserDao;
import se.teamgejm.safesend.entities.Message;
import se.teamgejm.safesend.events.CheckNewMessagesDoneEvent;
import se.teamgejm.safesend.rest.FetchMessageList;
import android.app.IntentService;
import android.content.Intent;
import de.greenrobot.event.EventBus;

public class CheckNewMessagesIntentService extends IntentService {
	
	private static final String TAG = "CheckNewMessagesIntentService";
	
    private DbUserDao dbUserDao;
    
    private Map<String, Integer> newMessagesByName;

	public CheckNewMessagesIntentService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		dbUserDao = new DbUserDao(getApplicationContext());
        dbUserDao.open();
        
        newMessagesByName = new HashMap<String, Integer>();
        
        try {
        	final List<Message> messageList = FetchMessageList.callSynchronously();
        	
        	for (Message message : messageList) {
        		String displayName = message.getSender().getDisplayName();
        		if (newMessagesByName.containsKey(displayName)) {
        			int noOfMessages = newMessagesByName.get(displayName);
        			newMessagesByName.put(displayName, noOfMessages + 1);
        		} else {
            		newMessagesByName.put(displayName, 1);
        		}

        		dbUserDao.addUser(message.getSender());
        	}
    		
        } finally {
        	dbUserDao.close();
        }

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
        EventBus.getDefault().post(new CheckNewMessagesDoneEvent(newMessagesByName));
	}
	
}
