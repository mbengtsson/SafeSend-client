package se.teamgejm.safesend.activities;

import java.security.Security;

import se.teamgejm.safesend.R;
import se.teamgejm.safesend.pgp.PgpHelper;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class TestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
		
		Button genKeys = (Button) findViewById(R.id.genKey);
		genKeys.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PgpHelper.generateKeyPair(v.getContext());
			}
		});
		
		Button encrypt = (Button) findViewById(R.id.encrypt);
		encrypt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Get the text from the user
				EditText text = (EditText) findViewById(R.id.message);
				// Create a file of the message
				PgpHelper.createMessage(v.getContext(), text.getText().toString());
				// Sign and encrypt
				String encryptedMessage = PgpHelper.signAndEncrypt(v.getContext());
				text.setText(encryptedMessage);
			}
		});
		
		Button decrypt = (Button) findViewById(R.id.decrypt);
		decrypt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Decrypt and display the message
				EditText text = (EditText) findViewById(R.id.message);
				String message = PgpHelper.decryptAndVerify(v.getContext());
				text.setText(message);
			}
		});
	}

}
