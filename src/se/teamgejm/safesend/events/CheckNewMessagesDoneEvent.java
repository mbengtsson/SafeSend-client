package se.teamgejm.safesend.events;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Gustav
 *
 */
public class CheckNewMessagesDoneEvent {
	
	private Map<String, Integer> newMessagesByName = new HashMap<String, Integer>();
	
	public CheckNewMessagesDoneEvent(Map<String, Integer> newMessageHolder) {
		this.newMessagesByName = newMessageHolder;
	}
	
	public Map<String, Integer> getNewMessagesHolder() {
		return newMessagesByName;
	}

}
