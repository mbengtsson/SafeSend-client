package se.teamgejm.safesend.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import de.greenrobot.event.EventBus;
import se.teamgejm.safesend.R;
import se.teamgejm.safesend.entities.CurrentUser;
import se.teamgejm.safesend.entities.request.ValidateCredentialsRequest;
import se.teamgejm.safesend.events.UserCredentialsFailedEvent;
import se.teamgejm.safesend.events.UserCredentialsSuccessEvent;
import se.teamgejm.safesend.io.CurrentUserHelper;
import se.teamgejm.safesend.rest.ValidateCredentials;

import java.util.Arrays;

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
        
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.title_login));
        }

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
                ValidateCredentials.call(new ValidateCredentialsRequest(CurrentUser.getInstance().getEmail(), passwordField.getText().toString()));
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.login_progress_bar);

        if (Arrays.asList(getApplicationContext().fileList()).contains(CurrentUserHelper.CREDENTIAL_FILE)) {
            CurrentUserHelper.readCurrentUserDetails(getApplicationContext());
        }
    }

    @Override
    protected void onResume () {
        super.onResume();

        if (CurrentUser.getInstance().getEmail() == null) {
            // Showing register container by default.
        }
        // No password means that the user is not logged in.
        else if (CurrentUser.getInstance().getPassword() == null) {
            registerContainer.setVisibility(View.GONE);
            loginContainer.setVisibility(View.VISIBLE);
            TextView loginLabel = (TextView) findViewById(R.id.login_label);
            loginLabel.setText(getString(R.string.login_label) + " " + CurrentUser.getInstance().getDisplayName());
        }
        // The user is registered and have a password.
        else {
            showProgress();
            registerContainer.setVisibility(View.GONE);
            ValidateCredentials.call(new ValidateCredentialsRequest(CurrentUser.getInstance().getEmail(), CurrentUser.getInstance().getPassword()));
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

    /**
     * Handle UserCredentialsFailedEvent events.
     *
     * This will happen if the credentials could not be validated.
     */
    public void onEvent (final UserCredentialsFailedEvent event) {
        hideProgress();
        Toast.makeText(this, "Login failed.", Toast.LENGTH_LONG).show();
        CurrentUser.getInstance().setPassword(null);
    }

    /**
     * Handle UserCredentialsSuccessEvent events.
     *
     * This will happen if the credentials were validated successfully.
     */
    public void onEvent (final UserCredentialsSuccessEvent event) {
        if (CurrentUser.getInstance().getPassword() == null) {
            CurrentUser.getInstance().setPassword(passwordField.getText().toString());
        }
        hideProgress();
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }
}
