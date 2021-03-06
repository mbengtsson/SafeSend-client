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

    private static final RestAdapter REST_ADAPTER_NO_AUTH = new RestAdapter.Builder()
            .setEndpoint(SafeSendConstants.API_URI)
            .setClient(new OkClient(ApiManager.getUnsafeOkHttpClient()))
            .setLogLevel(RestAdapter.LogLevel.NONE)
            .build();

    private static final RestAdapter REST_ADAPTER_WITH_AUTH = new RestAdapter.Builder()
            .setEndpoint(SafeSendConstants.API_URI)
            .setClient(new OkClient(ApiManager.getUnsafeOkHttpClient()))
            .setLogLevel(RestAdapter.LogLevel.NONE)
            .setRequestInterceptor(new SafeSendInterceptor())
            .build();

    private static final SafeSendService SAFESEND_SERVICE_NO_AUTH = REST_ADAPTER_NO_AUTH.create(SafeSendService.class);
    private static final SafeSendService SAFESEND_SERVICE = REST_ADAPTER_WITH_AUTH.create(SafeSendService.class);

    public static SafeSendService getSafesendService () {
        return SAFESEND_SERVICE;
    }

    public static SafeSendService getSafesendServiceNoAuth () {
        return SAFESEND_SERVICE_NO_AUTH;
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
