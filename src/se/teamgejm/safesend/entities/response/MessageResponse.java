package se.teamgejm.safesend.entities.response;

import se.teamgejm.safesend.entities.User;

import java.io.Serializable;

/**
 * @author Gustav
 */
public class MessageResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private long messageId;
    private User sender;
    private String senderPublicKey;
    private String message;
    private String timeStamp;

    public long getMessageId () {
        return messageId;
    }

    public User getSender () {
        return sender;
    }

    public String getSenderPublicKey () {
        return senderPublicKey;
    }

    public String getMessage () {
        return message;
    }

    // TODO: See if we can use the normal message object instead.
    public void setMessage (String message) {
        this.message = message;
    }

    public String getTimeStamp () {
        return timeStamp;
    }

    @Override
    public String toString () {
        return "MessageResponse{" +
                "messageId=" + messageId +
                ", sender=" + sender +
                ", senderPublicKey='" + senderPublicKey + '\'' +
                ", message='" + message + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }
}
