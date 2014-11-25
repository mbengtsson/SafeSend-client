package se.teamgejm.safesend.events;

import retrofit.RetrofitError;

/**
 * 
 * @author Gustav
 *
 */
public class MessageListFailedEvent extends ErrorEvent{

	public MessageListFailedEvent(RetrofitError error) {
		super(error);
	}

}
