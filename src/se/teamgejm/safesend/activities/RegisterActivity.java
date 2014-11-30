package se.teamgejm.safesend.activities;

import java.security.Security;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongycastle.util.encoders.Base64;

import se.teamgejm.safesend.R;
import se.teamgejm.safesend.database.dao.DbUserDao;
import se.teamgejm.safesend.entities.CurrentUser;
import se.teamgejm.safesend.entities.request.RegisterUserRequest;
import se.teamgejm.safesend.events.RegisterFailedEvent;
import se.teamgejm.safesend.events.RegisterSuccessEvent;
import se.teamgejm.safesend.io.CurrentUserHelper;
import se.teamgejm.safesend.rest.RegisterUser;
import se.teamgejm.safesend.service.GenerateKeysIntentService;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import de.greenrobot.event.EventBus;

/**
 * @author Emil Stjerneman
 */
public class RegisterActivity extends Activity {

    private final static String TAG = "RegisterActivity";

    private ProgressBar progressBar;
    private LinearLayout registerForm;
    
    private static final String EMAIL_PATTERN = 
    		"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
    		+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    
    private static final String DISPLAYNAME_PATTERN = "^[A-Za-z0-9]+(?:[ .][A-Za-z0-9]+)*$";
    
    private GenerateKeysResponseReciever genKeysReceiver;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);

        progressBar = (ProgressBar) findViewById(R.id.regsiter_process_bar);

        registerForm = (LinearLayout) findViewById(R.id.register_form);
        
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.title_register));
        }

        final Button registerButton = (Button) findViewById(R.id.register_button_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                showProgress();
                generateKeyPairs();
            }
        });
    }

    @Override
    public void onStart () {
        super.onStart();
        IntentFilter filter = new IntentFilter(GenerateKeysResponseReciever.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(genKeysReceiver = new GenerateKeysResponseReciever(), filter);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop () {
        EventBus.getDefault().unregister(this);
        unregisterReceiver(genKeysReceiver);
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
        // Get the password from the input field as its not returned by the server.
        final String password = ((TextView) findViewById(R.id.register_password)).getText().toString();

        CurrentUser.getInstance().setUserId(event.getUserResponse().getId());
        CurrentUser.getInstance().setEmail(event.getUserResponse().getEmail());
        CurrentUser.getInstance().setDisplayName(event.getUserResponse().getDisplayName());
        CurrentUser.getInstance().setPassword(password);

        // Save user details (not password) to a local file.
        CurrentUserHelper.writeCurrentUserDetails(getApplicationContext());

        DbUserDao dbUserDao = new DbUserDao(this);
        dbUserDao.open();

        dbUserDao.addUser(CurrentUser.getInstance());

        dbUserDao.close();

        hideProgress();
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
        final String displayName = ((TextView) findViewById(R.id.register_display_name)).getText().toString().trim();
        final String email = ((TextView) findViewById(R.id.register_email)).getText().toString().trim();
        final String password = ((TextView) findViewById(R.id.register_password)).getText().toString();
        
        Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
        Matcher emailMatcher = emailPattern.matcher(email);
        
        Pattern displayNamePattern = Pattern.compile(DISPLAYNAME_PATTERN);
        Matcher displayNameMatcher = displayNamePattern.matcher(displayName);
        
        if (displayName == null || displayName.length() < 4 || displayName.length() > 29) {
        	Toast.makeText(getApplicationContext(), getString(R.string.register_fail_displayname), Toast.LENGTH_SHORT).show();
        	hideProgress();
        	return;
        } else if (!displayNameMatcher.matches()) {
        	Toast.makeText(getApplicationContext(), getString(R.string.register_fail_displayname_char), Toast.LENGTH_SHORT).show();
        	hideProgress();
        	return;
        } else if (email == null || !emailMatcher.matches()) {
        	Toast.makeText(getApplicationContext(), getString(R.string.register_fail_email), Toast.LENGTH_SHORT).show();
        	hideProgress();
        	return;
        } else if (password == null || password.length() < 8) {
        	Toast.makeText(getApplicationContext(), getString(R.string.register_fail_password), Toast.LENGTH_SHORT).show();
        	hideProgress();
        	return;
        }

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
            final String publicKey = intent.getStringExtra(GenerateKeysIntentService.PUBLIC_KEY_OUT);
            registerUser(publicKey);
        }

    }
}
