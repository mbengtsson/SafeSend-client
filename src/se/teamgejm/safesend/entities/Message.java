package se.teamgejm.safesend.entities;

import se.teamgejm.safesend.enums.MessageType;

import java.io.Serializable;

/**
 * @author Gustav
 */
@SuppressWarnings("serial")
public class Message implements Serializable {

    private long id;
    private long messageId;
    private long timeStamp;
    private MessageType messageType;
    private String message;

    private User sender;
    private User receiver;

    public Message () {
        setMessageType(MessageType.TEXT);
    }

    public Message (User sender, long timeStamp) {
        this();
        setSender(sender);
        setTimeStamp(timeStamp);
    }

    public long getId () {
        return id;
    }

    public void setId (long id) {
        this.id = id;
    }

    public long getMessageId () {
        return messageId;
    }

    public void setMessageId (long messageId) {
        this.messageId = messageId;
    }

    public long getTimeStamp () {
        return timeStamp;
    }

    public void setTimeStamp (long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public MessageType getMessageType () {
        return messageType;
    }

    public void setMessageType (MessageType messageType) {
        this.messageType = messageType;
    }

    public String getMessage () {
        return message;
    }

    public void setMessage (String message) {
        this.message = message;
    }

    public User getSender () {
        return sender;
    }

    public void setSender (User sender) {
        this.sender = sender;
    }

    public User getReceiver () {
        return receiver;
    }

    public void setReceiver (User receiver) {
        this.receiver = receiver;
    }

    @Override
    public String toString () {
        return "Message{" +
                "id=" + id +
                ", messageId=" + messageId +
                ", timeStamp=" + timeStamp +
                ", messageType=" + messageType +
                ", message='" + message + '\'' +
                ", sender=" + sender +
                ", receiver=" + receiver +
                '}';
    }
}
