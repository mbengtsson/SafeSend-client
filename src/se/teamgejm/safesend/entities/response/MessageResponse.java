package se.teamgejm.safesend.entities.response;

/**
 * 
 * @author Gustav
 *
 */
public class MessageResponse {
	
	private String senderPublicKey;
	private String message;
	
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
