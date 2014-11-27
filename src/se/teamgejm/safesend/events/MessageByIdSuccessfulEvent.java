package se.teamgejm.safesend.events;

import se.teamgejm.safesend.entities.Message;

/**
 * @author Gustav
 */
public class MessageByIdSuccessfulEvent {

    private Message message;

    public MessageByIdSuccessfulEvent (Message response) {
        this.message = response;
    }

    public Message getMessage () {
        return message;
    }
}
