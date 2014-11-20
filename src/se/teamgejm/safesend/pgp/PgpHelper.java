package se.teamgejm.safesend.pgp;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.Date;

import org.spongycastle.bcpg.ArmoredOutputStream;
import org.spongycastle.bcpg.HashAlgorithmTags;
import org.spongycastle.jce.spec.ElGamalParameterSpec;
import org.spongycastle.openpgp.PGPEncryptedData;
import org.spongycastle.openpgp.PGPException;
import org.spongycastle.openpgp.PGPKeyPair;
import org.spongycastle.openpgp.PGPKeyRingGenerator;
import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPPublicKeyRing;
import org.spongycastle.openpgp.PGPSecretKeyRing;
import org.spongycastle.openpgp.PGPSignature;
import org.spongycastle.openpgp.PGPUtil;
import org.spongycastle.openpgp.operator.PGPDigestCalculator;
import org.spongycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.spongycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcaPGPKeyPair;
import org.spongycastle.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder;

import android.content.Context;
import android.util.Log;

public class PgpHelper {
	
	public static final String MESSAGE_PLAINTEXT = "message.txt";
	public static final String MESSAGE_SIGNED = "signed.asc";
	public static final String MESSAGE_ENCRYPTED = "message.asc";
	public static final String MESSAGE_DEFAULT_NAME = "signed.asc";
	
	public static final String KEY_PRIVATE = "private.asc";
	public static final String KEY_PUBLIC = "public.asc";
	
	public static final String PASSWORD = "password";
	public static final String IDENTITY = "john.doe@example.com";
	public static final String PRIME_MODULUS =
			"EF07B0F39662DC8600224E46AB8BE8CB72E552D52E88013D20EC039A0697ED9AAD018B16F0B910D4AD54437B8585A" +
			"AA4EAE0CE216E31F50EDF0CD05DAF5E02A73D399C91B38220EC3B62C42D1CF6BF06378533A70C1F8F4F4416DD542213" +
			"D3432412125FDBFF7B9473CE6F8812D860E66282C9F34C1774D1EA57D54DADDF7E37A12C4A6AD5B4A30128C29D27D03" +
			"B6535C0F7A8AF857E18ECAB992984E6D546918AAACB971A2AC2C2E7AF79A9547979E6342DB7443985E5F7EDF6F9F22B" +
			"600EEB42CB84A5F1ACD76E213C52E3052DAE1A9119801CFA28E6EFD4F6BC35FA06C8724D78A96AF054826C0BF865D0E" +
			"C5F6F4D31C1D3F7CF2FE6F16AF267A7BA04753AEF420D4D8C36BCE8D9694814B9E9C3DF468064EB5636405C71CA9D8" +
			"D50D36570B42639C9C2C02FB3A3D0C6B28DD200B0AF164C621D60B12E35E4D00129C8900F6EFDBB49FF34DD64CB13C" +
			"D4087A7F84FEFD77D4E8099C2B804BA643EAFCA66D1F02BD09AE44AC83A5149F60711B7B108C01D53FF15FA59B36BE6" +
			"2A870F163F5063CEE103B377808343AFBD32271199E26D93734011BED2305EDE2E841EAD512E23B8C9B8CD4D398C7B" +
			"4C8B76B355CC150B66B8EB7779E2CA519E10E45D0FB138676850C56F23DB135F546D364B92BC1C9423E089D30D4D57D" +
			"27D7885EE14AE135A488C0542C3719FBEF46F4BB5FB53A28DA26DDF84C8BC55348A8AA466ED9CC7";
	public static final String BASE_GENERATOR = "5";
	
	private static Context context;
	
	public static void generateKeyPair(Context context) {
		PgpHelper.context = context;

		try {
			KeyPair dsaKeyPair = generateDsaKeyPair(1024);
			KeyPair elGamalKeyPair = generateElGamalKeyPair(PRIME_MODULUS, BASE_GENERATOR);
			
			FileOutputStream privKey = context.openFileOutput(KEY_PRIVATE, Context.MODE_PRIVATE);
			FileOutputStream pubKey = context.openFileOutput(KEY_PUBLIC, Context.MODE_PRIVATE);

			exportKeyPair(privKey, pubKey, dsaKeyPair, elGamalKeyPair, IDENTITY, PASSWORD.toCharArray(), true);

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void createMessage(Context context, String message) {
		PgpHelper.context = context;
		
		try {
			OutputStream out = (PgpHelper.getContext().openFileOutput(MESSAGE_PLAINTEXT, Context.MODE_PRIVATE));
			
			out.write(message.getBytes());
	        
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public static String signAndEncrypt(Context context) {
		PgpHelper.context = context;
		
		String encryptedMessage = null;
		
		try {
			Log.d("PgpHelper", "STARTING SIGNING PROCESS");
			Log.d("PgpHelper", "Signing message...");

			File msg = new File(PgpHelper.getContext().getFilesDir(), MESSAGE_PLAINTEXT);
			
			FileOutputStream signedMessageStream = PgpHelper.getContext().openFileOutput(MESSAGE_SIGNED, Context.MODE_PRIVATE);
			InputStream privKey = PgpHelper.getContext().openFileInput(KEY_PRIVATE);
			PgpFileSigner.signFile(msg, privKey, signedMessageStream, PgpHelper.PASSWORD.toCharArray(), true);

			Log.d("PgpHelper", "Message signed!");
			Log.d("PgpHelper", "SIGNING PROCESS COMPLETE");
			
			Log.d("PgpHelper", "STARTING ENCRYPTION PROCESS");
			Log.d("PgpHelper", "Encrypting message...");
			
			PgpFileEncrypter.encryptFile(MESSAGE_ENCRYPTED, MESSAGE_SIGNED, KEY_PUBLIC, false, true);
			
			Log.d("PgpHelper", "Signed message encrypted!");
			Log.d("PgpHelper", "ENCRYPTION PROCESS COMPLETE");
			
			encryptedMessage = fileToString(MESSAGE_ENCRYPTED, PgpHelper.getContext());

		} catch (Exception e) {
			Log.e("PgpHelper", e.getMessage());
		}
		
		PgpHelper.getContext().deleteFile(MESSAGE_PLAINTEXT);
		PgpHelper.getContext().deleteFile(MESSAGE_SIGNED);
		
		return encryptedMessage;
	}
	
	public static String decryptAndVerify(Context context) {
		
		String message = null;

		try {
			Log.d("PgpHelper", "STARTING DECRYPTION PROCESS");
			
			PgpFileEncrypter.decryptFile(MESSAGE_ENCRYPTED, KEY_PRIVATE, PgpHelper.PASSWORD.toCharArray(), MESSAGE_DEFAULT_NAME);

			Log.d("PgpHelper", "DECRYPTION PROCESS COMPLETE");
			
			Log.d("PgpHelper", "STARTING VERIFICATION PROCESS");
			Log.d("PgpHelper", "Signing senders public key...");
			
			PGPSecretKeyRing secRing = new PGPSecretKeyRing(PGPUtil.getDecoderStream(PgpHelper.getContext().openFileInput(KEY_PRIVATE)),
					new JcaKeyFingerprintCalculator());
			PGPPublicKeyRing pubRing = new PGPPublicKeyRing(PGPUtil.getDecoderStream(PgpHelper.getContext().openFileInput(KEY_PUBLIC)),
					new JcaKeyFingerprintCalculator());
			PGPPublicKeyRing signedRing = new PGPPublicKeyRing(new ByteArrayInputStream(PgpFileSigner.signPublicKey(secRing.getSecretKey(), PgpHelper.PASSWORD, pubRing.getPublicKey(), "Auto-signed", "Safe-Send")), new JcaKeyFingerprintCalculator());

			Log.d("PgpHelper", "Senders public key signed!");
			
			Log.d("PgpHelper", "Verifying signature...");
			
			PgpFileSigner.verifyFile(MESSAGE_SIGNED, signedRing);

			Log.d("PgpHelper", "Signature verified!");
			Log.d("PgpHelper", "VERIFICATION PROCESS COMPLETE");
			
			message = fileToString(MESSAGE_PLAINTEXT, PgpHelper.getContext());
		} catch (FileNotFoundException e) {
			Log.e("PgpHelper", e.getMessage());
		} catch (IOException e) {
			Log.e("PgpHelper", e.getMessage());
		} catch (PGPException e) {
			Log.e("PgpHelper", e.getMessage());
		} catch (Exception e) {
			Log.e("PgpHelper", e.getMessage());
		}
		PgpHelper.getContext().deleteFile(MESSAGE_ENCRYPTED);
		PgpHelper.getContext().deleteFile(MESSAGE_SIGNED);
		
		return message;
	}
	
	public static String keyToString(byte[] key) {
		StringBuffer retString = new StringBuffer();
		for (int i = 0; i < key.length; ++i) {
            retString.append(Integer.toHexString(0x0100 + (key[i] & 0x00FF)).substring(1));
        }
		return retString.toString();
	}

	public static Context getContext() {
		return context;
	}
	
	private static void exportKeyPair(OutputStream secretOut, OutputStream publicOut, KeyPair dsaKp, KeyPair elgKp,
			String identity, char[] passPhrase, boolean armor) throws IOException, InvalidKeyException,
			NoSuchProviderException, SignatureException, PGPException {
		
		if (armor) {
			secretOut = new ArmoredOutputStream(secretOut);
		}

		PGPKeyPair dsaKeyPair = new JcaPGPKeyPair(PGPPublicKey.DSA, dsaKp, new Date());
		PGPKeyPair elgKeyPair = new JcaPGPKeyPair(PGPPublicKey.ELGAMAL_ENCRYPT, elgKp, new Date());
		PGPDigestCalculator sha1Calc = new JcaPGPDigestCalculatorProviderBuilder().build().get(HashAlgorithmTags.SHA1);
		PGPKeyRingGenerator keyRingGen = new PGPKeyRingGenerator(PGPSignature.POSITIVE_CERTIFICATION, dsaKeyPair,
				identity, sha1Calc, null, null, new JcaPGPContentSignerBuilder(dsaKeyPair.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1), new JcePBESecretKeyEncryptorBuilder(PGPEncryptedData.AES_256, sha1Calc).setProvider("SC").build(passPhrase));

		keyRingGen.addSubKey(elgKeyPair);

		keyRingGen.generateSecretKeyRing().encode(secretOut);

		secretOut.close();

		if (armor) {
			publicOut = new ArmoredOutputStream(publicOut);
		}

		keyRingGen.generatePublicKeyRing().encode(publicOut);

		publicOut.close();
	}

	private static final KeyPair generateDsaKeyPair(int keySize) throws NoSuchAlgorithmException,
			NoSuchProviderException {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA", "SC");
		keyPairGenerator.initialize(keySize);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		return keyPair;
	}

	private static final KeyPair generateElGamalKeyPair(String primeModulous, String baseGenerator) throws Exception {

		BigInteger p = new BigInteger(primeModulous, 16);
		BigInteger g = new BigInteger(baseGenerator, 16);

		ElGamalParameterSpec paramSpecs = new ElGamalParameterSpec(p, g);

		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ELGAMAL", "SC");
		keyPairGenerator.initialize(paramSpecs);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		return keyPair;
	}
	
	private static String fileToString(String fileName, Context context) throws IOException {
		File file = new File(context.getFilesDir(), fileName);
		InputStream in = context.openFileInput(fileName);
		byte[] b  = new byte[(int) file.length()];
		int len = b.length;
		int total = 0;

		while (total < len) {
		  int result = in.read(b, total, len - total);
		  if (result == -1) {
		    break;
		  }
		  total += result;
		}

		return new String(b , Charset.defaultCharset());
	}

}