package se.teamgejm.safesend.activities;

import se.teamgejm.safesend.R;
import se.teamgejm.safesend.entities.User;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class SendMessageActivity extends Activity {
	
	public static final String INTENT_USER = "user";
	
	private User user;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        
        setOnClickListeners();
        
        if (getIntent().hasExtra(INTENT_USER)) {
        	setUser((User) getIntent().getSerializableExtra(INTENT_USER));
        }
        
        TextView username = (TextView) findViewById(R.id.message_send_to);
        username.setText(getString(R.string.send_to) + " " + getUser().getUsername());
        
    }
    
    private void setOnClickListeners() {
    	ImageButton sendBtn = (ImageButton) findViewById(R.id.message_send_button);
    	sendBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i("SendMessageActivity", "Send button clicked");
				encryptAndSend();
			}
		});
    }
    
    private void encryptAndSend() {
    	// TODO sign and encrypt the message and send to server
    }

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
