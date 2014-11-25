package se.teamgejm.safesend.events;

import retrofit.RetrofitError;

public class SendMessageFailedEvent extends ErrorEvent {

	public SendMessageFailedEvent(RetrofitError error) {
		super(error);
	}

}
