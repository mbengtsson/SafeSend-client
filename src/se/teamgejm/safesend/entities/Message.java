package se.teamgejm.safesend.entities;

import se.teamgejm.safesend.enums.MessageType;


public class Message {

    private User origin;
    private String timestamp;
    private MessageType messageType;
    private String messageData;

    public Message () {

    }

    public Message (User origin, String timestamp, MessageType messageType) {
        setOrigin(origin);
        setTimestamp(timestamp);
        setMessageType(messageType);
    }

    public User getOrigin () {
        return origin;
    }

    public void setOrigin (User origin) {
        this.origin = origin;
    }

    public String getTimestamp () {
        return timestamp;
    }

    public void setTimestamp (String timestamp) {
        this.timestamp = timestamp;
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

    @Override
    public String toString () {
        return "Origin: " + origin + " Timestamp: " + timestamp + " Msgtype: " + messageType.getNiceName();
    }

}
