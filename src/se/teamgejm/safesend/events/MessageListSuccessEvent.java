package se.teamgejm.safesend.events;

import java.util.List;

import se.teamgejm.safesend.entities.Message;

/**
 * 
 * @author Gustav
 *
 */
public class MessageListSuccessEvent {
	
	private List<Message> messages;
	
	public MessageListSuccessEvent(List<Message> messages) {
		this.messages = messages;
	}

	public List<Message> getMessages() {
		return messages;
	}

}
