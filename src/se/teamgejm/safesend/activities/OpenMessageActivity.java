package se.teamgejm.safesend.activities;

import se.teamgejm.safesend.R;
import se.teamgejm.safesend.entities.Message;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class OpenMessageActivity extends Activity {
	
	public static final String INTENT_MESSAGE = "message";
	
	private Message message;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_message);
        
        setOnClickListeners();
        
        if (getIntent().hasExtra(INTENT_MESSAGE)) {
        	message = (Message) getIntent().getSerializableExtra(INTENT_MESSAGE);
        }
        
        TextView origin = (TextView) findViewById(R.id.message_origin);
        origin.setText(getString(R.string.from) + " " + getMessage().getOrigin().getUsername());
        
        TextView time = (TextView) findViewById(R.id.message_time);
        time.setText(getMessage().getTimestamp());
        
        TextView type = (TextView) findViewById(R.id.message_type);
        type.setText(getString(R.string.type) + " " + getMessage().getMessageType());
    }

	private void setOnClickListeners() {
		ImageButton openBtn = (ImageButton) findViewById(R.id.message_open_button);
		openBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i("OpenMessageActivity", "Open message button clicked");
				decryptAndOpen();
			}
		});
	}
	
	private void decryptAndOpen() {
		// TODO decrypt and show the message
		
	}
	
    public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}
}
