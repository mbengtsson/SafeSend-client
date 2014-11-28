package se.teamgejm.safesend.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import de.greenrobot.event.EventBus;
import org.spongycastle.util.encoders.Base64;
import se.teamgejm.safesend.R;
import se.teamgejm.safesend.entities.CurrentUser;
import se.teamgejm.safesend.entities.request.RegisterUserRequest;
import se.teamgejm.safesend.events.RegisterFailedEvent;
import se.teamgejm.safesend.events.RegisterSuccessEvent;
import se.teamgejm.safesend.io.CurrentUserHelper;
import se.teamgejm.safesend.rest.RegisterUser;
import se.teamgejm.safesend.service.GenerateKeysIntentService;

import java.security.Security;

/**
 * @author Emil Stjerneman
 */
public class RegisterActivity extends Activity {

    private final static String TAG = "RegisterActivity";

    private ProgressBar progressBar;
    private LinearLayout registerForm;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);

        progressBar = (ProgressBar) findViewById(R.id.regsiter_process_bar);

        registerForm = (LinearLayout) findViewById(R.id.register_form);

        final Button registerButton = (Button) findViewById(R.id.register_button_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                IntentFilter filter = new IntentFilter(GenerateKeysResponseReciever.ACTION_RESP);
                filter.addCategory(Intent.CATEGORY_DEFAULT);
                registerReceiver(new GenerateKeysResponseReciever(), filter);
                showProgress();
                generateKeyPairs();
            }
        });
    }

    @Override
    public void onStart () {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop () {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /**
     * Handle RegisterFailedEvent events.
     *
     * This will happen if the registration goes wrong.
     */
    public void onEvent (final RegisterFailedEvent event) {
        hideProgress();
        // Just tell the user that something went wrong.
        Toast.makeText(this, event.getError().getMessage(), Toast.LENGTH_LONG).show();
    }

    /**
     * Handle RegisterSuccessEvent events.
     *
     * This will happen if the registration is successfully completed.
     */
    public void onEvent (final RegisterSuccessEvent event) {
        hideProgress();

        // Get the password from the input field as its not returned by the server.
        final String password = ((TextView) findViewById(R.id.register_password)).getText().toString();

        CurrentUser.getInstance().setId(event.getUserResponse().getId());
        CurrentUser.getInstance().setEmail(event.getUserResponse().getEmail());
        CurrentUser.getInstance().setPassword(password);

        // Save user details (not password) to a local file.
        CurrentUserHelper.writeCurrentUserDetails(getApplicationContext());

        this.finish();
    }

    private void generateKeyPairs () {
        final String email = ((TextView) findViewById(R.id.register_email)).getText().toString();
        final String password = ((TextView) findViewById(R.id.register_password)).getText().toString();

        // Start service for generating key pairs.
        Intent genKeyIntent = new Intent(this, GenerateKeysIntentService.class);
        genKeyIntent.putExtra(GenerateKeysIntentService.EMAIL_IN, email);
        genKeyIntent.putExtra(GenerateKeysIntentService.PASSWORD_IN, password);
        startService(genKeyIntent);
    }

    private void registerUser (String publicKey) {
        final String displayName = ((TextView) findViewById(R.id.register_display_name)).getText().toString();
        final String email = ((TextView) findViewById(R.id.register_email)).getText().toString();
        final String password = ((TextView) findViewById(R.id.register_password)).getText().toString();

        RegisterUser.call(new RegisterUserRequest(email, displayName, password, Base64.toBase64String(publicKey.getBytes())));
    }

    private void showProgress () {
        progressBar.setVisibility(View.VISIBLE);
        registerForm.setVisibility(View.GONE);
    }

    private void hideProgress () {
        progressBar.setVisibility(View.GONE);
        registerForm.setVisibility(View.VISIBLE);
    }

    /**
     * Broadcast receiver called when key generation is complete.
     *
     * @author Gustav
     */
    public class GenerateKeysResponseReciever extends BroadcastReceiver {

        public static final String ACTION_RESP = "se.teamgejm.intent.action.MESSAGE_PROCESSED";

        @Override
        public void onReceive (Context context, Intent intent) {
            unregisterReceiver(this);
            final String publicKey = intent.getStringExtra(GenerateKeysIntentService.PUBLIC_KEY_OUT);
            registerUser(publicKey);
        }

    }
}
