package se.teamgejm.safesend.io;

import android.content.Context;
import android.util.Log;
import se.teamgejm.safesend.entities.CurrentUser;

import java.io.*;

/**
 * Helper class to read and write to the session file stored locally on the
 * device.
 *
 * @author Emil Stjerneman
 */
public class CurrentUserHelper {

    private final static String TAG = "UserCredentialsHelper";

    public final static String CREDENTIAL_FILE = "session";

    /**
     * Open and read the session file on the local device and sets the details
     * to the current user singleton.
     */
    public static void readCurrentUserDetails (Context context) {
        try {
            FileInputStream fis = context.openFileInput(CurrentUserHelper.CREDENTIAL_FILE);
            ObjectInputStream is = new ObjectInputStream(fis);

            CurrentUser currentUser = (CurrentUser) is.readObject();

            CurrentUser.getInstance().setUserId(currentUser.getUserId());
            CurrentUser.getInstance().setDisplayName(currentUser.getDisplayName());
            CurrentUser.getInstance().setEmail(currentUser.getEmail());

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
     * Writes the current user details to a local file.
     */
    public static void writeCurrentUserDetails (Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(CurrentUserHelper.CREDENTIAL_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(CurrentUser.getInstance());
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
