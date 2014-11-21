package se.teamgejm.safesend.io;

import android.content.Context;
import se.teamgejm.safesend.entities.UserCredentials;

import java.io.*;

/**
 * @author Emil Stjerneman
 */
public class UserCredentialsHelper {

    private final static String CREDENTIAL_FILE = "session";

    /**
     * Opens the credential file on the file system and returns the
     * UserCredentials object or null.
     *
     * @return a UserCredentials object or null.
     */
    public static UserCredentials readUserCredentials (Context context) {
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
        }
        catch (OptionalDataException e) {
            e.printStackTrace();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (StreamCorruptedException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Writes the UserCredentials object to a local file.
     *
     * @param userCredentials
     *         the UserCredentials object to save.
     */
    public static void writeUserCredentials (Context context, UserCredentials userCredentials) {
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
