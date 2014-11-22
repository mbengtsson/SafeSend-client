package se.teamgejm.safesend.io;

import android.content.Context;
import android.util.Log;
import se.teamgejm.safesend.entities.UserCredentials;

import java.io.*;

/**
 * Helper class to read and write to the session file stored locally on the
 * device.
 *
 * @author Emil Stjerneman
 */
public class UserCredentialsHelper {

    private final static String TAG = "UserCredentialsHelper";

    public final static String CREDENTIAL_FILE = "session";

    /**
     * Opens the credential file on the file system and returns the
     * UserCredentials object or null.
     */
    public static void readUserCredentials (Context context) {
        try {
            FileInputStream fis = context.openFileInput(UserCredentialsHelper.CREDENTIAL_FILE);
            ObjectInputStream is = new ObjectInputStream(fis);
            UserCredentials userCredentials = (UserCredentials) is.readObject();
            UserCredentials.getInstance().setEmail(userCredentials.getEmail());
            is.close();
            fis.close();
        }
        catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        catch (OptionalDataException e) {
            Log.e(TAG, e.getMessage());
        }
        catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        catch (StreamCorruptedException e) {
            Log.e(TAG, e.getMessage());
        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Writes the UserCredentials object to a local file.
     */
    public static void writeUserCredentials (Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(UserCredentialsHelper.CREDENTIAL_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(UserCredentials.getInstance());
            os.close();
        }
        catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
