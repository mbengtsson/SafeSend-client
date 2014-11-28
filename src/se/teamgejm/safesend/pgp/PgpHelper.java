package se.teamgejm.safesend.pgp;

import android.content.Context;
import android.util.Log;
import org.spongycastle.openpgp.PGPException;
import org.spongycastle.openpgp.PGPPublicKeyRing;
import org.spongycastle.openpgp.PGPSecretKeyRing;
import org.spongycastle.openpgp.PGPUtil;
import org.spongycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import se.teamgejm.safesend.entities.CurrentUser;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Helper class for all PGP purposes within the application. Easy-to-use methods.
 *
 * @author Gustav
 */
public class PgpHelper {

    // File names of messages in different encryption states.
    public static final String MESSAGE_PLAINTEXT = "message.txt";
    public static final String MESSAGE_SIGNED = "signed.asc";
    public static final String MESSAGE_ENCRYPTED = "message.asc";
    public static final String MESSAGE_DEFAULT_NAME = "signed.asc";

    // File names of keys
    public static final String KEY_PRIVATE = "private.asc";
    public static final String KEY_PUBLIC = "public.asc";

    private static Context context;

    private static final String TAG = "PgpHelper";

    /**
     * Generates a private and public key with the users credentials.
     *
     * @param context
     *         Application context need to open FileInput/OutputStreams.
     * @param identity
     *         The users email.
     * @param password
     *         The users password.
     *
     * @return The public key, null if an error occurred.
     */
    public static String generateKeyPair (Context context, String identity, String password) {
        PgpHelper.context = context;

        String publicKey = null;

        try {
            FileOutputStream privKey = context.openFileOutput(KEY_PRIVATE, Context.MODE_PRIVATE);
            FileOutputStream pubKey = context.openFileOutput(KEY_PUBLIC, Context.MODE_PRIVATE);

            publicKey = PgpKeyPairGenerator.exportKeyPair(privKey, pubKey, identity, password.toCharArray(), true);
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return publicKey;
    }

    /**
     * Signs a file with the senders private key and encrypts it with the receivers public key.
     *
     * @param context
     *         Application context need to open FileInput/OutputStreams.
     * @param publicKeyIn
     *         The receivers public key,
     *
     * @return The encrypted message, null if an error occurred.
     */
    public static String signAndEncrypt (Context context, InputStream publicKeyIn, String message) {
        PgpHelper.context = context;

        String encryptedMessage = null;

        PgpHelper.createFile(PgpHelper.getContext(), message.getBytes(), PgpHelper.MESSAGE_PLAINTEXT);

        try {
            Log.d(TAG, "STARTING SIGNING PROCESS");
            Log.d(TAG, "Signing message...");

            File msg = new File(PgpHelper.getContext().getFilesDir(), MESSAGE_PLAINTEXT);

            FileOutputStream signedMessageStream = PgpHelper.getContext().openFileOutput(MESSAGE_SIGNED, Context.MODE_PRIVATE);
            InputStream privKey = PgpHelper.getContext().openFileInput(KEY_PRIVATE);
            PgpSignedFileProcessor.signFile(msg, privKey, signedMessageStream, CurrentUser.getInstance().getPassword().toCharArray(), false);

            Log.d(TAG, "Message signed!");
            Log.d(TAG, "SIGNING PROCESS COMPLETE");

            Log.d(TAG, "STARTING ENCRYPTION PROCESS");
            Log.d(TAG, "Encrypting message...");

            PgpFileProcessor.encryptFile(MESSAGE_ENCRYPTED, MESSAGE_SIGNED, publicKeyIn, true, false);

            Log.d(TAG, "Signed message encrypted!");
            Log.d(TAG, "ENCRYPTION PROCESS COMPLETE");

            encryptedMessage = fileToString(MESSAGE_ENCRYPTED, PgpHelper.getContext());

        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        // Delete the files generated after encryption
        PgpHelper.getContext().deleteFile(MESSAGE_PLAINTEXT);
        PgpHelper.getContext().deleteFile(MESSAGE_SIGNED);
        PgpHelper.getContext().deleteFile(MESSAGE_ENCRYPTED);

        return encryptedMessage;
    }

    /**
     * Decrypts a file with the receivers private key and verifies the senders identity using his/her public key.
     *
     * @param context
     *         Application context need to open FileInput/OutputStreams.
     * @param publicKeyIn
     *         The senders public key.
     *
     * @return The decrypted message, null if an error occurred.
     */
    public static String decryptAndVerify (Context context, InputStream publicKeyIn, byte[] encryptedMessage) {
        PgpHelper.context = context;

        String message = null;

        PgpHelper.createFile(PgpHelper.getContext(), encryptedMessage, PgpHelper.MESSAGE_ENCRYPTED);

        try {
            Log.d(TAG, "STARTING DECRYPTION PROCESS");

            PgpFileProcessor.decryptFile(MESSAGE_ENCRYPTED, KEY_PRIVATE, CurrentUser.getInstance().getPassword().toCharArray(), MESSAGE_DEFAULT_NAME);

            Log.d(TAG, "DECRYPTION PROCESS COMPLETE");

            Log.d(TAG, "STARTING VERIFICATION PROCESS");
            Log.d(TAG, "Signing senders public key...");

            PGPSecretKeyRing secRing = new PGPSecretKeyRing(PGPUtil.getDecoderStream(PgpHelper.getContext().openFileInput(KEY_PRIVATE)),
                    new JcaKeyFingerprintCalculator());
            PGPPublicKeyRing pubRing = new PGPPublicKeyRing(PGPUtil.getDecoderStream(publicKeyIn),
                    new JcaKeyFingerprintCalculator());
            PGPPublicKeyRing signedRing = new PGPPublicKeyRing(new ByteArrayInputStream(PgpSignedFileProcessor.signPublicKey(secRing.getSecretKey(), CurrentUser.getInstance().getPassword(), pubRing.getPublicKey(), "Auto-signed", "Safe-Send")), new JcaKeyFingerprintCalculator());

            Log.d(TAG, "Senders public key signed!");

            Log.d(TAG, "Verifying signature...");

            PgpSignedFileProcessor.verifyFile(MESSAGE_SIGNED, signedRing);

            Log.d(TAG, "Signature verified!");
            Log.d(TAG, "VERIFICATION PROCESS COMPLETE");

            message = fileToString(MESSAGE_PLAINTEXT, PgpHelper.getContext());
        }
        catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        catch (PGPException e) {
            Log.e(TAG, e.getMessage());
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        // Delete the files generated after decryption
        PgpHelper.getContext().deleteFile(MESSAGE_ENCRYPTED);
        PgpHelper.getContext().deleteFile(MESSAGE_SIGNED);
        PgpHelper.getContext().deleteFile(MESSAGE_PLAINTEXT);

        return message;
    }

    /**
     * Creates a file of a byte array.
     *
     * @param context
     *         Application context need to open FileInput/OutputStreams.
     * @param content
     *         The byte array to persist.
     * @param fileName
     *         The file name.
     */
    public static void createFile (Context context, byte[] content, String fileName) {
        PgpHelper.context = context;

        try {
            OutputStream out = (PgpHelper.getContext().openFileOutput(fileName, Context.MODE_PRIVATE));

            out.write(content);

            out.close();
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Creates a String of a file.
     *
     * @param fileName
     *         The name of the file.
     * @param context
     *         Application context need to open FileInput/OutputStreams.
     *
     * @return The String representation of the file.
     */
    public static String fileToString (String fileName, Context context) {
        File file = new File(context.getFilesDir(), fileName);
        byte[] b = null;
        try {
            InputStream in = context.openFileInput(fileName);
            b = new byte[(int) file.length()];
            int len = b.length;
            int total = 0;

            while (total < len) {
                int result = in.read(b, total, len - total);
                if (result == -1) {
                    break;
                }
                total += result;
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return new String(b, Charset.defaultCharset());
    }

    public static Context getContext () {
        return context;
    }

}
