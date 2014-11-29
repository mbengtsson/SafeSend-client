package se.teamgejm.safesend.events;

import android.annotation.SuppressLint;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Gustav
 *
 */
@SuppressLint("UseSparseArrays")
public class CheckNewMessagesDoneEvent {
	
	private Map<Long, Integer> newMessagesByUserId = new HashMap<Long, Integer>();
	
	public CheckNewMessagesDoneEvent(Map<Long, Integer> newMessagesByUserId) {
		this.newMessagesByUserId = newMessagesByUserId;
	}
	
	public Map<Long, Integer> getNewMessagesByUserId() {
		return newMessagesByUserId;
	}

}
