package se.teamgejm.safesend.rest;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import se.teamgejm.safesend.entities.SendMessageRequest;
import se.teamgejm.safesend.entities.User;

import java.util.List;

/**
 * @author Emil Stjerneman
 */
public interface SafeSendService {

    @GET("/users")
    public void getUsers (Callback<List<User>> cb);

    @POST("/message/send")
    public void sendMessage (@Body SendMessageRequest message, Callback<String> cb);

}
