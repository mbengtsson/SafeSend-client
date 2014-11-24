package se.teamgejm.safesend.events;

import retrofit.RetrofitError;

/**
 * 
 * @author Gustav
 *
 */
public class MessageByIdFailedEvent extends ErrorEvent {

	public MessageByIdFailedEvent(RetrofitError error) {
		super(error);
	}

}
