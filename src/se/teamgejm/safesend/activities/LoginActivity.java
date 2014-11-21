package se.teamgejm.safesend.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import se.teamgejm.safesend.R;
import se.teamgejm.safesend.entities.UserCredentials;
import se.teamgejm.safesend.io.UserCredentialsHelper;

/**
 * @author Emil Stjerneman
 */
public class LoginActivity extends Activity {

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Button registerButton = (Button) findViewById(R.id.login_button_register);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                final Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume () {
        super.onResume();

        final UserCredentials userCredentials = UserCredentialsHelper.readUserCredentials(getApplicationContext());

        if (userCredentials != null) {
            final Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}