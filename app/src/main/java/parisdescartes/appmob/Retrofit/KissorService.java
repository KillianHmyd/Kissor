package parisdescartes.appmob.Retrofit;

import parisdescartes.appmob.Item.Event;
import parisdescartes.appmob.Item.Participation;
import parisdescartes.appmob.Item.User;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Killian on 25/02/2016.
 */

public interface KissorService {
    public static final String ENDPOINT = "http://172.30.46.62:8080/api";

    @GET("/user")
    void getUser(@Query("userid") int userid, @Header("TOKEN") String tokenFacebook, Callback<User> callback);

    @GET("/user")
    void getUser(@Header("TOKEN") String tokenFacebook, Callback<User> callback);

    @POST("/events")
    void createEvent(@Header("TOKEN") String tokenFacebook, @Body Event event, Callback<Event> callback);

    @GET("/events")
    void getEvent(@Header("TOKEN") String tokenFacebook, Callback<ResponseEvents> callback);

    @GET("/participation/user")
    void getParticipations(@Header("TOKEN") String tokenFacebook, Callback<ResponseParticipation> callback);

    @POST("/participation")
    void partcipe(@Header("TOKEN") String tokenFacebook, @Body Participation participation, Callback<Participation> callback);

    @GET("/user/{userid}")
    void getFriendlyUser(@Header("TOKEN") String tokenFacebook, @Path("userid") long userid, Callback<User> callback);



}
