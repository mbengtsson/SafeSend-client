package se.teamgejm.safesend.rest;

import retrofit.Callback;
import retrofit.http.GET;
import se.teamgejm.safesend.entities.User;

import java.util.List;

/**
 * @author Emil Stjerneman
 */
public interface SafeSendService {

    @GET("/users")
    public void getUsers (Callback<List<User>> cb);

}
