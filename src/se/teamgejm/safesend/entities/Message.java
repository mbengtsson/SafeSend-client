package se.teamgejm.safesend.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author Gustav
 */
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    public final static int STATUS_ENCRYPTED = 0;
    public final static int STATUS_DECRYPTED = 1;

    private long _id;

    @SerializedName("sender")
    private User sender;

    @SerializedName("receiver")
    private User receiver;

    @SerializedName("senderPublicKey")
    private String senderPublicKey;

    @SerializedName("id")
    private long messageId;

    @SerializedName("message")
    private String message;

    @SerializedName("timeStamp")
    private long timeStamp;

    public Message () {
    }

    public Message (User sender, User receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timeStamp = System.currentTimeMillis();
    }

    public Message (User sender, User receiver, String message, long _id) {
        this(sender, receiver, message);
        this._id = _id;
    }

    public Message (User sender, User receiver, String message, long _id, long timeStamp) {
        this(sender, receiver, message, _id);
        this.timeStamp = timeStamp;
    }

    public long getId () {
        return _id;
    }

    public User getSender () {
        return sender;
    }

    public User getReceiver () {
        return receiver;
    }

    public String getSenderPublicKey () {
        return senderPublicKey;
    }

    public long getMessageId () {
        return messageId;
    }

    public String getMessage () {
        return message;
    }

    public void setMessage (String message) {
        this.message = message;
    }

    public long getTimeStamp () {
        return timeStamp;
    }

    @Override
    public String toString () {
        return "Message{" +
                "_id=" + _id +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", senderPublicKey='" + senderPublicKey + '\'' +
                ", messageId=" + messageId +
                ", message='" + message + '\'' +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
