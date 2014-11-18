package se.teamgejm.safesend.events;

/**
 * @author Emil Stjerneman
 */
public class UserPubkeySuccessEvent {

    private String pubkey;

    public UserPubkeySuccessEvent (String pubkey) {
        this.pubkey = pubkey;
    }

    public String getPubkey () {
        return pubkey;
    }
}
