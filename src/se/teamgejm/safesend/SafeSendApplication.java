package se.teamgejm.safesend;

import android.app.Application;
import se.teamgejm.safesend.entities.UserCredentials;

/**
 * @author Emil Stjerneman
 */
public class SafeSendApplication extends Application {

    private static UserCredentials currentUser;

    public static UserCredentials getCurrentUser () {
        return SafeSendApplication.currentUser;
    }

    public static void setCurrentUser (UserCredentials currentUser) {
        SafeSendApplication.currentUser = currentUser;
    }
}
