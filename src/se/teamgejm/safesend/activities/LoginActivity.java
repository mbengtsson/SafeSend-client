package se.teamgejm.safesend.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import se.teamgejm.safesend.R;

/**
 * @author Emil Stjerneman
 */
public class LoginActivity extends Activity {

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button registerButton = (Button) findViewById(R.id.login_button_register);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                final Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
}
