package se.teamgejm.safesend.rsa;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.spec.RSAKeyGenParameterSpec;

import javax.crypto.Cipher;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

public class RsaHelper {
	
private static RsaHelper INSTANCE = null;
	
	public static RsaHelper getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new RsaHelper();
		}
		return INSTANCE;
	}
	
	private RsaHelper() {
		Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
	}
	
	public void createKeyPair(Context context) {
		KeyPair kp = null;
		FileOutputStream out1 = null;
		FileOutputStream out2 = null;
		
		try {
			kp = generateRSAKeyPair();
			
			out1 = context.openFileOutput("privKey.key", Context.MODE_PRIVATE);
			out2 = context.openFileOutput("pubKey.key", Context.MODE_PRIVATE);
			
			out1.write(Base64.encode(kp.getPrivate().getEncoded(), Base64.DEFAULT));
			out2.write(Base64.encode(kp.getPublic().getEncoded(), Base64.DEFAULT));
		} catch (Exception e) {
			Log.e("PgpHelper", "Failed to generate key pair: " + e.getMessage());
		} finally {
			try {
				out1.close();
				out2.close();
			} catch (IOException e) {
				Log.e("PgpHelper", e.getMessage());
			}
		}
		Log.d("PgpHelper", "Key pair successfully created.");
	}
	
	public byte[] encryptWithPublicKey(byte[] message, String publicKey) throws Exception {
		String strippedKey = Crypto.stripPublicKeyHeaders(publicKey); 
		PublicKey apiPublicKey= Crypto.getRSAPublicKeyFromString(strippedKey); 
		Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding", "SC");
		rsaCipher.init(Cipher.ENCRYPT_MODE, apiPublicKey); 
		return rsaCipher.doFinal(message);
	}
	
	public byte[] decryptWithPrivateKey(byte[] message, String privateKey) throws Exception {
		PrivateKey pKey = Crypto.getRSAPrivateKeyFromString(privateKey); 
		Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding", "SC");
		rsaCipher.init(Cipher.DECRYPT_MODE, pKey); 
		return rsaCipher.doFinal(message);
	}
	
	public byte[] signWithPrivateKey(byte[] bytes, String privateKey) throws Exception {
		Signature signature = Signature.getInstance("SHA256withRSA", "SC"); 
		signature.initSign(Crypto.getRSAPrivateKeyFromString(privateKey)); 
		signature.update(bytes); 
		return signature.sign();
	}
	
	public boolean verifyWithPublicKey(byte[] message, byte[] sign, String publicKey) throws Exception {
		Signature signature = Signature.getInstance("SHA256withRSA", "SC"); 
		signature.initVerify(Crypto.getRSAPublicKeyFromString(publicKey));
		signature.update(message);
		return signature.verify(sign);
	}
	
	private KeyPair generateRSAKeyPair() throws Exception {
		SecureRandom random = new SecureRandom(); 
		RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4); 
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "SC"); 
		generator.initialize(spec, random); 
		return generator.generateKeyPair();
	}

}
