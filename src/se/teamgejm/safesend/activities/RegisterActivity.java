package se.teamgejm.safesend.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import de.greenrobot.event.EventBus;
import org.spongycastle.util.encoders.Base64;
import se.teamgejm.safesend.R;
import se.teamgejm.safesend.activities.OpenMessageActivity.DecryptMessageResponseReciever;
import se.teamgejm.safesend.entities.UserCredentials;
import se.teamgejm.safesend.entities.request.RegisterUserRequest;
import se.teamgejm.safesend.events.RegisterFailedEvent;
import se.teamgejm.safesend.events.RegisterSuccessEvent;
import se.teamgejm.safesend.io.UserCredentialsHelper;
import se.teamgejm.safesend.pgp.PgpHelper;
import se.teamgejm.safesend.rest.RegisterUser;
import se.teamgejm.safesend.service.DecryptMessageIntentService;
import se.teamgejm.safesend.service.EncryptMessageIntentService;
import se.teamgejm.safesend.service.GenerateKeysIntentService;

import java.io.IOException;
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

        UserCredentials.getInstance().setEmail(event.getUserResponse().getEmail());
        UserCredentials.getInstance().setPassword(password);

        // Save the credentials (not password) to a local file.
        UserCredentialsHelper.writeUserCredentials(getApplicationContext());

        this.finish();
    }

    private void generateKeyPairs () {
        final String email = ((TextView) findViewById(R.id.register_email)).getText().toString();
        final String password = ((TextView) findViewById(R.id.register_password)).getText().toString();
        
        Intent genKeyIntent = new Intent(this, GenerateKeysIntentService.class);
        genKeyIntent.putExtra(GenerateKeysIntentService.EXTRA_EMAIL, email);
        genKeyIntent.putExtra(GenerateKeysIntentService.EXTRA_PWD, password);
        startService(genKeyIntent);
    }
    
    private void registerUser(String publicKey) {
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
     * 
     * @author Gustav
     *
     */
    public class GenerateKeysResponseReciever extends BroadcastReceiver {
    	
    	public static final String ACTION_RESP = "se.teamgejm.intent.action.MESSAGE_PROCESSED";

    	@Override
    	public void onReceive(Context context, Intent intent) {
			final String publicKey = intent.getStringExtra(GenerateKeysIntentService.PUBLIC_KEY);
			registerUser(publicKey);
			unregisterReceiver(this);
    	}

    }
}
