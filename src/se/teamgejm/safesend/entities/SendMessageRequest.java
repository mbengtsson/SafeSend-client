package se.teamgejm.safesend.entities;

/**
 * @author Emil Stjerneman
 */
public class SendMessageRequest {

    private long senderId;

    private long receiverId;

    private String message;

    private String password;

    public long getSenderId () {
        return senderId;
    }

    public void setSenderId (long senderId) {
        this.senderId = senderId;
    }

    public long getReceiverId () {
        return receiverId;
    }

    public void setReceiverId (long receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage () {
        return message;
    }

    public void setMessage (String message) {
        this.message = message;
    }

    public String getPassword () {
        return password;
    }

    public void setPassword (String password) {
        this.password = password;
    }
}
