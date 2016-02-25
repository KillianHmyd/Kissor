package parisdescartes.appmob.Retrofit;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Killian on 25/02/2016.
 */

public interface PartyService {
    public static final String ENDPOINT = "http://192.168.1.26:8080/api";

    @GET("/user")
    void getUser(@Query("userid") int userid, @Header("TOKEN") String tokenFacebook, Callback<User> callback);

    @GET("/user")
    void getUser(@Header("TOKEN") String tokenFacebook, Callback<User> callback);

    @POST("/events")
    void createEvent(@Header("TOKEN") String tokenFacebook, @Body Event event, Callback<Event> callback);

    @GET("/events")
    void getEvent(@Header("TOKEN") String tokenFacebook, Callback<List<Event>> callback);



}
