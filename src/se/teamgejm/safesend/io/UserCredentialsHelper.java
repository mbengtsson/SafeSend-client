package se.teamgejm.safesend.io;

import android.content.Context;
import se.teamgejm.safesend.entities.UserCredentials;

import java.io.*;

/**
 * @author Emil Stjerneman
 */
public class UserCredentialsHelper {

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
        try {
            FileInputStream fis = context.openFileInput(CREDENTIAL_FILE);
            ObjectInputStream is = new ObjectInputStream(fis);
            UserCredentials userCredentials = (UserCredentials) is.readObject();
            is.close();
            fis.close();
            return userCredentials;
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        catch (OptionalDataException e) {
            e.printStackTrace();
            return null;
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        catch (StreamCorruptedException e) {
            e.printStackTrace();
            return null;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
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
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
