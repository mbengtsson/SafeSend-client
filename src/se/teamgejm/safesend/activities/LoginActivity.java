package se.teamgejm.safesend.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import de.greenrobot.event.EventBus;
import se.teamgejm.safesend.R;
import se.teamgejm.safesend.SafeSendApplication;
import se.teamgejm.safesend.entities.UserCredentials;
import se.teamgejm.safesend.entities.request.ValidateCredentialsRequest;
import se.teamgejm.safesend.events.UserCredentialsFailedEvent;
import se.teamgejm.safesend.events.UserCredentialsSuccessEvent;
import se.teamgejm.safesend.io.UserCredentialsHelper;
import se.teamgejm.safesend.rest.ValidateCredentials;

/**
 * @author Emil Stjerneman
 */
public class LoginActivity extends Activity {

    private final static String TAG = "LoginActivity";

    private ProgressBar progressBar;

    private LinearLayout loginContainer;

    private LinearLayout registerContainer;

    private EditText passwordField;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginContainer = (LinearLayout) findViewById(R.id.login_container);
        registerContainer = (LinearLayout) findViewById(R.id.register_container);
        passwordField = (EditText) findViewById(R.id.login_password);

        final Button registerButton = (Button) findViewById(R.id.login_button_register);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                final Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        final Button loginButton = (Button) findViewById(R.id.login_button_login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                showProgress();
                ValidateCredentials.call(new ValidateCredentialsRequest(SafeSendApplication.getCurrentUser().getEmail(), passwordField.getText().toString()));
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.login_progress_bar);
    }

    @Override
    protected void onResume () {
        super.onResume();

        Log.d(TAG, "Here");
        if (SafeSendApplication.getCurrentUser() == null) {
            Log.d(TAG, "Here 1");
            final UserCredentials userCredentials = UserCredentialsHelper.getInstance().readUserCredentials(getApplicationContext());
            Log.d(TAG, "Here 2");
            SafeSendApplication.setCurrentUser(userCredentials);
            Log.d(TAG, "Here 3");
        }

        // No email means that the user is not registered.
        if (SafeSendApplication.getCurrentUser() == null) {
            // Showing register container by default.
            Log.d(TAG, "Here 4");
        }
        // No password means that the user is not logged in.
        else if (SafeSendApplication.getCurrentUser().getPassword() == null) {
            Log.d(TAG, "Here 5");
            registerContainer.setVisibility(View.GONE);
            loginContainer.setVisibility(View.VISIBLE);
        }
        // The user is registered and have a password.
        else {
            Log.d(TAG, "Here 6");
            showProgress();
            ValidateCredentials.call(new ValidateCredentialsRequest(SafeSendApplication.getCurrentUser().getEmail(), SafeSendApplication.getCurrentUser().getPassword()));
        }
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

    private void showProgress () {
        progressBar.setVisibility(View.VISIBLE);
        loginContainer.setVisibility(View.GONE);
    }

    private void hideProgress () {
        progressBar.setVisibility(View.GONE);
        loginContainer.setVisibility(View.VISIBLE);
    }

    public void onEvent (UserCredentialsSuccessEvent event) {
        SafeSendApplication.getCurrentUser().setPassword(passwordField.getText().toString());
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void onEvent (UserCredentialsFailedEvent event) {
        hideProgress();
        Toast.makeText(this, "Login failed.", Toast.LENGTH_LONG).show();
    }
}
