package se.teamgejm.safesend.rest;

import android.util.Base64;
import retrofit.RequestInterceptor;
import se.teamgejm.safesend.entities.CurrentUser;

/**
 * @author Emil Stjerneman
 */
public class SafeSendInterceptor implements RequestInterceptor {

    @Override
    public void intercept (RequestFacade requestFacade) {
        final String email = CurrentUser.getInstance().getEmail();
        final String password = CurrentUser.getInstance().getPassword();
        final String auth = email + ":" + password;

        requestFacade.addHeader("Authorization", "Basic " + Base64.encodeToString(auth.getBytes(), Base64.DEFAULT));
    }
}
