package se.teamgejm.safesend.rest;

import android.util.Base64;
import retrofit.RequestInterceptor;
import se.teamgejm.safesend.SafeSendApplication;

/**
 * @author Emil Stjerneman
 */
public class SafeSendInterceptor implements RequestInterceptor {

    @Override
    public void intercept (RequestFacade requestFacade) {
        final String email = SafeSendApplication.getCurrentUser().getEmail();
        final String password = SafeSendApplication.getCurrentUser().getPassword();
        final String auth = email + ":" + password;
        System.out.println(authpass);
        requestFacade.addHeader("Authorization", "Basic " + Base64.encodeToString(auth.getBytes(), Base64.DEFAULT));
    }
}
