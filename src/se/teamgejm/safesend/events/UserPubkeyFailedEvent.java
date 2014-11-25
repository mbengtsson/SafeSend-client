package se.teamgejm.safesend.events;

import retrofit.RetrofitError;

public class UserPubkeyFailedEvent extends ErrorEvent {

	public UserPubkeyFailedEvent(RetrofitError error) {
		super(error);
	}

}
