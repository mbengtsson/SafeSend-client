package se.teamgejm.safesend.activities;

import java.io.IOException;
import java.nio.charset.Charset;

import se.teamgejm.safesend.R;
import se.teamgejm.safesend.rsa.RsaHelper;
import se.teamgejm.safesend.rsa.RsaUtils;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class TestActivity extends Activity {
	
	private byte[] encryptedMessage = null;
	private byte[] signature = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		
		Button genKeys = (Button) findViewById(R.id.genKey);
		genKeys.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RsaHelper.getInstance().createKeyPair(v.getContext());
			}
		});
		
		Button encrypt = (Button) findViewById(R.id.encrypt);
		encrypt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText text = (EditText) findViewById(R.id.message);
				byte[] message = text.getText().toString().getBytes();
				
				try {
					encryptedMessage = RsaHelper.getInstance().encryptWithPublicKey(message, RsaUtils.fileToString("pubKey.key", v.getContext()));
					signature = RsaHelper.getInstance().signWithPrivateKey(encryptedMessage, RsaUtils.fileToString("privKey.key", v.getContext()));
					
					String encMessage = new String(encryptedMessage, Charset.defaultCharset());
					text.setText(encMessage);
					Log.i("message-encrypted", encMessage);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
			}
		});
		
		Button decrypt = (Button) findViewById(R.id.decrypt);
		decrypt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText text = (EditText) findViewById(R.id.message);

				byte[] decryptedMessage = null;
				
				try {
					if (RsaHelper.getInstance().verifyWithPublicKey(encryptedMessage, signature, RsaUtils.fileToString("pubKey.key", v.getContext()))) {
						Log.i("MainActivity", "Verified");
						decryptedMessage = RsaHelper.getInstance().decryptWithPrivateKey(encryptedMessage, RsaUtils.fileToString("privKey.key", v.getContext()));
						String decMessage = new String(decryptedMessage, Charset.defaultCharset());
						text.setText(decMessage);
						Log.i("message-decrypted", decMessage);
					} else {
						Log.i("MainActivity", "Not verified");
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
