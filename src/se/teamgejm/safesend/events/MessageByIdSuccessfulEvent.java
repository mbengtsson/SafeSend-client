package se.teamgejm.safesend.events;

import se.teamgejm.safesend.entities.response.MessageResponse;


/**
 * 
 * @author Gustav
 *
 */
public class MessageByIdSuccessfulEvent {
	
	private String senderPublicKey;
	private String message;
	
	public MessageByIdSuccessfulEvent(MessageResponse response) {
		this.senderPublicKey = response.getSenderPublicKey();
		this.message = response.getMessage();
	}
	
	public String getSenderPublicKey() {
		return senderPublicKey;
	}
	
	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
