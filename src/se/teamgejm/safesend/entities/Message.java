package se.teamgejm.safesend.entities;

import java.io.Serializable;

import se.teamgejm.safesend.enums.MessageType;

/**
 * 
 * @author Gustav
 *
 */
@SuppressWarnings("serial")
public class Message implements Serializable {

	private long messageId;
    private User sender;
    private long timeStamp;
    private MessageType messageType;
    private String messageData;

    public Message () {
    	setMessageType(MessageType.TEXT);
    }

    public Message (User sender, long timeStamp) {
    	this();
        setSender(sender);
        setTimeStamp(timeStamp);
    }

    public User getSender () {
        return sender;
    }

    public void setSender (User origin) {
        this.sender = origin;
    }

    public long getTimeStamp () {
        return timeStamp;
    }

    public void setTimeStamp (long timestamp) {
        this.timeStamp = timestamp;
    }

    public MessageType getMessageType () {
        return messageType;
    }

    public void setMessageType (MessageType messageType) {
        this.messageType = messageType;
    }

    public String getMessageData () {
        return messageData;
    }

    public void setMessageData (String messageData) {
        this.messageData = messageData;
    }

	public long getMessageId() {
		return messageId;
	}

	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}

    @Override
    public String toString () {
        return "Sender: " + sender + " Timestamp: " + timeStamp + " Msgtype: " + messageType.getNiceName();
    }

}
