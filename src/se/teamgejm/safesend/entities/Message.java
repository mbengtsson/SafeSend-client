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

    private transient long id;

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

    private int status;

    public Message () {
    }

    public Message (long id, User sender, User receiver, String senderPublicKey, long messageId, String message, long timeStamp, int status) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.senderPublicKey = senderPublicKey;
        this.messageId = messageId;
        this.message = message;
        this.timeStamp = timeStamp;
        this.status = status;
    }

    public long getId () {
        return id;
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

    public int getStatus () {
        return status;
    }

    public void setStatus (int status) {
        this.status = status;
    }

    @Override
    public String toString () {
        return "Message{" +
                "id=" + id +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", senderPublicKey='" + senderPublicKey + '\'' +
                ", messageId=" + messageId +
                ", message='" + message + '\'' +
                ", timeStamp=" + timeStamp +
                ", status=" + status +
                '}';
    }
}
