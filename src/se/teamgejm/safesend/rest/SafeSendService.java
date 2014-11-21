package se.teamgejm.safesend.rest;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import se.teamgejm.safesend.entities.SendMessageRequest;
import se.teamgejm.safesend.entities.User;
import se.teamgejm.safesend.entities.request.RegisterUserRequest;
import se.teamgejm.safesend.entities.request.ValidateCredentialsRequest;

import java.util.List;

/**
 * @author Emil Stjerneman
 */
public interface SafeSendService {

    @POST("/users")
    public void registerUser (@Body RegisterUserRequest request, Callback<User> cb);

    @GET("/users")
    public void getUsers (Callback<List<User>> cb);

    @POST("/users/validate_credentials")
    public void validateCredentials (@Body ValidateCredentialsRequest request, Callback<String> cb);

    @GET("/users/{id}/pubkey")
    public void getUserKey (@Path("id") long id, Callback<User> cb);

    @POST("/messages/send")
    public void sendMessage (@Body SendMessageRequest message, Callback<String> cb);

}
