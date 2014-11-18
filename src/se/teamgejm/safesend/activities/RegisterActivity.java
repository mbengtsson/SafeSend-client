package se.teamgejm.safesend.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import de.greenrobot.event.EventBus;
import se.teamgejm.safesend.R;
import se.teamgejm.safesend.entities.request.RegisterUserRequest;
import se.teamgejm.safesend.events.RegisterFailedEvent;
import se.teamgejm.safesend.events.RegisterSuccessEvent;
import se.teamgejm.safesend.rest.RegisterUser;

/**
 * @author Emil Stjerneman
 */
public class RegisterActivity extends Activity {

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
        Toast.makeText(this, "Register failed", Toast.LENGTH_LONG).show();
    }

    public void onEvent (RegisterSuccessEvent event) {
        hideProgress();
        Toast.makeText(this, "Register success", Toast.LENGTH_LONG).show();
        // TODO: Save something so we can use this to authenticate later and
        // "login" the user.
    }

    private void registerUser () {
        final String username = ((TextView) findViewById(R.id.register_email)).getText().toString();
        final String password = ((TextView) findViewById(R.id.register_password)).getText().toString();

        RegisterUser.call(new RegisterUserRequest(username, password, "test"));
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
