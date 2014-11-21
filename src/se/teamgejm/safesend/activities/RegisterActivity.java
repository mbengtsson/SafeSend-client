package se.teamgejm.safesend.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import de.greenrobot.event.EventBus;
import org.spongycastle.openpgp.PGPException;
import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.util.encoders.Base64;
import se.teamgejm.safesend.R;
import se.teamgejm.safesend.entities.UserCredentials;
import se.teamgejm.safesend.entities.request.RegisterUserRequest;
import se.teamgejm.safesend.events.RegisterFailedEvent;
import se.teamgejm.safesend.events.RegisterSuccessEvent;
import se.teamgejm.safesend.io.UserCredentialsHelper;
import se.teamgejm.safesend.rest.RegisterUser;
import se.teamgejm.safesend.rsa.PgpHelper;
import se.teamgejm.safesend.rsa.PgpUtils;

import java.io.IOException;

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

        progressBar = (ProgressBar) findViewById(R.id.regsiter_process_bar);

        registerForm = (LinearLayout) findViewById(R.id.register_form);

        final Button registerButton = (Button) findViewById(R.id.register_button_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                showProgress();
                registerUser();
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

    public void onEvent (RegisterFailedEvent event) {
        hideProgress();
        Toast.makeText(this, event.getError().getMessage(), Toast.LENGTH_LONG).show();
    }

    public void onEvent (RegisterSuccessEvent event) {
        hideProgress();

        final String password = ((TextView) findViewById(R.id.register_password)).getText().toString();

        final UserCredentials userCredentials = new UserCredentials();
        userCredentials.setEmail(event.getUser().getEmail());
        userCredentials.setDisplayName(event.getUser().getDisplayName());
        userCredentials.setPassword(password);

        UserCredentialsHelper.writeUserCredentials(getApplicationContext(), userCredentials);
        this.finish();
    }

    private void registerUser () {
        final String email = ((TextView) findViewById(R.id.register_email)).getText().toString();
        final String displayName = ((TextView) findViewById(R.id.register_display_name)).getText().toString();
        final String password = ((TextView) findViewById(R.id.register_password)).getText().toString();

        // Generate public and private keys.
        PgpHelper.generateKeyPair(getApplicationContext(), email, password);

        try {
            final PGPPublicKey pgpPublicKey = PgpUtils.readPublicKey(getApplicationContext(), "public.asc");
            final byte[] encodedPubKey = pgpPublicKey.getEncoded();
            final String base64PubKey = Base64.toBase64String(encodedPubKey);
            RegisterUser.call(new RegisterUserRequest(email, displayName, password, base64PubKey));
        }
        catch (IOException | PGPException e) {
            hideProgress();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, e.getMessage());
        }
    }

    private void showProgress () {
        progressBar.setVisibility(View.VISIBLE);
        registerForm.setVisibility(View.GONE);
    }

    private void hideProgress () {
        progressBar.setVisibility(View.GONE);
        registerForm.setVisibility(View.VISIBLE);
    }
}
