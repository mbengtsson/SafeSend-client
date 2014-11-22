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

    private final static String CREDENTIAL_FILE = "session";

    private static UserCredentialsHelper userCredentialsHelper;

    private UserCredentialsHelper () {
        // No instace.
    }

    public static UserCredentialsHelper getInstance () {
        if (userCredentialsHelper == null) {
            return new UserCredentialsHelper();
        }

        return userCredentialsHelper;
    }

    /**
     * Opens the credential file on the file system and returns the
     * UserCredentials object or null.
     *
     * @return a UserCredentials object or null.
     */
    public UserCredentials readUserCredentials (Context context) {
        UserCredentials userCredentials = null;
        try {
            FileInputStream fis = context.openFileInput(CREDENTIAL_FILE);
            ObjectInputStream is = new ObjectInputStream(fis);
            userCredentials = (UserCredentials) is.readObject();
            is.close();
            fis.close();
            return userCredentials;
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
        finally {
            return userCredentials;
        }
    }

    /**
     * Writes the UserCredentials object to a local file.
     *
     * @param userCredentials
     *         the UserCredentials object to save.
     */
    public void writeUserCredentials (Context context, UserCredentials userCredentials) {
        // Never save password.
        userCredentials.setPassword(null);

        try {
            FileOutputStream fos = context.openFileOutput(CREDENTIAL_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(userCredentials);
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
