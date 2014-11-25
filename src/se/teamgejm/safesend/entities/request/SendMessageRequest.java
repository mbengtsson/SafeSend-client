package se.teamgejm.safesend.entities.request;

public class SendMessageRequest {
	
	private long receiverId;
	private String message;

	public long getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(long recieverId) {
		this.receiverId = recieverId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
