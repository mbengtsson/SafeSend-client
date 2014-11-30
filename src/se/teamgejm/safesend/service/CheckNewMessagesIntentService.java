package se.teamgejm.safesend.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.teamgejm.safesend.database.dao.DbUserDao;
import se.teamgejm.safesend.entities.Message;
import se.teamgejm.safesend.entities.User;
import se.teamgejm.safesend.events.CheckNewMessagesDoneEvent;
import se.teamgejm.safesend.rest.FetchMessageList;
import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import de.greenrobot.event.EventBus;

/**
 * Service for checking new incoming messages.
 * @author Gustav
 *
 */
@SuppressLint("UseSparseArrays")
public class CheckNewMessagesIntentService extends IntentService {
	
	private static final String TAG = "CheckNewMessagesIntentService";
	
    private DbUserDao dbUserDao;
    
    private Map<Long, Integer> newMessagesByUserId;

	public CheckNewMessagesIntentService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		dbUserDao = new DbUserDao(getApplicationContext());
        dbUserDao.open();
        
        newMessagesByUserId = new HashMap<Long, Integer>();
        
        try {
        	final List<Message> messageList = FetchMessageList.callSynchronously();
        	
        	for (Message message : messageList) {
        		User user = dbUserDao.addUser(message.getSender());
        		long userId = user.getUserId();
    			if (newMessagesByUserId.containsKey(userId)) {
        			int noOfMessages = newMessagesByUserId.get(userId);
        			newMessagesByUserId.put(userId, noOfMessages + 1);
        		} else {
            		newMessagesByUserId.put(userId, 1);
        		}
        	}
    		
        } finally {
        	dbUserDao.close();
        }

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
        EventBus.getDefault().post(new CheckNewMessagesDoneEvent(newMessagesByUserId));
	}
	
}
