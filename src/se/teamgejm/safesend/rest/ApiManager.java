package se.teamgejm.safesend.rest;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import se.teamgejm.safesend.SafeSendConstants;

/**
 * @author Emil Stjerneman
 */
public final class ApiManager {

    private static final RestAdapter REST_ADAPTER = new RestAdapter.Builder()
            .setEndpoint(SafeSendConstants.API_URI)
            .setClient(new OkClient())
            .setLogLevel(RestAdapter.LogLevel.FULL)
                    //.setErrorHandler(new RestErrorHandler())
                    //.setRequestInterceptor(new GroggboxRequestInterceptor())
            .build();

    private static final SafeSendService SAFESEND_SERVICE = REST_ADAPTER.create(SafeSendService.class);

    public static SafeSendService getSafesendService () {
        return SAFESEND_SERVICE;
    }
}
