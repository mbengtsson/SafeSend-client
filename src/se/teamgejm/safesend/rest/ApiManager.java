package se.teamgejm.safesend.rest;

import com.squareup.okhttp.OkHttpClient;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import se.teamgejm.safesend.SafeSendConstants;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;


/**
 * @author Emil Stjerneman
 */
public final class ApiManager {

    private static final RestAdapter REST_ADAPTER = new RestAdapter.Builder()
            .setEndpoint(SafeSendConstants.API_URI)
            .setClient(new OkClient(ApiManager.getUnsafeOkHttpClient()))
            .setLogLevel(RestAdapter.LogLevel.FULL)
                    //.setErrorHandler(new RestErrorHandler())
                    //.setRequestInterceptor(new GroggboxRequestInterceptor())
            .build();

    private static final SafeSendService SAFESEND_SERVICE = REST_ADAPTER.create(SafeSendService.class);

    public static SafeSendService getSafesendService () {
        return SAFESEND_SERVICE;
    }

    private static OkHttpClient getUnsafeOkHttpClient () {
        try {
            // Install the all-trusting trust manager.
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, ApiManager.getTrustManager(), new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager.
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            final OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setSslSocketFactory(sslSocketFactory);
            okHttpClient.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify (String hostname, SSLSession session) {
                    // Allow all hostnames.
                    return true;
                }
            });

            return okHttpClient;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static TrustManager[] getTrustManager () {
        // Create a trust manager that does not validate certificate chains.
        final TrustManager[] trustManagers = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted (X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted (X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers () {
                        return null;
                    }
                }
        };

        return trustManagers;
    }
}
