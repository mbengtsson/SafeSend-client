package se.teamgejm.safesend.database.model;

import se.teamgejm.safesend.entities.User;

/**
 * @author Emil Stjerneman
 */
public class DbMessage {

    private long id;
    private long messageId;
    private String status;
    private String message;
    private User sender;
    private User receiver;

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

    public String getStatus () {
        return status;
    }

    public void setStatus (String status) {
        this.status = status;
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
        return "DbMessage{" +
                "id=" + id +
                ", messageId=" + messageId +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", sender=" + sender +
                ", receiver=" + receiver +
                '}';
    }
}
